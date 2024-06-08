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

package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.base.data.recipe.RailwaysRecipeProvider.GeneratedRecipe;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class DyedRecipeList implements Iterable<GeneratedRecipe> {
    private static final int COLOR_AMOUNT = DyeColor.values().length;

    protected final GeneratedRecipe[] values = new GeneratedRecipe[getColorCount()];

    public DyedRecipeList(Function<@NotNull DyeColor, GeneratedRecipe> filler) {
        for (DyeColor color : DyeColor.values()) {
            values[color.ordinal()] = filler.apply(color);
        }
    }

    protected int getColorCount() {
        return COLOR_AMOUNT;
    }

    public GeneratedRecipe get(@NotNull DyeColor color) {
        return values[color.ordinal()];
    }

    public GeneratedRecipe[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public Iterator<GeneratedRecipe> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }

            @Override
            public GeneratedRecipe next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return values[index++];
            }
        };
    }

    public static class NullableDyedRecipeList extends DyedRecipeList {
        public NullableDyedRecipeList(Function<@Nullable DyeColor, GeneratedRecipe> filler) {
            super(filler);
            values[values.length - 1] = filler.apply(null);
        }

        @Override
        protected int getColorCount() {
            return COLOR_AMOUNT + 1;
        }

        @Override
        public GeneratedRecipe get(@Nullable DyeColor color) {
            if (color == null)
                return values[values.length - 1];
            return super.get(color);
        }
    }
}
