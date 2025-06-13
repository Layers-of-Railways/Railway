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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.handcar.ik.DoubleArmIK;
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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.railwayteam.railways.registry.CRBlockPartials.*;
import static com.railwayteam.railways.registry.CRBlockPartials.NARROW_SCOTCH_PISTONS;

public class HandcarBogeyVisual implements BogeyVisual {

    private final TransformedInstance coupling;
    private final TransformedInstance frame;
    private final TransformedInstance handleFirstPerson;
    private final TransformedInstance handle;
    private final TransformedInstance largeCog;
    private final TransformedInstance smallCog;
    private final TransformedInstance[] wheels = new TransformedInstance[2];
    public HandcarBogeyVisual(VisualizationContext ctx, float partialTick, boolean inContraption) {
        coupling = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_SCOTCH_WHEELS))
                .createInstance();
        frame = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_SCOTCH_FRAME))
                .createInstance();
        handleFirstPerson = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_SCOTCH_WHEEL_PINS))
                .createInstance();
        handle = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_SCOTCH_PISTONS))
                .createInstance();
        largeCog = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_SCOTCH_PISTONS))
                .createInstance();
        smallCog = ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(NARROW_SCOTCH_PISTONS))
                .createInstance();

        ctx.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.SHAFT))
                .createInstances(wheels);
    }


    @Override
    public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {
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
        Vec2 couplingVec2 = new Vec2((float)coupling_pos.z, (float)coupling_pos.y);

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


        frame.translate(0, 5 / 16f, 0).setChanged();
        handle.translateY(39 / 16.f)
                .rotateZDegrees(180)
                .rotateXDegrees((float)(handleAngle - Math.toRadians(90-32.5)))
                .translateY(-34 / 16.f)
                .setChanged()
        ;
        handleFirstPerson.translateY(39f / 16.f)
                .rotateZDegrees(180)
                .rotateXDegrees((float)(handleAngle - Math.toRadians(90-32.5)))
                .translateY(-34 / 16.f)
                .setChanged();

        coupling.translate(coupling_pos)
                .rotateXDegrees((float)-(couplingAngle - Mth.HALF_PI))
                .setChanged();

        largeCog.translate(-8 / 16f, 12 / 16f, -3.5 / 16f)
                .rotateXDegrees((-wheelAngle / 2f) + 22.5f)
                .rotateZDegrees(90)
                .translate(0, -7 / 16f, 0)
                .setChanged();

        smallCog.translate(-8 / 16f, 12 / 16f, -1)
                .rotateXDegrees(wheelAngle)
                .rotateZDegrees(90)
                .translate(0, -7 / 16f, 0)
                .setChanged();

        for (int side : Iterate.positiveAndNegative)
        {
            wheels[(side+1)/2]
                    .translate(0, 12 / 16f, side)
                    .rotateXDegrees(wheelAngle)
                    .translate(0, -12 / 16f, 0)
                    .setChanged();


        }
    }

    @Override
    public void hide() {
        frame.setZeroTransform().setChanged();
        handle.setZeroTransform().setChanged();
        handleFirstPerson.setZeroTransform().setChanged();
        coupling.setZeroTransform().setChanged();
        largeCog.setZeroTransform().setChanged();
        smallCog.setZeroTransform().setChanged();
        for (TransformedInstance wheel : wheels)
            wheel.setZeroTransform().setChanged();
    }

    @Override
    public void updateLight(int packedLight) {
        for (TransformedInstance wheel : wheels)
            wheel.light(packedLight);
        frame.light(packedLight);
        handle.light(packedLight);
        handleFirstPerson.light(packedLight);
        coupling.light(packedLight);
        largeCog.light(packedLight);
        smallCog.light(packedLight);

    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        for (TransformedInstance wheel : wheels)
            consumer.accept(wheel);
        consumer.accept(frame);
        consumer.accept(handle);
        consumer.accept(handleFirstPerson);
        consumer.accept(coupling);
        consumer.accept(largeCog);
        consumer.accept(smallCog);
    }

    @Override
    public void delete() {
        for (TransformedInstance wheel : wheels)
            wheel.delete();
        frame.delete();
        handle.delete();
        handleFirstPerson.delete();
        coupling.delete();
        largeCog.delete();
        smallCog.delete();

    }
}
