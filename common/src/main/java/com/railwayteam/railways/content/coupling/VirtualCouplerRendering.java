/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.content.coupling;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.StandardBogeyBlockEntity;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class VirtualCouplerRendering {
    private VirtualCouplerRendering() {}

    public static void renderCoupler(Direction direction, double couplingDistance, boolean front, float partialTicks,
                                     PoseStack ms, MultiBufferSource buffer, int light, int overlay,
                                     StandardBogeyBlockEntity te) {
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        BlockState air = Blocks.AIR.defaultBlockState();

        if (te.getBlockState().getBlock() instanceof AbstractBogeyBlock<?> bogeyBlock) {
            Vec3 anchor = bogeyBlock.getConnectorAnchorOffset(false).multiply(front ? -1 : 1, 1, front ? -1 : 1);
//                .add(Vec3.atBottomCenterOf(te.getBlockPos()));
            Vec3 anchor2 = anchor.add(Vec3.atLowerCornerOf(direction.getNormal()).scale(couplingDistance));

            double diffX = anchor2.x - anchor.x;
            double diffY = anchor2.y - anchor.y;
            double diffZ = anchor2.z - anchor.z;
            float yRot = AngleHelper.deg(Mth.atan2(diffZ, diffX)) + 90;
            float xRot = AngleHelper.deg(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)));

            ms.pushPose();
            ms.pushPose();
            ms.translate(anchor.x, anchor.y, anchor.z);
            CachedBuffers.partial(AllPartialModels.TRAIN_COUPLING_HEAD, air)
                .rotateY(-yRot)
                .rotateX(xRot)
                .light(light)
                .renderInto(ms, vb);

            float margin = 3 / 16f;
            int couplingSegments = (int) Math.round(couplingDistance * 4);
            double stretch = 1.0;

            for (int j = 0; j < couplingSegments; j++) {
                CachedBuffers.partial(AllPartialModels.TRAIN_COUPLING_CABLE, air)
                    .rotateY(-yRot + 180)
                    .rotateX(-xRot)
                    .translate(0, 0, margin + 2 / 16f)
                    .scale(1, 1, (float) stretch)
                    .translate(0, 0, j / 4f)
                    .light(light)
                    .renderInto(ms, vb);
            }

            ms.popPose();

            ms.pushPose();
            ms.translate(anchor2.x, anchor2.y, anchor2.z);
            CachedBuffers.partial(AllPartialModels.TRAIN_COUPLING_HEAD, air)
                .rotateY(-yRot + 180)
                .rotateX(-xRot)
                .light(light)
                .renderInto(ms, vb);
            ms.popPose();
            ms.popPose();
        }
    }
}
