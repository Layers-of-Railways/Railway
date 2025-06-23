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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.handcar.ik.DoubleArmIK;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import static com.railwayteam.railways.registry.CRBlockPartials.HANDCAR_COUPLING;
import static com.railwayteam.railways.registry.CRBlockPartials.HANDCAR_HANDLE;
import static com.railwayteam.railways.registry.CRBlockPartials.HANDCAR_HANDLE_FIRST_PERSON;
import static com.railwayteam.railways.registry.CRBlockPartials.HANDCAR_LARGE_COG;
import static com.railwayteam.railways.registry.CRBlockPartials.HANDCAR_SMALL_COG;
import static com.railwayteam.railways.registry.CRBlockPartials.NARROW_SCOTCH_WHEELS;

public class HandcarBogeyRenderer implements BogeyRenderer {
	private CarriageBogey carriageBogey; // TODO - PORTING

	private boolean isFirstPerson() {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (!mc.options.getCameraType().isFirstPerson()) {
			return false;
		}
		if (player != null && player.getRootVehicle() instanceof CarriageContraptionEntity cce) {
			if (carriageBogey == null)
				return true;
			return cce.trainId.equals(carriageBogey.carriage.train.id)
					&& cce.carriageIndex == carriageBogey.carriage.train.carriages.indexOf(carriageBogey.carriage);
		}
		return false;
	}

	@Override
	public void render(CompoundTag bogeyData, float wheelAngle, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean inContraption) {
//      wheelAngle = AnimationTickHolder.getTicks(true) + AnimationTickHolder.getPartialTicks();
		VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());
		Vec3 coupling_pos;
		{
			final double couple_r = (3 / 16.) * Mth.SQRT_OF_TWO;
			final double couple_degrees = (-wheelAngle / 2) - 22.5;
			float couple_radians = (float) (couple_degrees * Mth.DEG_TO_RAD);
			double couple_x = couple_r * Mth.sin(couple_radians);
			double couple_y = couple_r * Mth.cos(couple_radians);
			coupling_pos = new Vec3(1.75 / 16., (12 / 16.) + couple_y, (-3.5 / 16.) + couple_x);
		}

		Vec2 upperVec2 = new Vec2(0, 39 / 16f);
		Vec2 couplingVec2 = new Vec2((float) coupling_pos.z, (float) coupling_pos.y);

		//                                                                             upper         lower
		Vec2 hingeOffset = DoubleArmIK.calculateJointOffset(upperVec2, couplingVec2, 14 / 16., 18 / 16.);
		Vec2 hingePos2 = hingeOffset.add(couplingVec2);

		double couplingAngle;
		double handleAngle;

		{
			couplingAngle = Mth.atan2((hingeOffset.y), (hingeOffset.x));

			Vec2 handle_offset = hingePos2.add(upperVec2.negated());
			handleAngle = Mth.atan2(handle_offset.y, handle_offset.x);
		}

		boolean firstPerson = isFirstPerson();

		CachedBuffers.partial(HANDCAR_HANDLE, Blocks.AIR.defaultBlockState())
				.translateY(39 / 16.f)
				.rotateZDegrees(180)
				.rotateXDegrees((float) (handleAngle - Math.toRadians(90 - 32.5)))
				.translateY(-34 / 16.f)
				.scale(firstPerson ? 0 : 1)
				.light(light)
				.overlay(overlay)
				.renderInto(poseStack, buffer);

		CachedBuffers.partial(HANDCAR_HANDLE_FIRST_PERSON, Blocks.AIR.defaultBlockState())
				.translateY(39f / 16.f)
				.rotateZDegrees(180)
				.rotateXDegrees((float) (handleAngle - Math.toRadians(90 - 32.5)))
				.translateY(-34 / 16.f)
				.scale(firstPerson ? 0 : 1)
				.light(light)
				.overlay(overlay)
				.renderInto(poseStack, buffer);

		CachedBuffers.partial(HANDCAR_COUPLING, Blocks.AIR.defaultBlockState())
				.translate(coupling_pos)
				.rotateXDegrees((float) -(couplingAngle - Mth.HALF_PI))
				.light(light)
				.overlay(overlay)
				.renderInto(poseStack, buffer);

		CachedBuffers.partial(HANDCAR_LARGE_COG, Blocks.AIR.defaultBlockState())
				.translate(-8 / 16f, 12 / 16f, -3.5 / 16f)
				.rotateXDegrees((-wheelAngle / 2f) + 22.5f)
				.rotateZDegrees(90)
				.translate(0, -7 / 16f, 0)
				.light(light)
				.overlay(overlay)
				.renderInto(poseStack, buffer);

		CachedBuffers.partial(HANDCAR_SMALL_COG, Blocks.AIR.defaultBlockState())
				.translate(-8 / 16f, 12 / 16f, -1)
				.rotateXDegrees(wheelAngle)
				.rotateZDegrees(90)
				.translate(0, -7 / 16f, 0)
				.light(light)
				.overlay(overlay)
				.renderInto(poseStack, buffer);

		SuperByteBuffer wheels = CachedBuffers.partial(NARROW_SCOTCH_WHEELS, Blocks.AIR.defaultBlockState());
		for (int side : Iterate.positiveAndNegative) {

			wheels
					.translate(0, 12 / 16f, side)
					.rotateXDegrees(wheelAngle)
					.translate(0, -12 / 16f, 0)
					.light(light)
					.overlay(overlay)
					.renderInto(poseStack, buffer);
		}

	}
}
