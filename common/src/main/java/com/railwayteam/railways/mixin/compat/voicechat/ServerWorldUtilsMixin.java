package com.railwayteam.railways.mixin.compat.voicechat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import de.maxhenkel.voicechat.voice.server.ServerWorldUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerWorldUtils.class)
public class ServerWorldUtilsMixin {
    @SuppressWarnings("unused")
    @WrapOperation(method = "getPlayersInRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;position()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 useConductorSpyPosition(ServerPlayer instance, Operation<Vec3> original) {
        if (ConductorPossessionController.isPossessingConductor(instance)) {
            return instance.getCamera().position();
        } else {
            return original.call(instance);
        }
    }
}
