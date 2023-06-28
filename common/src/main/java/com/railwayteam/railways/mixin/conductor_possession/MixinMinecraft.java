package com.railwayteam.railways.mixin.conductor_possession;

import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void snr$handleStart(CallbackInfo ci) {
        ConductorPossessionController.onHandleKeybinds((Minecraft) (Object) this, true);
    }

    @Inject(method = "handleKeybinds", at = @At("RETURN"))
    private void snr$handleEnd(CallbackInfo ci) {
        ConductorPossessionController.onHandleKeybinds((Minecraft) (Object) this, false);
    }
}
