package com.railwayteam.railways.util.forge;

import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidUtilsImpl {
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        if (be instanceof FuelTankBlockEntity fuelTankBlockEntity)
            return fuelTankBlockEntity.isController();
        return false;
    }

    public static Fluid getFluid(Object o) {
        if (!(o instanceof FluidStack fluidStack))
            throw new IllegalArgumentException("FluidUtils#getFluid expected to get a FluidStack but got " + o.getClass().getName());

        return fluidStack.getFluid();
    }
}
