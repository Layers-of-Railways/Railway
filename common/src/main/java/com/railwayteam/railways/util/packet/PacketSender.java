/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.RailwaysBuildInfo;
import com.railwayteam.railways.annotation.event.MultiLoaderEvent;
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

  @MultiLoaderEvent
  public static void notifyServerVersion(ServerPlayer player) {
    CRPackets.PACKETS.onPlayerJoin(player);
    CRPackets.PACKETS.sendTo(player, new ModVersionPacket(RailwaysBuildInfo.VERSION));
  }
}
