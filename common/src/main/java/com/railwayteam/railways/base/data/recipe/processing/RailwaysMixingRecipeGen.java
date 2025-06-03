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

package com.railwayteam.railways.base.data.recipe.processing;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.compat.emi.EmiRecipeDefaultsGen;
import com.railwayteam.railways.base.data.recipe.EnumRecipeList.DyedOnlyPalettesRecipeList;
import com.railwayteam.railways.content.palettes.painting.PaintPitcherItem;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.registry.CRPalettes.StyledList;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class RailwaysMixingRecipeGen extends RailwaysProcessingRecipeGen {
	@SuppressWarnings("unused")
    StyledList<DyedOnlyPalettesRecipeList> LOCOMETAL_DYEING = new StyledList<>(style -> new DyedOnlyPalettesRecipeList(
		color -> createWithDeferredId(
			() -> {
				ResourceLocation loc = Railways.asResource("palettes/dyeing/" + RegisteredObjects.getKeyOrThrow(style.get(color).asItem()).getPath());
				if (style != CRPalettes.Styles.FLYWHEEL) {
					EmiRecipeDefaultsGen.DEFAULT_RECIPES.add(Railways.asResource(getRecipeType().getId().getPath() + "/" + loc.getPath()));
				}
				return loc;
            },
			b -> b
				.require(style.dyeGroupTag)
				.require(Ingredients.palettesPaint(color, PaintPitcherItem.FLUID_PER_LEVEL))
				.output(style.get(color))
		)
	));

	public RailwaysMixingRecipeGen(PackOutput output) {
		super(output);
	}

	@Override
	protected AllRecipeTypes getRecipeType() {
		return AllRecipeTypes.MIXING;
	}

}
