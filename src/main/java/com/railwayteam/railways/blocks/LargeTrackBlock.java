package com.railwayteam.railways.blocks;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.Util;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class LargeTrackBlock extends Block {
  public static final String name = "large_track";

  public static EnumProperty<LargeTrackSide> TRACK_SIDE = EnumProperty.create("side", LargeTrackSide.class);

  public LargeTrackBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(TRACK_SIDE, LargeTrackSide.NORTH_SOUTH));
  }

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
    Block corner;
    for (int z=-1; z<2; z++) {
      for (int x=-1; x<2; x++) {
        if (x==0 || z==0) continue;
        corner = worldIn.getBlockState(pos.add(x,0,z)).getBlock();
        if (corner instanceof LargeTrackBlock) {
          worldIn.setBlockState(pos.add(x,0,z), ((LargeTrackBlock)corner).checkForConnections(state,worldIn,pos.add(x,0,z)));
        }
      }
    }
  }

  private BlockState checkForConnections (BlockState state, IWorld worldIn, BlockPos pos) {
    BlockPos other = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
    ArrayList<Vec3d> directions = new ArrayList<>();
  //  Railways.LOGGER.debug("Checking around " + other.toString());
    for (int x=-1; x<2; x++) {
      for (int z=-1; z<2; z++) {
        if (other.add(x,0,z).equals(pos)) continue;
      //  Railways.LOGGER.debug("  checking at " + other.add(x,0,z));
        if (worldIn.getBlockState(other.add(x,0,z)).getBlock() instanceof LargeTrackBlock) {
        //  Railways.LOGGER.debug("  found at " + x + "," + z);
          directions.add(new Vec3d(x,0,z));
        }
      }
    }
    // if directions is > 2 we need arbitration...
    if (directions.size() == 0) {
      // just use the default state
    }
    else if (directions.size() == 1) {
      state = state.with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(directions.get(0)));
    }
    else {
      state = state.with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(directions.get(0), directions.get(1)));
    }
  //  Railways.LOGGER.debug("result: " + state.get(TRACK_SIDE).getName());
    return state;
  }

  public static ModelFile partialModel (DataGenContext<?,?> ctx, RegistrateBlockstateProvider prov, String... suffix) {
    StringBuilder loc = new StringBuilder("block/wide_gauge/" + ctx.getName());
    for (String suf : suffix) loc.append("_" + suf);
    return prov.models().getExistingFile(prov.modLoc(loc.toString()));
  }

  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) { builder.add(TRACK_SIDE); }
}
