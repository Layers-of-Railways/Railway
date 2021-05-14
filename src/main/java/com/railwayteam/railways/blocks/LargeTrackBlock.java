package com.railwayteam.railways.blocks;

import com.railwayteam.railways.util.VectorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class LargeTrackBlock extends AbstractLargeTrackBlock {
  public static final String name = "large_track";

  // Priority sorting: Cardinal, Straight, North-South
  // Currently unused in code, but this is the ideal
  /*
  public static final LargeTrackSide[] priorityOrder = {
    LargeTrackSide.NORTH_SOUTH,
    LargeTrackSide.EAST_WEST,
    LargeTrackSide.NORTHEAST_SOUTHWEST,
    LargeTrackSide.NORTHWEST_SOUTHEAST,
    LargeTrackSide.NORTH_SOUTHEAST,
    LargeTrackSide.NORTH_SOUTHWEST,
    LargeTrackSide.SOUTH_NORTHEAST,
    LargeTrackSide.SOUTH_NORTHWEST,
    LargeTrackSide.EAST_NORTHWEST,
    LargeTrackSide.EAST_SOUTHWEST,
    LargeTrackSide.WEST_NORTHEAST,
    LargeTrackSide.WEST_SOUTHEAST
  };
  */
  public static EnumProperty<LargeTrackSide> TRACK_SIDE = EnumProperty.create("bigtrack", LargeTrackSide.class);

  public LargeTrackBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(TRACK_SIDE, LargeTrackSide.NORTH_SOUTH));
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return checkForConnections(
      getDefaultState().with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(VectorUtils.Vector.getClosest(context.getPlayer().getLookVec()).value)),
      context.getWorld(), context.getPos()
    );
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) { builder.add(TRACK_SIDE); }

  @Override
  protected boolean canConnectFrom (BlockState state, IWorld worldIn, BlockPos pos, VectorUtils.Vector direction) {
    return state.get(TRACK_SIDE).connectsTo(direction.value);
  }

  protected BlockState checkForConnections (BlockState state, IWorld world, BlockPos pos) {
    ArrayList<Vector3d> priority = new ArrayList<>();
    ArrayList<Vector3d> found = new ArrayList<>();
    for (int x=-1; x<2; x++) {
      for (int z=-1; z<2; z++) {
        if (pos.add(x,0,z).equals(pos)) continue; // skip the center point
        BlockState candidate = world.getBlockState(pos.add(x,0,z));
        if (candidate.getBlock() instanceof AbstractLargeTrackBlock) {
          Vector3d offset = new Vector3d(x,0,z);
          if ( ((AbstractLargeTrackBlock)candidate.getBlock()).canConnectFrom(
            candidate, world, pos.add(x,0,z),
            VectorUtils.Vector.getClosest(new Vector3d(x,0,z)).getOpposite())
          ) { // front of the line if it connects
            priority.add(offset);
          }
          else {  // to the back of the line if it doesn't connect
            found.add(offset);
          }
        }
      }
    }
    found.addAll(0, priority); // stack them together
    switch (found.size()) {
      case 0: // no valid connections, we'll just accept the default state
        break;
      case 1: // one valid side, attach to it
        state = state.with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(found.get(0)));
        break;
      case 2: // the perfect number of connections found
        if (LargeTrackSide.isValid(found.get(0), found.get(1))) {
          state = state.with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(found.get(0), found.get(1)));
          break;
        } // else fall through
      default: // too many, arbitrate.
        arbitration:
        for (Vector3d b : found) {
          for (Vector3d a : found) {
            if (LargeTrackSide.isValid(a,b)) {
              state = state.with(TRACK_SIDE, LargeTrackSide.findValidStateFrom(a,b));
              break arbitration;
            }
          }
        }
    }
  //  Railways.LOGGER.debug("offsets found:");
  //  for (Vec3i v : found) Railways.LOGGER.debug("  " + v.toShortString());
  //  Railways.LOGGER.debug("selected " + state.get(TRACK_SIDE).getName());
    return state;
  }
}
