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

package com.railwayteam.railways.compat.jei.forge;

import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.PaintFluid;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class PaintFluidSubtypeInterpreter implements IIngredientSubtypeInterpreter<FluidStack> {
    @Override
    public @NotNull String apply(@NotNull FluidStack ingredient, @NotNull UidContext context) {
        return PaintFluid.getColor(ingredient.getTag())
            .map(PalettesColor::getSerializedName)
            .orElse(NONE);
    }
}
