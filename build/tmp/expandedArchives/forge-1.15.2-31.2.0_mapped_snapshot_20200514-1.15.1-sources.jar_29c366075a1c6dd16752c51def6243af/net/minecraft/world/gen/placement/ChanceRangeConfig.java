package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ChanceRangeConfig implements IPlacementConfig {
   public final float chance;
   public final int bottomOffset;
   public final int topOffset;
   public final int top;

   public ChanceRangeConfig(float chance, int bottomOffset, int topOffset, int top) {
      this.chance = chance;
      this.bottomOffset = bottomOffset;
      this.topOffset = topOffset;
      this.top = top;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("chance"), ops.createFloat(this.chance), ops.createString("bottom_offset"), ops.createInt(this.bottomOffset), ops.createString("top_offset"), ops.createInt(this.topOffset), ops.createString("top"), ops.createInt(this.top))));
   }

   public static ChanceRangeConfig deserialize(Dynamic<?> p_214732_0_) {
      float f = p_214732_0_.get("chance").asFloat(0.0F);
      int i = p_214732_0_.get("bottom_offset").asInt(0);
      int j = p_214732_0_.get("top_offset").asInt(0);
      int k = p_214732_0_.get("top").asInt(0);
      return new ChanceRangeConfig(f, i, j, k);
   }
}