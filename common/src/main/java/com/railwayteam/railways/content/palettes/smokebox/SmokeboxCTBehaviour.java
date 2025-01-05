/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.content.palettes.smokebox;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class SmokeboxCTBehaviour extends ConnectedTextureBehaviour.Base {
	protected final CTSpriteShiftEntry shift;

	public SmokeboxCTBehaviour(CTSpriteShiftEntry shift) {
		super();
		this.shift = shift;
	}

	@Override
	protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
		return state.getValue(PalettesSmokeboxBlock.FACING);
	}

	@Override
	protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
		Direction facing = state.getValue(PalettesSmokeboxBlock.FACING);
		if (facing.getAxis().isVertical()) {
			return Direction.fromAxisAndDirection(Axis.X, face.getAxisDirection());
		} else {
			return facing.getClockWise();
		}
	}

	@Override
	@SuppressWarnings("RedundantIfStatement")
	public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
		if (state.getBlock() != other.getBlock()) {
			return false;
		}
		if (state.getValue(PalettesSmokeboxBlock.FACING) != other.getValue(PalettesSmokeboxBlock.FACING)) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean reverseUVsVertically(BlockState state, Direction face) {
		return face == Direction.DOWN && state.getValue(PalettesSmokeboxBlock.FACING).getAxis().isHorizontal();
	}

	@Override
	public CTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
		return state.getValue(PalettesSmokeboxBlock.FACING).getAxis() != direction.getAxis() ? shift : null;
	}
}
