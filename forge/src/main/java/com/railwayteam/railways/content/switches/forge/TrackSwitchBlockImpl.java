package com.railwayteam.railways.content.switches.forge;

import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TrackSwitchBlockImpl extends TrackSwitchBlock {
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
  public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
    return true;
  }

  @Override
  public boolean shouldCheckWeakPower(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
    return false;
  }
}
