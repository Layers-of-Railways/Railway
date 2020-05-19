package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

public final class VoxelShapeCube extends VoxelShape {
   protected VoxelShapeCube(VoxelShapePart p_i48182_1_) {
      super(p_i48182_1_);
   }

   protected DoubleList getValues(Direction.Axis axis) {
      return new DoubleRangeList(this.part.getSize(axis));
   }

   protected int getClosestIndex(Direction.Axis axis, double position) {
      int i = this.part.getSize(axis);
      return MathHelper.clamp(MathHelper.floor(position * (double)i), -1, i);
   }
}