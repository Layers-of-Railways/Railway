package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class LevitationTrigger extends AbstractCriterionTrigger<LevitationTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("levitation");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public LevitationTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      DistancePredicate distancepredicate = DistancePredicate.deserialize(json.get("distance"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("duration"));
      return new LevitationTrigger.Instance(distancepredicate, minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity player, Vec3d startPos, int duration) {
      this.func_227070_a_(player.getAdvancements(), (p_226852_3_) -> {
         return p_226852_3_.test(player, startPos, duration);
      });
   }

   public static class Instance extends CriterionInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.IntBound duration;

      public Instance(DistancePredicate p_i49729_1_, MinMaxBounds.IntBound p_i49729_2_) {
         super(LevitationTrigger.ID);
         this.distance = p_i49729_1_;
         this.duration = p_i49729_2_;
      }

      public static LevitationTrigger.Instance forDistance(DistancePredicate p_203930_0_) {
         return new LevitationTrigger.Instance(p_203930_0_, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(ServerPlayerEntity player, Vec3d startPos, int durationIn) {
         if (!this.distance.test(startPos.x, startPos.y, startPos.z, player.getPosX(), player.getPosY(), player.getPosZ())) {
            return false;
         } else {
            return this.duration.test(durationIn);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("distance", this.distance.serialize());
         jsonobject.add("duration", this.duration.serialize());
         return jsonobject;
      }
   }
}