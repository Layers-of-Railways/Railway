package com.railwayteam.railways;

import com.railwayteam.railways.capabilities.*;

import com.railwayteam.railways.packets.CustomPacketStationList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketDirection;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid=Railways.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class RailwaysEventHandler {
  @SubscribeEvent
  public void interactSpecificEntity (final EntityInteractSpecific eis) {
    PlayerEntity player = eis.getPlayer();
    Entity target = eis.getTarget();
    if (!(target instanceof MinecartEntity)) return;
    // else is minecart
    StationListCapability list = target.getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).orElse(null);
    if (list == null) return;
    // else process it
    if (player.isSneaking()) {
      // just check it, don't assign
      if (eis.getSide().isClient()) player.sendMessage(new StringTextComponent("station: " + list.getEntry()));
    } else {
      // assign to it
      String candidate = player.getDisplayName().getFormattedText();
      if (list.getEntry().equals(candidate)) {
        if (eis.getSide().isClient()) player.sendMessage(new StringTextComponent("station already assigned"));
        return;
      }
      list.setEntry(candidate);
      if (eis.getSide().isServer()) {
        RailwaysPacketHandler.channel.send(PacketDistributor.TRACKING_ENTITY.with (()->target), new CustomPacketStationList(target.getEntityId(), candidate));
        Railways.LOGGER.debug("sent update packet");
      }
      if (eis.getSide().isClient()) player.sendMessage(new StringTextComponent("assigned station: " + candidate));
    }
    eis.setCanceled(true);
    eis.setCancellationResult(ActionResultType.SUCCESS); // stop further events
  }

  @SubscribeEvent
  public void attachCapability (final AttachCapabilitiesEvent<Entity> ace) {
    if (ace.getObject() instanceof MinecartEntity) {
      ace.addCapability(CapabilitySetup.STATION_LIST_KEY, new StationListProvider());
    }
  }

  @SubscribeEvent
  public void onPlayerTrackEntity (final PlayerEvent.StartTracking pe) {
    pe.getTarget().getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).ifPresent(capability ->
      RailwaysPacketHandler.channel.send(PacketDistributor.TRACKING_ENTITY.with(
        pe::getTarget), new CustomPacketStationList(pe.getTarget().getEntityId(), capability.getEntry())
      )
    );
  }

  @SubscribeEvent
  public void onPlayerStopTrackingEntity (final PlayerEvent.StopTracking pe) {

  }
}
