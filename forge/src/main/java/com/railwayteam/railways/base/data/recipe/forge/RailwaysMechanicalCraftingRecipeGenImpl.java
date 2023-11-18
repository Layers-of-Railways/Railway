package com.railwayteam.railways.base.data.recipe.forge;

import com.railwayteam.railways.base.data.recipe.RailwaysMechanicalCraftingRecipeGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RailwaysMechanicalCraftingRecipeGenImpl extends RailwaysMechanicalCraftingRecipeGen {
    protected RailwaysMechanicalCraftingRecipeGenImpl(DataGenerator pGenerator) {
        super(pGenerator);
    }

    public static RecipeProvider create(DataGenerator gen) {
        RailwaysMechanicalCraftingRecipeGenImpl provider = new RailwaysMechanicalCraftingRecipeGenImpl(gen);
        return new RecipeProvider(gen) {
            @Override
            protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
                provider.registerRecipes(consumer);
            }
        };
    }
}
