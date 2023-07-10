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
import org.jetbrains.annotations.NotNull;

public class TrackBufferRenderer extends SmartBlockEntityRenderer<TrackBufferBlockEntity> {

    public TrackBufferRenderer(Context context) {
        super(context);
    }

    public static PartialModel getBufferOverlayModel() {
        return CRBlockPartials.BUFFER;
    }

    @Override
    protected void renderSafe(TrackBufferBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                              int packedLight, int packedOverlay) {
        super.renderSafe(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        renderEdgePoint(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, blockEntity.edgePoint);
    }

    private void renderEdgePoint(TrackBufferBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource,
                                 int packedLight, int packedOverlay, TrackTargetingBehaviour<TrackBuffer> target) {
        BlockPos pos = blockEntity.getBlockPos();
        boolean offsetToSide = CustomTrackOverlayRendering.overlayWillOverlap(target);

        BlockPos targetPosition = target.getGlobalPosition();
        Level level = blockEntity.getLevel();
        if (level == null)
            return;
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();

        if (!(block instanceof ITrackBlock))
            return;

        poseStack.pushPose();
        TransformStack.cast(poseStack)
            .translate(targetPosition.subtract(pos));
        CustomTrackOverlayRendering.renderOverlay(level, targetPosition, target.getTargetDirection(), target.getTargetBezier(), poseStack,
            bufferSource, packedLight, packedOverlay, getBufferOverlayModel(), 1, offsetToSide);
        poseStack.popPose();
    }

    public boolean shouldRenderOffScreen(@NotNull TrackBufferBlockEntity blockEntity) {
        return true;
    }

}
