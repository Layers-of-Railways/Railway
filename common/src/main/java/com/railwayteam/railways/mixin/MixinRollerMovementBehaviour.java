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
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.roller_extensions.TrackReplacePaver;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.simibubi.create.content.contraptions.actors.roller.PaveTask;
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(RollerMovementBehaviour.class)
public abstract class MixinRollerMovementBehaviour {
    @Shadow protected abstract BlockState getStateToPaveWith(MovementContext context);

    @Shadow @Nullable protected abstract PaveTask createHeightProfileForTracks(MovementContext context);

    @Unique
    private final List<BlockPos> railways$trackPositions = new ArrayList<>();

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

    @WrapOperation(method = "testBreakerTarget", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/actors/roller/RollerMovementBehaviour;canBreak(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean storeTrackPosition(RollerMovementBehaviour instance, Level world, BlockPos breakingPos, BlockState state, Operation<Boolean> original) {
        if (state.getBlock() instanceof TrackBlock && CRConfigs.server().rollersClearSnow.get())
            railways$trackPositions.add(breakingPos);
        return original.call(instance, world, breakingPos, state);
    }

    @WrapOperation(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/actors/roller/RollerMovementBehaviour;getPositionsToBreak(Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;Lnet/minecraft/core/BlockPos;)Ljava/util/List;"))
    private List<BlockPos> breakSnow(RollerMovementBehaviour instance, MovementContext context, BlockPos visitedPos, Operation<List<BlockPos>> original) {
        railways$trackPositions.clear();
        List<BlockPos> ret = original.call(instance, context, visitedPos);
        if (!railways$trackPositions.isEmpty()) {
            for (BlockPos pos : railways$trackPositions) {
                if (!(context.world.getBlockEntity(pos) instanceof TrackBlockEntity tbe))
                    continue;

                IHasTrackCasing hasCasing = ((IHasTrackCasing) tbe);
                if (hasCasing.railways$getTrackCasing() == Blocks.SNOW)
                    hasCasing.railways$setTrackCasing(null);

                for (BezierConnection bc : tbe.getConnections().values()) {
                    IHasTrackCasing bcCasing = (IHasTrackCasing) bc;
                    if (bcCasing.railways$getTrackCasing() == Blocks.SNOW)
                        bcCasing.railways$setTrackCasing(null);
                }
            }
            railways$trackPositions.clear();
        }
        return ret;
    }

    @WrapOperation(method = "createHeightProfileForTracks", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/actors/roller/PaveTask;put(IIF)V"), remap = false)
    private void setUpsideDown(PaveTask instance, int x, int z, float y, Operation<Void> original, @Local(name = "point") TravellingPoint point) {
        if(point.upsideDown)
            y -= 2;

        original.call(instance, x, z, y);
    }
}
