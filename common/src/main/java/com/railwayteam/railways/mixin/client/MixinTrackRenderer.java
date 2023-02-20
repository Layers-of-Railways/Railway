package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier;
import com.railwayteam.railways.mixin_interfaces.IMonorailBezier.MonorailAngles;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackRenderer;
import com.simibubi.create.content.logistics.trains.track.TrackShape;
import com.simibubi.create.content.logistics.trains.track.TrackTileEntity;
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
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils.reTexture;
import static com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils.renderBezierCasings;
import static com.simibubi.create.AllBlockPartials.*;
import static com.railwayteam.railways.registry.CRBlockPartials.MONORAIL_SEGMENT_TOP;
import static com.railwayteam.railways.registry.CRBlockPartials.MONORAIL_SEGMENT_BOTTOM;
import static com.railwayteam.railways.registry.CRBlockPartials.MONORAIL_SEGMENT_MIDDLE;

@Mixin(value = TrackRenderer.class, remap = false)
public class MixinTrackRenderer {
    @Nullable
    private static BezierConnection bezierConnection = null;

    @Inject(method = "renderBezierTurn", at = @At("HEAD"))
    private static void storeBezierConnection(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, CallbackInfo ci) {
        bezierConnection = bc;
    }

    @Inject(method = "renderBezierTurn", at = @At("RETURN"))
    private static void clearBezierConnection(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, CallbackInfo ci) {
        bezierConnection = null;
    }

    @Redirect(method = "renderBezierTurn", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
        target = "Lcom/simibubi/create/AllBlockPartials;TRACK_TIE:Lcom/jozufozu/flywheel/core/PartialModel;"))
    private static PartialModel replaceTie() {
        if (bezierConnection != null) {
            TrackMaterial material = ((IHasTrackMaterial) bezierConnection).getMaterial();
            if (material.isCustom()) {
                return CRBlockPartials.TRACK_PARTS.get(material).tie;
            }
        }
        return TRACK_TIE;
    }

    @Redirect(method = "renderBezierTurn", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
        target = "Lcom/simibubi/create/AllBlockPartials;TRACK_SEGMENT_LEFT:Lcom/jozufozu/flywheel/core/PartialModel;"))
    private static PartialModel replaceSegLeft() {
        if (bezierConnection != null) {
            TrackMaterial material = ((IHasTrackMaterial) bezierConnection).getMaterial();
            if (material.isCustom()) {
                return CRBlockPartials.TRACK_PARTS.get(material).segment_left;
            }
        }
        return TRACK_SEGMENT_LEFT;
    }

    @Redirect(method = "renderBezierTurn", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
        target = "Lcom/simibubi/create/AllBlockPartials;TRACK_SEGMENT_RIGHT:Lcom/jozufozu/flywheel/core/PartialModel;"))
    private static PartialModel replaceSegRight() {
        if (bezierConnection != null) {
            TrackMaterial material = ((IHasTrackMaterial) bezierConnection).getMaterial();
            if (material.isCustom()) {
                return CRBlockPartials.TRACK_PARTS.get(material).segment_right;
            }
        }
        return TRACK_SEGMENT_RIGHT;
    }

    @Inject(method = "renderSafe(Lcom/simibubi/create/content/logistics/trains/track/TrackTileEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"), remap = true)
    private void renderCasing(TrackTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        SlabBlock casingBlock = ((IHasTrackCasing) te).getTrackCasing();
        if (casingBlock != null) {
            TrackShape shape = te.getBlockState().getValue(TrackBlock.SHAPE);
            if (CRBlockPartials.TRACK_CASINGS.containsKey(shape)) {
                ms.pushPose();
                CRBlockPartials.TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
                if (((IHasTrackCasing) te).isAlternate())
                    spec = spec.getAltSpec();
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
                TextUtils.renderDebugText(ms, buffer, light, 1, true, "No casing for shape " + shape);
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
        at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/track/TrackRenderer;renderGirder(Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/logistics/trains/BezierConnection;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/core/BlockPos;)V", shift = At.Shift.AFTER, remap = true),
        cancellable = true)
    private static void renderMonorailMaybe(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, CallbackInfo ci) {
        if (((IHasTrackMaterial) bc).getMaterial().trackType == TrackMaterial.TrackType.MONORAIL) {
            renderActualMonorail(level, bc, ms, vb, bc.tePositions.getFirst());
            ms.popPose(); // clean up pose, since cancelled
            ci.cancel(); // Don't do normal rendering
        }
    }

    private static void renderActualMonorail(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb,
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
