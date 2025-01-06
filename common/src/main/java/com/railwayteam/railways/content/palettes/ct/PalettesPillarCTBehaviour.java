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

package com.railwayteam.railways.content.palettes.ct;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PalettesPillarCTBehaviour extends ConnectedTextureBehaviour.Base {
	protected final CTSpriteShiftEntry shift;

	public PalettesPillarCTBehaviour(CTSpriteShiftEntry shift) {
		super();
		this.shift = shift;
	}

	@Override
	protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
		if (state.hasProperty(BlockStateProperties.FACING)) {
			return state.getValue(BlockStateProperties.FACING);
		}
		if (state.hasProperty(BlockStateProperties.AXIS)) {
			Axis axis = state.getValue(BlockStateProperties.AXIS);
			return Direction.fromAxisAndDirection(axis, axis == Axis.Z ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
		}
		return super.getUpDirection(reader, pos, state, face);
	}

	@Override
	protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
		Direction facing;
		if (state.hasProperty(BlockStateProperties.FACING)) {
			facing = state.getValue(BlockStateProperties.FACING);
		} else if (state.hasProperty(BlockStateProperties.AXIS)) {
			Axis axis = state.getValue(BlockStateProperties.AXIS);
			facing = Direction.fromAxisAndDirection(axis, axis == Axis.Z ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
		} else {
			return super.getRightDirection(reader, pos, state, face);
		}
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
		if (state.hasProperty(BlockStateProperties.FACING)) {
			if (state.getValue(BlockStateProperties.FACING) != other.getValue(BlockStateProperties.FACING)) {
				return false;
			}
		} else if (state.hasProperty(BlockStateProperties.AXIS)) {
			if (state.getValue(BlockStateProperties.AXIS) != other.getValue(BlockStateProperties.AXIS)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean reverseUVsVertically(BlockState state, Direction face) {
		if (face != Direction.DOWN) {
			return false;
		}
		if (state.hasProperty(BlockStateProperties.FACING)) {
			return state.getValue(BlockStateProperties.FACING).getAxis().isHorizontal();
		}
		if (state.hasProperty(BlockStateProperties.AXIS)) {
			return state.getValue(BlockStateProperties.AXIS).isHorizontal();
		}
		return false;
	}

	@Override
	public CTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
		if (state.hasProperty(BlockStateProperties.FACING)) {
			return state.getValue(BlockStateProperties.FACING).getAxis() != direction.getAxis() ? shift : null;
		}
		if (state.hasProperty(BlockStateProperties.AXIS)) {
			return state.getValue(BlockStateProperties.AXIS) != direction.getAxis() ? shift : null;
		}
		return null;
	}
}
