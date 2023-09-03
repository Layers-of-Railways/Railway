package com.railwayteam.railways.content.fuel_tank;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FuelTankBlock extends FluidTankBlock {
    public static FuelTankBlock regular(Properties properties) {
        return new FuelTankBlock(properties, false);
    }

    protected FuelTankBlock(Properties properties, boolean creative) {
        super(setLightFunction(properties), creative);
        registerDefaultState(defaultBlockState().setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(SHAPE, Shape.WINDOW)
                .setValue(LIGHT_LEVEL, 0));
    }

    private static Properties setLightFunction(Properties properties) {
        return properties.lightLevel(state -> state.getValue(LIGHT_LEVEL));
    }
}
