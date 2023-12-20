package com.railwayteam.railways.base.data.recipe.forge;

import com.railwayteam.railways.base.data.recipe.RailwaysMechanicalCraftingRecipeGen;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RailwaysMechanicalCraftingRecipeGenImpl extends RailwaysMechanicalCraftingRecipeGen {
    protected RailwaysMechanicalCraftingRecipeGenImpl(PackOutput pPackoutput) {
        super(pPackoutput);
    }

    public static RecipeProvider create(PackOutput gen) {
        RailwaysMechanicalCraftingRecipeGenImpl provider = new RailwaysMechanicalCraftingRecipeGenImpl(gen);
        return new RecipeProvider(gen) {
            @Override
            protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer) {
                provider.buildRecipes(writer);
            }
        };
    }
}
