package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EntityHurtPlayerTrigger extends AbstractCriterionTrigger<EntityHurtPlayerTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public EntityHurtPlayerTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      DamagePredicate damagepredicate = DamagePredicate.deserialize(json.get("damage"));
      return new EntityHurtPlayerTrigger.Instance(damagepredicate);
   }

   public void trigger(ServerPlayerEntity player, DamageSource source, float amountDealt, float amountTaken, boolean wasBlocked) {
      this.func_227070_a_(player.getAdvancements(), (p_226603_5_) -> {
         return p_226603_5_.test(player, source, amountDealt, amountTaken, wasBlocked);
      });
   }

   public static class Instance extends CriterionInstance {
      private final DamagePredicate damage;

      public Instance(DamagePredicate damage) {
         super(EntityHurtPlayerTrigger.ID);
         this.damage = damage;
      }

      public static EntityHurtPlayerTrigger.Instance forDamage(DamagePredicate.Builder p_203921_0_) {
         return new EntityHurtPlayerTrigger.Instance(p_203921_0_.build());
      }

      public boolean test(ServerPlayerEntity player, DamageSource source, float amountDealt, float amountTaken, boolean wasBlocked) {
         return this.damage.test(player, source, amountDealt, amountTaken, wasBlocked);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("damage", this.damage.serialize());
         return jsonobject;
      }
   }
}