package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountRangeConfig implements IPlacementConfig {
   public final int count;
   public final int bottomOffset;
   public final int topOffset;
   public final int maximum;

   public CountRangeConfig(int count, int bottomOffset, int topOffset, int maximum) {
      this.count = count;
      this.bottomOffset = bottomOffset;
      this.topOffset = topOffset;
      this.maximum = maximum;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("count"), ops.createInt(this.count), ops.createString("bottom_offset"), ops.createInt(this.bottomOffset), ops.createString("top_offset"), ops.createInt(this.topOffset), ops.createString("maximum"), ops.createInt(this.maximum))));
   }

   public static CountRangeConfig deserialize(Dynamic<?> p_214733_0_) {
      int i = p_214733_0_.get("count").asInt(0);
      int j = p_214733_0_.get("bottom_offset").asInt(0);
      int k = p_214733_0_.get("top_offset").asInt(0);
      int l = p_214733_0_.get("maximum").asInt(0);
      return new CountRangeConfig(i, j, k, l);
   }
}