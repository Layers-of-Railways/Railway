/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.conductor.vent;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public abstract class CopycatVentModel extends CopycatModel {
    protected static final AABB CUBE_AABB = new AABB(BlockPos.ZERO);
    public CopycatVentModel(BakedModel originalModel) {
        super(originalModel);
    }

    @ExpectPlatform
    public static CopycatVentModel create(BakedModel bakedModel) {
        throw new AssertionError();
    }
}
