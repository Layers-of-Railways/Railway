package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum RareBiomeLayer implements IC1Transformer {
   INSTANCE;

   private static final int PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
   private static final int SUNFLOWER_PLAINS = Registry.BIOME.getId(Biomes.SUNFLOWER_PLAINS);

   public int apply(INoiseRandom context, int value) {
      return context.random(57) == 0 && value == PLAINS ? SUNFLOWER_PLAINS : value;
   }
}