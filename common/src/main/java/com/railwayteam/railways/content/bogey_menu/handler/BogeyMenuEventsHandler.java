package com.railwayteam.railways.content.bogey_menu.handler;

import com.railwayteam.railways.content.bogey_menu.BogeyMenuScreen;
import com.railwayteam.railways.registry.CRKeys;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.GameType;

public class BogeyMenuEventsHandler {
    public static int COOLDOWN = 0;

    public static void clientTick() {
        if (COOLDOWN > 0 && !CRKeys.BOGEY_MENU.isPressed())
            COOLDOWN--;
    }

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
