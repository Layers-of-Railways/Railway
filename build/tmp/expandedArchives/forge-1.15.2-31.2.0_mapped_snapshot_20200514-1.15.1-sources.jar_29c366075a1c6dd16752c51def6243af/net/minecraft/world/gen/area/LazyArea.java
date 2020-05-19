package net.minecraft.world.gen.area;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public final class LazyArea implements IArea {
   private final IPixelTransformer pixelTransformer;
   private final Long2IntLinkedOpenHashMap cachedValues;
   private final int maxCacheSize;

   public LazyArea(Long2IntLinkedOpenHashMap p_i51286_1_, int p_i51286_2_, IPixelTransformer p_i51286_3_) {
      this.cachedValues = p_i51286_1_;
      this.maxCacheSize = p_i51286_2_;
      this.pixelTransformer = p_i51286_3_;
   }

   public int getValue(int x, int z) {
      long i = ChunkPos.asLong(x, z);
      synchronized(this.cachedValues) {
         int j = this.cachedValues.get(i);
         if (j != Integer.MIN_VALUE) {
            return j;
         } else {
            int k = this.pixelTransformer.apply(x, z);
            this.cachedValues.put(i, k);
            if (this.cachedValues.size() > this.maxCacheSize) {
               for(int l = 0; l < this.maxCacheSize / 16; ++l) {
                  this.cachedValues.removeFirstInt();
               }
            }

            return k;
         }
      }
   }

   public int getmaxCacheSize() {
      return this.maxCacheSize;
   }
}