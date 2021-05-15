package com.railwayteam.railways.blocks;

import com.railwayteam.railways.util.VectorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class LargeSwitchTrackBlock extends AbstractLargeTrackBlock {
  public static final String name = "large_switch";
  public static EnumProperty<LargeSwitchSide> SWITCH_SIDE = EnumProperty.create("bigswitch", LargeSwitchSide.class);

  public LargeSwitchTrackBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState()
      .with(SWITCH_SIDE, LargeSwitchSide.NORTH_SOUTHEAST)
      .with(BlockStateProperties.ENABLED, false) // tracking whether it's turning or straight
    );
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return super.getStateForPlacement(context)
      .with(SWITCH_SIDE, LargeSwitchSide.NORTH_SOUTHEAST)
      .with(BlockStateProperties.ENABLED, false);
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(SWITCH_SIDE);
    builder.add(BlockStateProperties.ENABLED);
  }

  @Override
  protected boolean canConnectFrom (BlockState state, IWorld worldIn, BlockPos pos, VectorUtils.Vector direction) {
    return state.get(SWITCH_SIDE).connectsTo(direction.value);
  }

  @Override
  protected BlockState checkForConnections (BlockState state, IWorld worldIn, BlockPos pos) {
    BlockPos other = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
    ArrayList<BlockPos> directions = new ArrayList<>();
    for (int x=-1; x<2; x++) {
      for (int z=-1; z<2; z++) {
        if (other.add(x,0,z).equals(pos)) continue;
        //  Railways.LOGGER.debug("  checking at " + other.add(x,0,z));
        if (worldIn.getBlockState(other.add(x,0,z)).getBlock() instanceof AbstractLargeTrackBlock) {
          //  Railways.LOGGER.debug("  found at " + x + "," + z);
          directions.add(new BlockPos(x,0,z));
        }
      }
    }
    switch (directions.size()) {
      case 3:
        state = state.with(SWITCH_SIDE, LargeSwitchSide.findValidStateFrom(directions.get(0), directions.get(1), directions.get(2)));
        break;
      case 2:
        state = state.with(SWITCH_SIDE, LargeSwitchSide.findValidStateFrom(directions.get(0), directions.get(1)));
        break;
      case 1:
        state = state.with(SWITCH_SIDE, LargeSwitchSide.findValidStateFrom(directions.get(0)));
        break;
      case 0:
        // state = state; // use regular state
        break;
      default:
        boolean found = false;
        //  Railways.LOGGER.debug("Found " + directions.size() + " possible connections");
        for (BlockPos dir : directions) {
          //  Railways.LOGGER.debug("checking " + dir + " vs " + Util.opposite(dir));
          if (directions.contains(VectorUtils.opposite(dir))) {
            state = state.with(SWITCH_SIDE, LargeSwitchSide.findValidStateFrom(dir));
            found = true;
            //  Railways.LOGGER.debug("  found a straight connection");
          }
        }
        // else
        if (!found) state = state.with(SWITCH_SIDE, LargeSwitchSide.findValidStateFrom(directions.get(0),directions.get(1)));
    }
    return state;
  }

  public boolean isTurning (BlockState state) {
    return state.get(BlockStateProperties.ENABLED);
  }
}