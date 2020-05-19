package net.minecraft.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTimeManager {
   private final long[] values;
   private int count;
   private int index;

   public RenderTimeManager(int sizeIn) {
      this.values = new long[sizeIn];
   }

   public long nextValue(long valueIn) {
      if (this.count < this.values.length) {
         ++this.count;
      }

      this.values[this.index] = valueIn;
      this.index = (this.index + 1) % this.values.length;
      long i = Long.MAX_VALUE;
      long j = Long.MIN_VALUE;
      long k = 0L;

      for(int l = 0; l < this.count; ++l) {
         long i1 = this.values[l];
         k += i1;
         i = Math.min(i, i1);
         j = Math.max(j, i1);
      }

      if (this.count > 2) {
         k = k - (i + j);
         return k / (long)(this.count - 2);
      } else {
         return k > 0L ? (long)this.count / k : 0L;
      }
   }
}