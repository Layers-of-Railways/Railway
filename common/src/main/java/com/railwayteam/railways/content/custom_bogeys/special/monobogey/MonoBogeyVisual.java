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

package com.railwayteam.railways.content.custom_bogeys.special.monobogey;

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
import static com.simibubi.create.content.trains.entity.CarriageBogey.UPSIDE_DOWN_KEY;

public class MonoBogeyVisual implements BogeyVisual {
    private final TransformedInstance frame;
    private final TransformedInstance[] wheels = new TransformedInstance[4];
    private final TransformedInstance[] shafts = new TransformedInstance[4];
    
    private final boolean inContraption;
    
    public MonoBogeyVisual(VisualizationContext ctx, float partialTick, boolean inContraption) {
        this.inContraption = inContraption;
        
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
        boolean upsideDown = bogeyData.getBoolean(UPSIDE_DOWN_KEY);
        boolean specialUpsideDown = !inContraption && upsideDown; // tile entity renderer needs special handling

        frame.rotateYDegrees(specialUpsideDown ? 180 : 0)
                .translateY(specialUpsideDown ? -3 : 0)
                .setChanged();
        
        for (boolean left : Iterate.trueAndFalse) {
            for (int front : Iterate.positiveAndNegative) {
                int i = (left ? 1 : 0) + (front + 1);
                
                TransformedInstance shaft = shafts[i];
                shaft.translate(left ? -21 / 16f : 5 / 16f, specialUpsideDown ? 32 / 16f : 0 / 16f, -.5f + front * 8 / 16f)
                        .center()
                        .rotateZDegrees(left ? wheelAngle : -wheelAngle)
                        .uncenter()
                        .setChanged();

                TransformedInstance wheel = wheels[i];
                wheel.translate(left ? -13 / 16f : 13 / 16f, specialUpsideDown ? 32 / 16f : 0 / 16f, front * 16 / 16f)
                        .rotateYDegrees(left ? wheelAngle : -wheelAngle)
                        .translate(13 / 16f, 0, 16 / 16f)
                        .setChanged();
            }
        }
    }

    @Override
    public void hide() {
        for (int i = 0; i <= 3; i++) {
            wheels[i].setZeroTransform().setChanged();
            shafts[i].setZeroTransform().setChanged();
        }
    }

    @Override
    public void updateLight(int packedLight) {
        frame.light(packedLight);
        for (int i = 0; i <= 3; i++) {
            wheels[i].light(packedLight);
            shafts[i].light(packedLight);
        }
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(frame);
        for (int i = 0; i <= 3; i++) {
            consumer.accept(wheels[i]);
            consumer.accept(shafts[i]);
        }
    }

    @Override
    public void delete() {
        frame.delete();
        for (int i = 0; i <= 3; i++) {
            wheels[i].delete();
            shafts[i].delete();
        }
    }
}