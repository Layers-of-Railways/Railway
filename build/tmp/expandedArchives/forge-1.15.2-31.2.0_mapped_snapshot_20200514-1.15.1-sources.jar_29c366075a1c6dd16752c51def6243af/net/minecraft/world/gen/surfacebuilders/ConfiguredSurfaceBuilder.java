package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ConfiguredSurfaceBuilder<SC extends ISurfaceBuilderConfig> {
   public final SurfaceBuilder<SC> builder;
   public final SC config;

   public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> builder, SC config) {
      this.builder = builder;
      this.config = config;
   }

   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed) {
      this.builder.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, this.config);
   }

   public void setSeed(long seed) {
      this.builder.setSeed(seed);
   }

   public SC getConfig() {
      return this.config;
   }
}