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

package com.railwayteam.railways.content.minecarts.forge;

import com.railwayteam.railways.content.minecarts.MinecartWorkbench;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MinecartWorkbenchImpl extends MinecartWorkbench {

    protected MinecartWorkbenchImpl(Level level, double x, double y, double z) {
        super(level, x, y, z);
    }

    public MinecartWorkbenchImpl(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static MinecartWorkbench create(Level level, double x, double y, double z) {
        return new MinecartWorkbenchImpl(level, x, y, z);
    }

    public static MinecartWorkbench create(EntityType<?> type, Level level) {
        return new MinecartWorkbenchImpl(type, level);
    }
}
