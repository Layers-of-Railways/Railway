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
import com.railwayteam.railways.content.palettes.PalettesColor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class EnumRecipeList<E extends Enum<E>> implements Iterable<GeneratedRecipe> {
    protected final GeneratedRecipe[] values;
    protected final int[] ordinal_to_idx;

    public EnumRecipeList(Class<E> enumType, Function<@NotNull E, GeneratedRecipe> filler) {
        E[] enumVals = enumType.getEnumConstants();

        ordinal_to_idx = new int[enumVals.length];
        int idx = 0;
        for (E value : enumVals) {
            if (filter(value)) {
                ordinal_to_idx[value.ordinal()] = idx++;
            } else {
                ordinal_to_idx[value.ordinal()] = -1;
            }
        }
        values = new GeneratedRecipe[idx];

        for (E value : enumVals) {
            int i = ordinal_to_idx[value.ordinal()];
            if (i != -1) {
                values[i] = filler.apply(value);
            }
        }
    }

    protected boolean filter(E value) {
        return true;
    }

    public GeneratedRecipe get(@NotNull E value) {
        int idx = ordinal_to_idx[value.ordinal()];
        if (idx == -1) {
            throw new NoSuchElementException();
        }
        return values[idx];
    }

    public GeneratedRecipe[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public @NotNull Iterator<GeneratedRecipe> iterator() {
        return new GeneratedRecipeIterator();
    }

    private class GeneratedRecipeIterator implements Iterator<GeneratedRecipe> {
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
    }

    /*-----------------------------------------*/
    /* Utility classes for specific enum types */
    /*-----------------------------------------*/

    public static class PalettesRecipeList extends EnumRecipeList<PalettesColor> {
        public PalettesRecipeList(Function<@NotNull PalettesColor, GeneratedRecipe> filler) {
            super(PalettesColor.class, filler);
        }
    }

    public static class DyedOnlyPalettesRecipeList extends PalettesRecipeList {
        public DyedOnlyPalettesRecipeList(Function<@NotNull PalettesColor, GeneratedRecipe> filler) {
            super(filler);
        }

        @Override
        protected boolean filter(PalettesColor value) {
            return !value.isNetherite();
        }
    }
}
