package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class SingleRandomFeature implements IFeatureConfig {
   public final List<ConfiguredFeature<?, ?>> features;

   public SingleRandomFeature(List<ConfiguredFeature<?, ?>> features) {
      this.features = features;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("features"), ops.createList(this.features.stream().map((p_227326_1_) -> {
         return p_227326_1_.serialize(ops).getValue();
      })))));
   }

   public static <T> SingleRandomFeature deserialize(Dynamic<T> p_214664_0_) {
      List<ConfiguredFeature<?, ?>> list = p_214664_0_.get("features").asList(ConfiguredFeature::deserialize);
      return new SingleRandomFeature(list);
   }
}