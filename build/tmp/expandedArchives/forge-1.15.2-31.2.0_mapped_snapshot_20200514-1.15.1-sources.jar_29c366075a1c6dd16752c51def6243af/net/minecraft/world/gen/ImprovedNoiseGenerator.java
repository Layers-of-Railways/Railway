package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public final class ImprovedNoiseGenerator {
   private final byte[] permutations;
   public final double xCoord;
   public final double yCoord;
   public final double zCoord;

   public ImprovedNoiseGenerator(Random rand) {
      this.xCoord = rand.nextDouble() * 256.0D;
      this.yCoord = rand.nextDouble() * 256.0D;
      this.zCoord = rand.nextDouble() * 256.0D;
      this.permutations = new byte[256];

      for(int i = 0; i < 256; ++i) {
         this.permutations[i] = (byte)i;
      }

      for(int k = 0; k < 256; ++k) {
         int j = rand.nextInt(256 - k);
         byte b0 = this.permutations[k];
         this.permutations[k] = this.permutations[k + j];
         this.permutations[k + j] = b0;
      }

   }

   public double func_215456_a(double x, double y, double z, double p_215456_7_, double p_215456_9_) {
      double d0 = x + this.xCoord;
      double d1 = y + this.yCoord;
      double d2 = z + this.zCoord;
      int i = MathHelper.floor(d0);
      int j = MathHelper.floor(d1);
      int k = MathHelper.floor(d2);
      double d3 = d0 - (double)i;
      double d4 = d1 - (double)j;
      double d5 = d2 - (double)k;
      double d6 = MathHelper.perlinFade(d3);
      double d7 = MathHelper.perlinFade(d4);
      double d8 = MathHelper.perlinFade(d5);
      double d9;
      if (p_215456_7_ != 0.0D) {
         double d10 = Math.min(p_215456_9_, d4);
         d9 = (double)MathHelper.floor(d10 / p_215456_7_) * p_215456_7_;
      } else {
         d9 = 0.0D;
      }

      return this.func_215459_a(i, j, k, d3, d4 - d9, d5, d6, d7, d8);
   }

   private static double dotGrad(int gradIndex, double xFactor, double yFactor, double zFactor) {
      int i = gradIndex & 15;
      return SimplexNoiseGenerator.processGrad(SimplexNoiseGenerator.GRADS[i], xFactor, yFactor, zFactor);
   }

   private int getPermutValue(int permutIndex) {
      return this.permutations[permutIndex & 255] & 255;
   }

   public double func_215459_a(int p_215459_1_, int p_215459_2_, int p_215459_3_, double p_215459_4_, double p_215459_6_, double p_215459_8_, double p_215459_10_, double p_215459_12_, double p_215459_14_) {
      int i = this.getPermutValue(p_215459_1_) + p_215459_2_;
      int j = this.getPermutValue(i) + p_215459_3_;
      int k = this.getPermutValue(i + 1) + p_215459_3_;
      int l = this.getPermutValue(p_215459_1_ + 1) + p_215459_2_;
      int i1 = this.getPermutValue(l) + p_215459_3_;
      int j1 = this.getPermutValue(l + 1) + p_215459_3_;
      double d0 = dotGrad(this.getPermutValue(j), p_215459_4_, p_215459_6_, p_215459_8_);
      double d1 = dotGrad(this.getPermutValue(i1), p_215459_4_ - 1.0D, p_215459_6_, p_215459_8_);
      double d2 = dotGrad(this.getPermutValue(k), p_215459_4_, p_215459_6_ - 1.0D, p_215459_8_);
      double d3 = dotGrad(this.getPermutValue(j1), p_215459_4_ - 1.0D, p_215459_6_ - 1.0D, p_215459_8_);
      double d4 = dotGrad(this.getPermutValue(j + 1), p_215459_4_, p_215459_6_, p_215459_8_ - 1.0D);
      double d5 = dotGrad(this.getPermutValue(i1 + 1), p_215459_4_ - 1.0D, p_215459_6_, p_215459_8_ - 1.0D);
      double d6 = dotGrad(this.getPermutValue(k + 1), p_215459_4_, p_215459_6_ - 1.0D, p_215459_8_ - 1.0D);
      double d7 = dotGrad(this.getPermutValue(j1 + 1), p_215459_4_ - 1.0D, p_215459_6_ - 1.0D, p_215459_8_ - 1.0D);
      return MathHelper.lerp3(p_215459_10_, p_215459_12_, p_215459_14_, d0, d1, d2, d3, d4, d5, d6, d7);
   }
}