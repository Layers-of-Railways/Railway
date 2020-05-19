package net.minecraft.world.chunk;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.lighting.LevelBasedGraph;

public abstract class ChunkDistanceGraph extends LevelBasedGraph {
   protected ChunkDistanceGraph(int p_i50712_1_, int p_i50712_2_, int p_i50712_3_) {
      super(p_i50712_1_, p_i50712_2_, p_i50712_3_);
   }

   protected boolean isRoot(long pos) {
      return pos == ChunkPos.SENTINEL;
   }

   protected void notifyNeighbors(long pos, int level, boolean isDecreasing) {
      ChunkPos chunkpos = new ChunkPos(pos);
      int i = chunkpos.x;
      int j = chunkpos.z;

      for(int k = -1; k <= 1; ++k) {
         for(int l = -1; l <= 1; ++l) {
            long i1 = ChunkPos.asLong(i + k, j + l);
            if (i1 != pos) {
               this.propagateLevel(pos, i1, level, isDecreasing);
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
      ChunkPos chunkpos = new ChunkPos(pos);
      int j = chunkpos.x;
      int k = chunkpos.z;

      for(int l = -1; l <= 1; ++l) {
         for(int i1 = -1; i1 <= 1; ++i1) {
            long j1 = ChunkPos.asLong(j + l, k + i1);
            if (j1 == pos) {
               j1 = ChunkPos.SENTINEL;
            }

            if (j1 != excludedSourcePos) {
               int k1 = this.getEdgeLevel(j1, pos, this.getLevel(j1));
               if (i > k1) {
                  i = k1;
               }

               if (i == 0) {
                  return i;
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
      return startPos == ChunkPos.SENTINEL ? this.getSourceLevel(endPos) : startLevel + 1;
   }

   protected abstract int getSourceLevel(long pos);

   public void updateSourceLevel(long pos, int level, boolean isDecreasing) {
      this.scheduleUpdate(ChunkPos.SENTINEL, pos, level, isDecreasing);
   }
}