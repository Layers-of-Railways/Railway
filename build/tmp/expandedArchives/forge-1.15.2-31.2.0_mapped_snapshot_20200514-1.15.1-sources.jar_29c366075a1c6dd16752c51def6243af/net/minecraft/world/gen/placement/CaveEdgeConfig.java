package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.GenerationStage;

public class CaveEdgeConfig implements IPlacementConfig {
   protected final GenerationStage.Carving step;
   protected final float probability;

   public CaveEdgeConfig(GenerationStage.Carving step, float probability) {
      this.step = step;
      this.probability = probability;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("step"), ops.createString(this.step.toString()), ops.createString("probability"), ops.createFloat(this.probability))));
   }

   public static CaveEdgeConfig deserialize(Dynamic<?> p_214720_0_) {
      GenerationStage.Carving generationstage$carving = GenerationStage.Carving.valueOf(p_214720_0_.get("step").asString(""));
      float f = p_214720_0_.get("probability").asFloat(0.0F);
      return new CaveEdgeConfig(generationstage$carving, f);
   }
}