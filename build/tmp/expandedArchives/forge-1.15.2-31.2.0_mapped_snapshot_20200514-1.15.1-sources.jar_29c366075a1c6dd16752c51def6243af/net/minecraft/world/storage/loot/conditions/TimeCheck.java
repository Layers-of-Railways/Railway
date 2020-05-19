package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;

public class TimeCheck implements ILootCondition {
   @Nullable
   private final Long field_227570_a_;
   private final RandomValueRange field_227571_b_;

   private TimeCheck(@Nullable Long p_i225898_1_, RandomValueRange p_i225898_2_) {
      this.field_227570_a_ = p_i225898_1_;
      this.field_227571_b_ = p_i225898_2_;
   }

   public boolean test(LootContext p_test_1_) {
      ServerWorld serverworld = p_test_1_.getWorld();
      long i = serverworld.getDayTime();
      if (this.field_227570_a_ != null) {
         i %= this.field_227570_a_;
      }

      return this.field_227571_b_.isInRange((int)i);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<TimeCheck> {
      public Serializer() {
         super(new ResourceLocation("time_check"), TimeCheck.class);
      }

      public void serialize(JsonObject json, TimeCheck value, JsonSerializationContext context) {
         json.addProperty("period", value.field_227570_a_);
         json.add("value", context.serialize(value.field_227571_b_));
      }

      public TimeCheck deserialize(JsonObject json, JsonDeserializationContext context) {
         Long olong = json.has("period") ? JSONUtils.func_226161_m_(json, "period") : null;
         RandomValueRange randomvaluerange = JSONUtils.deserializeClass(json, "value", context, RandomValueRange.class);
         return new TimeCheck(olong, randomvaluerange);
      }
   }
}