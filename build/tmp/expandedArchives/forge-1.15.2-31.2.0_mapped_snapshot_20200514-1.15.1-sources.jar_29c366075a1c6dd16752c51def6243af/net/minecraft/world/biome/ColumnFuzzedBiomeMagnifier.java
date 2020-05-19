package net.minecraft.world.biome;

public enum ColumnFuzzedBiomeMagnifier implements IBiomeMagnifier {
   INSTANCE;

   public Biome getBiome(long seed, int x, int y, int z, BiomeManager.IBiomeReader biomeReader) {
      return FuzzedBiomeMagnifier.INSTANCE.getBiome(seed, x, 0, z, biomeReader);
   }
}