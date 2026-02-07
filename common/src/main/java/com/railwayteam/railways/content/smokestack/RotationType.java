/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.util.ShapeWrapper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface RotationType {
    BlockState makeDefaultState(BlockState state);
    void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder);
    @Nullable BlockState getStateForPlacement(BlockPlaceContext context, BlockState state);

    BlockState rotate(BlockState state, Rotation rot);
    BlockState mirror(BlockState state, Mirror mirror);
    BlockState cloneRotation(BlockState state, BlockState rotationSource);

    VoxelShape getShape(BlockState state, ShapeWrapper shape);
    int getModelYRot(BlockState state);

    RotationType NONE = new RotationType() {
        @Override
        public BlockState makeDefaultState(BlockState state) {
            return state;
        }

        @Override
        public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {}

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
            return state;
        }

        @Override
        public BlockState rotate(BlockState state, Rotation rot) {
            return state;
        }

        @Override
        public BlockState mirror(BlockState state, Mirror mirror) {
            return state;
        }

        @Override
        public BlockState cloneRotation(BlockState state, BlockState rotationSource) {
            return state;
        }

        @Override
        public VoxelShape getShape(BlockState state, ShapeWrapper shape) {
            return shape.get();
        }

        @Override
        public int getModelYRot(BlockState state) {
            return 0;
        }

        @Override
        public String toString() {
            return "RotationType.NONE";
        }
    };

    RotationType AXIS = new RotationType() {
        private static final EnumProperty<Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

        @Override
        public BlockState makeDefaultState(BlockState state) {
            return state.setValue(AXIS, Axis.X);
        }

        @Override
        public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(AXIS);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
            Axis axis = context.getHorizontalDirection().getAxis();
            return axis.isHorizontal() ? state.setValue(AXIS, axis) : state;
        }

        @Override
        public BlockState rotate(BlockState state, Rotation rot) {
            return RotatedPillarBlock.rotatePillar(state, rot);
        }

        @Override
        public BlockState mirror(BlockState state, Mirror mirror) {
            return state;
        }

        @Override
        public BlockState cloneRotation(BlockState state, BlockState rotationSource) {
            return state.setValue(AXIS, rotationSource.getValue(AXIS));
        }

        @Override
        public VoxelShape getShape(BlockState state, ShapeWrapper shape) {
            return shape.get(state.getValue(AXIS));
        }

        @Override
        public int getModelYRot(BlockState state) {
            return state.getValue(AXIS) == Axis.X ? 90 : 0;
        }

        @Override
        public String toString() {
            return "RotationType.AXIS";
        }
    };

    RotationType FACING = new RotationType() {
        private static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

        @Override
        public BlockState makeDefaultState(BlockState state) {
            return state.setValue(HORIZONTAL_FACING, Direction.NORTH);
        }

        @Override
        public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(HORIZONTAL_FACING);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
            return state.setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
        }

        @Override
        public BlockState rotate(BlockState state, Rotation rot) {
            return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
        }

        @Override
        public BlockState mirror(BlockState state, Mirror mirror) {
            return rotate(state, mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
        }

        @Override
        public BlockState cloneRotation(BlockState state, BlockState rotationSource) {
            return state.setValue(HORIZONTAL_FACING, rotationSource.getValue(HORIZONTAL_FACING));
        }

        @Override
        public VoxelShape getShape(BlockState state, ShapeWrapper shape) {
            return shape.get(state.getValue(HORIZONTAL_FACING));
        }

        @Override
        public int getModelYRot(BlockState state) {
            return ((int) state.getValue(HORIZONTAL_FACING).toYRot() + 180) % 360;
        }

        @Override
        public String toString() {
            return "RotationType.FACING";
        }
    };
}
