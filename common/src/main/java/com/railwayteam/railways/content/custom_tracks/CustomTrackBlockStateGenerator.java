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

package com.railwayteam.railways.content.custom_tracks;

import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CustomTrackBlockStateGenerator extends SpecialBlockStateGen {

  @ExpectPlatform
  public static CustomTrackBlockStateGenerator create() {
    throw new AssertionError();
  }

  @Override
  protected int getXRotation(BlockState state) {
    return 0;
  }

  @Override
  protected int getYRotation(BlockState state) {
    return state.getValue(TrackBlock.SHAPE)
        .getModelRotation();
  }
}

/*
 * All track models from track.json blockstate:
 * create:block/track/cross_diag
 * create:block/track/diag_2
 * create:block/track/cross_d1_xo
 * create:block/track/x_ortho
 * create:block/track/diag
 * create:block/track/teleport
 * create:block/track/cross_ortho
 * create:block/track/cross_d2_xo
 * create:block/track/ascending
 * create:block/track/z_ortho
 * minecraft:block/air
 * create:block/track/cross_d2_zo
 * create:block/track/cross_d1_zo
 *
 * textures: (teleport is exception here)
 * 0 - standard_track
 * 1 - standard_track_mip
 * 2 - standard_track_crossing
 * particle - create:block/palettes/stone_types/polished/andesite_cut_polished
 */