/*
 * Steam 'n' Rails
 * Copyright (c) 2024 The Railways Team
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

package com.railwayteam.railways.base.data.recipe;

import com.google.common.collect.Streams;
import com.railwayteam.railways.content.buffer.BlockStateBlockItem;
import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import com.tterrag.registrate.util.entry.ItemEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class BlockStateBlockItemGroupRecipeList implements Iterable<RailwaysRecipeProvider.GeneratedRecipe> {
    private final List<RailwaysRecipeProvider.GeneratedRecipe> values;

    public BlockStateBlockItemGroupRecipeList(BlockStateBlockItemGroup<?, ?> group, Function<@NotNull ItemEntry<? extends BlockStateBlockItem<?>>, RailwaysRecipeProvider.GeneratedRecipe> filler) {
        this.values = Streams
                .stream(group.getItems())
                .map(filler)
                .toList();
    }
    
    @Override
    public @NotNull Iterator<RailwaysRecipeProvider.GeneratedRecipe> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.size();
            }

            @Override
            public RailwaysRecipeProvider.GeneratedRecipe next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return values.get(index++);
            }
        };
    }
}
