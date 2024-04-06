package com.railwayteam.railways.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

public class FluidUtils {
    @ExpectPlatform
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        throw new AssertionError();
    }

    /**
     * @param o Either a FluidStack (forge & fabric) or FluidVariant (fabric)
     * @return The fluid
     * @throws IllegalArgumentException If any object that isn't an instance of FluidStack or FluidVariant is passed.
     */
    @ExpectPlatform
    public static Fluid getFluid(Object o) {
        throw new AssertionError();
    }
}
