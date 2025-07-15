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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium;

import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.ElementProvider;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.MEDIUM_QUINTUPLE_WHEEL_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.MEDIUM_SHARED_WHEELS;

public class MediumQuintupleWheelDisplay implements BogeyDisplay {
    private final Affine<?> frame;
    private final Affine<?>[] wheels;
    private final Affine<?>[] secondaryShafts;

    public MediumQuintupleWheelDisplay(ElementProvider<?> prov) {
        frame = prov.create(MEDIUM_QUINTUPLE_WHEEL_FRAME);
        wheels = prov.create(MEDIUM_SHARED_WHEELS, 5);
        secondaryShafts = prov.create(AllPartialModels.SHAFT, 4);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle) {
        for (int side = 0; side < 4; side++) {
            secondaryShafts[side]
                .translate(-.5f, .31f, 1.8f + side * -1.5)
                .center()
                .rotateTo(Direction.UP, Direction.SOUTH)
                .rotateYDegrees(wheelAngle)
                .uncenter();
        }

        frame.self();

        for (int side = -1; side < 4; side++) {
            wheels[side + 1]
                .translate(0, 13 / 16f, -1.5f + side * 1.5)
                .rotateXDegrees(wheelAngle)
                .translate(0, -13 / 16f, 0);
        }
    }
}
