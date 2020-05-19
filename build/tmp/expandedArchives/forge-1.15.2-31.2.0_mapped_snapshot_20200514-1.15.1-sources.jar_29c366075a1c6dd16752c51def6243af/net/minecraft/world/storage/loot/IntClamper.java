package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;

public class IntClamper implements IntUnaryOperator {
   private final Integer field_215852_a;
   private final Integer field_215853_b;
   private final IntUnaryOperator field_215854_c;

   private IntClamper(@Nullable Integer p_i51273_1_, @Nullable Integer p_i51273_2_) {
      this.field_215852_a = p_i51273_1_;
      this.field_215853_b = p_i51273_2_;
      if (p_i51273_1_ == null) {
         if (p_i51273_2_ == null) {
            this.field_215854_c = (p_215845_0_) -> {
               return p_215845_0_;
            };
         } else {
            int i = p_i51273_2_;
            this.field_215854_c = (p_215844_1_) -> {
               return Math.min(i, p_215844_1_);
            };
         }
      } else {
         int k = p_i51273_1_;
         if (p_i51273_2_ == null) {
            this.field_215854_c = (p_215846_1_) -> {
               return Math.max(k, p_215846_1_);
            };
         } else {
            int j = p_i51273_2_;
            this.field_215854_c = (p_215847_2_) -> {
               return MathHelper.clamp(p_215847_2_, k, j);
            };
         }
      }

   }

   public static IntClamper func_215843_a(int p_215843_0_, int p_215843_1_) {
      return new IntClamper(p_215843_0_, p_215843_1_);
   }

   public static IntClamper func_215848_a(int p_215848_0_) {
      return new IntClamper(p_215848_0_, (Integer)null);
   }

   public static IntClamper func_215851_b(int p_215851_0_) {
      return new IntClamper((Integer)null, p_215851_0_);
   }

   public int applyAsInt(int p_applyAsInt_1_) {
      return this.field_215854_c.applyAsInt(p_applyAsInt_1_);
   }

   public static class Serializer implements JsonDeserializer<IntClamper>, JsonSerializer<IntClamper> {
      public IntClamper deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "value");
         Integer integer = jsonobject.has("min") ? JSONUtils.getInt(jsonobject, "min") : null;
         Integer integer1 = jsonobject.has("max") ? JSONUtils.getInt(jsonobject, "max") : null;
         return new IntClamper(integer, integer1);
      }

      public JsonElement serialize(IntClamper p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         if (p_serialize_1_.field_215853_b != null) {
            jsonobject.addProperty("max", p_serialize_1_.field_215853_b);
         }

         if (p_serialize_1_.field_215852_a != null) {
            jsonobject.addProperty("min", p_serialize_1_.field_215852_a);
         }

         return jsonobject;
      }
   }
}