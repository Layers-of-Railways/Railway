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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.content.roller_extensions.TrackReplacePaver;
import com.simibubi.create.content.contraptions.actors.roller.PaveTask;
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.track.ITrackBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = RollerMovementBehaviour.class, remap = false)
public abstract class MixinRollerMovementBehaviour {
    @Shadow protected abstract BlockState getStateToPaveWith(MovementContext context);

    @Shadow @Nullable protected abstract PaveTask createHeightProfileForTracks(MovementContext context);

    @Inject(method = "triggerPaver", at = @At("HEAD"), cancellable = true)
    private void skipTracksAndPaveTracks(MovementContext context, BlockPos pos, CallbackInfo ci) {
        BlockState stateToPaveWith = getStateToPaveWith(context);
        int mode = context.blockEntityData.getInt("ScrollValue");
        if (mode == 3) { // TRACK_REPLACE
            ci.cancel();
            TrackReplacePaver.pave(context, pos, stateToPaveWith, createHeightProfileForTracks(context));
        } else if (stateToPaveWith.getBlock() instanceof ITrackBlock) {
            ci.cancel();
        }
    }

    @WrapOperation(method = "createHeightProfileForTracks", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/actors/roller/PaveTask;put(IIF)V"))
    private void setUpsideDown(PaveTask instance, int x, int z, float y, Operation<Void> original, @Local(name = "point") TravellingPoint point) {
        if(point.upsideDown)
            y -= 2;

        original.call(instance, x, z, y);
    }
}
