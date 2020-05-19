package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class TopSolidRangeConfig implements IPlacementConfig {
   public final int min;
   public final int max;

   public TopSolidRangeConfig(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("min"), ops.createInt(this.min), ops.createString("max"), ops.createInt(this.max))));
   }

   public static TopSolidRangeConfig deserialize(Dynamic<?> p_214725_0_) {
      int i = p_214725_0_.get("min").asInt(0);
      int j = p_214725_0_.get("max").asInt(0);
      return new TopSolidRangeConfig(i, j);
   }
}