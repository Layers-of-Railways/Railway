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

package com.railwayteam.railways.content.custom_bogeys.renderer.wide;

import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.ElementProvider;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

// fixme animated belts
public class WideComicallyLargeScotchYokeBogeyDisplay implements BogeyDisplay {
    private final Affine<?> frame;
    private final Affine<?> wheels;
    private final Affine<?> pins;
    private final Affine<?> pistons;
    private final Affine<?>[] primaryShafts = new Affine<?>[2];
    private final Affine<?>[] secondaryShafts = new Affine<?>[4];

    public WideComicallyLargeScotchYokeBogeyDisplay(ElementProvider<?> prov) {
        frame = prov.create(WIDE_COMICALLY_LARGE_FRAME);
        wheels = prov.create(WIDE_COMICALLY_LARGE_WHEELS);
        pins = prov.create(WIDE_COMICALLY_LARGE_PINS);
        pistons = prov.create(WIDE_COMICALLY_LARGE_PISTONS);
        prov.create(AllPartialModels.SHAFT, primaryShafts, secondaryShafts);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle) {
        for (int i : Iterate.zeroAndOne) {
            primaryShafts[i]
                .translate(-.5, 4 / 16., i * -1)
                .center()
                .rotateTo(Direction.UP, Direction.SOUTH)
                .rotateYDegrees(wheelAngle)
                .uncenter();
        }

        for (int i : Iterate.zeroAndOne) {
            for (int side : Iterate.zeroAndOne) {
                secondaryShafts[i + (side * 2)]
                    .translate(-1 + side, 4 / 16., (10 / 16.) + i * -(36 / 16.))
                    .center()
                    .rotateTo(Direction.UP, Direction.EAST)
                    .rotateYDegrees(wheelAngle)
                    .uncenter();
            }
        }

        frame.translate(0, 4 / 16., 0);

        pistons.translate(0, 1.5, (1 / 4f + (5 / 16.)) * Math.sin(AngleHelper.rad(wheelAngle)));

        wheels
            .translate(0, 1.5, 0)
            .rotateXDegrees(wheelAngle)
            .translate(0, 0, 0);

        pins
            .translate(0, 1.5, 0)
            .rotateXDegrees(wheelAngle)
            .translate(0, 1 / 4f + (5 / 16.), 0)
            .rotateXDegrees(-wheelAngle);
    }
}
