package com.railwayteam.railways.content.switches;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.util.CustomTrackOverlayRendering;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.logistics.trains.GraphLocation;
import com.simibubi.create.content.logistics.trains.ITrackBlock;
import com.simibubi.create.content.logistics.trains.TrackEdge;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.TrackEdgePoint;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.tileEntity.renderer.SmartTileEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TrackSwitchRenderer extends SmartTileEntityRenderer<TrackSwitchTileEntity> {
  public TrackSwitchRenderer(Context ctx) {
    super(ctx);
  }

  public static @NotNull PartialModel getOverlayModel(TrackSwitchTileEntity te) {
    if (te.hasBothExits()) {
      if (te.isNormal()) {
        return CRBlockPartials.SWITCH_3WAY_STRAIGHT;
      } else if (te.isReverseLeft()) {
        return CRBlockPartials.SWITCH_3WAY_LEFT;
      } else if (te.isReverseRight()) {
        return CRBlockPartials.SWITCH_3WAY_RIGHT;
      }
    } else if (te.hasLeftExit()) {
      if (te.isNormal()) {
        return CRBlockPartials.SWITCH_LEFT_STRAIGHT;
      } else if (te.isReverseLeft()) {
        return CRBlockPartials.SWITCH_LEFT_TURN;
      }
    } else if (te.hasRightExit()) {
      if (te.isNormal()) {
        return CRBlockPartials.SWITCH_RIGHT_STRAIGHT;
      } else if (te.isReverseRight()) {
        return CRBlockPartials.SWITCH_RIGHT_TURN;
      }
    }

    return CRBlockPartials.SWITCH_NONE;
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

    BlockPos targetPos = target.getGlobalPosition();
    Level level = te.getLevel();
    BlockState trackState = level.getBlockState(targetPos);
    Block block = trackState.getBlock();

    if (!(block instanceof ITrackBlock)) {
      return;
    }

    ms.pushPose();
    ms.translate(-pos.getX(), -pos.getY(), -pos.getZ());
    CustomTrackOverlayRendering.renderOverlay(
      level, targetPos, target.getTargetDirection(), target.getTargetBezier(),
      ms, buffer, light, overlay, getOverlayModel(te), 1, offsetToSide);
    ms.popPose();
  }
}
