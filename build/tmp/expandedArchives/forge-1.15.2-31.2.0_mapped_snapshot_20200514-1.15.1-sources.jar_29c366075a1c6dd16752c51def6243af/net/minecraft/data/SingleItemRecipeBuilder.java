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
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SingleItemRecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
   private String group;
   private final IRecipeSerializer<?> serializer;

   public SingleItemRecipeBuilder(IRecipeSerializer<?> serializerIn, Ingredient ingredientIn, IItemProvider resultProviderIn, int countIn) {
      this.serializer = serializerIn;
      this.result = resultProviderIn.asItem();
      this.ingredient = ingredientIn;
      this.count = countIn;
   }

   public static SingleItemRecipeBuilder stonecuttingRecipe(Ingredient ingredientIn, IItemProvider resultIn) {
      return new SingleItemRecipeBuilder(IRecipeSerializer.STONECUTTING, ingredientIn, resultIn, 1);
   }

   public static SingleItemRecipeBuilder stonecuttingRecipe(Ingredient ingredientIn, IItemProvider resultIn, int countIn) {
      return new SingleItemRecipeBuilder(IRecipeSerializer.STONECUTTING, ingredientIn, resultIn, countIn);
   }

   public SingleItemRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
      this.advancementBuilder.withCriterion(name, criterionIn);
      return this;
   }

   public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(this.result);
      if ((new ResourceLocation(save)).equals(resourcelocation)) {
         throw new IllegalStateException("Single Item Recipe " + save + " should remove its 'save' argument");
      } else {
         this.build(consumerIn, new ResourceLocation(save));
      }
   }

   public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
      this.validate(id);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
      consumerIn.accept(new SingleItemRecipeBuilder.Result(id, this.serializer, this.group == null ? "" : this.group, this.ingredient, this.result, this.count, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath())));
   }

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
      private final int count;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;
      private final IRecipeSerializer<?> serializer;

      public Result(ResourceLocation idIn, IRecipeSerializer<?> serializerIn, String groupIn, Ingredient ingredientIn, Item resultIn, int countIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
         this.id = idIn;
         this.serializer = serializerIn;
         this.group = groupIn;
         this.ingredient = ingredientIn;
         this.result = resultIn;
         this.count = countIn;
         this.advancementBuilder = advancementBuilderIn;
         this.advancementId = advancementIdIn;
      }

      public void serialize(JsonObject json) {
         if (!this.group.isEmpty()) {
            json.addProperty("group", this.group);
         }

         json.add("ingredient", this.ingredient.serialize());
         json.addProperty("result", Registry.ITEM.getKey(this.result).toString());
         json.addProperty("count", this.count);
      }

      /**
       * Gets the ID for the recipe.
       */
      public ResourceLocation getID() {
         return this.id;
      }

      public IRecipeSerializer<?> getSerializer() {
         return this.serializer;
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