/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.fuel;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.multiloader.PlatformAbstractionHelper;
import com.railwayteam.railways.util.FluidUtils;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.material.Fluid;

public class LiquidFuelTrainHandler {
    @ExpectPlatform
    public static int handleFuelDraining(CombinedTankWrapper fuelFluids) {
        throw new AssertionError();
    }

    public static int handleFuelChecking(Object o) {
        int burnTime;

        Fluid fluid = FluidUtils.getFluid(o);

        LiquidFuelType fuelType = getType(fluid);

        if (fuelType != null) {
            burnTime = fuelType.getInvalid() ? 0 : fuelType.getFuelTicks();
        } else {
            int bucketBurnTime = PlatformAbstractionHelper.getBurnTime(fluid.getBucket());

            // Divide burnTime by 10 to get burnTime for 1/10th of a bucket and then by divide by 4,
            // so it isn't so strong
            burnTime = (bucketBurnTime / 10) / 4;
        }

        return burnTime;
    }

    public static boolean isFuel(Fluid fluid) {
        // If realistic fuel tanks are enabled, check if the fluid/item is valid fuel
        if (CRConfigs.server().realism.realisticFuelTanks.get()) {

            LiquidFuelType fuelType = getType(fluid);

            if (fuelType != null) {
                return true;
            } else {
                return PlatformAbstractionHelper.getBurnTime(fluid.getBucket()) > 0;
            }
        }

        // else just return true
        return true;
    }

    public static LiquidFuelType getType(Fluid fluid) {
        LiquidFuelType fuelType = LiquidFuelManager.isInTag(fluid);

        if (fuelType == null) {
            fuelType = LiquidFuelManager.getTypeForFluid(fluid);
        }

        return fuelType;
    }
}
