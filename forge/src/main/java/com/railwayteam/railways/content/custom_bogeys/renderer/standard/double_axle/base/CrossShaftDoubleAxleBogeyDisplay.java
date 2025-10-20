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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle.base;

import com.railwayteam.railways.content.custom_bogeys.renderer.unified.ElementProvider;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class CrossShaftDoubleAxleBogeyDisplay extends DoubleAxleBogeyDisplay{
    private final Affine<?>[] shafts;

    public CrossShaftDoubleAxleBogeyDisplay(ElementProvider<?> prov, PartialModel frame, PartialModel wheels, boolean recenterWheels) {
        super(prov, frame, wheels, recenterWheels);

        this.shafts = prov.create(AllPartialModels.SHAFT, 2);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle) {
        super.update(bogeyData, wheelAngle);

        for (int i : Iterate.zeroAndOne) {
            shafts[i]
                .translate(-.5f, .25f, i * -1)
                .center()
                .rotateTo(Direction.UP, Direction.SOUTH)
                .rotateYDegrees(wheelAngle)
                .uncenter();
        }
    }
}
