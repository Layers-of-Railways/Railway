package com.railwayteam.railways.content.conductor.whistle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.coupling.CustomTrackOverlayRendering;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ConductorWhistleFlagRenderer extends SmartBlockEntityRenderer<ConductorWhistleFlagBlockEntity> {
    public ConductorWhistleFlagRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ConductorWhistleFlagBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
        renderEdgePoint(te, ms, buffer, light, overlay);

        CachedBufferer.partial(CRBlockPartials.CONDUCTOR_WHISTLE_FLAGS.get(te.getColor()), Blocks.AIR.defaultBlockState())
            .renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }

    private void renderEdgePoint(ConductorWhistleFlagBlockEntity te, PoseStack ms, MultiBufferSource buffer,
                                 int light, int overlay) {
        TrackTargetingBehaviour<GlobalStation> target = te.station;
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
            buffer, light, overlay, AllPartialModels.TRACK_STATION_OVERLAY, 1, false); //TODO real model
        ms.popPose();
    }
}
