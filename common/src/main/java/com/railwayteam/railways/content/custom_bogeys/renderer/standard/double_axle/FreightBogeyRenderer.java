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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;

import static com.railwayteam.railways.registry.CRBlockPartials.FREIGHT_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.LONG_SHAFTED_WHEELS;

public class FreightBogeyRenderer implements BogeyRenderer {

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay, boolean inContraption) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());

        SuperByteBuffer secondaryShafts = CachedBuffers.block(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z));

        for (int i : Iterate.zeroAndOne) {
            secondaryShafts
                    .translate(-.5f, .25f, i * -1)
                    .center()
                    .rotateZ(wheelAngle)
                    .uncenter()
                    .light(light)
                    .overlay(overlay)
                    .renderInto(ms, buffer);
        }

        CachedBuffers.partial(FREIGHT_FRAME, Blocks.AIR.defaultBlockState())
                .renderInto(ms, buffer);

        SuperByteBuffer wheel = CachedBuffers.partial(LONG_SHAFTED_WHEELS, Blocks.AIR.defaultBlockState());
        for (int side = -1; side < 2; side++) {
            wheel.translate(0, 12 / 16f, side)
                    .rotateX(wheelAngle)
                    .translate(0, -12 / 16f, 0)
                    .light(light)
                    .overlay(overlay)
                    .renderInto(ms,buffer);
        }
    }
}
