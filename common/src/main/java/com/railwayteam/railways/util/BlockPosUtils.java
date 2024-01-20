package com.railwayteam.railways.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public class BlockPosUtils {

    public static BlockPos normalize(BlockPos pos) {
        double length = Math.sqrt(pos.distSqr(Vec3i.ZERO));
        return new BlockPos(Math.round(pos.getX()/length), Math.round(pos.getY()/length), Math.round(pos.getZ()/length));
    }

    public static BlockPos min(BlockPos a, BlockPos b) {
        return new BlockPos(
            Math.min(a.getX(), b.getX()),
            Math.min(a.getY(), b.getY()),
            Math.min(a.getZ(), b.getZ())
        );
    }

    public static BlockPos max(BlockPos a, BlockPos b) {
        return new BlockPos(
            Math.max(a.getX(), b.getX()),
            Math.max(a.getY(), b.getY()),
            Math.max(a.getZ(), b.getZ())
        );
    }
}
