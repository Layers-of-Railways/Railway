package com.railwayteam.railways.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class HornBlock extends HorizontalFaceBlock {
    public static final IntegerProperty HORNS = IntegerProperty.create("horns", 1, 3);

    public HornBlock(Properties p_i48402_1_) {
        super(p_i48402_1_);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(HORNS, 1).with(FACE, AttachFace.WALL));
    }

    public void setHorns(BlockState blockState, World world, BlockPos pos, int horns) {
        world.setBlockState(pos, blockState.with(HORNS, horns), 1);
//        world.notifyNeighborsOfStateChange(pos, this);
    }

    // shape stuff
    public static VoxelShape ShapeBottomNorthSouth = Block.makeCuboidShape(0, 0, 1, 16, 15, 15);
    public static VoxelShape ShapeBottomWestEast = Block.makeCuboidShape(1, 0, 0, 15, 15, 16);

    public static VoxelShape ShapeSideNorth = Block.makeCuboidShape(0, 1, 1, 16, 15, 16);
    public static VoxelShape ShapeSideSouth = Block.makeCuboidShape(0, 1, 0, 16, 15, 15);
    public static VoxelShape ShapeSideWest = Block.makeCuboidShape(1, 1, 0, 16, 15, 16);
    public static VoxelShape ShapeSideEast = Block.makeCuboidShape(0, 1, 0, 15, 15, 16);

    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        Direction direction = blockState.get(HORIZONTAL_FACING);
        switch(blockState.get(FACE)) {
            case FLOOR:
                return direction.getAxis() == Direction.Axis.X ? ShapeBottomNorthSouth : ShapeBottomWestEast;
            case WALL:
                switch (direction) {
                    case WEST:
                        return ShapeSideWest;
                    case EAST:
                        return ShapeSideEast;
                    case NORTH:
                        return ShapeSideNorth;
                    case SOUTH:
                        return ShapeSideSouth;
                }
            default: return ShapeSideNorth;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(HORIZONTAL_FACING, HORNS, FACE);
    }
}
