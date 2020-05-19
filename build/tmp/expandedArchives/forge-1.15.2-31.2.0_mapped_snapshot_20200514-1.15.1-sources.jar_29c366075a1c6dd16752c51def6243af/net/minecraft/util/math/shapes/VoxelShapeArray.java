package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;

public final class VoxelShapeArray extends VoxelShape {
   private final DoubleList xPoints;
   private final DoubleList yPoints;
   private final DoubleList zPoints;

   protected VoxelShapeArray(VoxelShapePart shapePartIn, double[] xPointsIn, double[] yPointsIn, double[] zPointsIn) {
      this(shapePartIn, (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(xPointsIn, shapePartIn.getXSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(yPointsIn, shapePartIn.getYSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(zPointsIn, shapePartIn.getZSize() + 1)));
   }

   VoxelShapeArray(VoxelShapePart shapePartIn, DoubleList xPointsIn, DoubleList yPointsIn, DoubleList zPointsIn) {
      super(shapePartIn);
      int i = shapePartIn.getXSize() + 1;
      int j = shapePartIn.getYSize() + 1;
      int k = shapePartIn.getZSize() + 1;
      if (i == xPointsIn.size() && j == yPointsIn.size() && k == zPointsIn.size()) {
         this.xPoints = xPointsIn;
         this.yPoints = yPointsIn;
         this.zPoints = zPointsIn;
      } else {
         throw (IllegalArgumentException)Util.pauseDevMode(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
      }
   }

   protected DoubleList getValues(Direction.Axis axis) {
      switch(axis) {
      case X:
         return this.xPoints;
      case Y:
         return this.yPoints;
      case Z:
         return this.zPoints;
      default:
         throw new IllegalArgumentException();
      }
   }
}