package com.railwayteam.railways.blocks;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import java.util.ArrayList;

public class LargeTrackBlock extends AbstractLargeTrackBlock {
  public static final String name = "large_track";

  public static EnumProperty<LargeTrackSide> TRACK_SIDE = EnumProperty.create("bigtrack", LargeTrackSide.class);

  public LargeTrackBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(TRACK_SIDE, LargeTrackSide.NORTH_SOUTH));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) { builder.add(TRACK_SIDE); }

  @Override
  protected BlockState checkForConnections (BlockState state, IWorld worldIn, BlockPos pos) {
    BlockPos other = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
    ArrayList<Vec3i> directions = new ArrayList<>();
  //  Railways.LOGGER.debug("Checking around " + other.toString());
    for (int x=-1; x<2; x++) {
      for (int z=-1; z<2; z++) {
        if (other.add(x,0,z).equals(pos)) continue;
      //  Railways.LOGGER.debug("  checking at " + other.add(x,0,z));
      //  if (worldIn.getBlockState(other.add(x,0,z)).has(LargeSwitchTrackBlock.SWITCH_SIDE)) continue;
        if (worldIn.getBlockState(other.add(x,0,z)).getBlock() instanceof AbstractLargeTrackBlock) {
        //  Railways.LOGGER.debug("  found at " + x + "," + z);
          directions.add(new Vec3i(x,0,z));
        }
      }
    }
    switch (directions.size()) {
      case 2:
        state = state.with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(directions.get(0), directions.get(1)));
        break;
      case 1:
        state = state.with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(directions.get(0)));
        break;
      case 0:
        // state = state; // use regular state
        break;
      default:
        boolean found = false;
      //  Railways.LOGGER.debug("Found " + directions.size() + " possible connections");
        for (Vec3i dir : directions) {
        //  Railways.LOGGER.debug("checking " + dir + " vs " + Util.opposite(dir));
        //  if (worldIn.getBlockState(pos.add(dir.getX(), dir.getY(), dir.getZ())).getBlock() instanceof LargeSwitchTrackBlock) {
        //    state = state.with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(dir));
        //  }
          if (directions.contains(Util.opposite(dir))) {
            state = state.with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(dir));
            found = true;
            break;
          //  Railways.LOGGER.debug("  found a straight connection");
          }
        }
        // else
        if (!found) state = state.with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(directions.get(0),directions.get(1)));
    }
  //  Railways.LOGGER.debug("result: " + state.get(TRACK_SIDE).getName());
    return state;
  }
}
