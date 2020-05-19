package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class FeatureRadiusConfig implements IFeatureConfig {
   public final int radius;

   public FeatureRadiusConfig(int radius) {
      this.radius = radius;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("radius"), ops.createInt(this.radius))));
   }

   public static <T> FeatureRadiusConfig deserialize(Dynamic<T> p_214706_0_) {
      int i = p_214706_0_.get("radius").asInt(0);
      return new FeatureRadiusConfig(i);
   }
}