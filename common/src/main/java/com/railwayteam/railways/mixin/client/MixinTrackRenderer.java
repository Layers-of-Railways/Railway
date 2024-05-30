/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier.MonorailAngles;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.util.client.ClientTextUtils;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils.reTexture;
import static com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils.renderBezierCasings;
import static com.railwayteam.railways.registry.CRBlockPartials.*;

@Mixin(value = TrackRenderer.class, remap = false)
public class MixinTrackRenderer {
    @Inject(method = "renderSafe(Lcom/simibubi/create/content/trains/track/TrackBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"), remap = true)
    private void renderCasing(TrackBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        SlabBlock casingBlock = ((IHasTrackCasing) te).getTrackCasing();
        if (casingBlock != null) {
            TrackShape shape = te.getBlockState().getValue(TrackBlock.SHAPE);
            if (CRBlockPartials.TRACK_CASINGS.containsKey(shape)) {
                ms.pushPose();
                if (te.isTilted()) {
                    double angle = te.tilt.smoothingAngle.get();
                    switch (te.getBlockState().getValue(TrackBlock.SHAPE)) {
                        case ZO -> TransformStack.cast(ms)
                            .rotateX(-angle);
                        case XO -> TransformStack.cast(ms)
                            .rotateZ(angle);
                    }
                }

                TrackMaterial.TrackType trackType = null;
                if (te.getBlockState().getBlock() instanceof TrackBlock trackBlock)
                    trackType = trackBlock.getMaterial().trackType;

                CRBlockPartials.TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
                if (((IHasTrackCasing) te).isAlternate())
                    spec = spec.getNonNullAltSpec(trackType);
                else
                    spec = spec.getFor(trackType);
                CRBlockPartials.ModelTransform transform = spec.transform;

                PartialModel texturedPartial = reTexture(spec.model, casingBlock);

                CachedBufferer.partial(reTexture(spec.model, casingBlock), casingBlock.defaultBlockState())
                    .rotateX(transform.rx()).rotateY(transform.ry()).rotateZ(transform.rz())
                    .translate(transform.x(), transform.y(), transform.z())
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

                for (CRBlockPartials.ModelTransform additionalTransform : spec.additionalTransforms) {
                    CachedBufferer.partial(texturedPartial, casingBlock.defaultBlockState())
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
        SlabBlock casingBlock = ((IHasTrackCasing) bc).getTrackCasing();
        if (casingBlock != null) {
            renderBezierCasings(ms, level, reTexture(CRBlockPartials.TRACK_CASING_FLAT_THICK, casingBlock), casingBlock.defaultBlockState(), vb, bc);
        }
    }

    @Inject(method = "renderBezierTurn",
        at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/TrackRenderer;renderGirder(Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/trains/track/BezierConnection;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/core/BlockPos;)V", shift = At.Shift.AFTER, remap = true),
        cancellable = true)
    private static void renderMonorailMaybe(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, CallbackInfo ci) {
        if (bc.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
            railways$renderActualMonorail(level, bc, ms, vb, bc.tePositions.getFirst());
            ms.popPose(); // clean up pose, since cancelled
            ci.cancel(); // Don't do normal rendering
        }
    }

    @Unique
    private static void railways$renderActualMonorail(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb,
                                                 BlockPos tePosition) {

        BlockState air = Blocks.AIR.defaultBlockState();
        MonorailAngles[] monorails = ((IMonorailBezier) bc).getBakedMonorails();

        for (int i = 1; i < monorails.length; i++) {
            MonorailAngles segment = monorails[i];
            int light = LevelRenderer.getLightColor(level, segment.lightPosition.offset(tePosition));

            PoseStack.Pose beamTransform = segment.beam;
            CachedBufferer.partial(MONORAIL_SEGMENT_MIDDLE, air)
                .mulPose(beamTransform.pose())
                .mulNormal(beamTransform.normal())
                .light(light)
                .renderInto(ms, vb);

            for (boolean top : Iterate.trueAndFalse) {
                PoseStack.Pose beamCapTransform = segment.beamCaps.get(top);
                CachedBufferer.partial(top ? MONORAIL_SEGMENT_TOP : MONORAIL_SEGMENT_BOTTOM, air)
                    .mulPose(beamCapTransform.pose())
                    .mulNormal(beamCapTransform.normal())
                    .light(light)
                    .renderInto(ms, vb);
            }
        }
    }
}
