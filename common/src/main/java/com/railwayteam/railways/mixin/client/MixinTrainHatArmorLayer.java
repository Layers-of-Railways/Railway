package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.registry.CREntities;
import com.simibubi.create.content.trains.schedule.TrainHatArmorLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrainHatArmorLayer.class)
public class MixinTrainHatArmorLayer {
    @Inject(method = "shouldRenderOn", at = @At("HEAD"), cancellable = true)
    private void railways$shouldRenderOn(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        // If its conductor and the config says to not render the double cap then dont render it
        if (entity != null && CREntities.CONDUCTOR.is(entity) && !CRConfigs.client().renderNormalCap.get())
            cir.setReturnValue(false);
    }
}
