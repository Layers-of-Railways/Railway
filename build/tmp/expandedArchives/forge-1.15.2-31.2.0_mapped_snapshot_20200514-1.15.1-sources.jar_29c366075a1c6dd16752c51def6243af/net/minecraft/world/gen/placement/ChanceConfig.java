package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ChanceConfig implements IPlacementConfig {
   public final int chance;

   public ChanceConfig(int chance) {
      this.chance = chance;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("chance"), ops.createInt(this.chance))));
   }

   public static ChanceConfig deserialize(Dynamic<?> p_214722_0_) {
      int i = p_214722_0_.get("chance").asInt(0);
      return new ChanceConfig(i);
   }
}