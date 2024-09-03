/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
            if (pose == Pose.SLEEPING || pose == Pose.FALL_FLYING || pose == Pose.SPIN_ATTACK || pose == Pose.SWIMMING || pose == Pose.DYING)
                return;
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
