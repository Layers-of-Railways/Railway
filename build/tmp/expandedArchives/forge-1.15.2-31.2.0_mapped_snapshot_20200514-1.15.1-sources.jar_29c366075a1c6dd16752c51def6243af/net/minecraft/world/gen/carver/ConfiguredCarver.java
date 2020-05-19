package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ConfiguredCarver<WC extends ICarverConfig> {
   public final WorldCarver<WC> carver;
   public final WC config;

   public ConfiguredCarver(WorldCarver<WC> p_i49928_1_, WC p_i49928_2_) {
      this.carver = p_i49928_1_;
      this.config = p_i49928_2_;
   }

   public boolean shouldCarve(Random p_222730_1_, int p_222730_2_, int p_222730_3_) {
      return this.carver.shouldCarve(p_222730_1_, p_222730_2_, p_222730_3_, this.config);
   }

   public boolean func_227207_a_(IChunk p_227207_1_, Function<BlockPos, Biome> p_227207_2_, Random p_227207_3_, int p_227207_4_, int p_227207_5_, int p_227207_6_, int p_227207_7_, int p_227207_8_, BitSet p_227207_9_) {
      return this.carver.func_225555_a_(p_227207_1_, p_227207_2_, p_227207_3_, p_227207_4_, p_227207_5_, p_227207_6_, p_227207_7_, p_227207_8_, p_227207_9_, this.config);
   }
}