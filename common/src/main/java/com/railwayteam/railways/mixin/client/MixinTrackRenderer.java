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

package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.util.client.ClientTextUtils;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackRenderer;
import com.simibubi.create.content.trains.track.TrackShape;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils.reTexture;
import static com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils.renderBezierCasings;

@Mixin(value = TrackRenderer.class, remap = false)
public class MixinTrackRenderer {
    @Inject(method = "renderSafe(Lcom/simibubi/create/content/trains/track/TrackBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"), remap = true)
    private void renderCasing(TrackBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        Block casingBlock = ((IHasTrackCasing) te).railways$getTrackCasing();
        if (casingBlock != null) {
            TrackShape shape = te.getBlockState().getValue(TrackBlock.SHAPE);
            if (CRBlockPartials.TRACK_CASINGS.containsKey(shape)) {
                ms.pushPose();
                if (te.isTilted()) {
                    double angle = te.tilt.smoothingAngle.get();
                    switch (te.getBlockState().getValue(TrackBlock.SHAPE)) {
                        case ZO -> TransformStack.of(ms)
                            .rotateXDegrees((float) -angle);
                        case XO -> TransformStack.of(ms)
                            .rotateZDegrees((float) angle);
                    }
                }

                TrackMaterial.TrackType trackType = null;
                if (te.getBlockState().getBlock() instanceof TrackBlock trackBlock)
                    trackType = trackBlock.getMaterial().trackType;

                CRBlockPartials.TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
                if (((IHasTrackCasing) te).railways$isAlternate())
                    spec = spec.getNonNullAltSpec(trackType);
                else
                    spec = spec.getFor(trackType);
                CRBlockPartials.ModelTransform transform = spec.transform;

                PartialModel texturedPartial = reTexture(spec.model, casingBlock);

                CachedBuffers.partial(reTexture(spec.model, casingBlock), casingBlock.defaultBlockState())
                    .rotateX(transform.rx()).rotateY(transform.ry()).rotateZ(transform.rz())
                    .translate(transform.x(), transform.y(), transform.z())
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

                for (CRBlockPartials.ModelTransform additionalTransform : spec.additionalTransforms) {
                    CachedBuffers.partial(texturedPartial, casingBlock.defaultBlockState())
                        .rotateX(additionalTransform.rx()).rotateY(additionalTransform.ry()).rotateZ(additionalTransform.rz())
                        .translate(additionalTransform.x(), additionalTransform.y(), additionalTransform.z())
                        .light(light)
                        .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
                }
                ms.popPose();
            } else {
                ClientTextUtils.renderDebugText(ms, buffer, light, 1, true, "No casing for shape " + shape);
            }
        }
    }

    @Inject(method = "renderBezierTurn", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", remap = true), remap = false)
    private static void renderCurveCasings(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, CallbackInfo ci) {
        Block casingBlock = ((IHasTrackCasing) bc).railways$getTrackCasing();
        if (casingBlock != null) {
            renderBezierCasings(ms, level, reTexture(CRBlockPartials.TRACK_CASING_FLAT_THICK, casingBlock), casingBlock.defaultBlockState(), vb, bc);
        }
    }
}
