package net.minecraft.util.math.shapes;

import net.minecraft.util.Direction;

public final class PartSplitVoxelShape extends VoxelShapePart {
   private final VoxelShapePart part;
   private final int startX;
   private final int startY;
   private final int startZ;
   private final int endX;
   private final int endY;
   private final int endZ;

   protected PartSplitVoxelShape(VoxelShapePart partIn, int startXIn, int startYIn, int startZIn, int endXIn, int endYIn, int endZIn) {
      super(endXIn - startXIn, endYIn - startYIn, endZIn - startZIn);
      this.part = partIn;
      this.startX = startXIn;
      this.startY = startYIn;
      this.startZ = startZIn;
      this.endX = endXIn;
      this.endY = endYIn;
      this.endZ = endZIn;
   }

   public boolean isFilled(int x, int y, int z) {
      return this.part.isFilled(this.startX + x, this.startY + y, this.startZ + z);
   }

   public void setFilled(int x, int y, int z, boolean expandBounds, boolean filled) {
      this.part.setFilled(this.startX + x, this.startY + y, this.startZ + z, expandBounds, filled);
   }

   public int getStart(Direction.Axis axis) {
      return Math.max(0, this.part.getStart(axis) - axis.getCoordinate(this.startX, this.startY, this.startZ));
   }

   public int getEnd(Direction.Axis axis) {
      return Math.min(axis.getCoordinate(this.endX, this.endY, this.endZ), this.part.getEnd(axis) - axis.getCoordinate(this.startX, this.startY, this.startZ));
   }
}