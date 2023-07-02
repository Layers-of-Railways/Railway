package com.railwayteam.railways.content.switches.fabric;

import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import io.github.fabricators_of_create.porting_lib.block.ConnectableRedstoneBlock;
import io.github.fabricators_of_create.porting_lib.block.WeakPowerCheckingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;

public class TrackSwitchBlockImpl extends TrackSwitchBlock implements ConnectableRedstoneBlock, WeakPowerCheckingBlock {
  protected TrackSwitchBlockImpl(Properties properties, boolean isAutomatic) {
    super(properties, isAutomatic);
  }

  public static TrackSwitchBlock manual(Properties properties) {
    return new TrackSwitchBlockImpl(properties, false);
  }

  public static TrackSwitchBlock automatic(Properties properties) {
    return new TrackSwitchBlockImpl(properties, true);
  }

  @Override
  public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
    return true;
  }

  @Override
  public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
    return false;
  }
}
