package com.railwayteam.railways.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public abstract class HorizontalConnectedBlock extends HorizontalDirectionalBlock {

  public HorizontalConnectedBlock(Properties props) {
    super(props);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  @Override
  public void onPlace(BlockState newState, Level world, BlockPos pos, BlockState oldState, boolean moved) {
    if (world.isClientSide() || newState.is(oldState.getBlock()) || moved) return;

    updateConnections(newState, world, pos, oldState);
  }

  protected void updateConnections (BlockState state, Level world, BlockPos pos, BlockState oldState) {

  }

  // for controlling how big the multiblock can be. 0 = unconstrained.
  protected static int getMaxMajorHorizontal () { return 0; } // larger horizontal axis
  protected static int getMaxMinorHorizontal () { return 0; } // smaller horizontal axis
  protected static int getMaxVertical () { return 0; }

  // override to control how the block links up
  public Direction[] getConnectionDirections (BlockState state) {
    return Direction.values();
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
    super.createBlockStateDefinition(builder);
  }
}
