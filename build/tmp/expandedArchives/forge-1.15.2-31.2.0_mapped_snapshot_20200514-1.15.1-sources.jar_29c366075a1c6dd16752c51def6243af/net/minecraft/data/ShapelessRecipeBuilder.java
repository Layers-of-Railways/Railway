package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapelessRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final int count;
   private final List<Ingredient> ingredients = Lists.newArrayList();
   private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
   private String group;

   public ShapelessRecipeBuilder(IItemProvider resultIn, int countIn) {
      this.result = resultIn.asItem();
      this.count = countIn;
   }

   /**
    * Creates a new builder for a shapeless recipe.
    */
   public static ShapelessRecipeBuilder shapelessRecipe(IItemProvider resultIn) {
      return new ShapelessRecipeBuilder(resultIn, 1);
   }

   /**
    * Creates a new builder for a shapeless recipe.
    */
   public static ShapelessRecipeBuilder shapelessRecipe(IItemProvider resultIn, int countIn) {
      return new ShapelessRecipeBuilder(resultIn, countIn);
   }

   /**
    * Adds an ingredient that can be any item in the given tag.
    */
   public ShapelessRecipeBuilder addIngredient(Tag<Item> tagIn) {
      return this.addIngredient(Ingredient.fromTag(tagIn));
   }

   /**
    * Adds an ingredient of the given item.
    */
   public ShapelessRecipeBuilder addIngredient(IItemProvider itemIn) {
      return this.addIngredient(itemIn, 1);
   }

   /**
    * Adds the given ingredient multiple times.
    */
   public ShapelessRecipeBuilder addIngredient(IItemProvider itemIn, int quantity) {
      for(int i = 0; i < quantity; ++i) {
         this.addIngredient(Ingredient.fromItems(itemIn));
      }

      return this;
   }

   /**
    * Adds an ingredient.
    */
   public ShapelessRecipeBuilder addIngredient(Ingredient ingredientIn) {
      return this.addIngredient(ingredientIn, 1);
   }

   /**
    * Adds an ingredient multiple times.
    */
   public ShapelessRecipeBuilder addIngredient(Ingredient ingredientIn, int quantity) {
      for(int i = 0; i < quantity; ++i) {
         this.ingredients.add(ingredientIn);
      }

      return this;
   }

   /**
    * Adds a criterion needed to unlock the recipe.
    */
   public ShapelessRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
      this.advancementBuilder.withCriterion(name, criterionIn);
      return this;
   }

   public ShapelessRecipeBuilder setGroup(String groupIn) {
      this.group = groupIn;
      return this;
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn) {
      this.build(consumerIn, Registry.ITEM.getKey(this.result));
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}. Use {@link #build(Consumer)} if save is the same as the ID for
    * the result.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(this.result);
      if ((new ResourceLocation(save)).equals(resourcelocation)) {
         throw new IllegalStateException("Shapeless Recipe " + save + " should remove its 'save' argument");
      } else {
         this.build(consumerIn, new ResourceLocation(save));
      }
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
      this.validate(id);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
      consumerIn.accept(new ShapelessRecipeBuilder.Result(id, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath())));
   }

   /**
    * Makes sure that this recipe is valid and obtainable.
    */
   private void validate(ResourceLocation id) {
      if (this.advancementBuilder.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + id);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<Ingredient> ingredients;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation idIn, Item resultIn, int countIn, String groupIn, List<Ingredient> ingredientsIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
         this.id = idIn;
         this.result = resultIn;
         this.count = countIn;
         this.group = groupIn;
         this.ingredients = ingredientsIn;
         this.advancementBuilder = advancementBuilderIn;
         this.advancementId = advancementIdIn;
      }

      public void serialize(JsonObject json) {
         if (!this.group.isEmpty()) {
            json.addProperty("group", this.group);
         }

         JsonArray jsonarray = new JsonArray();

         for(Ingredient ingredient : this.ingredients) {
            jsonarray.add(ingredient.serialize());
         }

         json.add("ingredients", jsonarray);
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", Registry.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            jsonobject.addProperty("count", this.count);
         }

         json.add("result", jsonobject);
      }

      public IRecipeSerializer<?> getSerializer() {
         return IRecipeSerializer.CRAFTING_SHAPELESS;
      }

      /**
       * Gets the ID for the recipe.
       */
      public ResourceLocation getID() {
         return this.id;
      }

      /**
       * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
       */
      @Nullable
      public JsonObject getAdvancementJson() {
         return this.advancementBuilder.serialize();
      }

      /**
       * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
       * is non-null.
       */
      @Nullable
      public ResourceLocation getAdvancementID() {
         return this.advancementId;
      }
   }
}