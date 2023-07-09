package com.railwayteam.railways.content.buffer;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.util.CustomTrackOverlayRendering;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TrackBufferRenderer extends SmartBlockEntityRenderer<TrackBufferBlockEntity> {

    public TrackBufferRenderer(Context context) {
        super(context);
    }

    public static PartialModel getBufferOverlayModel(TrackBufferBlockEntity te) {
        return CRBlockPartials.BUFFER;
    }

    @Override
    protected void renderSafe(TrackBufferBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(te, partialTicks, ms, buffer, light, overlay);

        renderEdgePoint(te, ms, buffer, light, overlay, te.edgePoint);
    }

    private void renderEdgePoint(TrackBufferBlockEntity te, PoseStack ms, MultiBufferSource buffer,
                                 int light, int overlay, TrackTargetingBehaviour<TrackBuffer> target) {
        BlockPos pos = te.getBlockPos();
        boolean offsetToSide = CustomTrackOverlayRendering.overlayWillOverlap(target);

        BlockPos targetPosition = target.getGlobalPosition();
        Level level = te.getLevel();
        assert level != null;
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();

        if (!(block instanceof ITrackBlock))
            return;

        ms.pushPose();
        TransformStack.cast(ms)
            .translate(targetPosition.subtract(pos));
        CustomTrackOverlayRendering.renderOverlay(level, targetPosition, target.getTargetDirection(), target.getTargetBezier(), ms,
            buffer, light, overlay, getBufferOverlayModel(te), 1, offsetToSide);
        ms.popPose();
    }
}
