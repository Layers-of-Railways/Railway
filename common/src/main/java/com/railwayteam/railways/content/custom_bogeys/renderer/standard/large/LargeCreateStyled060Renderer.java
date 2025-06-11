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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.large;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class LargeCreateStyled060Renderer implements BogeyRenderer {
    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean inContraption) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer secondaryShafts = CachedBuffers.block(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X));
        SuperByteBuffer middleShafts = CachedBuffers.block(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z));

        for (int side : Iterate.positiveAndNegative) {
            secondaryShafts.translate(-.5f, .25f, -.5f + side * 2.681)
                    .center()
                    .rotateX(wheelAngle)
                    .uncenter()
                    .renderInto(poseStack, buffer);
        }

        for (int side = -2; side < 3; side++) {
            if (side == 0) continue;
            middleShafts.translate(-.5f, .25f, -.5f + side)
                    .center()
                    .rotateZ(wheelAngle)
                    .uncenter()
                    .renderInto(poseStack, buffer);
        }

        CachedBuffers.partial(LARGE_CREATE_STYLED_0_6_0_FRAME,Blocks.AIR.defaultBlockState())
                .renderInto(poseStack, buffer);

        CachedBuffers.partial(LARGE_CREATE_STYLED_0_6_0_PISTON, Blocks.AIR.defaultBlockState())
                .translate(0, 0, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)))
                .renderInto(poseStack, buffer);

        SuperByteBuffer wheels = CachedBuffers.partial(AllPartialModels.LARGE_BOGEY_WHEELS,Blocks.AIR.defaultBlockState());
        SuperByteBuffer pins = CachedBuffers.partial(AllPartialModels.BOGEY_PIN,Blocks.AIR.defaultBlockState());

        CachedBuffers.partial(LC_STYLE_SEMI_BLIND_WHEELS,Blocks.AIR.defaultBlockState())
                .translate(0, 1, 0)
                .rotateX(wheelAngle)
                .translate(0, -1, 0)
                .renderInto(poseStack, buffer);

        for (int side : Iterate.positiveAndNegative) {
            wheels.translate(0, 1, side * 1.6842)
                    .rotateX(wheelAngle)
                    .renderInto(poseStack, buffer);
        }

        for (int side = -1; side < 2; side++) {
            pins.translate(0, 1, side * 1.6842)
                    .rotateX(wheelAngle)
                    .translate(0, 1 / 4f, 0)
                    .rotateX(-wheelAngle)
                    .renderInto(poseStack, buffer);
        }
    }
}
