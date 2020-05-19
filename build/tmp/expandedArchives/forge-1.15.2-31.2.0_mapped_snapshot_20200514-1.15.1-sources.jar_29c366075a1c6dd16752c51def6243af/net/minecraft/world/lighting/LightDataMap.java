package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.NibbleArray;

public abstract class LightDataMap<M extends LightDataMap<M>> {
   private final long[] recentPositions = new long[2];
   private final NibbleArray[] recentArrays = new NibbleArray[2];
   private boolean useCaching;
   protected final Long2ObjectOpenHashMap<NibbleArray> arrays;

   protected LightDataMap(Long2ObjectOpenHashMap<NibbleArray> arrayStorage) {
      this.arrays = arrayStorage;
      this.invalidateCaches();
      this.useCaching = true;
   }

   public abstract M copy();

   public void copyArray(long sectionPosIn) {
      this.arrays.put(sectionPosIn, this.arrays.get(sectionPosIn).copy());
      this.invalidateCaches();
   }

   public boolean hasArray(long sectionPosIn) {
      return this.arrays.containsKey(sectionPosIn);
   }

   @Nullable
   public NibbleArray getArray(long sectionPosIn) {
      if (this.useCaching) {
         for(int i = 0; i < 2; ++i) {
            if (sectionPosIn == this.recentPositions[i]) {
               return this.recentArrays[i];
            }
         }
      }

      NibbleArray nibblearray = this.arrays.get(sectionPosIn);
      if (nibblearray == null) {
         return null;
      } else {
         if (this.useCaching) {
            for(int j = 1; j > 0; --j) {
               this.recentPositions[j] = this.recentPositions[j - 1];
               this.recentArrays[j] = this.recentArrays[j - 1];
            }

            this.recentPositions[0] = sectionPosIn;
            this.recentArrays[0] = nibblearray;
         }

         return nibblearray;
      }
   }

   @Nullable
   public NibbleArray removeArray(long sectionPosIn) {
      return this.arrays.remove(sectionPosIn);
   }

   public void setArray(long sectionPosIn, NibbleArray array) {
      this.arrays.put(sectionPosIn, array);
   }

   public void invalidateCaches() {
      for(int i = 0; i < 2; ++i) {
         this.recentPositions[i] = Long.MAX_VALUE;
         this.recentArrays[i] = null;
      }

   }

   public void disableCaching() {
      this.useCaching = false;
   }
}