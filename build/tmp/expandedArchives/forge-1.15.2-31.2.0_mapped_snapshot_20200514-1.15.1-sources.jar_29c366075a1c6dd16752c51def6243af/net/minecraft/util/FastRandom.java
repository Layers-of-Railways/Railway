package net.minecraft.util;

public class FastRandom {
   public static long mix(long p_226162_0_, long p_226162_2_) {
      p_226162_0_ = p_226162_0_ * (p_226162_0_ * 6364136223846793005L + 1442695040888963407L);
      p_226162_0_ = p_226162_0_ + p_226162_2_;
      return p_226162_0_;
   }
}