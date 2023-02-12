package com.railwayteam.railways.mixin;

import com.simibubi.create.content.logistics.trains.entity.Train;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Train.class)
public interface AccessorTrain {
    @Accessor("stress")
    double[] snr_getStress();

    @Accessor("stress")
    void snr_setStress(double[] stress);
}
