/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.handcar.ik.DoubleArmIK;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class HandcarBogeyRenderer extends BogeyRenderer {
    private CarriageBogey carriageBogey;

    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, HANDCAR_WHEELS, 2);
        createModelInstance(materialManager, HANDCAR_COUPLING, HANDCAR_FRAME, HANDCAR_HANDLE_FIRST_PERSON,
                HANDCAR_HANDLE, HANDCAR_LARGE_COG, HANDCAR_SMALL_COG);

        this.carriageBogey = carriageBogey;
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    private boolean isFirstPerson() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (!mc.options.getCameraType().isFirstPerson()) {
            return false;
        }
        if (player != null && player.getRootVehicle() instanceof CarriageContraptionEntity cce) {
            if (carriageBogey == null)
                return true;
            return cce.trainId.equals(carriageBogey.carriage.train.id)
                    && cce.carriageIndex == carriageBogey.carriage.train.carriages.indexOf(carriageBogey.carriage);
        }
        return false;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
//            wheelAngle = AnimationTickHolder.getTicks(true) + AnimationTickHolder.getPartialTicks();
        wheelAngle *= 2;
        boolean inInstancedContraption = vb == null;

        getTransform(HANDCAR_FRAME, ms, inInstancedContraption)
                .translate(0, 5 / 16f, 0)
                .render(ms, light, vb);

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
        Vec2 couplingVec2 = new Vec2((float) coupling_pos.z, (float) coupling_pos.y);

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

        boolean firstPerson = isFirstPerson();

        getTransform(HANDCAR_HANDLE, ms, inInstancedContraption)
                .translateY(39 / 16.)
                .rotateZ(180)
                .rotateXRadians(handleAngle - Math.toRadians(90 - 32.5))
                .translateY(-34 / 16.)
                .scale(firstPerson ? 0 : 1)
                .render(ms, light, vb);
        getTransform(HANDCAR_HANDLE_FIRST_PERSON, ms, inInstancedContraption)
                .translateY(39 / 16.)
                .rotateZ(180)
                .rotateXRadians(handleAngle - Math.toRadians(90 - 32.5))
                .translateY(-34 / 16.)
                .scale(firstPerson ? 1 : 0)
                .render(ms, light, vb);

        getTransform(HANDCAR_COUPLING, ms, inInstancedContraption)
                .translate(coupling_pos)
                .rotateXRadians(-(couplingAngle - Mth.HALF_PI))
                .render(ms, light, vb);

        getTransform(HANDCAR_LARGE_COG, ms, inInstancedContraption)
                .translate(-8 / 16f, 12 / 16f, -3.5 / 16f)
                .rotateX((-wheelAngle / 2) + 22.5)
                .rotateZ(90)
                .translate(0, -7 / 16f, 0)
                .render(ms, light, vb);

        getTransform(HANDCAR_SMALL_COG, ms, inInstancedContraption)
                .translate(-8 / 16f, 12 / 16f, -1)
                .rotateX(wheelAngle)
                .rotateZ(90)
                .translate(0, -7 / 16f, 0)
                .render(ms, light, vb);

        BogeyModelData[] wheels = getTransform(HANDCAR_WHEELS, ms, inInstancedContraption, 2);
        for (int side : Iterate.positiveAndNegative) {
            if (!inInstancedContraption)
                ms.pushPose();
            wheels[(side + 1) / 2]
                    .translate(0, 12 / 16f, side)
                    .rotateX(wheelAngle)
                    .translate(0, -12 / 16f, 0)
                    .render(ms, light, vb);
            if (!inInstancedContraption)
                ms.popPose();
        }
    }
}
