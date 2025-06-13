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

package com.railwayteam.railways.content.custom_bogeys.renderer.wide;

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

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class WideComicallyLargeScotchYokeBogeyVisual implements BogeyVisual {
    private final TransformedInstance wheel;
    private final TransformedInstance frame;
    private final TransformedInstance pins;
    private final TransformedInstance pistons;
    private final TransformedInstance[] primaryShafts = new TransformedInstance[2];
    private final TransformedInstance[] secondaryShafts = new TransformedInstance[4];

    public WideComicallyLargeScotchYokeBogeyVisual(VisualizationContext ctx, float partialTick, boolean inContraption) {
        wheel = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(WIDE_COMICALLY_LARGE_WHEELS))
                .createInstance();
        frame = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(WIDE_COMICALLY_LARGE_FRAME))
                .createInstance();
        pins = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(WIDE_COMICALLY_LARGE_PINS))
                .createInstance();
        pistons = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(WIDE_COMICALLY_LARGE_PISTONS))
                .createInstance();
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
                    .translate(-.5, 4 / 16., i * -1)
                    .center()
                    .rotateZDegrees(wheelAngle)
                    .uncenter()
                    .setChanged();
        }

        for (int i : Iterate.zeroAndOne) {
            for (int side : Iterate.zeroAndOne) {
                secondaryShafts[i + (side * 2)]
                        .translate(-1 + side, 4 / 16., (10 / 16.) + i * -(36 / 16.))
                        .center()
                        .rotateXDegrees(wheelAngle)
                        .uncenter()
                        .setChanged();
            }
        }
        frame.translate(0, 4 / 16., 0)
                .setChanged();

        pistons.translate(0, 1.5, (1 / 4f + (5 / 16.)) * Math.sin(AngleHelper.rad(wheelAngle)))
                .setChanged()
        ;
        wheel.translate(0, 1.5, 0)
                .rotateXDegrees(wheelAngle)
                .translate(0, 0, 0)
                .setChanged();
        pins.translate(0,1.5,0)
                .rotateXDegrees(wheelAngle)
                .translate(0,   1 / 4f + (5 / 16.), 0)
                .rotateXDegrees(-wheelAngle)
                .setChanged();
    }


    @Override
    public void hide() {
        wheel.setZeroTransform().setChanged();
        frame.setZeroTransform().setChanged();
        pins.setZeroTransform().setChanged();
        pistons.setZeroTransform().setChanged();
        for (TransformedInstance shaft : primaryShafts)
            shaft.setZeroTransform().setChanged();
        for (TransformedInstance shaft : secondaryShafts)
            shaft.setZeroTransform().setChanged();
    }

    @Override
    public void updateLight(int packedLight) {
        wheel.light(packedLight);
        frame.light(packedLight);
        pins.light(packedLight);
        pistons.light(packedLight);
        for (TransformedInstance shaft : primaryShafts)
            shaft.light(packedLight);
        for (TransformedInstance shaft : secondaryShafts)
            shaft.light(packedLight);
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(wheel);
        consumer.accept(frame);
        consumer.accept(pins);
        consumer.accept(pistons);
        for (TransformedInstance shaft : primaryShafts)
            consumer.accept(shaft);
        for (TransformedInstance shaft : secondaryShafts)
            consumer.accept(shaft);
    }

    @Override
    public void delete() {
        wheel.delete();
        frame.delete();
        pins.delete();
        pistons.delete();
        for (TransformedInstance shaft : primaryShafts)
            shaft.delete();
        for (TransformedInstance shaft : secondaryShafts)
            shaft.delete();
    }
}
