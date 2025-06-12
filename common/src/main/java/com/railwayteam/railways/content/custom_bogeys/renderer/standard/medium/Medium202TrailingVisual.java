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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium;

import com.mojang.blaze3d.vertex.PoseStack;
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
import static com.simibubi.create.AllPartialModels.SHAFT;

public class Medium202TrailingVisual  implements BogeyVisual {

    private final TransformedInstance wheel;
    private final TransformedInstance frame;
    private final TransformedInstance[] shafts = new TransformedInstance[2];


    public Medium202TrailingVisual(VisualizationContext ctx, float partialTick, boolean inContraption)
    {

        frame = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(MEDIUM_2_0_2_TRAILING_FRAME))
                .createInstance();

        wheel = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(MEDIUM_SHARED_WHEELS))
                .createInstance();
        ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(SHAFT))
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
        wheel
            .translate(0, 13 / 16f, 0)
            .rotateXDegrees(wheelAngle)
            .translate(0, -13 / 16f, 0)
            .setChanged();

    }

    @Override
    public void hide() {
        wheel.setZeroTransform().setChanged();
        frame.setZeroTransform().setChanged();
        for (TransformedInstance shaft : shafts)
            shaft.setZeroTransform().setChanged();
    }

    @Override
    public void updateLight(int packedLight) {
        wheel.light(packedLight);
        frame.light(packedLight);
        for (TransformedInstance shaft : shafts)
            shaft.light(packedLight);
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(wheel);
        consumer.accept(frame);
        for (TransformedInstance shaft : shafts)
            consumer.accept(shaft);
    }

    @Override
    public void delete() {
        wheel.delete();
        frame.delete();
        for (TransformedInstance shaft : shafts)
            shaft.delete();
    }
}