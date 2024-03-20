package com.railwayteam.railways.mixin;

import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CarriageContraption.class)
public interface AccessorCarriageContraption {
    @Accessor
    MountedStorageManager getStorageProxy();
}
