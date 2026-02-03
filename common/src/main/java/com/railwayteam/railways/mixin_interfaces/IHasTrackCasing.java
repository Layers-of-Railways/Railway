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

package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.trains.track.TrackBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IHasTrackCasing {
    @Nullable
    Block railways$getTrackCasing();
    void railways$setTrackCasing(@Nullable Block trackCasing);

    default boolean railways$isAlternate() {
        return false;
    }

    default void railways$setAlternate(boolean alternate) {}

    static @Nullable Block getTrackCasing(Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof IHasTrackCasing te) {
            return te.railways$getTrackCasing();
        }
        return null;
    }

    static void setTrackCasing(Level world, BlockPos pos, @Nullable Block trackCasing) {
        BlockState state = world.getBlockState(pos);
        if (state.hasProperty(TrackBlock.HAS_BE))
            world.setBlockAndUpdate(pos, state.setValue(TrackBlock.HAS_BE, true));
        if (world.getBlockEntity(pos) instanceof IHasTrackCasing te) {
            te.railways$setTrackCasing(trackCasing);
        }
    }

    static boolean isAlternate(Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof IHasTrackCasing te) {
            return te.railways$isAlternate();
        }
        return false;
    }

    static boolean setAlternateModel(Level world, BlockPos pos, boolean useAlternateModel) {
        if (world.getBlockEntity(pos) instanceof IHasTrackCasing te) {
            te.railways$setAlternate(useAlternateModel);
            return true;
        } else {
            return false;
        }
    }
}
