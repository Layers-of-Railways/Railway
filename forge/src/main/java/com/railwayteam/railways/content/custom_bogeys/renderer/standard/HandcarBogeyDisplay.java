/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.ElementProvider;
import com.railwayteam.railways.content.handcar.ik.DoubleArmIK;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class HandcarBogeyDisplay implements BogeyDisplay.EntityAware {
    private final Affine<?>[] wheels;
    private final Affine<?> coupling;
    private final Affine<?> frame;
    private final Affine<?> handle;
    private final Affine<?> handleFirstPerson;
    private final Affine<?> largeCog;
    private final Affine<?> smallCog;

    private boolean firstPerson = false;

    public HandcarBogeyDisplay(ElementProvider<?> prov) {
        wheels = prov.create(HANDCAR_WHEELS, 2);
        coupling = prov.create(HANDCAR_COUPLING);
        frame = prov.create(HANDCAR_FRAME);
        handle = prov.create(HANDCAR_HANDLE);
        handleFirstPerson = prov.create(HANDCAR_HANDLE_FIRST_PERSON);
        largeCog = prov.create(HANDCAR_LARGE_COG);
        smallCog = prov.create(HANDCAR_SMALL_COG);
    }

    @Override
    public void entityUpdate(CarriageContraptionEntity cce) {
        firstPerson = false;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !mc.options.getCameraType().isFirstPerson()) return;

        firstPerson = player.getRootVehicle() == cce;
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle) {
        wheelAngle *= 2;

        frame.translate(0, 5 / 16f, 0);

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

        handle
                .translateY(39 / 16f)
                .rotateZDegrees(180)
                .rotateX((float) (handleAngle - Math.toRadians(90-32.5)))
                .translateY(-34 / 16f)
                .scale(firstPerson ? 0 : 1);
        handleFirstPerson
                .translateY(39 / 16f)
                .rotateZDegrees(180)
                .rotateX((float) (handleAngle - Math.toRadians(90-32.5)))
                .translateY(-34 / 16f)
                .scale(firstPerson ? 1 : 0);

        coupling
                .translate(coupling_pos)
                .rotateX((float) -(couplingAngle - Mth.HALF_PI));

        largeCog
                .translate(-8 / 16f, 12 / 16f, -3.5 / 16f)
                .rotateXDegrees((-wheelAngle / 2) + 22.5f)
                .rotateZDegrees(90)
                .translate(0, -7 / 16f, 0);

        smallCog
                .translate(-8 / 16f, 12 / 16f, -1)
                .rotateXDegrees(wheelAngle)
                .rotateZDegrees(90)
                .translate(0, -7 / 16f, 0);

        for (int side : Iterate.positiveAndNegative) {
            wheels[(side + 1) / 2]
                    .translate(0, 12 / 16f, side)
                    .rotateXDegrees(wheelAngle)
                    .translate(0, -12 / 16f, 0);
        }
    }
}
