package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CookingRecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
   private String group;
   private final CookingRecipeSerializer<?> recipeSerializer;

   private CookingRecipeBuilder(IItemProvider resultIn, Ingredient ingredientIn, float experienceIn, int cookingTimeIn, CookingRecipeSerializer<?> serializer) {
      this.result = resultIn.asItem();
      this.ingredient = ingredientIn;
      this.experience = experienceIn;
      this.cookingTime = cookingTimeIn;
      this.recipeSerializer = serializer;
   }

   public static CookingRecipeBuilder cookingRecipe(Ingredient ingredientIn, IItemProvider resultIn, float experienceIn, int cookingTimeIn, CookingRecipeSerializer<?> serializer) {
      return new CookingRecipeBuilder(resultIn, ingredientIn, experienceIn, cookingTimeIn, serializer);
   }

   public static CookingRecipeBuilder blastingRecipe(Ingredient ingredientIn, IItemProvider resultIn, float experienceIn, int cookingTimeIn) {
      return cookingRecipe(ingredientIn, resultIn, experienceIn, cookingTimeIn, IRecipeSerializer.BLASTING);
   }

   public static CookingRecipeBuilder smeltingRecipe(Ingredient ingredientIn, IItemProvider resultIn, float experienceIn, int cookingTimeIn) {
      return cookingRecipe(ingredientIn, resultIn, experienceIn, cookingTimeIn, IRecipeSerializer.SMELTING);
   }

   public CookingRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
      this.advancementBuilder.withCriterion(name, criterionIn);
      return this;
   }

   public void build(Consumer<IFinishedRecipe> consumerIn) {
      this.build(consumerIn, Registry.ITEM.getKey(this.result));
   }

   public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(this.result);
      ResourceLocation resourcelocation1 = new ResourceLocation(save);
      if (resourcelocation1.equals(resourcelocation)) {
         throw new IllegalStateException("Recipe " + resourcelocation1 + " should remove its 'save' argument");
      } else {
         this.build(consumerIn, resourcelocation1);
      }
   }

   public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
      this.validate(id);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
      consumerIn.accept(new CookingRecipeBuilder.Result(id, this.group == null ? "" : this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath()), this.recipeSerializer));
   }

   /**
    * Makes sure that this obtainable.
    */
   private void validate(ResourceLocation id) {
      if (this.advancementBuilder.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + id);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final Ingredient ingredient;
      private final Item result;
      private final float experience;
      private final int cookingTime;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;
      private final IRecipeSerializer<? extends AbstractCookingRecipe> serializer;

      public Result(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, Item resultIn, float experienceIn, int cookingTimeIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn, IRecipeSerializer<? extends AbstractCookingRecipe> serializerIn) {
         this.id = idIn;
         this.group = groupIn;
         this.ingredient = ingredientIn;
         this.result = resultIn;
         this.experience = experienceIn;
         this.cookingTime = cookingTimeIn;
         this.advancementBuilder = advancementBuilderIn;
         this.advancementId = advancementIdIn;
         this.serializer = serializerIn;
      }

      public void serialize(JsonObject json) {
         if (!this.group.isEmpty()) {
            json.addProperty("group", this.group);
         }

         json.add("ingredient", this.ingredient.serialize());
         json.addProperty("result", Registry.ITEM.getKey(this.result).toString());
         json.addProperty("experience", this.experience);
         json.addProperty("cookingtime", this.cookingTime);
      }

      public IRecipeSerializer<?> getSerializer() {
         return this.serializer;
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