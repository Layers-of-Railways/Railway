package com.railwayteam.railways.blocks;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.Util;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraftforge.common.util.Constants;

public abstract class AbstractLargeTrackBlock extends Block {
  public AbstractLargeTrackBlock(Properties properties) { super(properties); }

  protected abstract BlockState checkForConnections (BlockState state, IWorld worldIn, BlockPos pos);
  protected abstract void fillStateContainer(StateContainer.Builder<Block, BlockState> builder);
  protected abstract boolean canConnectFrom (BlockState state, IWorld worldIn, BlockPos pos, Util.Vector direction);

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return checkForConnections(defaultBlockState(), context.getLevel(), context.getClickedPos());
  }

  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    return checkForConnections(stateIn, worldIn, currentPos);
  }

  @Override
  public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
    super.onPlace(state, worldIn, pos, oldState, isMoving);
    notifyCorners(state,worldIn,pos);
  }

  @Override
  public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    super.onRemove(state, worldIn, pos, newState, isMoving);
    notifyCorners(state,worldIn,pos);
  }

  private void notifyCorners (BlockState state, World worldIn, BlockPos pos) {
    BlockState corner;
    for (int z=-1; z<2; z++) {
      for (int x=-1; x<2; x++) {
        if (x==0 || z==0) continue;
        corner = worldIn.getBlockState(pos.offset(x,0,z));
        if (corner.getBlock() instanceof AbstractLargeTrackBlock) {
          worldIn.setBlock(pos.offset(x,0,z), ((AbstractLargeTrackBlock)corner.getBlock()).checkForConnections(corner,worldIn,pos.offset(x,0,z)), Constants.BlockFlags.DEFAULT);
        }
      }
    }
  }

  public static ModelFile partialModel (DataGenContext<?,?> ctx, RegistrateBlockstateProvider prov, String... suffix) {
    StringBuilder loc = new StringBuilder("block/wide_gauge/" + ctx.getName());
    for (String suf : suffix) loc.append("_" + suf);
    return prov.models().getExistingFile(prov.modLoc(loc.toString()));
  }
}
