package com.railwayteam.railways.content.palettes.boiler.fabric;

import com.simibubi.create.foundation.utility.fabric.ReachUtil;
import net.minecraft.world.entity.player.Player;

public class BoilerBigOutlinesImpl {
    public static double getRange(Player player) {
        return ReachUtil.reach(player);
    }
}
