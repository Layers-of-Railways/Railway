package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;

public class WeatherCheck implements ILootCondition {
   @Nullable
   private final Boolean raining;
   @Nullable
   private final Boolean thundering;

   private WeatherCheck(@Nullable Boolean raining, @Nullable Boolean thundering) {
      this.raining = raining;
      this.thundering = thundering;
   }

   public boolean test(LootContext p_test_1_) {
      ServerWorld serverworld = p_test_1_.getWorld();
      if (this.raining != null && this.raining != serverworld.isRaining()) {
         return false;
      } else {
         return this.thundering == null || this.thundering == serverworld.isThundering();
      }
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<WeatherCheck> {
      public Serializer() {
         super(new ResourceLocation("weather_check"), WeatherCheck.class);
      }

      public void serialize(JsonObject json, WeatherCheck value, JsonSerializationContext context) {
         json.addProperty("raining", value.raining);
         json.addProperty("thundering", value.thundering);
      }

      public WeatherCheck deserialize(JsonObject json, JsonDeserializationContext context) {
         Boolean obool = json.has("raining") ? JSONUtils.getBoolean(json, "raining") : null;
         Boolean obool1 = json.has("thundering") ? JSONUtils.getBoolean(json, "thundering") : null;
         return new WeatherCheck(obool, obool1);
      }
   }
}