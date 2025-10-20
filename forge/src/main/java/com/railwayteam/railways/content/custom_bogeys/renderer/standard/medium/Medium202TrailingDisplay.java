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
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class Medium202TrailingDisplay implements BogeyDisplay {
    private final Affine<?> frame;
    private final Affine<?> wheels;
    private final Affine<?>[] secondaryShafts;

    public Medium202TrailingDisplay(ElementProvider<?> prov) {
        frame = prov.create(MEDIUM_2_0_2_TRAILING_FRAME);
        wheels = prov.create(MEDIUM_SHARED_WHEELS);
        secondaryShafts = prov.create(AllPartialModels.SHAFT, 2);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle) {
        for (int i : Iterate.zeroAndOne) {
            secondaryShafts[i]
                .translate(-.5f, .25f, i * -1)
                .center()
                .rotateTo(Direction.UP, Direction.SOUTH)
                .rotateYDegrees(wheelAngle)
                .uncenter();
        }

        frame.self();

        wheels
            .translate(0, 13 / 16f, 0)
            .rotateXDegrees(wheelAngle)
            .translate(0, -13 / 16f, 0);
    }
}
