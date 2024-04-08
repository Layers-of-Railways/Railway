package com.railwayteam.railways.content.palettes.boiler.forge;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

public class BoilerBigOutlinesImpl {
    public static double getRange(Player player) {
        return player.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue();
    }
}
