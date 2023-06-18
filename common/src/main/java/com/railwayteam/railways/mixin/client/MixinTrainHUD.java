package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.switches.TrainHUDSwitchExtension;
import com.simibubi.create.content.trains.TrainHUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrainHUD.class, remap = false)
public class MixinTrainHUD {
    @Inject(method = "tick", at = @At("HEAD"))
    private static void tickHook(CallbackInfo ci) {
        TrainHUDSwitchExtension.tick();
    }
}
