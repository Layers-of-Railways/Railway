package com.railwayteam.railways.mixin;

import com.simibubi.create.content.trains.entity.Train;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Train.class)
public interface AccessorTrain {
    @Accessor(value = "stress", remap = false)
    double[] snr_getStress();

    @Accessor(value = "stress", remap = false)
    void snr_setStress(double[] stress);

    @Mixin(value = Train.Penalties.class, remap = false)
    public interface AccessorPenalties {
        @Accessor
        static int getRED_SIGNAL() {
            throw new RuntimeException("Should be mixed in");
        }

        @Accessor
        static int getREDSTONE_RED_SIGNAL() {
            throw new RuntimeException("Should be mixed in");
        }

        @Accessor
        static int getSTATION_WITH_TRAIN() {
            throw new RuntimeException("Should be mixed in");
        }

        @Accessor
        static int getSTATION() {
            throw new RuntimeException("Should be mixed in");
        }
    }
}
