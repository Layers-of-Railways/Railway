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

package com.railwayteam.railways.content.custom_bogeys.renderer.wide;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;

import static com.railwayteam.railways.registry.CRBlockPartials.*;
import static com.railwayteam.railways.registry.CRBlockPartials.WIDE_SCOTCH_PINS;

public class WideComicallyLargeScotchYokeBogeyRenderer implements BogeyRenderer {


    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean inContraption) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());

        SuperByteBuffer primaryShaft = CachedBuffers.block(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z));
        for (int i : Iterate.zeroAndOne) {
            primaryShaft.translate(-.5, 4 / 16., i * -1)
                    .center()
                    .rotateZDegrees(wheelAngle)
                    .uncenter()
                    .renderInto(poseStack, buffer);
        }


        SuperByteBuffer secondaryShaft = CachedBuffers.block(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X));
        for (int i : Iterate.zeroAndOne) {
            for (int side : Iterate.zeroAndOne) {
                secondaryShaft.translate(-1 + side, 4 / 16., (10 / 16.) + i * -(36 / 16.))
                        .center()
                        .rotateXDegrees(wheelAngle)
                        .uncenter()
                        .renderInto(poseStack, buffer);
            }
        }


        CachedBuffers.partial(WIDE_COMICALLY_LARGE_FRAME, Blocks.AIR.defaultBlockState())
                .translate(0, 4 / 16., 0)
                .renderInto(poseStack, buffer);

        CachedBuffers.partial(WIDE_COMICALLY_LARGE_PISTONS, Blocks.AIR.defaultBlockState())
                .translate(0, 1.5, (1 / 4f + (5 / 16.)) * Math.sin(AngleHelper.rad(wheelAngle)))
                .renderInto(poseStack, buffer);


        CachedBuffers.partial(WIDE_COMICALLY_LARGE_WHEELS, Blocks.AIR.defaultBlockState())
                .translate(0, 1.5, 0)
                .rotateXDegrees(wheelAngle)
                .translate(0, 0, 0)
                .renderInto(poseStack, buffer);

        CachedBuffers.partial(WIDE_COMICALLY_LARGE_PINS, Blocks.AIR.defaultBlockState())
                .translate(0, 1.5, 0)
                .rotateXDegrees(wheelAngle)
                .translate(0, 1 / 4f + (5 / 16.), 0)
                .rotateXDegrees(-wheelAngle)
                .renderInto(poseStack, buffer);

    }


}
