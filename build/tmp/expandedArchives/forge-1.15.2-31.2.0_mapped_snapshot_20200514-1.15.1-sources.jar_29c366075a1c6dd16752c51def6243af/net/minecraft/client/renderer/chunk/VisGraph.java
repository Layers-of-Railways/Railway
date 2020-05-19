package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VisGraph {
   private static final int DX = (int)Math.pow(16.0D, 0.0D);
   private static final int DZ = (int)Math.pow(16.0D, 1.0D);
   private static final int DY = (int)Math.pow(16.0D, 2.0D);
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BitSet bitSet = new BitSet(4096);
   private static final int[] INDEX_OF_EDGES = Util.make(new int[1352], (p_209264_0_) -> {
      int i = 0;
      int j = 15;
      int k = 0;

      for(int l = 0; l < 16; ++l) {
         for(int i1 = 0; i1 < 16; ++i1) {
            for(int j1 = 0; j1 < 16; ++j1) {
               if (l == 0 || l == 15 || i1 == 0 || i1 == 15 || j1 == 0 || j1 == 15) {
                  p_209264_0_[k++] = getIndex(l, i1, j1);
               }
            }
         }
      }

   });
   private int empty = 4096;

   public void setOpaqueCube(BlockPos pos) {
      this.bitSet.set(getIndex(pos), true);
      --this.empty;
   }

   private static int getIndex(BlockPos pos) {
      return getIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
   }

   private static int getIndex(int x, int y, int z) {
      return x << 0 | y << 8 | z << 4;
   }

   public SetVisibility computeVisibility() {
      SetVisibility setvisibility = new SetVisibility();
      if (4096 - this.empty < 256) {
         setvisibility.setAllVisible(true);
      } else if (this.empty == 0) {
         setvisibility.setAllVisible(false);
      } else {
         for(int i : INDEX_OF_EDGES) {
            if (!this.bitSet.get(i)) {
               setvisibility.setManyVisible(this.floodFill(i));
            }
         }
      }

      return setvisibility;
   }

   public Set<Direction> getVisibleFacings(BlockPos pos) {
      return this.floodFill(getIndex(pos));
   }

   private Set<Direction> floodFill(int pos) {
      Set<Direction> set = EnumSet.noneOf(Direction.class);
      IntPriorityQueue intpriorityqueue = new IntArrayFIFOQueue();
      intpriorityqueue.enqueue(pos);
      this.bitSet.set(pos, true);

      while(!intpriorityqueue.isEmpty()) {
         int i = intpriorityqueue.dequeueInt();
         this.addEdges(i, set);

         for(Direction direction : DIRECTIONS) {
            int j = this.getNeighborIndexAtFace(i, direction);
            if (j >= 0 && !this.bitSet.get(j)) {
               this.bitSet.set(j, true);
               intpriorityqueue.enqueue(j);
            }
         }
      }

      return set;
   }

   private void addEdges(int pos, Set<Direction> setFacings) {
      int i = pos >> 0 & 15;
      if (i == 0) {
         setFacings.add(Direction.WEST);
      } else if (i == 15) {
         setFacings.add(Direction.EAST);
      }

      int j = pos >> 8 & 15;
      if (j == 0) {
         setFacings.add(Direction.DOWN);
      } else if (j == 15) {
         setFacings.add(Direction.UP);
      }

      int k = pos >> 4 & 15;
      if (k == 0) {
         setFacings.add(Direction.NORTH);
      } else if (k == 15) {
         setFacings.add(Direction.SOUTH);
      }

   }

   private int getNeighborIndexAtFace(int pos, Direction facing) {
      switch(facing) {
      case DOWN:
         if ((pos >> 8 & 15) == 0) {
            return -1;
         }

         return pos - DY;
      case UP:
         if ((pos >> 8 & 15) == 15) {
            return -1;
         }

         return pos + DY;
      case NORTH:
         if ((pos >> 4 & 15) == 0) {
            return -1;
         }

         return pos - DZ;
      case SOUTH:
         if ((pos >> 4 & 15) == 15) {
            return -1;
         }

         return pos + DZ;
      case WEST:
         if ((pos >> 0 & 15) == 0) {
            return -1;
         }

         return pos - DX;
      case EAST:
         if ((pos >> 0 & 15) == 15) {
            return -1;
         }

         return pos + DX;
      default:
         return -1;
      }
   }
}