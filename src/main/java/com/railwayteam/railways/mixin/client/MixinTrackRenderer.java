package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackRenderer;
import com.simibubi.create.content.logistics.trains.track.TrackShape;
import com.simibubi.create.content.logistics.trains.track.TrackTileEntity;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.simibubi.create.AllBlockPartials.*;
import static com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils.reTexture;
import static com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils.renderBezierCasings;

@Mixin(value = TrackRenderer.class, remap = false)
public class MixinTrackRenderer {
  @Nullable
  private static BezierConnection bezierConnection = null;

  @Inject(method = "renderBezierTurn", at = @At("HEAD"), remap = false)
  private static void storeBezierConnection(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, CallbackInfo ci) {
    bezierConnection = bc;
  }

  @Inject(method = "renderBezierTurn", at = @At("RETURN"))
  private static void clearBezierConnection(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, CallbackInfo ci) {
    bezierConnection = null;
  }

  @Redirect(method = "renderBezierTurn", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lcom/simibubi/create/AllBlockPartials;TRACK_TIE:Lcom/jozufozu/flywheel/core/PartialModel;"), remap = false)
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
      target = "Lcom/simibubi/create/AllBlockPartials;TRACK_SEGMENT_LEFT:Lcom/jozufozu/flywheel/core/PartialModel;"), remap = false)
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
      target = "Lcom/simibubi/create/AllBlockPartials;TRACK_SEGMENT_RIGHT:Lcom/jozufozu/flywheel/core/PartialModel;"), remap = false)
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
      at = @At("HEAD"), remap = false)
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

  @Inject(method = "renderBezierTurn", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"), remap = false)
  private static void renderCurveCasings(Level level, BezierConnection bc, PoseStack ms, VertexConsumer vb, CallbackInfo ci) {
    //List<Vec3> casingPositions = CustomTrackBlock.casingPositions(bc);
    BlockPos tePosition = bc.tePositions.getFirst();
//    BlockEntity te = level.getBlockEntity(tePosition);
    if (true) {//te != null) {
      SlabBlock casingBlock = ((IHasTrackCasing) bc).getTrackCasing();//((IHasTrackCasing) te).getTrackCasing();
      if (casingBlock != null) {
//        PartialModel model = CRBlockPartials.TRACK_CASING_FLAT_THICK;
//        BakedModel slabModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(casingBlock.defaultBlockState());
//        BakedModel texturedCasing = new SpriteCopyingBakedModel(model.get(), slabModel);

//        PartialModel texturedPartial = RuntimeFakePartialModel.make(Railways.asResource("runtime_casing"), texturedCasing);

        renderBezierCasings(ms, level, reTexture(CRBlockPartials.TRACK_CASING_FLAT_THICK, casingBlock), casingBlock.defaultBlockState(), vb, bc);
      }
    }
  }
}
