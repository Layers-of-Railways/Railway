package com.railwayteam.railways.blocks;


import com.railwayteam.railways.ModSetup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;

public class NumericalSignalBlock extends Block {
    public NumericalSignalBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.POWER_0_15, 0));
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
        stateBuilder.add(BlockStateProperties.POWER_0_15);
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
        return getDefaultState().with(BlockStateProperties.POWER_0_15, getPower(ctx));
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
}
