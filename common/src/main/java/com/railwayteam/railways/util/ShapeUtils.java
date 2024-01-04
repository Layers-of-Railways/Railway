package com.railwayteam.railways.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapeUtils {
    public static boolean isTouching(Vec3 globalHitVec, BlockPos pos, VoxelShape shape) {
        return isTouching(globalHitVec.subtract(pos.getX(), pos.getY(), pos.getZ()), shape);
    }

    public static boolean isTouching(Vec3 hitVec, VoxelShape shape) {
        for (var box : shape.toAabbs()) {
            if (box.inflate(0.01).contains(hitVec))
                return true;
        }
        return false;
    }
}
