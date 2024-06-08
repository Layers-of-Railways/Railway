/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin_interfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Implementation was inspired/derived from <a href="https://github.com/XFactHD/FramedBlocks/blob/17c8274ca380c3a868763b1b05657d07860c364b/src/main/java/xfacthd/framedblocks/api/render/OutlineRenderer.java">Framed Blocks</a>
 * <p>
 * Which is licensed under <a href="https://github.com/XFactHD/FramedBlocks/blob/17c8274ca380c3a868763b1b05657d07860c364b/LICENSE">LGPL</a>
 */
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

    /**
     * Draws a line using the provided VertexConsumer, PoseStack, and 2 sets 3D coordinates.
     * Also adds the specified offset to the specified axis
     */
    default void drawLineWithAxisOffset(VertexConsumer vb, PoseStack ms, double x1, double y1, double z1, double x2, double y2, double z2, double offset, Direction.Axis axis) {
        switch (axis) {
            case X -> drawLine(vb, ms, x1 + offset, y1, z1, x2 + offset, y2, z2);
            case Y -> drawLine(vb, ms, x1, y1 + offset, z1, x2, y2 + offset, z2);
            case Z -> drawLine(vb, ms, x1, y1, z1 + offset, x2, y2, z2 + offset);
        }
    }
}
