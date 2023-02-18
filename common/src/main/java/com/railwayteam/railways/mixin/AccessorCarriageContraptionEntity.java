package com.railwayteam.railways.mixin;

import com.simibubi.create.content.logistics.trains.entity.Carriage;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CarriageContraptionEntity.class)
public interface AccessorCarriageContraptionEntity {
    @Accessor(value = "carriage", remap = false)
    Carriage snr_getCarriage();

    @Accessor(value = "carriage", remap = false)
    void snr_setCarriage(Carriage carriage);

    @Invoker(value = "bindCarriage", remap = false)
    void snr_bindCarriage();
}
