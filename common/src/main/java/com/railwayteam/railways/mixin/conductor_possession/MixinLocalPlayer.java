package com.railwayteam.railways.mixin.conductor_possession;

import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {
    @Redirect(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isControlledCamera()Z"))
    private boolean snr$isControlledCamera(LocalPlayer instance) {
        return instance.isLocalPlayer() || ConductorPossessionController.isPossessingConductor(instance);
    }
}
