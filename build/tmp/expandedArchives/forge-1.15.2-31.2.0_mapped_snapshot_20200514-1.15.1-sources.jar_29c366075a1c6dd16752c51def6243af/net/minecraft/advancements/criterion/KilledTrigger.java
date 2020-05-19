package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class KilledTrigger extends AbstractCriterionTrigger<KilledTrigger.Instance> {
   private final ResourceLocation id;

   public KilledTrigger(ResourceLocation id) {
      this.id = id;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public KilledTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      return new KilledTrigger.Instance(this.id, EntityPredicate.deserialize(json.get("entity")), DamageSourcePredicate.deserialize(json.get("killing_blow")));
   }

   public void trigger(ServerPlayerEntity player, Entity entity, DamageSource source) {
      this.func_227070_a_(player.getAdvancements(), (p_226846_3_) -> {
         return p_226846_3_.test(player, entity, source);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate entity;
      private final DamageSourcePredicate killingBlow;

      public Instance(ResourceLocation criterionIn, EntityPredicate entity, DamageSourcePredicate killingBlow) {
         super(criterionIn);
         this.entity = entity;
         this.killingBlow = killingBlow;
      }

      public static KilledTrigger.Instance playerKilledEntity(EntityPredicate.Builder p_203928_0_) {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, p_203928_0_.build(), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.Instance playerKilledEntity() {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.Instance playerKilledEntity(EntityPredicate.Builder p_203929_0_, DamageSourcePredicate.Builder p_203929_1_) {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, p_203929_0_.build(), p_203929_1_.build());
      }

      public static KilledTrigger.Instance entityKilledPlayer() {
         return new KilledTrigger.Instance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public boolean test(ServerPlayerEntity player, Entity entity, DamageSource source) {
         return !this.killingBlow.test(player, source) ? false : this.entity.test(player, entity);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("entity", this.entity.serialize());
         jsonobject.add("killing_blow", this.killingBlow.serialize());
         return jsonobject;
      }
   }
}