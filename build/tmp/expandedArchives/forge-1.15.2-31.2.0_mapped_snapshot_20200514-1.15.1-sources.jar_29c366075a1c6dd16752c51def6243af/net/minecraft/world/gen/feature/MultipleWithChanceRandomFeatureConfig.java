package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class MultipleWithChanceRandomFeatureConfig implements IFeatureConfig {
   public final List<ConfiguredFeature<?, ?>> features;
   public final int count;

   public MultipleWithChanceRandomFeatureConfig(List<ConfiguredFeature<?, ?>> features, int count) {
      this.features = features;
      this.count = count;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("features"), ops.createList(this.features.stream().map((p_227324_1_) -> {
         return p_227324_1_.serialize(ops).getValue();
      })), ops.createString("count"), ops.createInt(this.count))));
   }

   public static <T> MultipleWithChanceRandomFeatureConfig deserialize(Dynamic<T> p_214653_0_) {
      List<ConfiguredFeature<?, ?>> list = p_214653_0_.get("features").asList(ConfiguredFeature::deserialize);
      int i = p_214653_0_.get("count").asInt(0);
      return new MultipleWithChanceRandomFeatureConfig(list, i);
   }
}