package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.util.FastRandom;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public class LazyAreaLayerContext implements IExtendedNoiseRandom<LazyArea> {
   private final Long2IntLinkedOpenHashMap cache;
   private final int maxCacheSize;
   private final ImprovedNoiseGenerator field_215718_b;
   private final long field_215719_e;
   private long field_215720_f;

   public LazyAreaLayerContext(int maxCacheSizeIn, long seedIn, long seedModifierIn) {
      this.field_215719_e = func_227471_b_(seedIn, seedModifierIn);
      this.field_215718_b = new ImprovedNoiseGenerator(new Random(seedIn));
      this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
      this.cache.defaultReturnValue(Integer.MIN_VALUE);
      this.maxCacheSize = maxCacheSizeIn;
   }

   public LazyArea func_212861_a_(IPixelTransformer p_212861_1_) {
      return new LazyArea(this.cache, this.maxCacheSize, p_212861_1_);
   }

   public LazyArea func_212859_a_(IPixelTransformer p_212859_1_, LazyArea p_212859_2_) {
      return new LazyArea(this.cache, Math.min(1024, p_212859_2_.getmaxCacheSize() * 4), p_212859_1_);
   }

   public LazyArea makeArea(IPixelTransformer p_212860_1_, LazyArea p_212860_2_, LazyArea p_212860_3_) {
      return new LazyArea(this.cache, Math.min(1024, Math.max(p_212860_2_.getmaxCacheSize(), p_212860_3_.getmaxCacheSize()) * 4), p_212860_1_);
   }

   public void setPosition(long x, long z) {
      long i = this.field_215719_e;
      i = FastRandom.mix(i, x);
      i = FastRandom.mix(i, z);
      i = FastRandom.mix(i, x);
      i = FastRandom.mix(i, z);
      this.field_215720_f = i;
   }

   public int random(int bound) {
      int i = (int)Math.floorMod(this.field_215720_f >> 24, (long)bound);
      this.field_215720_f = FastRandom.mix(this.field_215720_f, this.field_215719_e);
      return i;
   }

   public ImprovedNoiseGenerator getNoiseGenerator() {
      return this.field_215718_b;
   }

   private static long func_227471_b_(long p_227471_0_, long p_227471_2_) {
      long lvt_4_1_ = FastRandom.mix(p_227471_2_, p_227471_2_);
      lvt_4_1_ = FastRandom.mix(lvt_4_1_, p_227471_2_);
      lvt_4_1_ = FastRandom.mix(lvt_4_1_, p_227471_2_);
      long lvt_6_1_ = FastRandom.mix(p_227471_0_, lvt_4_1_);
      lvt_6_1_ = FastRandom.mix(lvt_6_1_, lvt_4_1_);
      lvt_6_1_ = FastRandom.mix(lvt_6_1_, lvt_4_1_);
      return lvt_6_1_;
   }
}