package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.registry.CRPackets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class PacketSender {
  public static void updateJukeboxClientside(MinecartJukebox sender, ItemStack newDisc) {
    CRPackets.PACKETS.sendTo(PlayerSelection.tracking(sender), new JukeboxCartPacket(sender, newDisc));
  }

  public static void syncMountedToolboxNBT(Entity entity, CompoundTag nbt) {
    CRPackets.PACKETS.sendTo(PlayerSelection.tracking(entity), new MountedToolboxSyncPacket(entity, nbt));
  }

  public static void notifyServerVersion(ServerPlayer player) {
    CRPackets.PACKETS.onPlayerJoin(player);
    CRPackets.PACKETS.sendTo(player, new ModVersionPacket(Railways.VERSION));
  }
}
