package com.railwayteam.railways.mixin.conductor_possession;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
    @SuppressWarnings({"ConstantValue"})
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void snr$turn(double yaw, double pitch, CallbackInfo ci) {
        ConductorEntity conductor;
        if ((Object) this instanceof Player this$player &&
                (conductor = ConductorPossessionController.getPossessingConductor(this$player)) != null) {
            ci.cancel();
            conductor.turnView(yaw, pitch);
        }
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void snr$stopPushing(Entity entity, CallbackInfo ci) {
        if (ConductorPossessionController.getPossessingConductor((Entity) (Object) this) == entity)
            ci.cancel();
    }
}
