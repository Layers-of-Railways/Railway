package com.railwayteam.railways.content.fuel.fabric;

import com.railwayteam.railways.content.fuel.LiquidFuelTrainHandler;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class LiquidFuelTrainHandlerImpl {
    public static int handleFuelDraining(CombinedTankWrapper fuelFluids) {
        try (Transaction t = TransferUtil.getTransaction()) {
            for (StorageView<FluidVariant> view : TransferUtil.getNonEmpty(fuelFluids)) {
                FluidVariant held = view.getResource();

                // Extract 100 Mb worth of fluid (1/10th of a bucket)
                if (view.extract(held, 8100, t) != 8100)
                    continue;

                int burnTime = LiquidFuelTrainHandler.handleFuelChecking(held);

                if (burnTime <= 0)
                    continue;

                t.commit();
                return burnTime;
            }
        }

        return 0;
    }
}
