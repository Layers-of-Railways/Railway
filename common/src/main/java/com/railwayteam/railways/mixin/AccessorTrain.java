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

package com.railwayteam.railways.mixin;

import com.simibubi.create.content.trains.entity.Train;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Train.class)
public interface AccessorTrain {
    @Accessor(value = "stress", remap = false)
    double[] railways$getStress();

    @Accessor(value = "stress", remap = false)
    void railways$setStress(double[] stress);

    @Mixin(value = Train.Penalties.class, remap = false)
    interface AccessorPenalties {
        @Accessor("RED_SIGNAL")
        static int railways$getRedSignal() {
            throw new AssertionError();
        }

        @Accessor("REDSTONE_RED_SIGNAL")
        static int railways$getRedstoneRedSignal() {
            throw new AssertionError();
        }

        @Accessor("STATION_WITH_TRAIN")
        static int railways$getStationWithTrain() {
            throw new AssertionError();
        }

        @Accessor("STATION")
        static int railways$getStation() {
            throw new AssertionError();
        }
    }
}
