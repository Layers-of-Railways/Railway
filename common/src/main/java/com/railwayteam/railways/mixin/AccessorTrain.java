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
