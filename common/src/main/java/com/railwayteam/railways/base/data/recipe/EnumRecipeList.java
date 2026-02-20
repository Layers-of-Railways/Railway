/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

import com.railwayteam.railways.base.EnumFilledList;
import com.railwayteam.railways.base.data.recipe.RailwaysRecipeProvider.GeneratedRecipe;
import com.railwayteam.railways.content.palettes.PalettesColor;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public class EnumRecipeList<E extends Enum<E>> extends EnumFilledList<E, GeneratedRecipe> {

    public EnumRecipeList(Class<E> clazz, Function<@NotNull E, GeneratedRecipe> filler) {
        super(clazz, filler);
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

    public static class VanillaDyedOnlyPalettesRecipeList extends PalettesRecipeList {
        public VanillaDyedOnlyPalettesRecipeList(BiFunction<PalettesColor, DyeColor, GeneratedRecipe> filler) {
            super(c -> filler.apply(c, c.toDyeColor()));
        }

        @Override
        protected boolean filter(PalettesColor value) {
            return value.isMainSeries();
        }
    }
}
