package net.minecraft.util;

import net.minecraft.util.math.SectionPos;
import net.minecraft.world.lighting.LevelBasedGraph;

public abstract class SectionDistanceGraph extends LevelBasedGraph {
   protected SectionDistanceGraph(int p_i50706_1_, int p_i50706_2_, int p_i50706_3_) {
      super(p_i50706_1_, p_i50706_2_, p_i50706_3_);
   }

   protected boolean isRoot(long pos) {
      return pos == Long.MAX_VALUE;
   }

   protected void notifyNeighbors(long pos, int level, boolean isDecreasing) {
      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               long l = SectionPos.withOffset(pos, i, j, k);
               if (l != pos) {
                  this.propagateLevel(pos, l, level, isDecreasing);
               }
            }
         }
      }

   }

   /**
    * Computes level propagated from neighbors of specified position with given existing level, excluding the given
    * source position.
    */
   protected int computeLevel(long pos, long excludedSourcePos, int level) {
      int i = level;

      for(int j = -1; j <= 1; ++j) {
         for(int k = -1; k <= 1; ++k) {
            for(int l = -1; l <= 1; ++l) {
               long i1 = SectionPos.withOffset(pos, j, k, l);
               if (i1 == pos) {
                  i1 = Long.MAX_VALUE;
               }

               if (i1 != excludedSourcePos) {
                  int j1 = this.getEdgeLevel(i1, pos, this.getLevel(i1));
                  if (i > j1) {
                     i = j1;
                  }

                  if (i == 0) {
                     return i;
                  }
               }
            }
         }
      }

      return i;
   }

   /**
    * Returns level propagated from start position with specified level to the neighboring end position.
    */
   protected int getEdgeLevel(long startPos, long endPos, int startLevel) {
      return startPos == Long.MAX_VALUE ? this.getSourceLevel(endPos) : startLevel + 1;
   }

   protected abstract int getSourceLevel(long pos);

   public void updateSourceLevel(long pos, int level, boolean isDecreasing) {
      this.scheduleUpdate(Long.MAX_VALUE, pos, level, isDecreasing);
   }
}