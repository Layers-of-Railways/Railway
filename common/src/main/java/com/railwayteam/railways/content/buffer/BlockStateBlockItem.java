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

import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BlockStateBlockItem<T extends Comparable<T>> extends BlockItem {
    protected final Property<T> property;
    protected final T value;
    protected final boolean primary;

    protected BlockStateBlockItem(Block block, Properties properties, Property<T> property, T value, boolean primary) {
        super(block, properties);
        this.property = property;
        this.value = value;
        this.primary = primary;
    }

    public static <T extends Comparable<T>> NonNullBiFunction<Block, Properties, BlockStateBlockItem<T>> create(Property<T> property, T value, boolean primary) {
        return (b, p) -> new BlockStateBlockItem<>(b, p, property, value, primary);
    }

    public static <T extends Comparable<T>> NonNullFunction<Properties, BlockStateBlockItem<T>> create(NonNullSupplier<Block> blockSupplier, Property<T> property, T value, boolean primary) {
        return (p) -> new BlockStateBlockItem<>(blockSupplier.get(), p, property, value, primary);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab category, @NotNull NonNullList<ItemStack> items) {
        if (this.allowedIn(category)) {
            items.add(new ItemStack(this));
        }
    }

    @Nullable
    protected BlockState getPlacementState(@NotNull BlockPlaceContext context) {
        BlockState blockState = this.getBlock().getStateForPlacement(context);
        if (blockState != null) {
            blockState = blockState.setValue(this.property, this.value);
        }
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }

    @Override
    public void registerBlocks(@NotNull Map<Block, Item> blockToItemMap, @NotNull Item item) {
        if (this.primary) {
            super.registerBlocks(blockToItemMap, item);
        }
    }
}
