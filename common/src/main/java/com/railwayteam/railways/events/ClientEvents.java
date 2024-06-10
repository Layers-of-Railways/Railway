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

import com.railwayteam.railways.annotation.event.MultiLoaderEvent;
import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.bogey_menu.handler.BogeyMenuEventsHandler;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.content.cycle_menu.TagCycleHandlerClient;
import com.railwayteam.railways.content.qol.TrackEdgePointHighlighter;
import com.railwayteam.railways.registry.CRKeys;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.ConfigureDevCapeC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

public class ClientEvents {

    @ApiStatus.Internal
    public static boolean previousDevCapeSetting = false;

    @MultiLoaderEvent
    public static void onClientTickStart(Minecraft mc) {
        CRKeys.fixBinds();
        PhantomSpriteManager.tick(mc);

        Level level = mc.level;
        long ticks = level == null ? 1 : level.getGameTime();
        if (ticks % 40 == 0 && previousDevCapeSetting != (previousDevCapeSetting = CRConfigs.client().useDevCape.get())) {
            CRPackets.PACKETS.send(new ConfigureDevCapeC2SPacket(previousDevCapeSetting));
        }

        if (DummyRailwayMarkerHandler.getInstance() != null) {
            if (ticks % CRConfigs.client().journeymapRemoveObsoleteTicks.get() == 0) {
                DummyRailwayMarkerHandler.getInstance().removeObsolete();
                DummyRailwayMarkerHandler.getInstance().reloadMarkers();
            }

            if (ticks % CRConfigs.client().journeymapUpdateTicks.get() == 0) {
                DummyRailwayMarkerHandler.getInstance().runUpdates();
            }
        }

        if (isGameActive()) {
            BogeyMenuEventsHandler.clientTick();
            TagCycleHandlerClient.clientTick();
            ConductorPossessionController.onClientTick(mc, true);
            TrackEdgePointHighlighter.clientTick(mc);
        }
    }

    @MultiLoaderEvent
    public static void onClientTickEnd(Minecraft mc) {
        if (isGameActive()) {
            ConductorPossessionController.onClientTick(mc, false);
        }
    }

    @MultiLoaderEvent
    public static void onClientWorldLoad(Level level) {
        DummyRailwayMarkerHandler.getInstance().onJoinWorld();
        PhantomSpriteManager.firstRun = true;
    }

    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }

    @MultiLoaderEvent
    public static void onKeyInput(int key, boolean pressed) {
        if (Minecraft.getInstance().screen != null)
            return;
        BogeyMenuEventsHandler.onKeyInput(key, pressed);
        if (Minecraft.getInstance().screen != null)
            return;
        TagCycleHandlerClient.onKeyInput(key, pressed);
    }

    @MultiLoaderEvent
    public static void onTagsUpdated() {
        TagCycleHandlerClient.onTagsUpdated();
    }
}