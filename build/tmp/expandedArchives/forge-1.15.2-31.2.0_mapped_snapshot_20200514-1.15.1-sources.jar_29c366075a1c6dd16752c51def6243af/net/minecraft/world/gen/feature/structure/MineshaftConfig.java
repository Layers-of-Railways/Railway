package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class MineshaftConfig implements IFeatureConfig {
   public final double probability;
   public final MineshaftStructure.Type type;

   public MineshaftConfig(double probability, MineshaftStructure.Type type) {
      this.probability = probability;
      this.type = type;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("probability"), ops.createDouble(this.probability), ops.createString("type"), ops.createString(this.type.getName()))));
   }

   public static <T> MineshaftConfig deserialize(Dynamic<T> data) {
      float f = data.get("probability").asFloat(0.0F);
      MineshaftStructure.Type mineshaftstructure$type = MineshaftStructure.Type.byName(data.get("type").asString(""));
      return new MineshaftConfig((double)f, mineshaftstructure$type);
   }
}