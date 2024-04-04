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

            // Divide burnTime by 100 to get burnTime for 1/10th of a bucket and then by divide by 4,
            // so it isn't so strong
            burnTime = (bucketBurnTime / 100) / 4;
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
