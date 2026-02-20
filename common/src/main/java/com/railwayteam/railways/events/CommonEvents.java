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

package com.railwayteam.railways.events;

import com.railwayteam.railways.annotation.multiloader.MultiLoaderEvent;
import com.railwayteam.railways.content.cycle_menu.TagCycleHandlerServer;
import com.railwayteam.railways.content.schedule.RedstoneLinkInstruction;
import com.railwayteam.railways.util.packet.PacketSender;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class CommonEvents {

    @MultiLoaderEvent
    public static void onWorldTickStart(Level level) {
        if (level.isClientSide)
            return;
        RedstoneLinkInstruction.tick(level);
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
