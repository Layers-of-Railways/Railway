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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
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

import static com.railwayteam.railways.registry.CRBlockPartials.MEDIUM_SHARED_WHEELS;
import static com.railwayteam.railways.registry.CRBlockPartials.MEDIUM_STANDARD_FRAME;

public class MediumStandardRenderer implements BogeyRenderer {

	@Override
	public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean inContraption) {

		VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());
		SuperByteBuffer primaryShaft = CachedBuffers.block(AllBlocks.SHAFT.getDefaultState()
				.setValue(ShaftBlock.AXIS, Direction.Axis.Z));

		for (int i : Iterate.zeroAndOne) {
			primaryShaft
					.translate(-.5f, .25f, i * -1)
					.center()
					.rotateZDegrees(wheelAngle)
					.uncenter()
					.light(light)
					.overlay(overlay)
					.renderInto(poseStack, buffer);
		}

		CachedBuffers.partial(MEDIUM_STANDARD_FRAME, Blocks.AIR.defaultBlockState())
				.light(light)
				.overlay(overlay)
				.renderInto(poseStack, buffer);

		SuperByteBuffer wheels = CachedBuffers.partial(MEDIUM_SHARED_WHEELS, Blocks.AIR.defaultBlockState());
		for (int side : Iterate.positiveAndNegative) {
			wheels.translate(0, 13 / 16f, side)
					.rotateXDegrees(wheelAngle)
					.translate(0, -13 / 16f, 0)
					.light(light)
					.overlay(overlay)
					.renderInto(poseStack, buffer);
		}
	}
}
