package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoiseDependant implements IPlacementConfig {
   public final double noiseLevel;
   public final int belowNoise;
   public final int aboveNoise;

   public NoiseDependant(double noiseLevel, int belowNoise, int aboveNoise) {
      this.noiseLevel = noiseLevel;
      this.belowNoise = belowNoise;
      this.aboveNoise = aboveNoise;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("noise_level"), ops.createDouble(this.noiseLevel), ops.createString("below_noise"), ops.createInt(this.belowNoise), ops.createString("above_noise"), ops.createInt(this.aboveNoise))));
   }

   public static NoiseDependant deserialize(Dynamic<?> p_214734_0_) {
      double d0 = p_214734_0_.get("noise_level").asDouble(0.0D);
      int i = p_214734_0_.get("below_noise").asInt(0);
      int j = p_214734_0_.get("above_noise").asInt(0);
      return new NoiseDependant(d0, i, j);
   }
}