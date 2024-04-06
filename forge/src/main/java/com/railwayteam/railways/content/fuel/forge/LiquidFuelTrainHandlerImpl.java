package com.railwayteam.railways.content.fuel.forge;

import com.railwayteam.railways.content.fuel.LiquidFuelTrainHandler;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class LiquidFuelTrainHandlerImpl {
    public static int handleFuelDraining(CombinedTankWrapper fuelFluids) {
        for (int tanks = 0; tanks < fuelFluids.getTanks(); tanks++) {
            FluidStack fluidStack = fuelFluids.drain(100, IFluidHandler.FluidAction.SIMULATE);

            if (fluidStack.getAmount() != 100)
                continue;

            int burnTime = LiquidFuelTrainHandler.handleFuelChecking(fluidStack);

            if (burnTime <= 0)
                continue;

            // Extract 100 Mb worth of fluid (1/10th of a bucket)
            fuelFluids.drain(100, IFluidHandler.FluidAction.EXECUTE);

            return burnTime;
        }

        return 0;
    }
}
