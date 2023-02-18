package com.railwayteam.railways.mixin;

import com.simibubi.create.content.logistics.trains.entity.Train;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Train.class)
public interface AccessorTrain {
    @Accessor(value = "stress", remap = false)
    double[] snr_getStress();

    @Accessor(value = "stress", remap = false)
    void snr_setStress(double[] stress);
}
