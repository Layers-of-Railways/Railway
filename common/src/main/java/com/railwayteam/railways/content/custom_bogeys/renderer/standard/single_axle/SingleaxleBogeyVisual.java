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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.single_axle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.simibubi.create.AllPartialModels.SMALL_BOGEY_WHEELS;

public class SingleaxleBogeyVisual implements BogeyVisual {
	private final TransformedInstance wheel;
	private final TransformedInstance frame;

	private final boolean inContraption;

	public SingleaxleBogeyVisual(VisualizationContext ctx, float partialTick, boolean inContraption) {
		wheel = ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(SMALL_BOGEY_WHEELS))
				.createInstance();
		frame = ctx.instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.SHAFT))
				.createInstance();
		this.inContraption = inContraption;

	}

	@Override
	public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {
		wheel.translate(0, 12 / 16f, 0)
				.rotateXDegrees(wheelAngle)
				.setChanged();
		frame.setChanged();
	}

	@Override
	public void hide() {
		wheel.setZeroTransform().setChanged();
		frame.setZeroTransform().setChanged();
	}

	@Override
	public void updateLight(int packedLight) {
		wheel.light(packedLight);
		frame.light(packedLight);
	}

	@Override
	public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
		consumer.accept(wheel);
		consumer.accept(frame);
	}

	@Override
	public void delete() {
		wheel.delete();
		frame.delete();
	}
}
