/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlockEntity;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.TrackShapeLookup;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.TrackShapeLookup.GenericCrossingData;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackPlacement;
import com.simibubi.create.content.trains.track.TrackShape;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.simibubi.create.content.trains.track.TrackBlock.SHAPE;

@Mixin(value = TrackPlacement.class)
public class MixinTrackPlacement {
    // minimum curve length for gauges
    @ModifyExpressionValue(method = "tryConnect", at = {
        @At(value = "CONSTANT", args = "doubleValue=7"),
        @At(value = "CONSTANT", args = "doubleValue=3.25")
    })
    private static double modifiedCurvesForGauges(double value, Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack) {
        // Wide Gauge
        if (TrackMaterial.fromItem(stack.getItem()).trackType == CRTrackMaterials.CRTrackType.WIDE_GAUGE)
            return value * 2;
        // Narrow Gauge and Phantom Tracks
        else if (TrackMaterial.fromItem(stack.getItem()).trackType == CRTrackMaterials.CRTrackType.NARROW_GAUGE ||
                TrackMaterial.fromItem(stack.getItem()).trackType == CRTrackMaterials.CRTrackType.UNIVERSAL)
            return value * 0.5;

        return value;
    }

    @Inject(method = "placeTracks", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/ITrackBlock;overlay(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private static void placeGenericCrossing0(Level level, TrackPlacement.PlacementInfo info, BlockState state1,
                                             BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate,
                                             CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir,
                                             @Share("oldToPlace") LocalRef<BlockState> oldToPlace, @Local(name="toPlace") BlockState toPlace) {
        oldToPlace.set(toPlace);
    }

    @Inject(method = "placeTracks", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/simibubi/create/content/trains/track/ITrackBlock;overlay(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;", shift = At.Shift.AFTER))
    private static void placeGenericCrossing(Level level, TrackPlacement.PlacementInfo info, BlockState state1,
                                             BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate,
                                             CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir,
                                             @Local(name="stateAtPos") BlockState stateAtPos,
                                             @Local(name="toPlace") LocalRef<BlockState> toPlace,
                                             @Share("oldToPlace") LocalRef<BlockState> oldToPlace,
                                             @Share("crossingData") LocalRef<@Nullable GenericCrossingData> crossingData) {
        crossingData.set(null);
        BlockState place = oldToPlace.get();
        if (stateAtPos == toPlace.get()) {
            if (stateAtPos.hasProperty(SHAPE) && place.hasProperty(SHAPE)
                && stateAtPos.getBlock() instanceof ITrackBlock exisingTrack
                && place.getBlock() instanceof ITrackBlock overlayTrack) {
                TrackShape existingShape = stateAtPos.getValue(SHAPE);
                TrackShape overlayShape = place.getValue(SHAPE);

                Pair<TrackShape, Boolean> merged = TrackShapeLookup.getMerged(existingShape, overlayShape);
                if (merged != null) {
                    crossingData.set(new GenericCrossingData(merged, exisingTrack.getMaterial(), overlayTrack.getMaterial()));
                    toPlace.set(CRBlocks.GENERIC_CROSSING.getDefaultState().setValue(GenericCrossingBlock.SHAPE, merged.getFirst()));
                }
            }
        }
    }
    
    @Inject(method = "placeTracks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift = At.Shift.AFTER))
    private static void maybePlaceCrossing(Level level, TrackPlacement.PlacementInfo info, BlockState state1,
                                           BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate,
                                           CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir,
                                           @Local(name = "offsetPos") BlockPos offsetPos,
                                           @Share("crossingData") LocalRef<@Nullable GenericCrossingData> crossingDataRef) {
        @Nullable GenericCrossingData crossingData = crossingDataRef.get();
        if (crossingData != null && level.getBlockEntity(offsetPos) instanceof GenericCrossingBlockEntity genericCrossingBE) {
            genericCrossingBE.initFrom(crossingData);
        }
        crossingDataRef.set(null);
    }
}
