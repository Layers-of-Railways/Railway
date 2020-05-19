package net.minecraft.world.storage.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class RandomRanges {
   private static final Map<ResourceLocation, Class<? extends IRandomRange>> field_216132_a = Maps.newHashMap();

   public static IRandomRange deserialize(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
      if (json.isJsonPrimitive()) {
         return context.deserialize(json, ConstantRange.class);
      } else {
         JsonObject jsonobject = json.getAsJsonObject();
         String s = JSONUtils.getString(jsonobject, "type", IRandomRange.UNIFORM.toString());
         Class<? extends IRandomRange> oclass = field_216132_a.get(new ResourceLocation(s));
         if (oclass == null) {
            throw new JsonParseException("Unknown generator: " + s);
         } else {
            return context.deserialize(jsonobject, oclass);
         }
      }
   }

   public static JsonElement serialize(IRandomRange randomRange, JsonSerializationContext context) {
      JsonElement jsonelement = context.serialize(randomRange);
      if (jsonelement.isJsonObject()) {
         jsonelement.getAsJsonObject().addProperty("type", randomRange.getType().toString());
      }

      return jsonelement;
   }

   static {
      field_216132_a.put(IRandomRange.UNIFORM, RandomValueRange.class);
      field_216132_a.put(IRandomRange.BINOMIAL, BinomialRange.class);
      field_216132_a.put(IRandomRange.CONSTANT, ConstantRange.class);
   }
}