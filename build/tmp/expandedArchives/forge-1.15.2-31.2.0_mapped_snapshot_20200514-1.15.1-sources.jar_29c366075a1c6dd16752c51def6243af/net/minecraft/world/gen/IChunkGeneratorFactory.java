package net.minecraft.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;

public interface IChunkGeneratorFactory<C extends GenerationSettings, T extends ChunkGenerator<C>> {
   T create(World p_create_1_, BiomeProvider p_create_2_, C p_create_3_);
}