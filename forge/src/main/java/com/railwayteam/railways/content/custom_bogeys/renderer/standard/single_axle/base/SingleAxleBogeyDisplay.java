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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.single_axle.base;

import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.ElementProvider;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.minecraft.nbt.CompoundTag;

public class SingleAxleBogeyDisplay implements BogeyDisplay {
    private final Affine<?> wheels;
    private final Affine<?> frame;

    public SingleAxleBogeyDisplay(ElementProvider<?> prov, PartialModel frame) {
        this.wheels = prov.create(AllPartialModels.SMALL_BOGEY_WHEELS);
        this.frame = prov.create(frame);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle) {
        frame.self();

        wheels
            .translate(0, 12 / 16f, 0)
            .rotateXDegrees(wheelAngle);
    }
}
