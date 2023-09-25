package com.railwayteam.railways.util;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;

/**
 * Used for when you register blocks on a per-loader
 * basis usually due to doing fluids
 */
public class AbstractionUtils {
    @ExpectPlatform
    public static BlockEntry<?> getFluidTankBlockEntry() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static BlockEntry<?> getPortableFuelInterfaceBlockEntry() {
        throw new AssertionError();
    }
}
