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
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;

import static com.railwayteam.railways.registry.CRBlockPartials.CR_WIDE_BOGEY_WHEELS;
import static com.railwayteam.railways.registry.CRBlockPartials.WIDE_DEFAULT_FRAME;

public class WideDefaultBogeyRenderer implements BogeyRenderer {
	@Override
	public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack ms, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean inContraption) {
		VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());

		SuperByteBuffer shaft = CachedBuffers.block(AllBlocks.SHAFT.getDefaultState()
				.setValue(ShaftBlock.AXIS, Direction.Axis.Z));
		for (int i : Iterate.zeroAndOne) {
			shaft.translate(-.5f, 6 / 16., .5f + i * -2)
					.center()
					.rotateZDegrees(wheelAngle)
					.uncenter()
					.light(packedLight)
					.overlay(packedOverlay)
					.renderInto(ms, buffer);
		}

		CachedBuffers.partial(WIDE_DEFAULT_FRAME, Blocks.AIR.defaultBlockState())
				.renderInto(ms, buffer);

		SuperByteBuffer wheel = CachedBuffers.partial(CR_WIDE_BOGEY_WHEELS, Blocks.AIR.defaultBlockState());
		for (int side = -1; side < 2; side++) {
			wheel.translate(0, 14 / 16., side * 1.5)
					.rotateXDegrees(wheelAngle)
					.renderInto(ms, buffer);
		}
	}
}
