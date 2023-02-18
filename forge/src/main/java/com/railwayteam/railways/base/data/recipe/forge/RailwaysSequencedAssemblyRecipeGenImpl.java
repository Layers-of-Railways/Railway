package com.railwayteam.railways.base.data.recipe.forge;

import com.railwayteam.railways.base.data.recipe.RailwaysSequencedAssemblyRecipeGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RailwaysSequencedAssemblyRecipeGenImpl extends RailwaysSequencedAssemblyRecipeGen {
	protected RailwaysSequencedAssemblyRecipeGenImpl(DataGenerator pGenerator) {
		super(pGenerator);
	}

	public static RecipeProvider create(DataGenerator gen) {
		RailwaysSequencedAssemblyRecipeGenImpl provider = new RailwaysSequencedAssemblyRecipeGenImpl(gen);
		return new RecipeProvider(gen) {
			@Override
			protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
				provider.registerRecipes(consumer);
			}
		};
	}
}
