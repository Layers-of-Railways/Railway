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

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.PaintPitcherItem;
import com.railwayteam.railways.registry.CRFluids;
import com.railwayteam.railways.registry.CRItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.railwayteam.railways.base.data.recipe.RailwaysRecipeProvider.Ingredients.palettesPaint;

@JeiPlugin
public class RailwaysJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = Railways.asResource("jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        final ResourceLocation spoutFillingCategory = Create.asResource("spout_filling");
        final RecipeType<FillingRecipe> spoutFillingType = new RecipeType<>(spoutFillingCategory, FillingRecipe.class);

        List<FillingRecipe> recipes = new ArrayList<>();
        for (PalettesColor color : PalettesColor.values()) {
            if (color.isNetherite()) continue;

            String path = "create/filling/railways/empty_paint_pitcher/with/railways/paint/" + color.getSerializedName();
            recipes.add(
                new ProcessingRecipeBuilder<>(FillingRecipe::new, Railways.asResource(path))
                    .withItemIngredients(Ingredient.of(CRItems.EMPTY_PAINT_PITCHER))
                    .withFluidIngredients(palettesPaint(color, PaintPitcherItem.FLUID_PER_LEVEL * PaintPitcherItem.MAX_LEVELS))
                    .withSingleItemOutput(CRItems.PAINT_PITCHERS.get(color).asStack())
                    .build()
            );
        }
        registration.addRecipes(spoutFillingType, recipes);
    }

    @Override
    public <T> void registerFluidSubtypes(@NotNull ISubtypeRegistration registration, @NotNull IPlatformFluidHelper<T> platformFluidHelper) {
        PaintFluidSubtypeInterpreter interpreter = new PaintFluidSubtypeInterpreter();
        VirtualFluid paintFluid = CRFluids.PAINT.get();
        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, paintFluid.getSource(), interpreter);
        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, paintFluid.getFlowing(), interpreter);
    }
}
