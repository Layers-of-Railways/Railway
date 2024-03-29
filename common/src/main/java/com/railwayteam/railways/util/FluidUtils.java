package com.railwayteam.railways.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

public class FluidUtils {
    /**
     * All isFuel checks for anything else should go through this.
     * @return true if it is a valid fuel item, false if it isn't
     */
    @ExpectPlatform
    public static boolean isFuel(Fluid fluid) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        throw new AssertionError();
    }
}
