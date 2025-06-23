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
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;

import static com.railwayteam.railways.registry.CRBlockPartials.LARGE_CREATE_STYLED_0_12_0_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.LARGE_CREATE_STYLED_0_12_0_PISTON;
import static com.railwayteam.railways.registry.CRBlockPartials.LC_STYLE_FULL_BLIND_WHEELS;
import static com.railwayteam.railways.registry.CRBlockPartials.LC_STYLE_SEMI_BLIND_WHEELS;

public class LargeCreateStyled0120Renderer implements BogeyRenderer {


	@Override
	public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean inContraption) {
		VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());
		SuperByteBuffer secondaryShafts = CachedBuffers.block(AllBlocks.SHAFT.getDefaultState()
				.setValue(ShaftBlock.AXIS, Direction.Axis.X));
		SuperByteBuffer middleShafts = CachedBuffers.block(AllBlocks.SHAFT.getDefaultState()
				.setValue(ShaftBlock.AXIS, Direction.Axis.Z));

		for (int side : Iterate.positiveAndNegative) {
			secondaryShafts.translate(-.5, .25, -.5f + side * 5.364)
					.center()
					.rotateXDegrees(wheelAngle)
					.uncenter()
					.light(light)
					.overlay(overlay)
					.renderInto(poseStack, buffer);
		}

		for (int side = -3; side < 4; side++) {
			if (side == 0) continue;
			middleShafts.translate(-.5f, .25f, -.5f + side * -1.7)
					.center()
					.rotateZDegrees(wheelAngle)
					.uncenter()
					.light(light)
					.overlay(overlay)
					.renderInto(poseStack, buffer);
		}

		CachedBuffers.partial(LARGE_CREATE_STYLED_0_12_0_FRAME, Blocks.AIR.defaultBlockState())
				.light(light)
				.overlay(overlay)
				.renderInto(poseStack, buffer);

		CachedBuffers.partial(LARGE_CREATE_STYLED_0_12_0_PISTON, Blocks.AIR.defaultBlockState())
				.translate(0, 0, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)))
				.light(light)
				.overlay(overlay)
				.renderInto(poseStack, buffer);

		SuperByteBuffer fullBlindWheels = CachedBuffers.partial(LC_STYLE_FULL_BLIND_WHEELS, Blocks.AIR.defaultBlockState());
		SuperByteBuffer semiBlindWheels = CachedBuffers.partial(LC_STYLE_SEMI_BLIND_WHEELS, Blocks.AIR.defaultBlockState());
		SuperByteBuffer wheels = CachedBuffers.partial(AllPartialModels.LARGE_BOGEY_WHEELS, Blocks.AIR.defaultBlockState());
		SuperByteBuffer pins = CachedBuffers.partial(AllPartialModels.BOGEY_PIN, Blocks.AIR.defaultBlockState());

		for (int side : Iterate.positiveAndNegative) {
			fullBlindWheels.translate(0, 1, side * .8733)
					.rotateXDegrees(wheelAngle)
					.translate(0, -1, 0)
					.light(light)
					.overlay(overlay)
					.renderInto(poseStack, buffer);

			semiBlindWheels.translate(0, 1, side * 2.62)
					.rotateXDegrees(wheelAngle)
					.translate(0, -1, 0)
					.light(light)
					.overlay(overlay)
					.renderInto(poseStack, buffer);

			wheels.translate(0, 1, side * 4.3665)
					.rotateXDegrees(wheelAngle)
					.light(light)
					.overlay(overlay)
					.renderInto(poseStack, buffer);
		}

		for (int side = -3; side < 3; side++) {
			pins.translate(0, 1, .8733f + side * 1.74657)
					.rotateXDegrees(wheelAngle)
					.translate(0, 1 / 4f, 0)
					.rotateXDegrees(-wheelAngle)
					.light(light)
					.overlay(overlay)
					.renderInto(poseStack, buffer);
		}
	}
}
