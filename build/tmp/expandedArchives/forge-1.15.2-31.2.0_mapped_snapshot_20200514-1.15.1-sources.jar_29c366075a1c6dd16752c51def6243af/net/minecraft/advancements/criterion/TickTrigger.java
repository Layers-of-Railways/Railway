package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class TickTrigger extends AbstractCriterionTrigger<TickTrigger.Instance> {
   public static final ResourceLocation ID = new ResourceLocation("tick");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public TickTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      return new TickTrigger.Instance();
   }

   public void trigger(ServerPlayerEntity player) {
      this.func_227071_b_(player.getAdvancements());
   }

   public static class Instance extends CriterionInstance {
      public Instance() {
         super(TickTrigger.ID);
      }
   }
}