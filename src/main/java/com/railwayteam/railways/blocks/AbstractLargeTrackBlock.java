package com.railwayteam.railways.blocks;

import com.railwayteam.railways.util.VectorUtils;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nullable;

public abstract class AbstractLargeTrackBlock extends Block {
  private static final VoxelShape SHAPE = Block.makeCuboidShape(
    0d, 0d, 0d,
    16, 2d, 16d
  );
  public AbstractLargeTrackBlock(Properties properties) { super(properties); }

  protected abstract BlockState checkForConnections (BlockState state, IWorld worldIn, BlockPos pos);
  protected abstract void fillStateContainer(StateContainer.Builder<Block, BlockState> builder);
  protected abstract boolean canConnectFrom (BlockState state, IWorld worldIn, BlockPos pos, VectorUtils.Vector direction);

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return checkForConnections(getDefaultState(), context.getWorld(), context.getPos());
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    return checkForConnections(stateIn, worldIn, currentPos);
  }

  @Override
  public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
    super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
    notifyCorners(state,worldIn,pos);
  }

  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    super.onReplaced(state, worldIn, pos, newState, isMoving);
    notifyCorners(state,worldIn,pos);
  }

  private void notifyCorners (BlockState state, World worldIn, BlockPos pos) {
    BlockState corner;
    for (int z=-1; z<2; z++) {
      for (int x=-1; x<2; x++) {
        if (x==0 || z==0) continue;
        corner = worldIn.getBlockState(pos.add(x,0,z));
        if (corner.getBlock() instanceof AbstractLargeTrackBlock) {
          worldIn.setBlockState(pos.add(x,0,z), ((AbstractLargeTrackBlock)corner.getBlock()).checkForConnections(corner,worldIn,pos.add(x,0,z)));
        }
      }
    }
  }

  public static ModelFile partialModel(DataGenContext<?,?> ctx, RegistrateBlockstateProvider prov, String... suffix) {
    return partialModel(false, ctx, prov, suffix);
  }

  public static ModelFile partialModel (boolean wooden, DataGenContext<?,?> ctx, RegistrateBlockstateProvider prov, String... suffix) {
    StringBuilder loc = new StringBuilder((wooden ? "block/wide_gauge/wooden/" : "block/wide_gauge/andesite/") + ctx.getName().replace("_wooden",""));
    for (String suf : suffix) loc.append("_" + suf);
    return prov.models().getExistingFile(prov.modLoc(loc.toString()));
  }

  @Override
  public VoxelShape getShape (BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
    return SHAPE;
  }
}
