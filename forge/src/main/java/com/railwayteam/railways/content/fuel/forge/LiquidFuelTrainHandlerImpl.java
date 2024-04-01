package com.railwayteam.railways.content.fuel.forge;

import com.railwayteam.railways.content.fuel.LiquidFuelManager;
import com.railwayteam.railways.content.fuel.LiquidFuelType;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class LiquidFuelTrainHandlerImpl {
    public static int handleFuelDraining(CombinedTankWrapper fuelFluids) {
        for (int tanks = 0; tanks < fuelFluids.getTanks(); tanks++) {
            FluidStack fluidStack = fuelFluids.drain(100, IFluidHandler.FluidAction.SIMULATE);

            int burnTime;
            Fluid fluid = fluidStack.getFluid();

            LiquidFuelType fuelType = LiquidFuelManager.isInTag(fluid);

            if (fuelType == null) {
                fuelType = LiquidFuelManager.getTypeForFluid(fluid);
            }

            if (fuelType != null) {
                burnTime = fuelType.getFuelTicks();
            } else {
                int bucketBurnTime = ForgeHooks.getBurnTime(fluid.getBucket().getDefaultInstance(), null);

                // Divide burnTime by 100 to get burnTime for 1/10th of a bucket and then by divide by 4,
                // so it isn't so strong
                burnTime = (bucketBurnTime / 100) / 4;
            }

            if (burnTime <= 0)
                continue;

            // Extract 100 Mb worth of fluid (1/10th of a bucket)
            fuelFluids.drain(100, IFluidHandler.FluidAction.EXECUTE);

            return burnTime;
        }

        return 0;
    }
}
