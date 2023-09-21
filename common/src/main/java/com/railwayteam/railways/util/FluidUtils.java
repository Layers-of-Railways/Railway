package com.railwayteam.railways.util;

import com.simibubi.create.foundation.fluid.FluidHelper;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class FluidUtils {
    public static boolean isFuel(Fluid fluid) {
        return isFuel(FluidHelper.convertToStill(fluid).getBucket());
    }

    /**
     * All isFuel checks for anything else should go through this.
     * @return true if it is a valid fuel item, false if it isn't
     */
    @ExpectPlatform
    public static boolean isFuel(Item item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean portableFuelBlockHasState(BlockState state) {
        throw new AssertionError();
    }
}
