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

package com.railwayteam.railways.content.switches.fabric;

import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import io.github.fabricators_of_create.porting_lib.block.ConnectableRedstoneBlock;
import io.github.fabricators_of_create.porting_lib.block.WeakPowerCheckingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.SignalGetter;
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
  public boolean shouldCheckWeakPower(BlockState state, SignalGetter world, BlockPos pos, Direction side) {
    return false;
  }
}
