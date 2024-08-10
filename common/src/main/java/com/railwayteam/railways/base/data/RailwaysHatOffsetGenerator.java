/*
 * Steam 'n' Rails
 * Copyright (c) 2024 The Railways Team
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

package com.railwayteam.railways.base.data;

import com.railwayteam.railways.registry.CREntities;
import com.simibubi.create.api.data.TrainHatInfoProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.phys.Vec3;

public class RailwaysHatOffsetGenerator extends TrainHatInfoProvider {
    public RailwaysHatOffsetGenerator(PackOutput output) {
        super(output);
    }
    
    @Override
    protected void createOffsets() {
        makeInfoFor(CREntities.CONDUCTOR.get(), new Vec3(0f, -1f, 0f));
    }
}
