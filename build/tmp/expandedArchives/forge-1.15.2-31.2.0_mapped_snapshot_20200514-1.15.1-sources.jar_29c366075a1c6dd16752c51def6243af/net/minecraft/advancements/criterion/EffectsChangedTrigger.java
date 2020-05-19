package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class EffectsChangedTrigger extends AbstractCriterionTrigger<EffectsChangedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public EffectsChangedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.deserialize(json.get("effects"));
      return new EffectsChangedTrigger.Instance(mobeffectspredicate);
   }

   public void trigger(ServerPlayerEntity player) {
      this.func_227070_a_(player.getAdvancements(), (p_226524_1_) -> {
         return p_226524_1_.test(player);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MobEffectsPredicate effects;

      public Instance(MobEffectsPredicate effects) {
         super(EffectsChangedTrigger.ID);
         this.effects = effects;
      }

      public static EffectsChangedTrigger.Instance forEffect(MobEffectsPredicate p_203917_0_) {
         return new EffectsChangedTrigger.Instance(p_203917_0_);
      }

      public boolean test(ServerPlayerEntity player) {
         return this.effects.test(player);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("effects", this.effects.serialize());
         return jsonobject;
      }
   }
}