package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RandomValueRange implements IRandomRange {
   private final float min;
   private final float max;

   public RandomValueRange(float minIn, float maxIn) {
      this.min = minIn;
      this.max = maxIn;
   }

   public RandomValueRange(float value) {
      this.min = value;
      this.max = value;
   }

   public static RandomValueRange of(float minIn, float maxIn) {
      return new RandomValueRange(minIn, maxIn);
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   public int generateInt(Random rand) {
      return MathHelper.nextInt(rand, MathHelper.floor(this.min), MathHelper.floor(this.max));
   }

   public float generateFloat(Random rand) {
      return MathHelper.nextFloat(rand, this.min, this.max);
   }

   public boolean isInRange(int value) {
      return (float)value <= this.max && (float)value >= this.min;
   }

   public ResourceLocation getType() {
      return UNIFORM;
   }

   public static class Serializer implements JsonDeserializer<RandomValueRange>, JsonSerializer<RandomValueRange> {
      public RandomValueRange deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (JSONUtils.isNumber(p_deserialize_1_)) {
            return new RandomValueRange(JSONUtils.getFloat(p_deserialize_1_, "value"));
         } else {
            JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "value");
            float f = JSONUtils.getFloat(jsonobject, "min");
            float f1 = JSONUtils.getFloat(jsonobject, "max");
            return new RandomValueRange(f, f1);
         }
      }

      public JsonElement serialize(RandomValueRange p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         if (p_serialize_1_.min == p_serialize_1_.max) {
            return new JsonPrimitive(p_serialize_1_.min);
         } else {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("min", p_serialize_1_.min);
            jsonobject.addProperty("max", p_serialize_1_.max);
            return jsonobject;
         }
      }
   }
}