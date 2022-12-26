package com.railwayteam.railways.content.coupling.coupler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.coupling.CustomTrackOverlayRendering;
import com.simibubi.create.content.logistics.trains.ITrackBlock;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.foundation.tileEntity.renderer.SmartTileEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TrackCouplerRenderer extends SmartTileEntityRenderer<TrackCouplerTileEntity> {

    public TrackCouplerRenderer(Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TrackCouplerTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(te, partialTicks, ms, buffer, light, overlay);

        renderEdgePoint(te, ms, buffer, light, overlay, te.edgePoint);
        renderEdgePoint(te, ms, buffer, light, overlay, te.secondEdgePoint);
    }

    private void renderEdgePoint(TrackCouplerTileEntity te, PoseStack ms, MultiBufferSource buffer,
                                 int light, int overlay, TrackTargetingBehaviour<TrackCoupler> target) {
        BlockPos pos = te.getBlockPos();

        BlockPos targetPosition = target.getGlobalPosition();
        Level level = te.getLevel();
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();

        if (!(block instanceof ITrackBlock))
            return;

        ms.pushPose();
        ms.translate(-pos.getX(), -pos.getY(), -pos.getZ());
        CustomTrackOverlayRendering.renderOverlay(level, targetPosition, target.getTargetDirection(), target.getTargetBezier(), ms,
            buffer, light, overlay, CustomTrackOverlayRendering.getCouplerOverlayModel(te.areEdgePointsOk(), !te.areEdgePointsOk()), 1);
        ms.popPose();
    }
}