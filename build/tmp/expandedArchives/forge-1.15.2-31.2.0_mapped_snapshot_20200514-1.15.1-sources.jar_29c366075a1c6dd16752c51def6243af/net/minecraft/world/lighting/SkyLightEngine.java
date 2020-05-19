package net.minecraft.world.lighting;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableInt;

public final class SkyLightEngine extends LightEngine<SkyLightStorage.StorageMap, SkyLightStorage> {
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final Direction[] CARDINALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

   public SkyLightEngine(IChunkLightProvider p_i51289_1_) {
      super(p_i51289_1_, LightType.SKY, new SkyLightStorage(p_i51289_1_));
   }

   /**
    * Returns level propagated from start position with specified level to the neighboring end position.
    */
   protected int getEdgeLevel(long startPos, long endPos, int startLevel) {
      if (endPos == Long.MAX_VALUE) {
         return 15;
      } else {
         if (startPos == Long.MAX_VALUE) {
            if (!this.storage.func_215551_l(endPos)) {
               return 15;
            }

            startLevel = 0;
         }

         if (startLevel >= 15) {
            return startLevel;
         } else {
            MutableInt mutableint = new MutableInt();
            BlockState blockstate = this.func_227468_a_(endPos, mutableint);
            if (mutableint.getValue() >= 15) {
               return 15;
            } else {
               int i = BlockPos.unpackX(startPos);
               int j = BlockPos.unpackY(startPos);
               int k = BlockPos.unpackZ(startPos);
               int l = BlockPos.unpackX(endPos);
               int i1 = BlockPos.unpackY(endPos);
               int j1 = BlockPos.unpackZ(endPos);
               boolean flag = i == l && k == j1;
               int k1 = Integer.signum(l - i);
               int l1 = Integer.signum(i1 - j);
               int i2 = Integer.signum(j1 - k);
               Direction direction;
               if (startPos == Long.MAX_VALUE) {
                  direction = Direction.DOWN;
               } else {
                  direction = Direction.byLong(k1, l1, i2);
               }

               BlockState blockstate1 = this.func_227468_a_(startPos, (MutableInt)null);
               if (direction != null) {
                  VoxelShape voxelshape = this.getVoxelShape(blockstate1, startPos, direction);
                  VoxelShape voxelshape1 = this.getVoxelShape(blockstate, endPos, direction.getOpposite());
                  if (VoxelShapes.faceShapeCovers(voxelshape, voxelshape1)) {
                     return 15;
                  }
               } else {
                  VoxelShape voxelshape3 = this.getVoxelShape(blockstate1, startPos, Direction.DOWN);
                  if (VoxelShapes.faceShapeCovers(voxelshape3, VoxelShapes.empty())) {
                     return 15;
                  }

                  int j2 = flag ? -1 : 0;
                  Direction direction1 = Direction.byLong(k1, j2, i2);
                  if (direction1 == null) {
                     return 15;
                  }

                  VoxelShape voxelshape2 = this.getVoxelShape(blockstate, endPos, direction1.getOpposite());
                  if (VoxelShapes.faceShapeCovers(VoxelShapes.empty(), voxelshape2)) {
                     return 15;
                  }
               }

               boolean flag1 = startPos == Long.MAX_VALUE || flag && j > i1;
               return flag1 && startLevel == 0 && mutableint.getValue() == 0 ? 0 : startLevel + Math.max(1, mutableint.getValue());
            }
         }
      }
   }

   protected void notifyNeighbors(long pos, int level, boolean isDecreasing) {
      long i = SectionPos.worldToSection(pos);
      int j = BlockPos.unpackY(pos);
      int k = SectionPos.mask(j);
      int l = SectionPos.toChunk(j);
      int i1;
      if (k != 0) {
         i1 = 0;
      } else {
         int j1;
         for(j1 = 0; !this.storage.hasSection(SectionPos.withOffset(i, 0, -j1 - 1, 0)) && this.storage.func_215550_a(l - j1 - 1); ++j1) {
            ;
         }

         i1 = j1;
      }

      long i3 = BlockPos.offset(pos, 0, -1 - i1 * 16, 0);
      long k1 = SectionPos.worldToSection(i3);
      if (i == k1 || this.storage.hasSection(k1)) {
         this.propagateLevel(pos, i3, level, isDecreasing);
      }

      long l1 = BlockPos.offset(pos, Direction.UP);
      long i2 = SectionPos.worldToSection(l1);
      if (i == i2 || this.storage.hasSection(i2)) {
         this.propagateLevel(pos, l1, level, isDecreasing);
      }

      for(Direction direction : CARDINALS) {
         int j2 = 0;

         while(true) {
            long k2 = BlockPos.offset(pos, direction.getXOffset(), -j2, direction.getZOffset());
            long l2 = SectionPos.worldToSection(k2);
            if (i == l2) {
               this.propagateLevel(pos, k2, level, isDecreasing);
               break;
            }

            if (this.storage.hasSection(l2)) {
               this.propagateLevel(pos, k2, level, isDecreasing);
            }

            ++j2;
            if (j2 > i1 * 16) {
               break;
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
         long l = SectionPos.worldToSection(k);
         NibbleArray nibblearray1;
         if (j1 == l) {
            nibblearray1 = nibblearray;
         } else {
            nibblearray1 = this.storage.getArray(l, true);
         }

         if (nibblearray1 != null) {
            if (k != excludedSourcePos) {
               int k1 = this.getEdgeLevel(k, pos, this.getLevelFromArray(nibblearray1, k));
               if (i > k1) {
                  i = k1;
               }

               if (i == 0) {
                  return i;
               }
            }
         } else if (direction != Direction.DOWN) {
            for(k = BlockPos.func_218288_f(k); !this.storage.hasSection(l) && !this.storage.func_215549_m(l); k = BlockPos.offset(k, 0, 16, 0)) {
               l = SectionPos.withOffset(l, Direction.UP);
            }

            NibbleArray nibblearray2 = this.storage.getArray(l, true);
            if (k != excludedSourcePos) {
               int i1;
               if (nibblearray2 != null) {
                  i1 = this.getEdgeLevel(k, pos, this.getLevelFromArray(nibblearray2, k));
               } else {
                  i1 = this.storage.func_215548_n(l) ? 0 : 15;
               }

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

   protected void scheduleUpdate(long worldPos) {
      this.storage.processAllLevelUpdates();
      long i = SectionPos.worldToSection(worldPos);
      if (this.storage.hasSection(i)) {
         super.scheduleUpdate(worldPos);
      } else {
         for(worldPos = BlockPos.func_218288_f(worldPos); !this.storage.hasSection(i) && !this.storage.func_215549_m(i); worldPos = BlockPos.offset(worldPos, 0, 16, 0)) {
            i = SectionPos.withOffset(i, Direction.UP);
         }

         if (this.storage.hasSection(i)) {
            super.scheduleUpdate(worldPos);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public String getDebugString(long sectionPosIn) {
      return super.getDebugString(sectionPosIn) + (this.storage.func_215549_m(sectionPosIn) ? "*" : "");
   }

   // FORGE START

   @Override
   public int queuedUpdateSize() {
      return 0;
   }
}