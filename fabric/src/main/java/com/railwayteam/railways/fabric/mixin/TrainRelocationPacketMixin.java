/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

package com.railwayteam.railways.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.shadow_realm.ShadowRealm;
import com.railwayteam.railways.content.shadow_realm.ShadowRealm.RestorationTarget;
import com.simibubi.create.content.trains.entity.TrainRelocationPacket;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.foundation.networking.SimplePacketBase.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

// earlier priority to bypass OPAC protections, which freak out about there being no entity associated with the relocation
@Mixin(value = TrainRelocationPacket.class, priority = 500)
public class TrainRelocationPacketMixin {
    @Shadow
    UUID trainId;

    @Shadow
    BlockPos pos;

    @Shadow private BezierTrackPointLocation hoveredBezier;

    @Shadow private boolean direction;

    @Shadow
    Vec3 lookAngle;

    @WrapOperation(method = "lambda$handle$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;closerThan(Lnet/minecraft/core/Position;D)Z", ordinal = 1))
    private boolean unrestrictRange(Vec3 instance, Position pos, double distance, Operation<Boolean> original,
                                    @Local(name = "sender") ServerPlayer sender) {
        if (sender.isCreative() && CRConfigs.server().unlimitedCreativeRelocation.get())
            return true;

        return original.call(instance, pos, distance);
    }

    @Inject(method = "lambda$handle$2", at = @At("HEAD"), cancellable = true, remap = false)
    private void relocateShadowTrain(Context context, CallbackInfo ci) {
        ServerPlayer sender = context.getSender();
        if (sender == null) {
            ShadowRealm.LOGGER.warn("Received TrainRelocationPacket without sender, ignoring");
            return;
        }

        RestorationTarget target = new RestorationTarget(sender.level(), pos, hoveredBezier, direction, lookAngle);
        ShadowRealm.handleTrainRelocationPacket(sender, trainId, target, ci);
    }
}
