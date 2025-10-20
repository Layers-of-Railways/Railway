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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.content.custom_tracks.monorail.CustomTrackBlockOutline;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = TrackBlockOutline.class, remap = false)
public abstract class MixinTrackBlockOutline {
    @ModifyExpressionValue(method = "pickCurves",
            at = @At(
                    value = "INVOKE",
                target = "Lnet/createmod/catnip/math/VoxelShaper;get(Lnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
                remap = true
            )
    )
    private static VoxelShape pickWithCorrectShape(VoxelShape original, @Local BezierConnection bc) {
        return CustomTrackBlockOutline.convert(original, bc.getMaterial());
    }

    @Unique
    private static TrackMaterial railways$resultMaterial;

    @ModifyVariable(
            method = "pickCurves",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/track/BezierTrackPointLocation;<init>(Lnet/minecraft/core/BlockPos;I)V",
                    remap = true
            )
    )
    private static BezierConnection railways$grabResultMonorailState(BezierConnection bc) {
        railways$resultMaterial = bc.getMaterial();
        return bc;
    }

    @ModifyArg(
            method = "drawCurveSelection",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/track/TrackBlockOutline;renderShape(Lnet/minecraft/world/phys/shapes/VoxelShape;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/lang/Boolean;)V",
                    remap = true
            )
    )
    private static VoxelShape railways$renderCurvedMonorailShape(VoxelShape shape) {
        return CustomTrackBlockOutline.convert(shape, railways$resultMaterial);
    }

    private static TrackMaterial railways$walkingMaterial;

    @ModifyVariable(
            method = "drawCustomBlockSelection",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    remap = true
            )
    )
    private static BlockState railways$grabMonorailState(BlockState state) {
        railways$walkingMaterial = state.getBlock() instanceof TrackBlock trackBlock ? trackBlock.getMaterial() : TrackMaterial.ANDESITE;
        return state;
    }

    @ModifyArg(
            method = "walkShapes",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"
            )
    )
    private static Object railways$renderMonorailBlockShape(Object o) {
        return CustomTrackBlockOutline.convert(o, railways$walkingMaterial);
    }

    @ModifyExpressionValue(method = "drawCustomBlockSelection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", remap = true))
    private static Block genericCrossingsAreCustom(Block original) {
        return original instanceof GenericCrossingBlock ? AllBlocks.TRACK.get() : original;
    }
}



