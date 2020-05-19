package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeUnlockedTrigger extends AbstractCriterionTrigger<RecipeUnlockedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public RecipeUnlockedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "recipe"));
      return new RecipeUnlockedTrigger.Instance(resourcelocation);
   }

   public void trigger(ServerPlayerEntity player, IRecipe<?> recipe) {
      this.func_227070_a_(player.getAdvancements(), (p_227018_1_) -> {
         return p_227018_1_.test(recipe);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ResourceLocation recipe;

      public Instance(ResourceLocation recipe) {
         super(RecipeUnlockedTrigger.ID);
         this.recipe = recipe;
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("recipe", this.recipe.toString());
         return jsonobject;
      }

      public boolean test(IRecipe<?> recipe) {
         return this.recipe.equals(recipe.getId());
      }
   }
}