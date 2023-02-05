package com.railwayteam.railways.util;

import com.simibubi.create.foundation.utility.VoxelShaper;
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
