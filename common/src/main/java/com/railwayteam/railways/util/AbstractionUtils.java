package com.railwayteam.railways.util;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Used for when you register blocks on a per-loader
 * basis, usually due to doing fluids
 */
public class AbstractionUtils {
    @ExpectPlatform
    public static BlockEntry<?> getFluidTankBlockEntry() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isInstanceOfFuelTankBlockEntity(BlockEntity blockEntity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static BlockEntry<?> getPortableFuelInterfaceBlockEntry() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean portableFuelInterfaceBlockHasState(BlockState state) {
        throw new AssertionError();
    }
}
