package net.minecraft.client.renderer.color;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColorCache {
   private final ThreadLocal<ColorCache.Entry> threadCacheEntry = ThreadLocal.withInitial(() -> {
      return new ColorCache.Entry();
   });
   private final Long2ObjectLinkedOpenHashMap<int[]> cache = new Long2ObjectLinkedOpenHashMap<>(256, 0.25F);
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

   public int getColor(BlockPos blockPosIn, IntSupplier colorSupplier) {
      int i = blockPosIn.getX() >> 4;
      int j = blockPosIn.getZ() >> 4;
      ColorCache.Entry colorcache$entry = this.threadCacheEntry.get();
      if (colorcache$entry.chunkX != i || colorcache$entry.chunkZ != j) {
         colorcache$entry.chunkX = i;
         colorcache$entry.chunkZ = j;
         colorcache$entry.colorCache = this.getChunkCache(i, j);
      }

      int k = blockPosIn.getX() & 15;
      int l = blockPosIn.getZ() & 15;
      int i1 = l << 4 | k;
      int j1 = colorcache$entry.colorCache[i1];
      if (j1 != -1) {
         return j1;
      } else {
         int k1 = colorSupplier.getAsInt();
         colorcache$entry.colorCache[i1] = k1;
         return k1;
      }
   }

   public void invalidateChunk(int chunkX, int chunkZ) {
      try {
         this.lock.writeLock().lock();

         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               long k = ChunkPos.asLong(chunkX + i, chunkZ + j);
               this.cache.remove(k);
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   public void invalidateAll() {
      try {
         this.lock.writeLock().lock();
         this.cache.clear();
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   private int[] getChunkCache(int chunkX, int chunkZ) {
      long i = ChunkPos.asLong(chunkX, chunkZ);
      this.lock.readLock().lock();

      int[] aint;
      try {
         aint = this.cache.get(i);
      } finally {
         this.lock.readLock().unlock();
      }

      if (aint != null) {
         return aint;
      } else {
         int[] aint1 = new int[256];
         Arrays.fill(aint1, -1);

         try {
            this.lock.writeLock().lock();
            if (this.cache.size() >= 256) {
               this.cache.removeFirst();
            }

            this.cache.put(i, aint1);
         } finally {
            this.lock.writeLock().unlock();
         }

         return aint1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Entry {
      public int chunkX = Integer.MIN_VALUE;
      public int chunkZ = Integer.MIN_VALUE;
      public int[] colorCache;

      private Entry() {
      }
   }
}