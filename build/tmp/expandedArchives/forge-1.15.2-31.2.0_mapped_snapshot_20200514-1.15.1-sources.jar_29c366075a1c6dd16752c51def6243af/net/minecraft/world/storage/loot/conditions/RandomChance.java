package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class RandomChance implements ILootCondition {
   private final float chance;

   private RandomChance(float chanceIn) {
      this.chance = chanceIn;
   }

   public boolean test(LootContext p_test_1_) {
      return p_test_1_.getRandom().nextFloat() < this.chance;
   }

   public static ILootCondition.IBuilder builder(float chanceIn) {
      return () -> {
         return new RandomChance(chanceIn);
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<RandomChance> {
      protected Serializer() {
         super(new ResourceLocation("random_chance"), RandomChance.class);
      }

      public void serialize(JsonObject json, RandomChance value, JsonSerializationContext context) {
         json.addProperty("chance", value.chance);
      }

      public RandomChance deserialize(JsonObject json, JsonDeserializationContext context) {
         return new RandomChance(JSONUtils.getFloat(json, "chance"));
      }
   }
}