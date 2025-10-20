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

package com.railwayteam.railways.util;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapeWrapper {
    private final Object shape;

    public ShapeWrapper(VoxelShape shape) {
        this.shape = shape;
    }

    public ShapeWrapper(VoxelShaper shaper) {
        this.shape = shaper;
    }

    public static ShapeWrapper wrapped(VoxelShape shape) {
        return new ShapeWrapper(shape);
    }

    public static ShapeWrapper wrapped(VoxelShaper shaper) {
        return new ShapeWrapper(shaper);
    }

    public VoxelShape get() {
        if (shape instanceof VoxelShape) {
            return (VoxelShape) shape;
        } else if (shape instanceof VoxelShaper) {
            return ((VoxelShaper) shape).get(Direction.Axis.X);
        } else {
            return null;
        }
    }

    public VoxelShape get(Direction.Axis axis) {
        if (shape instanceof VoxelShape) {
            return (VoxelShape) shape;
        } else if (shape instanceof VoxelShaper) {
            return ((VoxelShaper) shape).get(axis);
        } else {
            return null;
        }
    }

    public VoxelShape get(Direction direction) {
        if (shape instanceof VoxelShape) {
            return (VoxelShape) shape;
        } else if (shape instanceof VoxelShaper) {
            return ((VoxelShaper) shape).get(direction);
        } else {
            return null;
        }
    }
}
