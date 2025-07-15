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

import static com.railwayteam.railways.registry.CRBlockPartials.LARGE_CREATE_STYLED_0_4_0_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.LARGE_CREATE_STYLED_0_4_0_PISTON;

public class LargeCreateStyled040Display implements BogeyDisplay {
    private final Affine<?> frame;
    private final Affine<?> piston;
    private final Affine<?>[] wheels;
    private final Affine<?>[] pins;
    private final Affine<?>[] secondaryShafts = new Affine<?>[2];
    private final Affine<?>[] middleShafts = new Affine<?>[2];

    public LargeCreateStyled040Display(ElementProvider<?> prov) {
        frame = prov.create(LARGE_CREATE_STYLED_0_4_0_FRAME);
        piston = prov.create(LARGE_CREATE_STYLED_0_4_0_PISTON);
        wheels = prov.create(AllPartialModels.LARGE_BOGEY_WHEELS, 2);
        pins = prov.create(AllPartialModels.BOGEY_PIN, 2);
        prov.create(AllPartialModels.SHAFT, secondaryShafts, middleShafts);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle) {
        for (int side : Iterate.positiveAndNegative) {
            secondaryShafts[(side + 1) / 2]
                .translate(-.5f, .25f, -.5f + side * 1.87)
                .center()
                .rotateTo(Direction.UP, Direction.EAST)
                .rotateYDegrees(wheelAngle)
                .uncenter();

            middleShafts[(side + 1) / 2]
                .translate(-.5f, .25f, -.5f + side * 1.2)
                .center()
                .rotateTo(Direction.UP, Direction.SOUTH)
                .rotateZDegrees(wheelAngle)
                .uncenter();
        }

        frame.self();

        piston.translate(0, 0, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)));

        for (int side : Iterate.positiveAndNegative) {
            wheels[(side + 1) / 2]
                .translate(0, 1, side * .8732)
                .rotateXDegrees(wheelAngle);

            pins[(side + 1) / 2]
                .translate(0, 1, side * .8732)
                .rotateXDegrees(wheelAngle)
                .translate(0, 1 / 4f, 0)
                .rotateXDegrees(-wheelAngle);
        }
    }
}
