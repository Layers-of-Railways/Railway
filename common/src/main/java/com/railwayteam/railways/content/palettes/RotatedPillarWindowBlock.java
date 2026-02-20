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

package com.railwayteam.railways.content.palettes;

import com.simibubi.create.content.decoration.palettes.ConnectedGlassBlock;
import com.simibubi.create.content.decoration.palettes.WindowBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.world.level.block.RotatedPillarBlock.rotatePillar;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RotatedPillarWindowBlock extends WindowBlock {

    public static final EnumProperty<Axis> AXIS = RotatedPillarBlock.AXIS;

    @SuppressWarnings("unused")
    public static RotatedPillarWindowBlock translucent(Properties properties) {
        return new RotatedPillarWindowBlock(properties, true);
    }

    public static RotatedPillarWindowBlock transparent(Properties properties) {
        return new RotatedPillarWindowBlock(properties, false);
    }

    public RotatedPillarWindowBlock(Properties properties, boolean translucent) {
        super(properties, translucent);
        this.registerDefaultState(defaultBlockState()
            .setValue(AXIS, Axis.Y));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return rotatePillar(state, rotation);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        boolean axisDisagreement = false;
        Axis myAxis = state.getOptionalValue(AXIS).orElse(null);
        Axis otherAxis = adjacentBlockState.getOptionalValue(AXIS).orElse(null);
        if (state.getBlock() instanceof RotatedPillarWindowBlock && adjacentBlockState.getBlock() instanceof RotatedPillarWindowBlock) {
            if (state.getValue(AXIS) != adjacentBlockState.getValue(AXIS)) {
                axisDisagreement = true;
            }
        }
        if (!axisDisagreement && state.getBlock() == adjacentBlockState.getBlock()) {
            return true;
        }
        if (state.getBlock() instanceof WindowBlock windowBlock
            && adjacentBlockState.getBlock() instanceof ConnectedGlassBlock) {
            return !windowBlock.isTranslucent() && side.getAxis() != myAxis && side.getAxis() != otherAxis;
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }
}
