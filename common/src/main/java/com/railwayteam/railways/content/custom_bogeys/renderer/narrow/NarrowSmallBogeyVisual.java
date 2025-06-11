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
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class NarrowSmallBogeyVisual implements BogeyVisual {
    private final TransformedInstance frame;
    private final TransformedInstance[] shafts = new TransformedInstance[2];
    private final TransformedInstance[] wheels = new TransformedInstance[4];

    public NarrowSmallBogeyVisual(VisualizationContext ctx, float partialTick, boolean inContraption) {
        ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_WHEELS))
                .createInstances(wheels);
        frame = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_FRAME))
                .createInstance();
        ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.SHAFT_HALF))
                .createInstances(shafts);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {
        for (int i : Iterate.zeroAndOne) {
            shafts[i]
                    .translate(-.5, 1 / 16., -(18 / 16.) + (i * 12 / 16.))
                    .center()
                    .rotateZDegrees(wheelAngle)
                    .uncenter()
                    .setChanged();
        }
        frame.translate(0, 5 / 16f, 0)
                .setChanged();
        for (int side : Iterate.positiveAndNegative) {
            wheels[(side +1)/2]
                    .translate(0, 11 / 16., side * (10 / 16.))
                    .rotateXDegrees(wheelAngle)
                    .uncenter()
                    .setChanged();
        }

    }


    @Override
    public void hide() {
        frame.setZeroTransform().setChanged();
        for (TransformedInstance shaft : shafts)
            shaft.setZeroTransform().setChanged();
        for (TransformedInstance wheel : wheels)
            wheel.setZeroTransform().setChanged();
    }

    @Override
    public void updateLight(int packedLight) {
        frame.light(packedLight);
        for (TransformedInstance shaft : shafts)
            shaft.light(packedLight);
        for (TransformedInstance wheel : wheels)
            wheel.light(packedLight);
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(frame);
        for (TransformedInstance shaft : shafts)
            consumer.accept(shaft);
        for (TransformedInstance wheel : wheels)
            consumer.accept(wheel);
    }

    @Override
    public void delete() {
        frame.delete();
        for (TransformedInstance shaft : shafts)
            shaft.delete();
        for (TransformedInstance wheel : wheels)
            wheel.delete();
    }
}
