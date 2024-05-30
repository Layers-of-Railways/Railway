/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.registry.CRBlockEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WideTrackBufferBlock extends TrackBufferBlock<TrackBufferBlockEntity> {
    public WideTrackBufferBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<TrackBufferBlockEntity> getBlockEntityClass() {
        return TrackBufferBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TrackBufferBlockEntity> getBlockEntityType() {
        return CRBlockEntities.TRACK_BUFFER.get();
    }

    @Override
    protected BlockState getCycledStyle(BlockState originalState, Direction targetedFace) {
        return originalState;
    }
}
