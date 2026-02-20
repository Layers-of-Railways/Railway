/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2026 The Railways Team
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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.coupling.TrainUtils;
import com.railwayteam.railways.content.shadow_realm.ShadowRealm;
import com.railwayteam.railways.mixin_interfaces.IHandcarTrain;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainRelocationPacket;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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

    @Inject(method = "relocate", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;collectInitiallyOccupiedSignalBlocks()V", shift = At.Shift.AFTER, remap = false))
    private static void tryToApproachStation(Train train, Level level, BlockPos pos, BezierTrackPointLocation bezier,
                                             boolean bezierDirection, Vec3 lookAngle, boolean simulate,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (!simulate && !level.isClientSide && !((IHandcarTrain) train).railways$isHandcar())
            TrainUtils.tryToParkNearby(train, 1.25);
    }

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
}
