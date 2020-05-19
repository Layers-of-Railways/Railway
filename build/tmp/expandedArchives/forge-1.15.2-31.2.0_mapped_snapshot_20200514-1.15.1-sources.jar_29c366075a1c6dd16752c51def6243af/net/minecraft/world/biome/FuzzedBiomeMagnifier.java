package net.minecraft.world.biome;

import net.minecraft.util.FastRandom;

public enum FuzzedBiomeMagnifier implements IBiomeMagnifier {
   INSTANCE;

   public Biome getBiome(long seed, int x, int y, int z, BiomeManager.IBiomeReader biomeReader) {
      int i = x - 2;
      int j = y - 2;
      int k = z - 2;
      int l = i >> 2;
      int i1 = j >> 2;
      int j1 = k >> 2;
      double d0 = (double)(i & 3) / 4.0D;
      double d1 = (double)(j & 3) / 4.0D;
      double d2 = (double)(k & 3) / 4.0D;
      double[] adouble = new double[8];

      for(int k1 = 0; k1 < 8; ++k1) {
         boolean flag = (k1 & 4) == 0;
         boolean flag1 = (k1 & 2) == 0;
         boolean flag2 = (k1 & 1) == 0;
         int l1 = flag ? l : l + 1;
         int i2 = flag1 ? i1 : i1 + 1;
         int j2 = flag2 ? j1 : j1 + 1;
         double d3 = flag ? d0 : d0 - 1.0D;
         double d4 = flag1 ? d1 : d1 - 1.0D;
         double d5 = flag2 ? d2 : d2 - 1.0D;
         adouble[k1] = func_226845_a_(seed, l1, i2, j2, d3, d4, d5);
      }

      int k2 = 0;
      double d6 = adouble[0];

      for(int l2 = 1; l2 < 8; ++l2) {
         if (d6 > adouble[l2]) {
            k2 = l2;
            d6 = adouble[l2];
         }
      }

      int i3 = (k2 & 4) == 0 ? l : l + 1;
      int j3 = (k2 & 2) == 0 ? i1 : i1 + 1;
      int k3 = (k2 & 1) == 0 ? j1 : j1 + 1;
      return biomeReader.getNoiseBiome(i3, j3, k3);
   }

   private static double func_226845_a_(long p_226845_0_, int p_226845_2_, int p_226845_3_, int p_226845_4_, double p_226845_5_, double p_226845_7_, double p_226845_9_) {
      long lvt_11_1_ = FastRandom.mix(p_226845_0_, (long)p_226845_2_);
      lvt_11_1_ = FastRandom.mix(lvt_11_1_, (long)p_226845_3_);
      lvt_11_1_ = FastRandom.mix(lvt_11_1_, (long)p_226845_4_);
      lvt_11_1_ = FastRandom.mix(lvt_11_1_, (long)p_226845_2_);
      lvt_11_1_ = FastRandom.mix(lvt_11_1_, (long)p_226845_3_);
      lvt_11_1_ = FastRandom.mix(lvt_11_1_, (long)p_226845_4_);
      double d0 = func_226844_a_(lvt_11_1_);
      lvt_11_1_ = FastRandom.mix(lvt_11_1_, p_226845_0_);
      double d1 = func_226844_a_(lvt_11_1_);
      lvt_11_1_ = FastRandom.mix(lvt_11_1_, p_226845_0_);
      double d2 = func_226844_a_(lvt_11_1_);
      return func_226843_a_(p_226845_9_ + d2) + func_226843_a_(p_226845_7_ + d1) + func_226843_a_(p_226845_5_ + d0);
   }

   private static double func_226844_a_(long p_226844_0_) {
      double d0 = (double)((int)Math.floorMod(p_226844_0_ >> 24, 1024L)) / 1024.0D;
      return (d0 - 0.5D) * 0.9D;
   }

   private static double func_226843_a_(double p_226843_0_) {
      return p_226843_0_ * p_226843_0_;
   }
}