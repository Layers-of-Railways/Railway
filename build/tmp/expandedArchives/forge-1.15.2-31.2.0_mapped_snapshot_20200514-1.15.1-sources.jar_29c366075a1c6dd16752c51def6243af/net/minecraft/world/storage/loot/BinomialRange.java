package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public final class BinomialRange implements IRandomRange {
   private final int n;
   private final float p;

   public BinomialRange(int n, float p) {
      this.n = n;
      this.p = p;
   }

   public int generateInt(Random rand) {
      int i = 0;

      for(int j = 0; j < this.n; ++j) {
         if (rand.nextFloat() < this.p) {
            ++i;
         }
      }

      return i;
   }

   public static BinomialRange of(int nIn, float pIn) {
      return new BinomialRange(nIn, pIn);
   }

   public ResourceLocation getType() {
      return BINOMIAL;
   }

   public static class Serializer implements JsonDeserializer<BinomialRange>, JsonSerializer<BinomialRange> {
      public BinomialRange deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "value");
         int i = JSONUtils.getInt(jsonobject, "n");
         float f = JSONUtils.getFloat(jsonobject, "p");
         return new BinomialRange(i, f);
      }

      public JsonElement serialize(BinomialRange p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("n", p_serialize_1_.n);
         jsonobject.addProperty("p", p_serialize_1_.p);
         return jsonobject;
      }
   }
}