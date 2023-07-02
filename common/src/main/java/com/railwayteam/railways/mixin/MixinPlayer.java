package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
    private MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getStandingEyeHeight", at = @At("RETURN"), cancellable = true)
    private void conductorsAreSmaller(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        if (ConductorEntity.isPlayerDisguised((Player) (Object) this)) {
            if (pose == Pose.SLEEPING || pose == Pose.FALL_FLYING || pose == Pose.SPIN_ATTACK || pose == Pose.SWIMMING || pose == Pose.DYING)
                return;
            // conductor eye height is 1.5 * 0.76
            // player eye height is 1.62
            cir.setReturnValue(cir.getReturnValueF() * (1.5f * 0.76f / 1.62f));
        }
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void shrinkConductorPlayer(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (ConductorEntity.isPlayerDisguised((Player) (Object) this)) {
            if (pose == Pose.SLEEPING || pose == Pose.FALL_FLYING || pose == Pose.SPIN_ATTACK || pose == Pose.SWIMMING || pose == Pose.DYING) return;
            EntityDimensions dimensions = cir.getReturnValue();
            cir.setReturnValue(dimensions.scale(1.0f, 1.5f / 1.8f));
        }
    }

    private boolean wasDisguised = false;
    @Inject(method = "tick", at = @At("HEAD"))
    private void updateDimensions(CallbackInfo ci) {
        boolean isDisguised = ConductorEntity.isPlayerDisguised((Player) (Object) this);
        if (isDisguised != wasDisguised) {
            wasDisguised = isDisguised;
            this.refreshDimensions();
        }
    }
}
