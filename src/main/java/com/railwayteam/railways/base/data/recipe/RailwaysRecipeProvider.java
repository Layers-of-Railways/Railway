package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.simibubi.create.AllItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class RailwaysRecipeProvider extends RecipeProvider {

  protected final List<GeneratedRecipe> all = new ArrayList<>();

  public RailwaysRecipeProvider(DataGenerator pGenerator) {
    super(pGenerator);
  }

  @Override
  protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> p_200404_1_) {
    all.forEach(c -> c.register(p_200404_1_));
    Railways.LOGGER.info(getName() + " registered " + all.size() + " recipe" + (all.size() == 1 ? "" : "s"));
  }

  protected GeneratedRecipe register(GeneratedRecipe recipe) {
    all.add(recipe);
    return recipe;
  }

  @FunctionalInterface
  public interface GeneratedRecipe {
    void register(Consumer<FinishedRecipe> consumer);
  }

  protected static class I {
    static TagKey<Item> string() {
      return Tags.Items.STRING;
    }

    static ItemLike precisionMechanism() {
      return AllItems.PRECISION_MECHANISM.get();
    }
  }
}
