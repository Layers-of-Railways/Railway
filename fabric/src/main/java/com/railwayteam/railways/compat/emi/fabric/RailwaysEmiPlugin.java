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

package com.railwayteam.railways.compat.emi.fabric;

import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.PaintPitcherItem;
import com.railwayteam.railways.registry.CRFluids;
import com.railwayteam.railways.registry.CRItems;
import com.simibubi.create.compat.emi.recipes.SpoutEmiRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import static com.railwayteam.railways.base.data.recipe.RailwaysRecipeProvider.Ingredients.palettesPaint;

public class RailwaysEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        for (PalettesColor color : PalettesColor.values()) {
            if (color.isNetherite()) continue;

            registry.setDefaultComparison(CRFluids.PAINT.get().getSource(), Comparison.compareNbt());

            String path = "create/filling/railways/empty_paint_pitcher/with/railways/paint/" + color.getSerializedName();
            registry.addRecipe(new SpoutEmiRecipe(
                new ProcessingRecipeBuilder<>(FillingRecipe::new, new ResourceLocation("emi", path))
                    .withItemIngredients(Ingredient.of(CRItems.EMPTY_PAINT_PITCHER))
                    .withFluidIngredients(palettesPaint(color, PaintPitcherItem.FLUID_PER_LEVEL * PaintPitcherItem.MAX_LEVELS))
                    .withSingleItemOutput(CRItems.PAINT_PITCHERS.get(color).asStack())
                    .build()
            ));
        }
    }
}
