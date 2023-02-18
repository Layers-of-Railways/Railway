package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.multiloader.CommonTag;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class RailwaysRecipeProvider extends RecipeProvider {

  protected final List<GeneratedRecipe> all = new ArrayList<>();

  public RailwaysRecipeProvider(DataGenerator pGenerator) {
    super(pGenerator);
  }

  public void registerRecipes(@NotNull Consumer<FinishedRecipe> p_200404_1_) {
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

  @SuppressWarnings("SameReturnValue")
  public static class Ingredients {
    public static TagKey<Item> string() {
      return CommonTag.STRING.resolve();
    }

    public static ItemLike precisionMechanism() {
      return AllItems.PRECISION_MECHANISM.get();
    }

    public static TagKey<Item> ironNugget() {
      return CommonTag.IRON_NUGGETS.resolve();
    }

    public static TagKey<Item> zincNugget() {
      return CommonTag.ZINC_NUGGETS.resolve();
    }

    public static ItemLike girder() {
      return AllBlocks.METAL_GIRDER.get();
    }

    public static ItemLike metalBracket() {
      return AllBlocks.METAL_BRACKET.get();
    }

    public static ItemLike ironSheet() {
      return AllItems.IRON_SHEET.get();
    }
  }
}
