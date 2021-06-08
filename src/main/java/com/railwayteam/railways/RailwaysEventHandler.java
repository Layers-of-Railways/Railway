package com.railwayteam.railways;

import com.railwayteam.railways.capabilities.CapabilitySetup;
import com.railwayteam.railways.capabilities.StationListCapability;
import com.railwayteam.railways.capabilities.StationListProvider;
import com.railwayteam.railways.items.ConductorItem;
import com.railwayteam.railways.items.StationEditorItem;
import com.railwayteam.railways.items.engineers_cap.EngineersCapItem;
import com.railwayteam.railways.packets.CustomPacketStationList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid=Railways.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class RailwaysEventHandler {
  @SubscribeEvent
  public void interactSpecificEntity (final EntityInteractSpecific eis) {
    PlayerEntity player = eis.getPlayer();
    Entity target = eis.getTarget();
    if (!(target instanceof MinecartEntity)) return;
    if (player.getHeldItemMainhand().getItem() instanceof StationEditorItem) return;
    // else is minecart
    if(player.getHeldItemMainhand().getItem() instanceof ConductorItem) {
      eis.setCanceled(true);
      eis.setCancellationResult(((ConductorItem) player.getHeldItemMainhand().getItem()).onMinecartRightClicked(player, player.getHeldItemMainhand(), Hand.MAIN_HAND, (MinecartEntity) target));
      return;
    }
    StationListCapability list = target.getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).orElse(null);
    if (list == null) return;
    // else process it

    if (player.isSneaking()) {
      // just check it, don't assign
      if (eis.getSide().isClient()) {
        player.sendStatusMessage(new StringTextComponent("stations:"), false);
        Iterator<String> iter = list.iterate();
        while (iter.hasNext()) player.sendStatusMessage(new StringTextComponent("  " + iter.next()), false);
      }
    } else {
      // assign to it
      String candidate = player.getDisplayName().getString(); // TODO: dont know if getFormattedText and getString are the same
      if (list.contains(candidate)) {
        if (eis.getSide().isClient()) player.sendStatusMessage(new StringTextComponent("station already assigned"), false);
        return;
      }
      list.add(candidate);
      if (eis.getSide().isServer()) {
        RailwaysPacketHandler.channel.send(PacketDistributor.TRACKING_ENTITY.with (()->target), new CustomPacketStationList(target.getEntityId(), list.copy()));
        Railways.LOGGER.debug("sent update packet for list: " + list.copy().get(0));
      }
      if (eis.getSide().isClient()) player.sendStatusMessage(new StringTextComponent("assigned station: " + candidate), false);
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
        pe::getTarget), new CustomPacketStationList(pe.getTarget().getEntityId(), capability.copy())
      )
    );
  //  Railways.LOGGER.debug("sent tracking packet");
  }

  @SubscribeEvent
  public void onPlayerStopTrackingEntity (final PlayerEvent.StopTracking pe) {

  }

  int ticksToRcsUpdate = 20;

  @SubscribeEvent
  public void onServerTick(TickEvent.ServerTickEvent event) {
    if(event.phase == TickEvent.Phase.END) return;
    ticksToRcsUpdate--;
    if(ticksToRcsUpdate <= 0) {
      ticksToRcsUpdate = 20;
      List<ServerPlayerEntity> enableRCS = Railways.instance.enableRCS;
      DyeColor color1 = DyeColor.byId(1);
      for(ServerPlayerEntity plr : enableRCS) {
        if(!(plr.getHeldItemMainhand().getItem() instanceof ConductorItem)) {
          plr.setHeldItem(Hand.MAIN_HAND, new ItemStack(ConductorItem.g(color1)));
        }
        if(!(plr.getHeldItemOffhand().getItem() instanceof EngineersCapItem)) {
         plr.setHeldItem(Hand.OFF_HAND, new ItemStack(ModSetup.ENGINEERS_CAPS.get(color1).get()));
        }
        if(!(plr.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof EngineersCapItem)) {
          plr.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(ModSetup.ENGINEERS_CAPS.get(color1).get()));
        }
        int color = ((ConductorItem) plr.getHeldItemMainhand().getItem()).color.getId();
        color %= 16;
        color++;
        DyeColor dyeColor = DyeColor.byId(color);
        plr.setHeldItem(Hand.MAIN_HAND, new ItemStack(ConductorItem.g(dyeColor)));
        plr.setHeldItem(Hand.OFF_HAND, new ItemStack(ModSetup.ENGINEERS_CAPS.get(dyeColor).get()));
        plr.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(ModSetup.ENGINEERS_CAPS.get(dyeColor).get()));
      }
    }
  }
}