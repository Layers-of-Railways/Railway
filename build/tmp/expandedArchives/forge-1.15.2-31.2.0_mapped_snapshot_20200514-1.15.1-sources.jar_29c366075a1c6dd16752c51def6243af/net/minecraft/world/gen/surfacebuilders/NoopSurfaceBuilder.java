package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class NoopSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   public NoopSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51307_1_) {
      super(p_i51307_1_);
   }

   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
   }
}