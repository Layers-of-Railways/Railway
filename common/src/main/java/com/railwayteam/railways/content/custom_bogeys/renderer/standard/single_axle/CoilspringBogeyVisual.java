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
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.railwayteam.railways.registry.CRBlockPartials.COILSPRING_FRAME;
import static com.simibubi.create.AllPartialModels.SMALL_BOGEY_WHEELS;

public class CoilspringBogeyVisual implements BogeyVisual {
    private final TransformedInstance frame;
    private final TransformedInstance wheel;

    private final boolean inContraption;


    public CoilspringBogeyVisual(VisualizationContext ctx, float partialTick, boolean inContraption){
        this.inContraption = inContraption;
        frame = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(COILSPRING_FRAME))
                .createInstance();
        wheel = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(SMALL_BOGEY_WHEELS))
                .createInstance();
    }
    @Override
    public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {

        wheel.translate(0, 12 / 16f, 0)
                .rotateX(wheelAngle)
                .setChanged();
        frame.setChanged();
    }

    @Override
    public void hide() {
        frame.setZeroTransform().setChanged();
        wheel.setZeroTransform().setChanged();
    }

    @Override
    public void updateLight(int packedLight) {
        frame.light(packedLight);
        wheel.light(packedLight);
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(frame);
        consumer.accept(wheel);
    }

    @Override
    public void delete() {
        frame.delete();
        wheel.delete();
    }
}
