package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.registry.CRPackets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketSender {
  public static void updateJukeboxClientside (MinecartJukebox sender, ItemStack newDisc) {
    CRPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(()->sender), new JukeboxCartPacket(sender, newDisc));
  }

  public static void syncMountedToolboxNBT(Entity entity, CompoundTag nbt) {
    CRPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MountedToolboxSyncPacket(entity, nbt));
  }
}
