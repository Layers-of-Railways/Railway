package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum AddBambooForestLayer implements IC1Transformer {
   INSTANCE;

   private static final int JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
   private static final int BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);

   public int apply(INoiseRandom context, int value) {
      return context.random(10) == 0 && value == JUNGLE ? BAMBOO_JUNGLE : value;
   }
}