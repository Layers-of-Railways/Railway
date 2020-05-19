package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IDynamicDeserializer<T> {
   Logger field_214908_a = LogManager.getLogger();

   T deserialize(Dynamic<?> p_deserialize_1_);

   static <T, V, U extends IDynamicDeserializer<V>> V func_214907_a(Dynamic<T> p_214907_0_, Registry<U> p_214907_1_, String p_214907_2_, V p_214907_3_) {
      U u = p_214907_1_.getOrDefault(new ResourceLocation(p_214907_0_.get(p_214907_2_).asString("")));
      V v;
      if (u != null) {
         v = (V)u.deserialize(p_214907_0_);
      } else {
         field_214908_a.error("Unknown type {}, replacing with {}", p_214907_0_.get(p_214907_2_).asString(""), p_214907_3_);
         v = p_214907_3_;
      }

      return v;
   }
}