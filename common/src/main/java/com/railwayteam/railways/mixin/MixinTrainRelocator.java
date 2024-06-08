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

import com.railwayteam.railways.content.coupling.TrainUtils;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrainRelocator.class)
public class MixinTrainRelocator {
    @Inject(method = "relocate", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;collectInitiallyOccupiedSignalBlocks()V", shift = At.Shift.AFTER))
    private static void tryToApproachStation(Train train, Level level, BlockPos pos, BezierTrackPointLocation bezier,
                                             boolean bezierDirection, Vec3 lookAngle, boolean simulate,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (!simulate && !level.isClientSide)
            TrainUtils.tryToParkNearby(train, 1.25);
    }
}
