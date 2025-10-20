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

package com.railwayteam.railways.util.client;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple implementation of BlockGetter for testing occlusion culling. (For example, to test copycats)
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OcclusionTestLevel implements BlockGetter {
    private final Map<BlockPos, BlockState> blocks = new HashMap<>();
    private final BlockGetter blockGetter;

    public OcclusionTestLevel(BlockGetter blockGetter) {
        this.blockGetter = blockGetter;
    }

    public void setBlock(BlockPos pos, BlockState state) {
        blocks.put(pos.immutable(), state);
    }

    public void clear() {
        blocks.clear();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return blocks.get(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public int getHeight() {
        return blockGetter.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return blockGetter.getMinBuildHeight();
    }
}

