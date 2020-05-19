package net.minecraft.world.biome;

public interface IBiomeMagnifier {
   Biome getBiome(long seed, int x, int y, int z, BiomeManager.IBiomeReader biomeReader);
}