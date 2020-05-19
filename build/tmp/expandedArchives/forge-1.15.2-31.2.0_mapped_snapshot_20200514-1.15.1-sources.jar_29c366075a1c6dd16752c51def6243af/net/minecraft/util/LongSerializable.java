package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public final class LongSerializable implements IDynamicSerializable {
   private final long value;

   private LongSerializable(long p_i51540_1_) {
      this.value = p_i51540_1_;
   }

   public long get() {
      return this.value;
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createLong(this.value);
   }

   public static LongSerializable deserialize(Dynamic<?> p_223462_0_) {
      return new LongSerializable(p_223462_0_.asNumber(Integer.valueOf(0)).longValue());
   }

   public static LongSerializable of(long p_223463_0_) {
      return new LongSerializable(p_223463_0_);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         LongSerializable longserializable = (LongSerializable)p_equals_1_;
         return this.value == longserializable.value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Long.hashCode(this.value);
   }

   public String toString() {
      return Long.toString(this.value);
   }
}