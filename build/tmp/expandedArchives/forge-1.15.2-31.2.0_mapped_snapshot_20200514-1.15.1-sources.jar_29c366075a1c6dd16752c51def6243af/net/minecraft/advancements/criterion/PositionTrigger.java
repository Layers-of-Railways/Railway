package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class PositionTrigger extends AbstractCriterionTrigger<PositionTrigger.Instance> {
   private final ResourceLocation id;

   public PositionTrigger(ResourceLocation id) {
      this.id = id;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public PositionTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      LocationPredicate locationpredicate = LocationPredicate.deserialize(json);
      return new PositionTrigger.Instance(this.id, locationpredicate);
   }

   public void trigger(ServerPlayerEntity player) {
      this.func_227070_a_(player.getAdvancements(), (p_226923_1_) -> {
         return p_226923_1_.test(player.getServerWorld(), player.getPosX(), player.getPosY(), player.getPosZ());
      });
   }

   public static class Instance extends CriterionInstance {
      private final LocationPredicate location;

      public Instance(ResourceLocation criterionIn, LocationPredicate location) {
         super(criterionIn);
         this.location = location;
      }

      public static PositionTrigger.Instance forLocation(LocationPredicate p_203932_0_) {
         return new PositionTrigger.Instance(CriteriaTriggers.LOCATION.id, p_203932_0_);
      }

      public static PositionTrigger.Instance sleptInBed() {
         return new PositionTrigger.Instance(CriteriaTriggers.SLEPT_IN_BED.id, LocationPredicate.ANY);
      }

      public static PositionTrigger.Instance func_215120_d() {
         return new PositionTrigger.Instance(CriteriaTriggers.HERO_OF_THE_VILLAGE.id, LocationPredicate.ANY);
      }

      public boolean test(ServerWorld world, double x, double y, double z) {
         return this.location.test(world, x, y, z);
      }

      public JsonElement serialize() {
         return this.location.serialize();
      }
   }
}