package net.minecraft.world.biome.provider;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.storage.WorldInfo;

public class OverworldBiomeProviderSettings implements IBiomeProviderSettings {
   private final long seed;
   private final WorldType worldType;
   private OverworldGenSettings generatorSettings = new OverworldGenSettings();

   public OverworldBiomeProviderSettings(WorldInfo p_i225751_1_) {
      this.seed = p_i225751_1_.getSeed();
      this.worldType = p_i225751_1_.getGenerator();
   }

   public OverworldBiomeProviderSettings setGeneratorSettings(OverworldGenSettings p_205441_1_) {
      this.generatorSettings = p_205441_1_;
      return this;
   }

   public long getSeed() {
      return this.seed;
   }

   public WorldType getWorldType() {
      return this.worldType;
   }

   public OverworldGenSettings getGeneratorSettings() {
      return this.generatorSettings;
   }
}