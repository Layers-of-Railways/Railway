package com.railwayteam.railways.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public abstract class VoxelUtils {
    public static VoxelShape rotateShape(final Direction from, final Direction to, final VoxelShape shape) {
        if (from == to) {
            return shape;
        }

        final VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        final int times = (to.getHorizontalIndex() - from.getHorizontalIndex() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    VoxelShapes.or(buffer[1], VoxelShapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    public static class Shape {
        public final VoxelShape north;

        public Shape(VoxelShape north) {
            this.north = north;
        }

        public Shape(double x, double y, double z, double sizeX, double sizeY, double sizeZ) {
            this(Block.makeCuboidShape(x, y, z, sizeX, sizeY, sizeZ));
        }

        public VoxelShape forDir(VoxelShape shape, Direction dir) {
            return rotateShape(Direction.NORTH, dir, shape);
        }

        public VoxelShape forDir(Direction dir) {
            return forDir(north, dir);
        }

        public VoxelShape get(BlockState blockState, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
            return forDir(blockState.get(HorizontalBlock.HORIZONTAL_FACING));
        }
    }
}
