package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.stream.IntStream;
import net.minecraft.util.SharedSeedRandom;

public class PerlinNoiseGenerator implements INoiseGenerator {
   private final SimplexNoiseGenerator[] noiseLevels;
   private final double field_227462_b_;
   private final double field_227463_c_;

   public PerlinNoiseGenerator(SharedSeedRandom p_i225880_1_, int p_i225880_2_, int p_i225880_3_) {
      this(p_i225880_1_, new IntRBTreeSet(IntStream.rangeClosed(-p_i225880_2_, p_i225880_3_).toArray()));
   }

   public PerlinNoiseGenerator(SharedSeedRandom p_i225881_1_, IntSortedSet p_i225881_2_) {
      if (p_i225881_2_.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int i = -p_i225881_2_.firstInt();
         int j = p_i225881_2_.lastInt();
         int k = i + j + 1;
         if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            SimplexNoiseGenerator simplexnoisegenerator = new SimplexNoiseGenerator(p_i225881_1_);
            int l = j;
            this.noiseLevels = new SimplexNoiseGenerator[k];
            if (j >= 0 && j < k && p_i225881_2_.contains(0)) {
               this.noiseLevels[j] = simplexnoisegenerator;
            }

            for(int i1 = j + 1; i1 < k; ++i1) {
               if (i1 >= 0 && p_i225881_2_.contains(l - i1)) {
                  this.noiseLevels[i1] = new SimplexNoiseGenerator(p_i225881_1_);
               } else {
                  p_i225881_1_.skip(262);
               }
            }

            if (j > 0) {
               long k1 = (long)(simplexnoisegenerator.func_227464_a_(simplexnoisegenerator.xo, simplexnoisegenerator.yo, simplexnoisegenerator.zo) * (double)9.223372E18F);
               SharedSeedRandom sharedseedrandom = new SharedSeedRandom(k1);

               for(int j1 = l - 1; j1 >= 0; --j1) {
                  if (j1 < k && p_i225881_2_.contains(l - j1)) {
                     this.noiseLevels[j1] = new SimplexNoiseGenerator(sharedseedrandom);
                  } else {
                     sharedseedrandom.skip(262);
                  }
               }
            }

            this.field_227463_c_ = Math.pow(2.0D, (double)j);
            this.field_227462_b_ = 1.0D / (Math.pow(2.0D, (double)k) - 1.0D);
         }
      }
   }

   public double noiseAt(double x, double y, boolean useNoiseOffsets) {
      double d0 = 0.0D;
      double d1 = this.field_227463_c_;
      double d2 = this.field_227462_b_;

      for(SimplexNoiseGenerator simplexnoisegenerator : this.noiseLevels) {
         if (simplexnoisegenerator != null) {
            d0 += simplexnoisegenerator.getValue(x * d1 + (useNoiseOffsets ? simplexnoisegenerator.xo : 0.0D), y * d1 + (useNoiseOffsets ? simplexnoisegenerator.yo : 0.0D)) * d2;
         }

         d1 /= 2.0D;
         d2 *= 2.0D;
      }

      return d0;
   }

   public double noiseAt(double x, double y, double z, double p_215460_7_) {
      return this.noiseAt(x, y, true) * 0.55D;
   }
}