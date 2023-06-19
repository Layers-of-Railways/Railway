package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.multiloader.CommonTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
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
      return CommonTags.STRING.tag;
    }

    public static ItemLike precisionMechanism() {
      return AllItems.PRECISION_MECHANISM.get();
    }

    public static TagKey<Item> ironNugget() {
      return CommonTags.IRON_NUGGETS.tag;
    }

    public static TagKey<Item> ironIngot() {
      return CommonTags.IRON_INGOTS.tag;
    }

    public static TagKey<Item> zincNugget() {
      return CommonTags.ZINC_NUGGETS.tag;
    }

    public static ItemLike girder() {
      return AllBlocks.METAL_GIRDER.get();
    }

    public static ItemLike metalBracket() {
      return AllBlocks.METAL_BRACKET.get();
    }

    public static TagKey<Item> ironSheet() {
      return CommonTags.IRON_PLATES.tag;
    }

    public static TagKey<Item> fence() {
      return TagKey.<Item>create(Registry.ITEM_REGISTRY, new ResourceLocation("minecraft:fences"));
    }

    public static ItemLike campfire() {
      return Blocks.CAMPFIRE;
    }

    public static ItemLike redstone() {
      return Items.REDSTONE;
    }

    public static ItemLike lever() { return Items.LEVER; }

    public static ItemLike cogwheel() {
      return AllBlocks.COGWHEEL.get();
    }

    public static ItemLike railwayCasing() {
      return AllBlocks.RAILWAY_CASING.get();
    }

    public static ItemLike brassCasing() {
      return AllBlocks.BRASS_CASING.get();
    }

    public static ItemLike andesiteCasing() {
      return AllBlocks.ANDESITE_CASING.get();
    }

    public static ItemLike propeller() {
      return AllItems.PROPELLER.get();
    }

    public static ItemLike electronTube() {
      return AllItems.ELECTRON_TUBE.get();
    }

    public static TagKey<Item> copperIngot() {
      return CommonTags.COPPER_INGOTS.tag;
    }

    public static TagKey<Item> brassNugget() {
      return CommonTags.BRASS_NUGGETS.tag;
    }

    public static ItemLike phantomMembrane() {
      return Items.PHANTOM_MEMBRANE;
    }
  }
}
