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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.large;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class LargeCreateStyled0120Renderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager,  LARGE_CREATE_STYLED_0_12_0_FRAME, LARGE_CREATE_STYLED_0_12_0_PISTON);
        createModelInstance(materialManager, LC_STYLE_FULL_BLIND_WHEELS, 2);
        createModelInstance(materialManager, LC_STYLE_SEMI_BLIND_WHEELS, 2);
        createModelInstance(materialManager, AllPartialModels.LARGE_BOGEY_WHEELS, 2);
        createModelInstance(materialManager, AllPartialModels.BOGEY_PIN, 6);
        createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X), 2);
        createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), 6);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.LARGE;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;

        BogeyModelData[] secondaryShafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X), ms, inInstancedContraption, 2);
        BogeyModelData[] middleShafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), ms, inInstancedContraption, 6);

        for (int side : Iterate.positiveAndNegative) {
            BogeyModelData shaft = secondaryShafts[(side + 1) / 2];
            shaft.translate(-.5, .25, -.5f + side * 5.364)
                    .centre()
                    .rotateX(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
        }

        for (int side = -3; side < 4; side++) {
            if (side == 0) continue;
            int shaftNum = side > 0 ? side + 2 : side + 3;
            BogeyModelData shaft = middleShafts[shaftNum];
            shaft.translate(-.5f, .25f, -.5f + side * -1.7)
                    .centre()
                    .rotateZ(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
        }

        getTransform(LARGE_CREATE_STYLED_0_12_0_FRAME, ms, inInstancedContraption)
                .render(ms, light, vb);

        getTransform(LARGE_CREATE_STYLED_0_12_0_PISTON, ms, inInstancedContraption)
                .translate(0, 0, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)))
                .render(ms, light, vb);

        BogeyModelData[] fullBlindWheels = getTransform(LC_STYLE_FULL_BLIND_WHEELS, ms, inInstancedContraption, 2);
        BogeyModelData[] semiBlindWheels = getTransform(LC_STYLE_SEMI_BLIND_WHEELS, ms, inInstancedContraption, 2);
        BogeyModelData[] wheels = getTransform(AllPartialModels.LARGE_BOGEY_WHEELS, ms, inInstancedContraption, 2);
        BogeyModelData[] pins = getTransform(AllPartialModels.BOGEY_PIN, ms, inInstancedContraption, 6);

        if (!inInstancedContraption)
            ms.pushPose();

        for (int side : Iterate.positiveAndNegative) {
            BogeyModelData fullBlindWheel = fullBlindWheels[(side + 1) / 2];
            fullBlindWheel.translate(0, 1, side * .8733)
                    .rotateX(wheelAngle)
                    .translate(0, -1, 0)
                    .render(ms, light, vb);

            BogeyModelData semiBlindWheel = semiBlindWheels[(side + 1) / 2];
            semiBlindWheel.translate(0, 1, side * 2.62)
                    .rotateX(wheelAngle)
                    .translate(0, -1, 0)
                    .render(ms, light, vb);

            BogeyModelData wheel = wheels[(side + 1) / 2];
            wheel.translate(0, 1, side * 4.3665)
                    .rotateX(wheelAngle)
                    .render(ms, light, vb);
        }

        for (int side = -3; side < 3; side++) {
            BogeyModelData pin = pins[side + 3];
            pin.translate(0, 1, .8733f + side * 1.74657)
                    .rotateX(wheelAngle)
                    .translate(0, 1 / 4f, 0)
                    .rotateX(-wheelAngle)
                    .render(ms, light, vb);
        }

        if (!inInstancedContraption)
            ms.popPose();
    }
}
