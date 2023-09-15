package com.railwayteam.railways.util.fabric;

import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FluidUtilsImpl {
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        if (be instanceof FuelTankBlockEntity)
            return ((FuelTankBlockEntity) be).isController();
        return false;
    }
}
