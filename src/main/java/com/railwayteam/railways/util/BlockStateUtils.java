package com.railwayteam.railways.util;

import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import net.minecraft.world.level.block.state.BlockState;

import static com.simibubi.create.content.logistics.trains.track.TrackBlock.SHAPE;
import static com.simibubi.create.content.logistics.trains.track.TrackBlock.HAS_TE;
import static com.simibubi.create.content.logistics.trains.track.TrackBlock.WATERLOGGED;

public class BlockStateUtils {
  /**
   * @param block an instance of TrackBlock
   * @param state reference BlockState
   * @return block with state applied to it
   */
  public static BlockState trackWith(TrackBlock block, BlockState state) {
    return block.defaultBlockState()
        .setValue(SHAPE, state.getValue(SHAPE))
        .setValue(HAS_TE, state.getValue(HAS_TE))
        .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
  }
}
