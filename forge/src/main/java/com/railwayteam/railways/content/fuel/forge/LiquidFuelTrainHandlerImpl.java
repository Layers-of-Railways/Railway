/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
