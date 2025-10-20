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

package com.railwayteam.railways.mixin.client;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationRenderer;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StationRenderer.class)
public class MixinStationRenderer {
    @Inject(method = "renderSafe(Lcom/simibubi/create/content/trains/station/StationBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/render/CachedBuffers;partial(Ldev/engine_room/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/createmod/catnip/render/SuperByteBuffer;"))
    private void shiftAssemblyOverlayOnEncasedTracks(
        StationBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay,
        CallbackInfo ci, @Local(name = "currentPos") MutableBlockPos currentPos, @Local(name = "trackState") BlockState trackState) {
        ms.pushPose();

        if (trackState.getBlock() instanceof TrackBlock trackBlock
            && be.getLevel().getBlockEntity(currentPos) instanceof IHasTrackCasing casing && casing.getTrackCasing() != null) {
            TrackShape shape = trackState.getValue(TrackBlock.SHAPE);
            CRBlockPartials.TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
            TrackMaterial.TrackType trackType = trackBlock.getMaterial().trackType;
            if (spec != null)
                TransformStack.of(ms)
                    .translate(
                        spec.getXShift(trackType),
                        (spec.getTopSurfacePixelHeight(trackType, casing.isAlternate()) - 2) / 16f,
                        spec.getZShift(trackType)
                    );
        }

    }

    @Inject(method = "renderSafe(Lcom/simibubi/create/content/trains/station/StationBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V", shift = At.Shift.AFTER))
    private void cleanup(StationBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        ms.popPose();
    }
}
