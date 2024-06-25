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

package com.railwayteam.railways.content.palettes.boiler;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class BoilerCTBehaviour extends ConnectedTextureBehaviour {
    private final CTSpriteShiftEntry shift;

    public BoilerCTBehaviour(CTSpriteShiftEntry shift) {
        this.shift = shift;
    }

    @Override
    public @Nullable CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return shift;
    }

    @Override
    public @Nullable CTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        CTSpriteShiftEntry shift = getShift(state, direction, null);
        if (shift == null)
            return null;
        return shift.getType();
    }

    private boolean connectsTo(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction offset) {
        BlockPos otherPos = pos.offset(offset.getNormal());
        BlockState other = reader.getBlockState(otherPos);

        if (other.getBlock() != state.getBlock())
            return false;

        return isSameState(other, state, BoilerBlock.HORIZONTAL_AXIS, BoilerBlock.RAISED);
    }

    @Override
    public CTContext buildContext(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face, ContextRequirement requirement) {
        CTContext context = new CTContext();

        Direction offset1 = Direction.fromAxisAndDirection(state.getValue(BoilerBlock.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
        Direction offset2 = Direction.fromAxisAndDirection(state.getValue(BoilerBlock.HORIZONTAL_AXIS), Direction.AxisDirection.NEGATIVE);

        context.right = connectsTo(reader, pos, state, offset1);
        context.left = connectsTo(reader, pos, state, offset2);

        return context;
    }

    private boolean isSameState(BlockState state, BlockState otherState, Property<?>... properties) {
        for (Property<?> property : properties) {
            if (state.getValue(property) != otherState.getValue(property))
                return false;
        }

        return true;
    }
}
