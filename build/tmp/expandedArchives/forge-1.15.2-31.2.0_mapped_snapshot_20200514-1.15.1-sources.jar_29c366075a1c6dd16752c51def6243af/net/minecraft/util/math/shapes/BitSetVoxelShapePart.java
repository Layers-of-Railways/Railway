package net.minecraft.util.math.shapes;

import java.util.BitSet;
import net.minecraft.util.Direction;

public final class BitSetVoxelShapePart extends VoxelShapePart {
   private final BitSet bitSet;
   private int startX;
   private int startY;
   private int startZ;
   private int endX;
   private int endY;
   private int endZ;

   public BitSetVoxelShapePart(int xSizeIn, int ySizeIn, int zSizeIn) {
      this(xSizeIn, ySizeIn, zSizeIn, xSizeIn, ySizeIn, zSizeIn, 0, 0, 0);
   }

   public BitSetVoxelShapePart(int xSizeIn, int ySizeIn, int zSizeIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      super(xSizeIn, ySizeIn, zSizeIn);
      this.bitSet = new BitSet(xSizeIn * ySizeIn * zSizeIn);
      this.startX = minX;
      this.startY = minY;
      this.startZ = minZ;
      this.endX = maxX;
      this.endY = maxY;
      this.endZ = maxZ;
   }

   public BitSetVoxelShapePart(VoxelShapePart shapePart) {
      super(shapePart.xSize, shapePart.ySize, shapePart.zSize);
      if (shapePart instanceof BitSetVoxelShapePart) {
         this.bitSet = (BitSet)((BitSetVoxelShapePart)shapePart).bitSet.clone();
      } else {
         this.bitSet = new BitSet(this.xSize * this.ySize * this.zSize);

         for(int i = 0; i < this.xSize; ++i) {
            for(int j = 0; j < this.ySize; ++j) {
               for(int k = 0; k < this.zSize; ++k) {
                  if (shapePart.isFilled(i, j, k)) {
                     this.bitSet.set(this.getIndex(i, j, k));
                  }
               }
            }
         }
      }

      this.startX = shapePart.getStart(Direction.Axis.X);
      this.startY = shapePart.getStart(Direction.Axis.Y);
      this.startZ = shapePart.getStart(Direction.Axis.Z);
      this.endX = shapePart.getEnd(Direction.Axis.X);
      this.endY = shapePart.getEnd(Direction.Axis.Y);
      this.endZ = shapePart.getEnd(Direction.Axis.Z);
   }

   protected int getIndex(int x, int y, int z) {
      return (x * this.ySize + y) * this.zSize + z;
   }

   public boolean isFilled(int x, int y, int z) {
      return this.bitSet.get(this.getIndex(x, y, z));
   }

   public void setFilled(int x, int y, int z, boolean expandBounds, boolean filled) {
      this.bitSet.set(this.getIndex(x, y, z), filled);
      if (expandBounds && filled) {
         this.startX = Math.min(this.startX, x);
         this.startY = Math.min(this.startY, y);
         this.startZ = Math.min(this.startZ, z);
         this.endX = Math.max(this.endX, x + 1);
         this.endY = Math.max(this.endY, y + 1);
         this.endZ = Math.max(this.endZ, z + 1);
      }

   }

   public boolean isEmpty() {
      return this.bitSet.isEmpty();
   }

   public int getStart(Direction.Axis axis) {
      return axis.getCoordinate(this.startX, this.startY, this.startZ);
   }

   public int getEnd(Direction.Axis axis) {
      return axis.getCoordinate(this.endX, this.endY, this.endZ);
   }

   protected boolean isZAxisLineFull(int fromZ, int toZ, int x, int y) {
      if (x >= 0 && y >= 0 && fromZ >= 0) {
         if (x < this.xSize && y < this.ySize && toZ <= this.zSize) {
            return this.bitSet.nextClearBit(this.getIndex(x, y, fromZ)) >= this.getIndex(x, y, toZ);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void setZAxisLine(int fromZ, int toZ, int x, int y, boolean filled) {
      this.bitSet.set(this.getIndex(x, y, fromZ), this.getIndex(x, y, toZ), filled);
   }

   static BitSetVoxelShapePart func_197852_a(VoxelShapePart first, VoxelShapePart second, IDoubleListMerger xMerger, IDoubleListMerger yMerger, IDoubleListMerger zMerger, IBooleanFunction op) {
      BitSetVoxelShapePart bitsetvoxelshapepart = new BitSetVoxelShapePart(xMerger.func_212435_a().size() - 1, yMerger.func_212435_a().size() - 1, zMerger.func_212435_a().size() - 1);
      int[] aint = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      xMerger.forMergedIndexes((p_199628_7_, p_199628_8_, p_199628_9_) -> {
         boolean[] aboolean = new boolean[]{false};
         boolean flag = yMerger.forMergedIndexes((p_199627_10_, p_199627_11_, p_199627_12_) -> {
            boolean[] aboolean1 = new boolean[]{false};
            boolean flag1 = zMerger.forMergedIndexes((p_199629_12_, p_199629_13_, p_199629_14_) -> {
               boolean flag2 = op.apply(first.contains(p_199628_7_, p_199627_10_, p_199629_12_), second.contains(p_199628_8_, p_199627_11_, p_199629_13_));
               if (flag2) {
                  bitsetvoxelshapepart.bitSet.set(bitsetvoxelshapepart.getIndex(p_199628_9_, p_199627_12_, p_199629_14_));
                  aint[2] = Math.min(aint[2], p_199629_14_);
                  aint[5] = Math.max(aint[5], p_199629_14_);
                  aboolean1[0] = true;
               }

               return true;
            });
            if (aboolean1[0]) {
               aint[1] = Math.min(aint[1], p_199627_12_);
               aint[4] = Math.max(aint[4], p_199627_12_);
               aboolean[0] = true;
            }

            return flag1;
         });
         if (aboolean[0]) {
            aint[0] = Math.min(aint[0], p_199628_9_);
            aint[3] = Math.max(aint[3], p_199628_9_);
         }

         return flag;
      });
      bitsetvoxelshapepart.startX = aint[0];
      bitsetvoxelshapepart.startY = aint[1];
      bitsetvoxelshapepart.startZ = aint[2];
      bitsetvoxelshapepart.endX = aint[3] + 1;
      bitsetvoxelshapepart.endY = aint[4] + 1;
      bitsetvoxelshapepart.endZ = aint[5] + 1;
      return bitsetvoxelshapepart;
   }
}