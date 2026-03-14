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

package com.railwayteam.railways.content.smokestack.block;

import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import com.railwayteam.railways.content.smokestack.RotationType;
import com.railwayteam.railways.content.smokestack.SmokeEmissionParams;
import com.railwayteam.railways.content.smokestack.SmokestackStyle;
import com.railwayteam.railways.util.ShapeWrapper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class StyledSmokeStackBlock extends SmokeStackBlock {
    public static final EnumProperty<SmokestackStyle> STYLE = EnumProperty.create("style", SmokestackStyle.class);

    private final Supplier<BlockStateBlockItemGroup<SmokestackStyle.Context, SmokestackStyle>> cycleGroup;

    public StyledSmokeStackBlock(Properties properties, RotationType rotationType, SmokeEmissionParams emissionParams, ShapeWrapper shape, boolean createsStationarySmoke, Supplier<BlockStateBlockItemGroup<SmokestackStyle.Context, SmokestackStyle>> cycleGroup) {
        super(properties, rotationType, emissionParams, shape, createsStationarySmoke);
        this.cycleGroup = cycleGroup;
    }

    @Override
    protected BlockState makeDefaultState() {
        return super.makeDefaultState().setValue(STYLE, SmokestackStyle.STEEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(STYLE));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return cycleGroup.get().get(state.getValue(STYLE)).asStack();
    }
}
