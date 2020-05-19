package net.minecraft.world.biome.provider;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.storage.WorldInfo;

public class SingleBiomeProviderSettings implements IBiomeProviderSettings {
   private Biome biome = Biomes.PLAINS;

   public SingleBiomeProviderSettings(WorldInfo p_i225748_1_) {
   }

   public SingleBiomeProviderSettings setBiome(Biome biomeIn) {
      this.biome = biomeIn;
      return this;
   }

   public Biome getBiome() {
      return this.biome;
   }
}