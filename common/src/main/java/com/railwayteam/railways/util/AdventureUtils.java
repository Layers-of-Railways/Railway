package com.railwayteam.railways.util;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

// copied from Create Fabric
public class AdventureUtils {
    public static boolean isAdventure(@Nullable Player player) {
        return player != null && !player.mayBuild() && !player.isSpectator();
    }
}
