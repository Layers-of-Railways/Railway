package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.registry.CRPackets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketSender {
  public static void updateJukeboxClientside (MinecartJukebox sender, ItemStack newDisc) {
    CRPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(()->sender), new JukeboxCartPacket(sender, newDisc));
  }

  public static void syncMountedToolboxNBT(Entity entity, CompoundTag nbt) {
    CRPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MountedToolboxSyncPacket(entity, nbt));
  }

  public static void notifyServerVersion(Supplier<ServerPlayer> playerSupplier) {
    CRPackets.channel.send(PacketDistributor.PLAYER.with(playerSupplier), new ModVersionPacket(Railways.VERSION));
  }
}
