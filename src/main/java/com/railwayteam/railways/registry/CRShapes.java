package com.railwayteam.railways.registry;

import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.simibubi.create.AllShapes.Builder;

import static net.minecraft.core.Direction.UP;
import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.WEST;

public class CRShapes {

    public static final VoxelShaper
        SEMAPHORE = shape(4,0,4,12,16,12)
            .forDirectional(NORTH);
    public static final VoxelShape
        MONORAIL_COLLISION = shape(0, 0, 0, 16, 16, 14).build();

    private static Builder shape(VoxelShape shape) {
        return new Builder(shape);
    }

    private static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
