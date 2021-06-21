package com.railwayteam.railways.blocks;


import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.util.VoxelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;

public class SpeedSignalBlock extends HorizontalBlock {
    public SpeedSignalBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.POWER_0_15, 0).with(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    public TileEntity createTileEntity (final BlockState state, final IBlockReader world) {
        return ModSetup.R_TE_NUMERICAL_SIGNAL.create();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(BlockStateProperties.POWER_0_15, HORIZONTAL_FACING);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    public static int getPower(World world, BlockPos pos) { // gets the max power it can find
//        return Arrays.asList(pos.down(), pos.up(), pos.north(), pos.south(), pos.west(), pos.east()).stream().mapToInt(world.getRedstonePower())
        return Arrays.asList(Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST).stream()
                .mapToInt((d) -> world.getRedstonePower(pos.offset(d), d)).max().getAsInt();
    }
    public static int getPower(BlockItemUseContext ctx) {return getPower(ctx.getWorld(), ctx.getPos());}

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return getDefaultState()
                .with(BlockStateProperties.POWER_0_15, getPower(ctx))
                .with(HORIZONTAL_FACING, ctx.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState oldState, IWorld worldInterface, BlockPos pos, BlockPos oldPos) {
        World world = (World) worldInterface;
        int power = getPower(world, pos);
//        if(power != state.get(BlockStateProperties.POWER_0_15)) {
//            ((NumericalSignalTileEntity) world.getTileEntity(pos)).power = power;
//        }
        return state.with(BlockStateProperties.POWER_0_15, power);
    }

    VoxelUtils.Shape shape = new VoxelUtils.Shape(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        // the shape is full cube so rotation doesnt change it, but ill do this just in case we ever change the model
        return shape.forDir(state.get(HORIZONTAL_FACING));
    }
}
