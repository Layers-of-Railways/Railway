/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.events;

import com.railwayteam.railways.annotation.event.MultiLoaderEvent;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.cycle_menu.TagCycleHandlerServer;
import com.railwayteam.railways.content.schedule.RedstoneLinkInstruction;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.PacketSender;
import com.railwayteam.railways.util.packet.TrainMarkerDataUpdatePacket;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CommonEvents {

    public static final Set<UUID> journeymapUsers = new HashSet<>();

    @MultiLoaderEvent
    public static void onWorldTickStart(Level level) {
        if (level.isClientSide)
            return;
        RedstoneLinkInstruction.tick(level);
        long ticks = level.getGameTime();
        for (Train train : Create.RAILWAYS.trains.values()) {
            long offsetTicks = ticks + train.id.hashCode();
            if (offsetTicks % CRConfigs.server().journeymap.farTrainSyncTicks.get() == 0) {
                CRPackets.PACKETS.sendTo(PlayerSelection.allWith(p -> journeymapUsers.contains(p.getUUID())),
                    new TrainMarkerDataUpdatePacket(train));
            }
            if (offsetTicks % CRConfigs.server().journeymap.nearTrainSyncTicks.get() == 0) { //DONE train *might* not have any carriages if it just got coupled, fix that
                if (!train.carriages.isEmpty()) {
                    Entity trainEntity = train.carriages.get(0).anyAvailableEntity();
                    if (trainEntity != null)
                        CRPackets.PACKETS.sendTo(PlayerSelection.trackingWith(trainEntity, p -> journeymapUsers.contains(p.getUUID())),
                            new TrainMarkerDataUpdatePacket(train));
                }
            }
        }
    }

    @MultiLoaderEvent
    public static void onPlayerJoin(ServerPlayer player) {
        PacketSender.notifyServerVersion(player);
    }

    @MultiLoaderEvent
    public static void onTagsUpdated() {
        TagCycleHandlerServer.onTagsUpdated();
    }
}
