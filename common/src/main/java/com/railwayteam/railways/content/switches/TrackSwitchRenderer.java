package com.railwayteam.railways.content.switches;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.util.CustomTrackOverlayRendering;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TrackSwitchRenderer extends SmartBlockEntityRenderer<TrackSwitchTileEntity> {
  public TrackSwitchRenderer(Context ctx) {
    super(ctx);
  }

  @Override
  protected void renderSafe(TrackSwitchTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
    renderFlagState(te, partialTicks, ms, buffer, light);
    renderTrackOverlay(te, ms, buffer, light, overlay, te.edgePoint);
  }

  private void renderFlagState(TrackSwitchTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light) {
    // TODO: Animate flag state changes?

    BlockState state = te.getBlockState();
    ms.pushPose();

    float yRot = AngleHelper.horizontalAngle(state.getValue(TrackSwitchBlock.FACING));

    TransformStack msr = TransformStack.cast(ms);
    msr.centre()
      .rotateY(yRot)
      .unCentre();

    SuperByteBuffer buf;
    if (te.isAutomatic()) {
      ms.pushPose();
      ms.translate(0, -2.0 / 16, 0);

      buf = CachedBufferer.partial(CRBlockPartials.BRASS_SWITCH_FLAG, state)
        .light(light)
        .rotateCentered(Direction.UP, 1.5708f)
        .translate(0.5, 8.5 / 16, 0.5);

      // Rotate just enough to touch the front or back edge
      if (te.isReverseLeft()) {
        buf = buf.rotate(Direction.NORTH, -1.1f);
      } else if (te.isReverseRight()) {
        buf = buf.rotate(Direction.NORTH, 1.1f);
      }

      buf
        .translate(-0.5, -7.5 / 16, -0.5)
        .renderInto(ms, buffer.getBuffer(RenderType.solid()));

      ms.popPose();
    } else {
      buf = CachedBufferer.partial(CRBlockPartials.ANDESITE_SWITCH_FLAG, state)
        .light(light);

      if (te.isReverseLeft()) {
        buf = buf.rotateCentered(Direction.UP, 1.5708f);
      } else if (te.isReverseRight()) {
        buf = buf.rotateCentered(Direction.UP, -1.5708f);  // 90°
      }
      buf.renderInto(ms, buffer.getBuffer(RenderType.solid()));

      CachedBufferer.partial(CRBlockPartials.ANDESITE_SWITCH_HANDLE, state)
        .light(light)
        .rotateCentered(Direction.UP, -1.5708f)  // 90°
        .renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    ms.popPose();
  }

  private void renderTrackOverlay(TrackSwitchTileEntity te, PoseStack ms, MultiBufferSource buffer,
                                  int light, int overlay, TrackTargetingBehaviour<TrackSwitch> target) {
    BlockPos pos = te.getBlockPos();
    boolean offsetToSide = CustomTrackOverlayRendering.overlayWillOverlap(target);

    BlockPos targetPosition = target.getGlobalPosition();
    Level level = te.getLevel();
    BlockState trackState = level.getBlockState(targetPosition);
    Block block = trackState.getBlock();

    if (!(block instanceof ITrackBlock))
      return;

    ms.pushPose();
    TransformStack.cast(ms)
            .translate(targetPosition.subtract(pos));
    CustomTrackOverlayRendering.renderOverlay(level, targetPosition, target.getTargetDirection(), target.getTargetBezier(), ms,
            buffer, light, overlay, te.getOverlayModel(), 1, offsetToSide);
    ms.popPose();
  }
}
