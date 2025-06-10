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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.createmod.catnip.data.Iterate;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.railwayteam.railways.registry.CRBlockPartials.MONOBOGEY_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.MONOBOGEY_WHEEL;

public class FreightBogeyVisual implements BogeyVisual {

    private final TransformedInstance[] wheels = new TransformedInstance[2];
    private final TransformedInstance frame;
    private final TransformedInstance[] shafts = new TransformedInstance[2];

    public FreightBogeyVisual(VisualizationContext ctx, float partialTick, boolean inContraption)
    {

        frame = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(MONOBOGEY_FRAME))
                .createInstance();

        ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(MONOBOGEY_WHEEL))
                .createInstances(wheels);
        ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.SHAFT))
                .createInstances(shafts);

    }
    @Override
    public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {
        for (int i : Iterate.zeroAndOne) {
            shafts[i]
                    .translate(-.5f, .25f, i * -1)
                    .center()
                    .rotateZDegrees(wheelAngle)
                    .uncenter()
                    .setChanged();
        }

        frame.setChanged();
        for (int side = -1; side < 2; side++) {
            wheels[side + 1]
                    .translate(0, 12 / 16f, side)
                    .rotateXDegrees(wheelAngle)
                    .translate(0, -12 / 16f, 0)
                    .setChanged();
        }
    }

    @Override
    public void hide() {
        for (TransformedInstance wheel : wheels)
            wheel.setZeroTransform().setChanged();
        frame.setZeroTransform().setChanged();
        for (TransformedInstance shaft : shafts)
            shaft.setZeroTransform().setChanged();
    }

    @Override
    public void updateLight(int packedLight) {
        for (TransformedInstance wheel : wheels)
            wheel.light(packedLight);
        frame.light(packedLight);
        for (TransformedInstance shaft : shafts)
            shaft.light(packedLight);
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        for (TransformedInstance wheel : wheels)
            consumer.accept(wheel);
        consumer.accept(frame);
        for (TransformedInstance shaft : shafts)
            consumer.accept(shaft);
    }

    @Override
    public void delete() {
        for (TransformedInstance wheel : wheels)
            wheel.delete();
        frame.delete();
        for (TransformedInstance shaft : shafts)
            shaft.delete();
    }
}
