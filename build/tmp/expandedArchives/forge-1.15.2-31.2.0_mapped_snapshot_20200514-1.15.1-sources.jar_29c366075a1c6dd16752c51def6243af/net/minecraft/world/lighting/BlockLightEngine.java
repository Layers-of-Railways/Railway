package net.minecraft.world.lighting;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import org.apache.commons.lang3.mutable.MutableInt;

public final class BlockLightEngine extends LightEngine<BlockLightStorage.StorageMap, BlockLightStorage> {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BlockPos.Mutable scratchPos = new BlockPos.Mutable();

   public BlockLightEngine(IChunkLightProvider p_i51301_1_) {
      super(p_i51301_1_, LightType.BLOCK, new BlockLightStorage(p_i51301_1_));
   }

   private int getLightValue(long worldPos) {
      int i = BlockPos.unpackX(worldPos);
      int j = BlockPos.unpackY(worldPos);
      int k = BlockPos.unpackZ(worldPos);
      IBlockReader iblockreader = this.chunkProvider.getChunkForLight(i >> 4, k >> 4);
      return iblockreader != null ? iblockreader.getLightValue(this.scratchPos.setPos(i, j, k)) : 0;
   }

   /**
    * Returns level propagated from start position with specified level to the neighboring end position.
    */
   protected int getEdgeLevel(long startPos, long endPos, int startLevel) {
      if (endPos == Long.MAX_VALUE) {
         return 15;
      } else if (startPos == Long.MAX_VALUE) {
         return startLevel + 15 - this.getLightValue(endPos);
      } else if (startLevel >= 15) {
         return startLevel;
      } else {
         int i = Integer.signum(BlockPos.unpackX(endPos) - BlockPos.unpackX(startPos));
         int j = Integer.signum(BlockPos.unpackY(endPos) - BlockPos.unpackY(startPos));
         int k = Integer.signum(BlockPos.unpackZ(endPos) - BlockPos.unpackZ(startPos));
         Direction direction = Direction.byLong(i, j, k);
         if (direction == null) {
            return 15;
         } else {
            MutableInt mutableint = new MutableInt();
            BlockState blockstate = this.func_227468_a_(endPos, mutableint);
            if (mutableint.getValue() >= 15) {
               return 15;
            } else {
               BlockState blockstate1 = this.func_227468_a_(startPos, (MutableInt)null);
               VoxelShape voxelshape = this.getVoxelShape(blockstate1, startPos, direction);
               VoxelShape voxelshape1 = this.getVoxelShape(blockstate, endPos, direction.getOpposite());
               return VoxelShapes.faceShapeCovers(voxelshape, voxelshape1) ? 15 : startLevel + Math.max(1, mutableint.getValue());
            }
         }
      }
   }

   protected void notifyNeighbors(long pos, int level, boolean isDecreasing) {
      long i = SectionPos.worldToSection(pos);

      for(Direction direction : DIRECTIONS) {
         long j = BlockPos.offset(pos, direction);
         long k = SectionPos.worldToSection(j);
         if (i == k || this.storage.hasSection(k)) {
            this.propagateLevel(pos, j, level, isDecreasing);
         }
      }

   }

   /**
    * Computes level propagated from neighbors of specified position with given existing level, excluding the given
    * source position.
    */
   protected int computeLevel(long pos, long excludedSourcePos, int level) {
      int i = level;
      if (Long.MAX_VALUE != excludedSourcePos) {
         int j = this.getEdgeLevel(Long.MAX_VALUE, pos, 0);
         if (level > j) {
            i = j;
         }

         if (i == 0) {
            return i;
         }
      }

      long j1 = SectionPos.worldToSection(pos);
      NibbleArray nibblearray = this.storage.getArray(j1, true);

      for(Direction direction : DIRECTIONS) {
         long k = BlockPos.offset(pos, direction);
         if (k != excludedSourcePos) {
            long l = SectionPos.worldToSection(k);
            NibbleArray nibblearray1;
            if (j1 == l) {
               nibblearray1 = nibblearray;
            } else {
               nibblearray1 = this.storage.getArray(l, true);
            }

            if (nibblearray1 != null) {
               int i1 = this.getEdgeLevel(k, pos, this.getLevelFromArray(nibblearray1, k));
               if (i > i1) {
                  i = i1;
               }

               if (i == 0) {
                  return i;
               }
            }
         }
      }

      return i;
   }

   public void func_215623_a(BlockPos p_215623_1_, int p_215623_2_) {
      this.storage.processAllLevelUpdates();
      this.scheduleUpdate(Long.MAX_VALUE, p_215623_1_.toLong(), 15 - p_215623_2_, true);
   }

   // FORGE START

   @Override
   public int queuedUpdateSize() {
      return storage.queuedUpdateSize();
   }
}