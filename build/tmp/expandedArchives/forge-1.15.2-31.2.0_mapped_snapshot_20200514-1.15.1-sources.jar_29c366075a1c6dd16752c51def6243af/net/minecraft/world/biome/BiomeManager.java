package net.minecraft.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.provider.BiomeProvider;

public class BiomeManager {
   private final BiomeManager.IBiomeReader reader;
   private final long seed;
   private final IBiomeMagnifier magnifier;

   public BiomeManager(BiomeManager.IBiomeReader readerIn, long seedIn, IBiomeMagnifier magnifierIn) {
      this.reader = readerIn;
      this.seed = seedIn;
      this.magnifier = magnifierIn;
   }

   public BiomeManager copyWithProvider(BiomeProvider newProvider) {
      return new BiomeManager(newProvider, this.seed, this.magnifier);
   }

   public Biome getBiome(BlockPos posIn) {
      return this.magnifier.getBiome(this.seed, posIn.getX(), posIn.getY(), posIn.getZ(), this.reader);
   }

   public interface IBiomeReader {
      Biome getNoiseBiome(int x, int y, int z);
   }
}