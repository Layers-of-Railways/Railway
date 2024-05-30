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
            for (StorageView<FluidVariant> view : fuelFluids.nonEmptyViews()) {
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
