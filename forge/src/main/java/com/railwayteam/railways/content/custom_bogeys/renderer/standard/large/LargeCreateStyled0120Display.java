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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.large;

import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.ElementProvider;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class LargeCreateStyled0120Display implements BogeyDisplay {
    private final Affine<?> frame;
    private final Affine<?> piston;
    private final Affine<?>[] fullBlindWheels;
    private final Affine<?>[] semiBlindWheels;
    private final Affine<?>[] wheels;
    private final Affine<?>[] pins;
    private final Affine<?>[] secondaryShafts = new Affine<?>[2];
    private final Affine<?>[] middleShafts = new Affine<?>[6];

    public LargeCreateStyled0120Display(ElementProvider<?> prov) {
        frame = prov.create(LARGE_CREATE_STYLED_0_12_0_FRAME);
        piston = prov.create(LARGE_CREATE_STYLED_0_12_0_PISTON);
        fullBlindWheels = prov.create(LC_STYLE_FULL_BLIND_WHEELS, 2);
        semiBlindWheels = prov.create(LC_STYLE_SEMI_BLIND_WHEELS, 2);
        wheels = prov.create(AllPartialModels.LARGE_BOGEY_WHEELS, 2);
        pins = prov.create(AllPartialModels.BOGEY_PIN, 6);
        prov.create(AllPartialModels.SHAFT, secondaryShafts, middleShafts);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle) {
        for (int side : Iterate.positiveAndNegative) {
            secondaryShafts[(side + 1) / 2]
                .translate(-.5, .25, -.5f + side * 5.364)
                .center()
                .rotateTo(Direction.UP, Direction.EAST)
                .rotateYDegrees(wheelAngle)
                .uncenter();
        }

        for (int side = -3; side <= 3; side++) {
            if (side == 0) continue;
            int shaftNum = side > 0 ? side + 2 : side + 3;
            middleShafts[shaftNum]
                .translate(-.5f, .25f, -.5f + side * -1.7)
                .center()
                .rotateTo(Direction.UP, Direction.SOUTH)
                .rotateYDegrees(wheelAngle)
                .uncenter();
        }

        frame.self();

        piston.translate(0, 0, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)));

        for (int side : Iterate.positiveAndNegative) {
            fullBlindWheels[(side + 1) / 2]
                .translate(0, 1, side * .8733)
                .rotateXDegrees(wheelAngle)
                .translate(0, -1, 0);

            semiBlindWheels[(side + 1) / 2]
                .translate(0, 1, side * 2.62)
                .rotateXDegrees(wheelAngle)
                .translate(0, -1, 0);

            wheels[(side + 1) / 2]
                .translate(0, 1, side * 4.3665)
                .rotateXDegrees(wheelAngle);
        }

        for (int side = -3; side < 3; side++) {
            pins[side + 3]
                .translate(0, 1, .8733f + side * 1.74657)
                .rotateXDegrees(wheelAngle)
                .translate(0, 1 / 4f, 0)
                .rotateXDegrees(-wheelAngle);
        }
    }
}
