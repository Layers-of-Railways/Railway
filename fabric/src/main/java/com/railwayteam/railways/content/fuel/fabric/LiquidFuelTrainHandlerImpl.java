package com.railwayteam.railways.content.fuel.fabric;

import com.railwayteam.railways.content.fuel.LiquidFuelManager;
import com.railwayteam.railways.content.fuel.LiquidFuelType;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.level.material.Fluid;

public class LiquidFuelTrainHandlerImpl {
    public static int handleFuelDraining(CombinedTankWrapper fuelFluids) {
        try (Transaction t = TransferUtil.getTransaction()) {
            for (StorageView<FluidVariant> view : fuelFluids.nonEmptyViews()) {
                FluidVariant held = view.getResource();

                int burnTime;
                Fluid fluid = held.getFluid();
                LiquidFuelType fuelType = LiquidFuelManager.getTypeForFluid(fluid);
                if (fuelType != null) {
                    burnTime = fuelType.getFuelTicks();
                } else {
                    int bucketBurnTime = FuelRegistry.INSTANCE.get(fluid.getBucket());

                    // Divide burnTime by 100 to get burnTime for 1/10th of a bucket and then by divide by 4,
                    // so it isn't so strong
                    burnTime = (bucketBurnTime / 100) / 4;
                }

                if (burnTime <= 0)
                    continue;

                // Extract 100 Mb worth of fluid (1/10th of a bucket)
                if (view.extract(held, 8100, t) != 8100)
                    continue;

                t.commit();
                return burnTime;
            }
        }

        return 0;
    }
}
