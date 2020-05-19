package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class GiantTreeTaigaSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   public GiantTreeTaigaSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51312_1_) {
      super(p_i51312_1_);
   }

   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
      if (noise > 1.75D) {
         SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.CORASE_DIRT_DIRT_GRAVEL_CONFIG);
      } else if (noise > -0.95D) {
         SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.PODZOL_DIRT_GRAVEL_CONFIG);
      } else {
         SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG);
      }

   }
}