package com.railwayteam.railways.util;

import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
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

  @ExpectPlatform
  public static SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
    throw new AssertionError();
  }
}
