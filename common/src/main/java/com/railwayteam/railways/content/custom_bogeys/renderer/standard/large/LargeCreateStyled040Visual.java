/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.railwayteam.railways.registry.CRBlockPartials.LARGE_CREATE_STYLED_0_4_0_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.LARGE_CREATE_STYLED_0_4_0_PISTON;
import static com.simibubi.create.AllPartialModels.BOGEY_PIN;
import static com.simibubi.create.AllPartialModels.LARGE_BOGEY_WHEELS;
import static com.simibubi.create.AllPartialModels.SHAFT;

public class LargeCreateStyled040Visual implements BogeyVisual {
	private final TransformedInstance frame;
	private final TransformedInstance piston;
	private final TransformedInstance[] wheels = new TransformedInstance[2];
	private final TransformedInstance[] pins = new TransformedInstance[2];
	private final TransformedInstance[] primaryShafts = new TransformedInstance[2];
	private final TransformedInstance[] secondaryShafts = new TransformedInstance[2];

	public LargeCreateStyled040Visual(VisualizationContext ctx, float partialTick, boolean inContraption) {

		frame = ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(LARGE_CREATE_STYLED_0_4_0_FRAME))
				.createInstance();
		piston = ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(LARGE_CREATE_STYLED_0_4_0_PISTON))
				.createInstance();
		ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(LARGE_BOGEY_WHEELS))
				.createInstances(wheels);
		ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(BOGEY_PIN))
				.createInstances(pins);
		ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(SHAFT))
				.createInstances(primaryShafts);
		ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(SHAFT))
				.createInstances(secondaryShafts);

	}

	@Override
	public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {
		for (int side : Iterate.positiveAndNegative) {
			primaryShafts[(side + 1) / 2]
					.translate(-.5f, .25f, -.5f + side * 1.87)
					.center()
					.rotateXDegrees(wheelAngle)
					.uncenter()
					.setChanged();

			secondaryShafts[(side + 1) / 2]
					.translate(-.5f, .25f, -.5f + side * 1.2)
					.center()
					.rotateZDegrees(wheelAngle)
					.uncenter()
					.setChanged();
		}

		frame.setChanged();
		piston.translate(0, 0, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)))
				.setChanged();
		for (int side : Iterate.positiveAndNegative) {
			wheels[(side + 1) / 2]
					.translate(0, 13 / 16f, -1.5f + side * 1.5)
					.rotateXDegrees(wheelAngle)
					.translate(0, -13 / 16f, 0)
					.setChanged();

			pins[(side + 1) / 2].translate(0, 1, side * .8732)
					.rotateXDegrees(wheelAngle)
					.translate(0, 1 / 4f, 0)
					.rotateXDegrees(-wheelAngle)
					.setChanged();
		}

	}

	@Override
	public void hide() {
		for (TransformedInstance wheel : wheels)
			wheel.setZeroTransform().setChanged();
		frame.setZeroTransform().setChanged();
		piston.setZeroTransform().setChanged();

		for (TransformedInstance shaft : primaryShafts)
			shaft.setZeroTransform().setChanged();

		for (TransformedInstance shaft : secondaryShafts)
			shaft.setZeroTransform().setChanged();

		for (TransformedInstance pin : pins)
			pin.setZeroTransform().setChanged();
	}

	@Override
	public void updateLight(int packedLight) {
		for (TransformedInstance wheel : wheels)
			wheel.light(packedLight);
		frame.light(packedLight);
		piston.light(packedLight);

		for (TransformedInstance shaft : primaryShafts)
			shaft.light(packedLight);

		for (TransformedInstance shaft : secondaryShafts)
			shaft.light(packedLight);
		for (TransformedInstance pin : pins)
			pin.light(packedLight);
	}

	@Override
	public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
		for (TransformedInstance wheel : wheels)
			consumer.accept(wheel);
		consumer.accept(frame);
		consumer.accept(piston);

		for (TransformedInstance shaft : primaryShafts)
			consumer.accept(shaft);


		for (TransformedInstance shaft : primaryShafts)
			consumer.accept(shaft);

		for (TransformedInstance pin : pins)
			consumer.accept(pin);

	}

	@Override
	public void delete() {
		for (TransformedInstance wheel : wheels)
			wheel.delete();
		frame.delete();
		piston.delete();
		for (TransformedInstance shaft : primaryShafts)
			shaft.delete();

		for (TransformedInstance shaft : secondaryShafts)
			shaft.delete();

		for (TransformedInstance pin : pins)
			pin.delete();
	}
}