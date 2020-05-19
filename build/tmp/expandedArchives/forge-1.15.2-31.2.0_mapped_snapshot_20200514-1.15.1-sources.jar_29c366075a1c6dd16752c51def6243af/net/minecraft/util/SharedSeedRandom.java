package net.minecraft.util;

import java.util.Random;

public class SharedSeedRandom extends Random {
   private int usageCount;

   public SharedSeedRandom() {
   }

   public SharedSeedRandom(long seed) {
      super(seed);
   }

   public void skip(int bits) {
      for(int i = 0; i < bits; ++i) {
         this.next(1);
      }

   }

   protected int next(int p_next_1_) {
      ++this.usageCount;
      return super.next(p_next_1_);
   }

   public long setBaseChunkSeed(int x, int z) {
      long i = (long)x * 341873128712L + (long)z * 132897987541L;
      this.setSeed(i);
      return i;
   }

   public long setDecorationSeed(long baseSeed, int x, int z) {
      this.setSeed(baseSeed);
      long i = this.nextLong() | 1L;
      long j = this.nextLong() | 1L;
      long k = (long)x * i + (long)z * j ^ baseSeed;
      this.setSeed(k);
      return k;
   }

   public long setFeatureSeed(long baseSeed, int x, int z) {
      long i = baseSeed + (long)x + (long)(10000 * z);
      this.setSeed(i);
      return i;
   }

   public long setLargeFeatureSeed(long seed, int x, int z) {
      this.setSeed(seed);
      long i = this.nextLong();
      long j = this.nextLong();
      long k = (long)x * i ^ (long)z * j ^ seed;
      this.setSeed(k);
      return k;
   }

   public long setLargeFeatureSeedWithSalt(long baseSeed, int x, int z, int modifier) {
      long i = (long)x * 341873128712L + (long)z * 132897987541L + baseSeed + (long)modifier;
      this.setSeed(i);
      return i;
   }

   public static Random seedSlimeChunk(int p_205190_0_, int p_205190_1_, long p_205190_2_, long p_205190_4_) {
      return new Random(p_205190_2_ + (long)(p_205190_0_ * p_205190_0_ * 4987142) + (long)(p_205190_0_ * 5947611) + (long)(p_205190_1_ * p_205190_1_) * 4392871L + (long)(p_205190_1_ * 389711) ^ p_205190_4_);
   }
}