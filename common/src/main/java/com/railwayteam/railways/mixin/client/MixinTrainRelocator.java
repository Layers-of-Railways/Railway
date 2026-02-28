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

package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.shadow_realm.ShadowRealm;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainRelocationPacket;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(TrainRelocator.class)
public class MixinTrainRelocator {
    @Shadow
    static UUID relocatingTrain;

    @Inject(method = "getRelocating", at = @At("HEAD"), cancellable = true)
    private static void getShadowRelocating(LevelAccessor level, CallbackInfoReturnable<Train> cir) {
        if (ShadowRealm.MARKER.equals(relocatingTrain))
            cir.setReturnValue(ShadowRealm.clientShadowRestoringTrain);
    }

    @Inject(method = "clientTick", at = @At("HEAD"), remap = false)
    private static void clearShadowRestoringTrain(CallbackInfo ci) {
        if (!ShadowRealm.MARKER.equals(relocatingTrain))
            ShadowRealm.clientShadowRestoringTrain = null;
    }

    @WrapOperation(method = "relocateClient", at = @At(value = "NEW", target = "(Ljava/util/UUID;Lnet/minecraft/core/BlockPos;Lcom/simibubi/create/content/trains/track/BezierTrackPointLocation;ZLnet/minecraft/world/phys/Vec3;I)Lcom/simibubi/create/content/trains/entity/TrainRelocationPacket;"))
    private static TrainRelocationPacket relocateShadowTrain(UUID trainId, BlockPos pos, BezierTrackPointLocation hoveredBezier, boolean direction, Vec3 lookAngle, int entityId, Operation<TrainRelocationPacket> original) {
        if (ShadowRealm.MARKER.equals(trainId) && ShadowRealm.clientShadowRestoringTrain != null) {
            trainId = ShadowRealm.clientShadowRestoringTrain.id;
        }
        return original.call(trainId, pos, hoveredBezier, direction, lookAngle, entityId);
    }

    @WrapOperation(method = {"clientTick", "onClicked"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;closerThan(Lnet/minecraft/core/Position;D)Z"))
    private static boolean unrestrictRange(Vec3 instance, Position pos, double distance, Operation<Boolean> original,
                                           @Local(name = "player") LocalPlayer player) {
        if (ShadowRealm.MARKER.equals(relocatingTrain) || (player.isCreative() && CRConfigs.server().unlimitedCreativeRelocation.get()))
            return true;

        return original.call(instance, pos, distance);
    }
}
