package com.railwayteam.railways.content.coupling.coupler;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.util.CustomTrackOverlayRendering;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TrackCouplerRenderer extends SmartBlockEntityRenderer<TrackCouplerBlockEntity> {

    public TrackCouplerRenderer(Context context) {
        super(context);
    }

    @Nullable
    public static PartialModel getCouplerOverlayModel(TrackCouplerTileEntity te) {
        if (te.areEdgePointsOk()) {
            TrackCouplerTileEntity.AllowedOperationMode mode = te.getAllowedOperationMode();
            if (mode.canCouple && mode.canDecouple) return CRBlockPartials.COUPLER_BOTH;
            if (mode.canCouple) return CRBlockPartials.COUPLER_COUPLE;
            if (mode.canDecouple) return CRBlockPartials.COUPLER_DECOUPLE;
        } else {
            return CRBlockPartials.COUPLER_NONE;
        }
        return null;
    }

    @Override
    protected void renderSafe(TrackCouplerBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(te, partialTicks, ms, buffer, light, overlay);

        renderEdgePoint(te, ms, buffer, light, overlay, te.edgePoint);
        renderEdgePoint(te, ms, buffer, light, overlay, te.secondEdgePoint);
    }

    private void renderEdgePoint(TrackCouplerBlockEntity te, PoseStack ms, MultiBufferSource buffer,
                                 int light, int overlay, TrackTargetingBehaviour<TrackCoupler> target) {
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
            buffer, light, overlay, getCouplerOverlayModel(te), 1, offsetToSide);
        ms.popPose();
    }
}
