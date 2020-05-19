package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoFeatureConfig implements IFeatureConfig {
   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.emptyMap());
   }

   public static <T> NoFeatureConfig deserialize(Dynamic<T> p_214639_0_) {
      return NO_FEATURE_CONFIG;
   }
}