package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BrewedPotionTrigger extends AbstractCriterionTrigger<BrewedPotionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("brewed_potion");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public BrewedPotionTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      Potion potion = null;
      if (json.has("potion")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "potion"));
         potion = Registry.POTION.getValue(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown potion '" + resourcelocation + "'");
         });
      }

      return new BrewedPotionTrigger.Instance(potion);
   }

   public void trigger(ServerPlayerEntity player, Potion potionIn) {
      this.func_227070_a_(player.getAdvancements(), (p_226301_1_) -> {
         return p_226301_1_.test(potionIn);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Potion potion;

      public Instance(@Nullable Potion potion) {
         super(BrewedPotionTrigger.ID);
         this.potion = potion;
      }

      public static BrewedPotionTrigger.Instance brewedPotion() {
         return new BrewedPotionTrigger.Instance((Potion)null);
      }

      public boolean test(Potion potion) {
         return this.potion == null || this.potion == potion;
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.potion != null) {
            jsonobject.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return jsonobject;
      }
   }
}