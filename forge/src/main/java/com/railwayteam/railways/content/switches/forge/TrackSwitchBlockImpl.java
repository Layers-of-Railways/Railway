/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
