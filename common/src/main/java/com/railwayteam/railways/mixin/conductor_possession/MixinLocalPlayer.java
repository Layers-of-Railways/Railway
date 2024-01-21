package com.railwayteam.railways.mixin.conductor_possession;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {
    @WrapOperation(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isControlledCamera()Z"))
    private boolean railways$isControlledCamera(LocalPlayer instance, Operation<Integer> original) {
        return instance.isLocalPlayer() || ConductorPossessionController.isPossessingConductor(instance);
    }
}
