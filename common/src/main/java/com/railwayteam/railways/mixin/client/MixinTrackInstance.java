package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.api.Material;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.light.LightUpdater;
import com.jozufozu.flywheel.util.box.GridAlignedBB;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils;
import com.railwayteam.railways.mixin_interfaces.IGetBezierConnection;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackInstance;
import com.simibubi.create.content.logistics.trains.track.TrackShape;
import com.simibubi.create.content.logistics.trains.track.TrackTileEntity;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

import static com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils.casingPositions;

@Mixin(value = TrackInstance.class, remap = false)
public abstract class MixinTrackInstance extends BlockEntityInstance<TrackTileEntity> implements IGetBezierConnection {
  private MixinTrackInstance(MaterialManager materialManager, TrackTileEntity blockEntity) {
    super(materialManager, blockEntity);
  }

  @Shadow
  public abstract void remove();

  @Nullable
  private BezierConnection bezierConnection = null;

  private final List<Pair<ModelData, BlockPos>> casingData = new ArrayList<>();

  @Override
  public @Nullable BezierConnection getBezierConnection() {
    return bezierConnection;
  }

  @Inject(method = "createInstance", at = @At("HEAD"))
  private void preCreateInstance(BezierConnection bc, CallbackInfoReturnable<?> cir) {
    this.bezierConnection = bc;
  }

  @Inject(method = "update", at = @At(value = "RETURN", ordinal = 0))
  private void updateWithoutConnections(CallbackInfo ci) { //otherwise it visually stays when an encased track is broken
    this.remove();
    makeCasingData(false);
    LightUpdater.get(world)
        .addListener(this);
  }

  @Inject(method = "update", at = @At(value = "RETURN", ordinal = 1))
  private void updateWithConnections(CallbackInfo ci) {
    makeCasingData(true);
  }

  @Inject(method = "updateLight", at = @At("HEAD"))
  private void snr_updateLight(CallbackInfo ci) {
    casingData.forEach((data) -> data.getFirst().updateLight(this.world, data.getSecond()));
  }

  @Inject(method = "remove", at = @At("HEAD"))
  private void snr_remove(CallbackInfo ci) {
    casingData.forEach((data) -> data.getFirst().delete());
    casingData.clear();
  }

  @Inject(method = "getVolume", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
  private void snr_getVolume(CallbackInfoReturnable<GridAlignedBB> cir, List<BlockPos> out) {
    out.add(this.pos);
  }

  private void makeCasingData(boolean connections) {
    Material<ModelData> mat = this.materialManager.cutout(RenderType.cutoutMipped()).material(Materials.TRANSFORMED);

    PoseStack pose = new PoseStack();
    TransformStack.cast(pose)
        .translate(getInstancePosition())
        .nudge((int) this.pos.asLong());

    SlabBlock casingBlock = ((IHasTrackCasing) this.blockEntity).getTrackCasing();
    if (casingBlock != null) {
      TrackShape shape = this.blockState.getValue(TrackBlock.SHAPE);
      if (CRBlockPartials.TRACK_CASINGS.containsKey(shape)) {
        CRBlockPartials.TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
        if (((IHasTrackCasing) this.blockEntity).isAlternate())
          spec = spec.getAltSpec();
        PartialModel rawCasingModel = spec.model;
        CRBlockPartials.ModelTransform transform = spec.transform;

        ModelData casingInstance = CasingRenderUtils.makeCasingInstance(rawCasingModel, casingBlock, mat);
        casingInstance.setTransform(pose)
            .rotateX(transform.rx())
            .rotateY(transform.ry())
            .rotateZ(transform.rz())
            .translate(transform.x(), transform.y(), transform.z());
        casingInstance.updateLight(this.world, this.pos);
        casingData.add(Pair.of(casingInstance, this.pos));

        for (CRBlockPartials.ModelTransform additionalTransform : spec.additionalTransforms) {
          ModelData additionalInstance = CasingRenderUtils.makeCasingInstance(rawCasingModel, casingBlock, mat);
          additionalInstance.setTransform(pose)
              .rotateX(additionalTransform.rx())
              .rotateY(additionalTransform.ry())
              .rotateZ(additionalTransform.rz())
              .translate(additionalTransform.x(), additionalTransform.y(), additionalTransform.z());
          additionalInstance.updateLight(this.world, this.pos);
          casingData.add(Pair.of(additionalInstance, this.pos.offset(additionalTransform.x(), additionalTransform.y(), additionalTransform.z())));
        }
      }
    }

    if (connections) {
      for (BezierConnection bc : this.blockEntity.getConnections().values()) {
        if (!bc.isPrimary()) continue;
        casingBlock = ((IHasTrackCasing) bc).getTrackCasing();
        if (casingBlock != null) {
          int heightDiff = Math.abs(bc.tePositions.get(false).getY() - bc.tePositions.get(true).getY());
          double shiftDown = ((IHasTrackCasing) bc).isAlternate() && heightDiff > 0 ? -0.25 : 0;
          if (heightDiff / bc.getLength() <= 4 / 30d) {
            for (Vec3 pos : casingPositions(bc)) {
              ModelData casingInstance = CasingRenderUtils.makeCasingInstance(heightDiff==0 ? CRBlockPartials.TRACK_CASING_FLAT :
                  CRBlockPartials.TRACK_CASING_FLAT_THICK, casingBlock, mat);
              casingInstance.setTransform(pose)
                  .translate(0, shiftDown, 0)
                  .translate(pos.x, pos.y, pos.z)
                  .scale(1.001f);
              BlockPos relativePos = new BlockPos(this.pos.getX() + pos.x, this.pos.getY() + pos.y, this.pos.getZ() + pos.z);
              casingInstance.updateLight(this.world, relativePos);
              casingData.add(Pair.of(casingInstance, relativePos));
            }
          } else {
            BezierConnection.SegmentAngles[] segments = bc.getBakedSegments();

            for (int i = 1; i < segments.length; i++) {
              if (i % 2 == 0) continue;
              BezierConnection.SegmentAngles segment = segments[i];
              Matrix4f pose_matrix = segment.tieTransform.pose().copy();
              pose_matrix.translate(new Vector3f(0, (i % 4) * 0.001f, 0));

              ModelData casingInstance = CasingRenderUtils.makeCasingInstance(heightDiff==0 ? CRBlockPartials.TRACK_CASING_FLAT :
                  CRBlockPartials.TRACK_CASING_FLAT_THICK, casingBlock, mat);
              casingInstance.setTransform(pose)
                  .mulPose(pose_matrix)
                  .mulNormal(segment.tieTransform.normal())
                  .translate(0, shiftDown, 0)
                  .scale(1.001f);
              BlockPos relativePos = segment.lightPosition.offset(this.pos);
              casingInstance.updateLight(this.world, relativePos);
              casingData.add(Pair.of(casingInstance, relativePos));

              for (boolean first : Iterate.trueAndFalse) {
                PoseStack.Pose transform = segment.railTransforms.get(first);
                Matrix4f pose_matrix2 = transform.pose().copy();
                pose_matrix2.translate(new Vector3f(0, (i % 4) * 0.001f, 0));

                ModelData casingInstance2 = CasingRenderUtils.makeCasingInstance(heightDiff==0 ? CRBlockPartials.TRACK_CASING_FLAT :
                    CRBlockPartials.TRACK_CASING_FLAT_THICK, casingBlock, mat);
                casingInstance2.setTransform(pose)
                    .mulPose(pose_matrix2)
                    .mulNormal(transform.normal())
                    .translate(-0.5, shiftDown, 0);
                BlockPos relativePos2 = segment.lightPosition.offset(this.pos);
                casingInstance2.updateLight(this.world, relativePos2);
                casingData.add(Pair.of(casingInstance2, relativePos2));
              }
            }
          }
        }
      }
    }
  }
}
