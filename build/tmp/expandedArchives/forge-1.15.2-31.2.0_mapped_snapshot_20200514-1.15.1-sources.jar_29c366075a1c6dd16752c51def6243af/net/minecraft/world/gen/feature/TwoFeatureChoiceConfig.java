package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class TwoFeatureChoiceConfig implements IFeatureConfig {
   public final ConfiguredFeature<?, ?> field_227285_a_;
   public final ConfiguredFeature<?, ?> field_227286_b_;

   public TwoFeatureChoiceConfig(ConfiguredFeature<?, ?> p_i225835_1_, ConfiguredFeature<?, ?> p_i225835_2_) {
      this.field_227285_a_ = p_i225835_1_;
      this.field_227286_b_ = p_i225835_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("feature_true"), this.field_227285_a_.serialize(ops).getValue(), ops.createString("feature_false"), this.field_227286_b_.serialize(ops).getValue())));
   }

   public static <T> TwoFeatureChoiceConfig deserialize(Dynamic<T> p_227287_0_) {
      ConfiguredFeature<?, ?> configuredfeature = ConfiguredFeature.deserialize(p_227287_0_.get("feature_true").orElseEmptyMap());
      ConfiguredFeature<?, ?> configuredfeature1 = ConfiguredFeature.deserialize(p_227287_0_.get("feature_false").orElseEmptyMap());
      return new TwoFeatureChoiceConfig(configuredfeature, configuredfeature1);
   }
}