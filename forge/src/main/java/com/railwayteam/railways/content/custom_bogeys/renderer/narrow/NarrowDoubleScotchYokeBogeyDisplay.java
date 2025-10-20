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

package com.railwayteam.railways.content.custom_bogeys.renderer.narrow;

import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.ElementProvider;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class NarrowDoubleScotchYokeBogeyDisplay implements BogeyDisplay {
    private final Affine<?> frame;
    private final Affine<?> pistons;
    private final Affine<?>[] wheels;
    private final Affine<?>[] pins;
    private final Affine<?>[] primaryShafts = new Affine<?>[2];
    private final Affine<?>[] secondaryShafts = new Affine<?>[2];

    public NarrowDoubleScotchYokeBogeyDisplay(ElementProvider<?> prov) {
        frame = prov.create(NARROW_DOUBLE_SCOTCH_FRAME);
        pistons = prov.create(NARROW_DOUBLE_SCOTCH_PISTONS);
        wheels = prov.create(NARROW_SCOTCH_WHEELS, 2);
        pins = prov.create(NARROW_SCOTCH_WHEEL_PINS, 2);
        prov.create(AllPartialModels.SHAFT, primaryShafts, secondaryShafts);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle) {
        for (int i : Iterate.zeroAndOne) {
            primaryShafts[i]
                .translate(-.5, 1 / 16., (7/16.) + i * -(30 / 16.))
                .center()
                .rotateTo(Direction.UP, Direction.SOUTH)
                .rotateYDegrees(wheelAngle)
                .uncenter();
        }

        for (int i : Iterate.zeroAndOne) {
            secondaryShafts[i]
                .translate(-.5f, 6 / 16., (18 / 16.) + i * -(52 / 16.))
                .center()
                .rotateTo(Direction.UP, Direction.EAST)
                .rotateYDegrees(wheelAngle)
                .uncenter();
        }

        frame.translate(0, 5 / 16f, 0);

        pistons.translate(0, 14 / 16f, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)));

        for (int side : Iterate.positiveAndNegative) {
            wheels[(side + 1) / 2]
                .translate(0, 14 / 16., side * (12 / 16.))
                .rotateXDegrees(wheelAngle)
                .translate(0, 0, 0);

            pins[(side + 1) / 2]
                .translate(0, 14 / 16., side * (12 / 16.))
                .rotateXDegrees(wheelAngle)
                .translate(0, 1 / 4f, 0)
                .rotateXDegrees(-wheelAngle);
        }
    }
}
