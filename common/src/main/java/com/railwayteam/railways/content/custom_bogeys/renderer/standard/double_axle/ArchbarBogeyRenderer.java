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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;

import static com.railwayteam.railways.registry.CRBlockPartials.ARCHBAR_FRAME;

public class ArchbarBogeyRenderer implements BogeyRenderer {
    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean inContraption) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());
        
        SuperByteBuffer secondaryShaft = CachedBuffers.block(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z));

        for (int i : Iterate.zeroAndOne) {
            secondaryShaft
                    .translate(-.5f, .25f, i * -1)
                    .center()
                    .rotateZ(wheelAngle)
                    .uncenter()
                    .light(light)
                    .overlay(overlay)
                    .renderInto(poseStack, buffer);
        }

        CachedBuffers.partial(ARCHBAR_FRAME, Blocks.AIR.defaultBlockState())
                .light(light)
                .overlay(overlay)
                .renderInto(poseStack, buffer);

        SuperByteBuffer wheels = CachedBuffers.partial(AllPartialModels.SMALL_BOGEY_WHEELS, Blocks.AIR.defaultBlockState());
        for (int side : Iterate.positiveAndNegative) {
            wheels.translate(0, 11.975 / 16f, side * 0.998)
                    .rotateXDegrees(wheelAngle)
                    .light(light)
                    .overlay(overlay)
                    .renderInto(poseStack, buffer);
        }
    }
}
