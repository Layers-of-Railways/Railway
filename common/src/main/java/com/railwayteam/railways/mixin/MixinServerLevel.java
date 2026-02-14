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

package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ServerLevel.class)
public class MixinServerLevel {
    @WrapOperation(
        method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;"
        ),
        slice = @Slice(
            from = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=iceandsnow"),
            to = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=tickBlocks")
        )
    )
    private BlockPos lowerHeightForTracks(ServerLevel instance, Heightmap.Types types, BlockPos pos, Operation<BlockPos> original) {
        BlockPos topPos = original.call(instance, types, pos);
        if (instance.getBlockState(topPos).isAir()) {
            BlockPos below = topPos.below();
            if (instance.getBlockState(below).getBlock() instanceof TrackBlock) {
                return below;
            }
        }

        return topPos;
    }

    @WrapOperation(
        method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
        ),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
        )
    )
    private boolean snowInTracks(ServerLevel instance, BlockPos pos, BlockState state, Operation<Boolean> original) {
        if (!(instance.getBlockState(pos).getBlock() instanceof TrackBlock))
            return instance.setBlockAndUpdate(pos, state);

        if (instance.getBlockEntity(pos) instanceof TrackBlockEntity be && instance.random.nextInt(2) == 0) {
            for (BezierConnection bc : be.getConnections().values()) {
                IHasTrackCasing hasCasing = ((IHasTrackCasing) bc);
                if (hasCasing.railways$getTrackCasing() == null) {
                    hasCasing.railways$setTrackCasing(Blocks.SNOW);
                    hasCasing.railways$setAlternate(false);
                    break; // only fill one at a time
                }
            }
        }

        Block existingCasing = IHasTrackCasing.getTrackCasing(instance, pos);
        if (existingCasing == null) {
            IHasTrackCasing.setTrackCasing(instance, pos, Blocks.SNOW);
            IHasTrackCasing.setAlternateModel(instance, pos, true);
            if (instance.getBlockEntity(pos) instanceof TrackBlockEntity be)
                be.notifyUpdate();
            return true;
        } else if (existingCasing == Blocks.SNOW && instance.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT) >= 3) {
            if (IHasTrackCasing.isAlternate(instance, pos) && instance.random.nextInt(4) == 0) {
                IHasTrackCasing.setAlternateModel(instance, pos, false);
                if (instance.getBlockEntity(pos) instanceof TrackBlockEntity be)
                    be.notifyUpdate();
                return true;
            }
        }

        return false;
    }
}
