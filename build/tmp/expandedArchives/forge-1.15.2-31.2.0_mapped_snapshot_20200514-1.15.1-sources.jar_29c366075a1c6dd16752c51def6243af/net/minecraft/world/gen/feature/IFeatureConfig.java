package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface IFeatureConfig {
   NoFeatureConfig NO_FEATURE_CONFIG = new NoFeatureConfig();

   <T> Dynamic<T> serialize(DynamicOps<T> ops);
}