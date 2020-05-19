package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class FrequencyConfig implements IPlacementConfig {
   public final int count;

   public FrequencyConfig(int count) {
      this.count = count;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("count"), ops.createInt(this.count))));
   }

   public static FrequencyConfig deserialize(Dynamic<?> p_214721_0_) {
      int i = p_214721_0_.get("count").asInt(0);
      return new FrequencyConfig(i);
   }
}