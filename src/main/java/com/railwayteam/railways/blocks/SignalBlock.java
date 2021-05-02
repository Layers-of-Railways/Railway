package com.railwayteam.railways.blocks;

import com.railwayteam.railways.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;

public class SignalBlock extends Block {
  public static final String name = "basic_signal";

  public SignalBlock(Properties props) {
    super(props);
    this.setDefaultState(this.stateContainer.getBaseState()
      .with(BlockStateProperties.POWERED, false)
      .with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
    );
  }

  @Override
  public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState oldState, IWorld world, BlockPos pos, BlockPos oldPos) {
    return state
      .with(BlockStateProperties.POWERED, world.getWorld().isBlockPowered(pos))
    ;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return getDefaultState()
      .with(BlockStateProperties.POWERED, context.getWorld().isBlockPowered(context.getPos()))
      .with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing())
    ;
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(BlockStateProperties.POWERED);
    builder.add(BlockStateProperties.HORIZONTAL_FACING);
  }

  @Override
  public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
    return (side != null) && (!side.getAxis().equals(Direction.Axis.Y));
  }
}
