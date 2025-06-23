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

package com.railwayteam.railways.content.custom_bogeys.renderer.narrow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
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

import static com.railwayteam.railways.registry.CRBlockPartials.NARROW_DOUBLE_SCOTCH_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.NARROW_DOUBLE_SCOTCH_PISTONS;
import static com.railwayteam.railways.registry.CRBlockPartials.NARROW_SCOTCH_WHEELS;
import static com.railwayteam.railways.registry.CRBlockPartials.NARROW_SCOTCH_WHEEL_PINS;

public class NarrowDoubleScotchYokeBogeyVisual implements BogeyVisual {
	private final TransformedInstance frame;
	private final TransformedInstance pistons;
	private final TransformedInstance[] wheels = new TransformedInstance[2];
	private final TransformedInstance[] wheelPins = new TransformedInstance[2];
	private final TransformedInstance[] primaryShafts = new TransformedInstance[2];
	private final TransformedInstance[] secondaryShafts = new TransformedInstance[2];

	public NarrowDoubleScotchYokeBogeyVisual(VisualizationContext ctx, float partialTick, boolean inContraption) {
		frame = ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_DOUBLE_SCOTCH_FRAME))
				.createInstance();
		pistons = ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_DOUBLE_SCOTCH_PISTONS))
				.createInstance();
		ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_SCOTCH_WHEELS))
				.createInstances(wheels);
		ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_SCOTCH_WHEEL_PINS))
				.createInstances(wheelPins);
		ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.SHAFT))
				.createInstances(primaryShafts);
		ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.SHAFT))
				.createInstances(secondaryShafts);
	}

	@Override
	public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {
		for (int i : Iterate.zeroAndOne) {
			primaryShafts[i]
					.translate(-.5, 1 / 16., (7 / 16.) + i * -(30 / 16.))
					.center()
					.rotateZDegrees(wheelAngle)
					.uncenter()
					.setChanged();
		}

		for (int i : Iterate.zeroAndOne) {
			secondaryShafts[i]
					.translate(-.5f, 6 / 16., (18 / 16.) + i * -(52 / 16.))
					.center()
					.rotateXDegrees(wheelAngle)
					.uncenter()
					.setChanged();
		}
		frame.translate(0, 5 / 16f, 0)
				.setChanged();
		pistons.translate(0, 14 / 16f, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)))
				.setChanged();


		for (int side : Iterate.positiveAndNegative) {
			wheels[(side + 1) / 2]
					.translate(0, 14 / 16., side * (12 / 16.))
					.rotateXDegrees(wheelAngle)
					.translate(0, 0, 0)
					.setChanged();

			wheelPins[(side + 1) / 2]
					.translate(0, 14 / 16., side * (12 / 16.))
					.rotateXDegrees(wheelAngle)
					.translate(0, 1 / 4f, 0)
					.rotateXDegrees(-wheelAngle)
					.setChanged();
		}
	}


	@Override
	public void hide() {
		frame.setZeroTransform().setChanged();
		pistons.setZeroTransform().setChanged();
		for (TransformedInstance wheel : wheels)
			wheel.setZeroTransform().setChanged();
		for (TransformedInstance pin : wheelPins)
			pin.setZeroTransform().setChanged();
		for (TransformedInstance shaft : primaryShafts)
			shaft.setZeroTransform().setChanged();
		for (TransformedInstance shaft : secondaryShafts)
			shaft.setZeroTransform().setChanged();
	}

	@Override
	public void updateLight(int packedLight) {
		frame.light(packedLight);
		pistons.light(packedLight);
		for (TransformedInstance wheel : wheels)
			wheel.light(packedLight);
		for (TransformedInstance pin : wheelPins)
			pin.light(packedLight);
		for (TransformedInstance shaft : primaryShafts)
			shaft.light(packedLight);
		for (TransformedInstance shaft : secondaryShafts)
			shaft.light(packedLight);
	}

	@Override
	public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
		consumer.accept(frame);
		consumer.accept(pistons);
		for (TransformedInstance shaft : primaryShafts)
			consumer.accept(shaft);
		for (TransformedInstance shaft : secondaryShafts)
			consumer.accept(shaft);
		for (TransformedInstance wheel : wheels)
			consumer.accept(wheel);
		for (TransformedInstance pin : wheelPins)
			consumer.accept(pin);
	}

	@Override
	public void delete() {
		frame.delete();
		pistons.delete();
		for (TransformedInstance wheel : wheels)
			wheel.delete();
		for (TransformedInstance pin : wheelPins)
			pin.delete();
		for (TransformedInstance shaft : primaryShafts)
			shaft.delete();
		for (TransformedInstance shaft : secondaryShafts)
			shaft.delete();
	}
}