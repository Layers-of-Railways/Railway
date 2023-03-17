package com.railwayteam.railways.base.data.recipe.fabric;

import com.railwayteam.railways.base.data.recipe.RailwaysSequencedAssemblyRecipeGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.function.Consumer;

public class RailwaysSequencedAssemblyRecipeGenImpl extends RailwaysSequencedAssemblyRecipeGen {
	protected RailwaysSequencedAssemblyRecipeGenImpl(DataGenerator pGenerator) {
		super(pGenerator);
	}

	public static RecipeProvider create(DataGenerator gen) {
		RailwaysSequencedAssemblyRecipeGenImpl provider = new RailwaysSequencedAssemblyRecipeGenImpl(gen);
		return new FabricRecipeProvider((FabricDataGenerator) gen) {
			@Override
			protected void generateRecipes(Consumer<FinishedRecipe> exporter) {
				provider.registerRecipes(exporter);
			}
		};
	}
}
