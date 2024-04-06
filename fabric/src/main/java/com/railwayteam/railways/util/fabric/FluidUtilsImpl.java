package com.railwayteam.railways.util.fabric;

import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

public class FluidUtilsImpl {
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        if (be instanceof FuelTankBlockEntity fuelTankBlockEntity)
            return fuelTankBlockEntity.isController();
        return false;
    }

    public static Fluid getFluid(Object o) {
        Fluid fluid;

        if (o instanceof FluidVariant fluidVariant) {
            fluid = fluidVariant.getFluid();
        } else if (o instanceof FluidStack fluidStack) {
            fluid = fluidStack.getFluid();
        } else {
            throw new IllegalArgumentException("FluidUtils#getFluid expected to get a FluidVariant or FluidStack but got " + o.getClass().getName());
        }

        return fluid;
    }
}
