package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class OceanRuinConfig implements IFeatureConfig {
   public final OceanRuinStructure.Type field_204031_a;
   public final float largeProbability;
   public final float clusterProbability;

   public OceanRuinConfig(OceanRuinStructure.Type p_i48866_1_, float largeProbability, float clusterProbability) {
      this.field_204031_a = p_i48866_1_;
      this.largeProbability = largeProbability;
      this.clusterProbability = clusterProbability;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("biome_temp"), ops.createString(this.field_204031_a.getName()), ops.createString("large_probability"), ops.createFloat(this.largeProbability), ops.createString("cluster_probability"), ops.createFloat(this.clusterProbability))));
   }

   public static <T> OceanRuinConfig deserialize(Dynamic<T> p_214640_0_) {
      OceanRuinStructure.Type oceanruinstructure$type = OceanRuinStructure.Type.getType(p_214640_0_.get("biome_temp").asString(""));
      float f = p_214640_0_.get("large_probability").asFloat(0.0F);
      float f1 = p_214640_0_.get("cluster_probability").asFloat(0.0F);
      return new OceanRuinConfig(oceanruinstructure$type, f, f1);
   }
}