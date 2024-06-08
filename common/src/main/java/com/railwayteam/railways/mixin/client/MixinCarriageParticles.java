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

package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarriageParticles.class)
public class MixinCarriageParticles {
    @Unique private boolean railways$isHandcar;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void railways$checkIfHandcar(CarriageContraptionEntity entity, CallbackInfo ci) {
        railways$isHandcar = entity.getCarriage().bogeys.both(b -> b == null || b.getStyle() == CRBogeyStyles.HANDCAR);
    }

    @WrapWithCondition(method = "tick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
    ))
    private boolean railways$skipParticlesForHandcar(Level instance, ParticleOptions particleData, double x, double y, double z,
                                                double xSpeed, double ySpeed, double zSpeed, @Local(ordinal = 1) boolean spark) {
        return spark || !railways$isHandcar;
    }
}
