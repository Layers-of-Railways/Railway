package net.minecraft.world.biome.provider;

import net.minecraft.world.storage.WorldInfo;

public class EndBiomeProviderSettings implements IBiomeProviderSettings {
   private final long seed;

   public EndBiomeProviderSettings(WorldInfo p_i225752_1_) {
      this.seed = p_i225752_1_.getSeed();
   }

   public long getSeed() {
      return this.seed;
   }
}