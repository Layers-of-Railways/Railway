package net.minecraft.world.gen.carver;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class CanyonWorldCarver extends WorldCarver<ProbabilityConfig> {
   private final float[] field_202536_i = new float[1024];

   public CanyonWorldCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49930_1_) {
      super(p_i49930_1_, 256);
   }

   public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
      return rand.nextFloat() <= config.probability;
   }

   public boolean func_225555_a_(IChunk p_225555_1_, Function<BlockPos, Biome> p_225555_2_, Random p_225555_3_, int p_225555_4_, int p_225555_5_, int p_225555_6_, int p_225555_7_, int p_225555_8_, BitSet p_225555_9_, ProbabilityConfig p_225555_10_) {
      int i = (this.func_222704_c() * 2 - 1) * 16;
      double d0 = (double)(p_225555_5_ * 16 + p_225555_3_.nextInt(16));
      double d1 = (double)(p_225555_3_.nextInt(p_225555_3_.nextInt(40) + 8) + 20);
      double d2 = (double)(p_225555_6_ * 16 + p_225555_3_.nextInt(16));
      float f = p_225555_3_.nextFloat() * ((float)Math.PI * 2F);
      float f1 = (p_225555_3_.nextFloat() - 0.5F) * 2.0F / 8.0F;
      double d3 = 3.0D;
      float f2 = (p_225555_3_.nextFloat() * 2.0F + p_225555_3_.nextFloat()) * 2.0F;
      int j = i - p_225555_3_.nextInt(i / 4);
      int k = 0;
      this.func_227204_a_(p_225555_1_, p_225555_2_, p_225555_3_.nextLong(), p_225555_4_, p_225555_7_, p_225555_8_, d0, d1, d2, f2, f, f1, 0, j, 3.0D, p_225555_9_);
      return true;
   }

   private void func_227204_a_(IChunk p_227204_1_, Function<BlockPos, Biome> p_227204_2_, long p_227204_3_, int p_227204_5_, int p_227204_6_, int p_227204_7_, double p_227204_8_, double p_227204_10_, double p_227204_12_, float p_227204_14_, float p_227204_15_, float p_227204_16_, int p_227204_17_, int p_227204_18_, double p_227204_19_, BitSet p_227204_21_) {
      Random random = new Random(p_227204_3_);
      float f = 1.0F;

      for(int i = 0; i < 256; ++i) {
         if (i == 0 || random.nextInt(3) == 0) {
            f = 1.0F + random.nextFloat() * random.nextFloat();
         }

         this.field_202536_i[i] = f * f;
      }

      float f4 = 0.0F;
      float f1 = 0.0F;

      for(int j = p_227204_17_; j < p_227204_18_; ++j) {
         double d0 = 1.5D + (double)(MathHelper.sin((float)j * (float)Math.PI / (float)p_227204_18_) * p_227204_14_);
         double d1 = d0 * p_227204_19_;
         d0 = d0 * ((double)random.nextFloat() * 0.25D + 0.75D);
         d1 = d1 * ((double)random.nextFloat() * 0.25D + 0.75D);
         float f2 = MathHelper.cos(p_227204_16_);
         float f3 = MathHelper.sin(p_227204_16_);
         p_227204_8_ += (double)(MathHelper.cos(p_227204_15_) * f2);
         p_227204_10_ += (double)f3;
         p_227204_12_ += (double)(MathHelper.sin(p_227204_15_) * f2);
         p_227204_16_ = p_227204_16_ * 0.7F;
         p_227204_16_ = p_227204_16_ + f1 * 0.05F;
         p_227204_15_ += f4 * 0.05F;
         f1 = f1 * 0.8F;
         f4 = f4 * 0.5F;
         f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f4 = f4 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (random.nextInt(4) != 0) {
            if (!this.func_222702_a(p_227204_6_, p_227204_7_, p_227204_8_, p_227204_12_, j, p_227204_18_, p_227204_14_)) {
               return;
            }

            this.func_227208_a_(p_227204_1_, p_227204_2_, p_227204_3_, p_227204_5_, p_227204_6_, p_227204_7_, p_227204_8_, p_227204_10_, p_227204_12_, d0, d1, p_227204_21_);
         }
      }

   }

   protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
      return (p_222708_1_ * p_222708_1_ + p_222708_5_ * p_222708_5_) * (double)this.field_202536_i[p_222708_7_ - 1] + p_222708_3_ * p_222708_3_ / 6.0D >= 1.0D;
   }
}