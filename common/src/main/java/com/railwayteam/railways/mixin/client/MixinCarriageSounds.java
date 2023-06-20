package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CarriageSounds.class, remap = false)
public class MixinCarriageSounds {
    private boolean skip;
    @Inject(method = "<init>", at = @At("RETURN"))
    private void skipIfInvisible(CarriageContraptionEntity entity, CallbackInfo ci) {
        skip = entity.getCarriage().bogeys.both((b) -> b == null || b.getStyle() == CRBogeyStyles.INVISIBLE);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void actuallySkip(Carriage.DimensionalCarriageEntity dce, CallbackInfo ci) {
        if (skip)
            ci.cancel();;
    }
}
