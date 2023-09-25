package com.railwayteam.railways.util.forge;

import com.railwayteam.railways.registry.forge.CRBlocksImpl;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AbstractionUtilsImpl {
    public static BlockEntry<?> getFluidTankBlockEntry() {
        return CRBlocksImpl.FUEL_TANK;
    }

    public static BlockEntry<?> getPortableFuelInterfaceBlockEntry() {
        return CRBlocksImpl.PORTABLE_FUEL_INTERFACE;
    }
}
