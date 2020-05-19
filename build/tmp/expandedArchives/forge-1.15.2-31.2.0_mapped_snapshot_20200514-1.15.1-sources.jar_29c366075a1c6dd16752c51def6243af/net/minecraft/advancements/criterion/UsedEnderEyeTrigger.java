package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeTrigger extends AbstractCriterionTrigger<UsedEnderEyeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public UsedEnderEyeTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(json.get("distance"));
      return new UsedEnderEyeTrigger.Instance(minmaxbounds$floatbound);
   }

   public void trigger(ServerPlayerEntity player, BlockPos pos) {
      double d0 = player.getPosX() - (double)pos.getX();
      double d1 = player.getPosZ() - (double)pos.getZ();
      double d2 = d0 * d0 + d1 * d1;
      this.func_227070_a_(player.getAdvancements(), (p_227325_2_) -> {
         return p_227325_2_.test(d2);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.FloatBound distance;

      public Instance(MinMaxBounds.FloatBound p_i49730_1_) {
         super(UsedEnderEyeTrigger.ID);
         this.distance = p_i49730_1_;
      }

      public boolean test(double distanceSq) {
         return this.distance.testSquared(distanceSq);
      }
   }
}