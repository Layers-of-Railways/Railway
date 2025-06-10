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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.single_axle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;

import static com.railwayteam.railways.registry.CRBlockPartials.*;
import static com.simibubi.create.AllPartialModels.SMALL_BOGEY_WHEELS;

public class CoilspringBogeyRenderer implements BogeyRenderer {

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean inContraption) {

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());

        CachedBuffers.partial(COILSPRING_FRAME, Blocks.AIR.defaultBlockState())
                .light(packedLight)
                .overlay(packedOverlay)
                .renderInto(poseStack, buffer);

        CachedBuffers.partial(SMALL_BOGEY_WHEELS, Blocks.AIR.defaultBlockState())
                .translate(0, 12 / 16f, 0)
                .rotateX(wheelAngle)
                .light(packedLight)
                .overlay(packedOverlay)
                .renderInto(poseStack, buffer);
    }
}
