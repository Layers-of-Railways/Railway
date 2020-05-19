package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class TameAnimalTrigger extends AbstractCriterionTrigger<TameAnimalTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("tame_animal");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public TameAnimalTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      EntityPredicate entitypredicate = EntityPredicate.deserialize(json.get("entity"));
      return new TameAnimalTrigger.Instance(entitypredicate);
   }

   public void trigger(ServerPlayerEntity player, AnimalEntity entity) {
      this.func_227070_a_(player.getAdvancements(), (p_227251_2_) -> {
         return p_227251_2_.test(player, entity);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate entity;

      public Instance(EntityPredicate entity) {
         super(TameAnimalTrigger.ID);
         this.entity = entity;
      }

      public static TameAnimalTrigger.Instance any() {
         return new TameAnimalTrigger.Instance(EntityPredicate.ANY);
      }

      public static TameAnimalTrigger.Instance create(EntityPredicate p_215124_0_) {
         return new TameAnimalTrigger.Instance(p_215124_0_);
      }

      public boolean test(ServerPlayerEntity player, AnimalEntity entity) {
         return this.entity.test(player, entity);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("entity", this.entity.serialize());
         return jsonobject;
      }
   }
}