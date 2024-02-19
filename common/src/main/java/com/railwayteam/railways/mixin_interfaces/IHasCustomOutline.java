package com.railwayteam.railways.mixin_interfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public interface IHasCustomOutline {
    void customOutline(PoseStack poseStack, VertexConsumer consumer, BlockState state);

    void matrixRotation(PoseStack poseStack, BlockState state);

    /**
     * Draws a line using the provided VertexConsumer, PoseStack, and 2 sets 3D coordinates.
     */
    default void drawLine(VertexConsumer vb, PoseStack ms, double x1, double y1, double z1, double x2, double y2, double z2) {
        PoseStack.Pose transform = ms.last();

        float xDiff = (float) (x2 - x1);
        float yDiff = (float) (y2 - y1);
        float zDiff = (float) (z2 - z1);
        float length = Mth.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);

        xDiff /= length;
        yDiff /= length;
        zDiff /= length;

        vb.vertex(transform.pose(), (float) (x1 / 16), (float) (y1 / 16), (float) (z1 / 16)).color(0F, 0F, 0F, 0.4F)
                .normal(transform.normal(), xDiff, yDiff, zDiff).endVertex();
        vb.vertex(transform.pose(), (float) (x2 / 16), (float) (y2 / 16), (float) (z2 / 16)).color(0F, 0F, 0F, 0.4F)
                .normal(transform.normal(), xDiff, yDiff, zDiff).endVertex();
    }
}
