package com.railwayteam.railways.mixin;

import com.simibubi.create.content.logistics.trains.entity.Carriage;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CarriageContraptionEntity.class)
public interface AccessorCarriageContraptionEntity {
    @Accessor("carriage")
    Carriage snr_getCarriage();

    @Accessor("carriage")
    void snr_setCarriage(Carriage carriage);

    @Invoker("bindCarriage")
    void snr_bindCarriage();
}
