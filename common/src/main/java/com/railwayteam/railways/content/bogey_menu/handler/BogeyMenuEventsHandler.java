/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.content.bogey_menu.handler;

import com.railwayteam.railways.annotation.event.MultiLoaderEvent;
import com.railwayteam.railways.content.bogey_menu.BogeyMenuScreen;
import com.railwayteam.railways.registry.CRKeys;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.AllBlocks;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.GameType;

public class BogeyMenuEventsHandler {
    public static int COOLDOWN = 0;

    @MultiLoaderEvent
    public static void clientTick() {
        if (COOLDOWN > 0 && !CRKeys.BOGEY_MENU.isPressed())
            COOLDOWN--;
    }

    @MultiLoaderEvent
    public static void onKeyInput(int key, boolean pressed) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        if (key != CRKeys.BOGEY_MENU.getBoundCode() || !pressed)
            return;
        if (COOLDOWN > 0)
            return;
        LocalPlayer player = mc.player;
        if (player == null)
            return;

        if (!EntityUtils.isHolding(player, AllBlocks.RAILWAY_CASING::isIn))
            return;

        ScreenOpener.open(new BogeyMenuScreen());
    }
}
