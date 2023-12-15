package com.railwayteam.railways.base.data.recipe.fabric;

import com.railwayteam.railways.base.data.recipe.RailwaysMechanicalCraftingRecipeGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.function.Consumer;

public class RailwaysMechanicalCraftingRecipeGenImpl extends RailwaysMechanicalCraftingRecipeGen {
    protected RailwaysMechanicalCraftingRecipeGenImpl(PackOutput pPackoutput) {
        super(pPackoutput);
    }

    public static RecipeProvider create(PackOutput gen) {
        RailwaysMechanicalCraftingRecipeGenImpl provider = new RailwaysMechanicalCraftingRecipeGenImpl(gen);
        return new FabricRecipeProvider(gen) {
            @Override
            public void buildRecipes(Consumer<FinishedRecipe> exporter) {
                provider.buildRecipes(exporter);
            }
        };
    }
}
