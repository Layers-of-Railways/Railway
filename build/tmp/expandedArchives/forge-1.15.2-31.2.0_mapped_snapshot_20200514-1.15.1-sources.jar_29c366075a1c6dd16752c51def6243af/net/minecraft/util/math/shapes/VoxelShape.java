package net.minecraft.util.math.shapes;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class VoxelShape {
   protected final VoxelShapePart part;
   @Nullable
   private VoxelShape[] projectionCache;

   VoxelShape(VoxelShapePart part) {
      this.part = part;
   }

   public double getStart(Direction.Axis axis) {
      int i = this.part.getStart(axis);
      return i >= this.part.getSize(axis) ? Double.POSITIVE_INFINITY : this.getValueUnchecked(axis, i);
   }

   public double getEnd(Direction.Axis axis) {
      int i = this.part.getEnd(axis);
      return i <= 0 ? Double.NEGATIVE_INFINITY : this.getValueUnchecked(axis, i);
   }

   public AxisAlignedBB getBoundingBox() {
      if (this.isEmpty()) {
         throw (UnsupportedOperationException)Util.pauseDevMode(new UnsupportedOperationException("No bounds for empty shape."));
      } else {
         return new AxisAlignedBB(this.getStart(Direction.Axis.X), this.getStart(Direction.Axis.Y), this.getStart(Direction.Axis.Z), this.getEnd(Direction.Axis.X), this.getEnd(Direction.Axis.Y), this.getEnd(Direction.Axis.Z));
      }
   }

   protected double getValueUnchecked(Direction.Axis axis, int index) {
      return this.getValues(axis).getDouble(index);
   }

   protected abstract DoubleList getValues(Direction.Axis axis);

   public boolean isEmpty() {
      return this.part.isEmpty();
   }

   public VoxelShape withOffset(double xOffset, double yOffset, double zOffset) {
      return (VoxelShape)(this.isEmpty() ? VoxelShapes.empty() : new VoxelShapeArray(this.part, (DoubleList)(new OffsetDoubleList(this.getValues(Direction.Axis.X), xOffset)), (DoubleList)(new OffsetDoubleList(this.getValues(Direction.Axis.Y), yOffset)), (DoubleList)(new OffsetDoubleList(this.getValues(Direction.Axis.Z), zOffset))));
   }

   public VoxelShape simplify() {
      VoxelShape[] avoxelshape = new VoxelShape[]{VoxelShapes.empty()};
      this.forEachBox((p_197763_1_, p_197763_3_, p_197763_5_, p_197763_7_, p_197763_9_, p_197763_11_) -> {
         avoxelshape[0] = VoxelShapes.combine(avoxelshape[0], VoxelShapes.create(p_197763_1_, p_197763_3_, p_197763_5_, p_197763_7_, p_197763_9_, p_197763_11_), IBooleanFunction.OR);
      });
      return avoxelshape[0];
   }

   @OnlyIn(Dist.CLIENT)
   public void forEachEdge(VoxelShapes.ILineConsumer action) {
      this.part.forEachEdge((p_197750_2_, p_197750_3_, p_197750_4_, p_197750_5_, p_197750_6_, p_197750_7_) -> {
         action.consume(this.getValueUnchecked(Direction.Axis.X, p_197750_2_), this.getValueUnchecked(Direction.Axis.Y, p_197750_3_), this.getValueUnchecked(Direction.Axis.Z, p_197750_4_), this.getValueUnchecked(Direction.Axis.X, p_197750_5_), this.getValueUnchecked(Direction.Axis.Y, p_197750_6_), this.getValueUnchecked(Direction.Axis.Z, p_197750_7_));
      }, true);
   }

   public void forEachBox(VoxelShapes.ILineConsumer action) {
      DoubleList doublelist = this.getValues(Direction.Axis.X);
      DoubleList doublelist1 = this.getValues(Direction.Axis.Y);
      DoubleList doublelist2 = this.getValues(Direction.Axis.Z);
      this.part.forEachBox((p_224789_4_, p_224789_5_, p_224789_6_, p_224789_7_, p_224789_8_, p_224789_9_) -> {
         action.consume(doublelist.getDouble(p_224789_4_), doublelist1.getDouble(p_224789_5_), doublelist2.getDouble(p_224789_6_), doublelist.getDouble(p_224789_7_), doublelist1.getDouble(p_224789_8_), doublelist2.getDouble(p_224789_9_));
      }, true);
   }

   public List<AxisAlignedBB> toBoundingBoxList() {
      List<AxisAlignedBB> list = Lists.newArrayList();
      this.forEachBox((p_203431_1_, p_203431_3_, p_203431_5_, p_203431_7_, p_203431_9_, p_203431_11_) -> {
         list.add(new AxisAlignedBB(p_203431_1_, p_203431_3_, p_203431_5_, p_203431_7_, p_203431_9_, p_203431_11_));
      });
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public double min(Direction.Axis axis, double p_197764_2_, double p_197764_4_) {
      Direction.Axis direction$axis = AxisRotation.FORWARD.rotate(axis);
      Direction.Axis direction$axis1 = AxisRotation.BACKWARD.rotate(axis);
      int i = this.getClosestIndex(direction$axis, p_197764_2_);
      int j = this.getClosestIndex(direction$axis1, p_197764_4_);
      int k = this.part.firstFilled(axis, i, j);
      return k >= this.part.getSize(axis) ? Double.POSITIVE_INFINITY : this.getValueUnchecked(axis, k);
   }

   @OnlyIn(Dist.CLIENT)
   public double max(Direction.Axis p_197760_1_, double p_197760_2_, double p_197760_4_) {
      Direction.Axis direction$axis = AxisRotation.FORWARD.rotate(p_197760_1_);
      Direction.Axis direction$axis1 = AxisRotation.BACKWARD.rotate(p_197760_1_);
      int i = this.getClosestIndex(direction$axis, p_197760_2_);
      int j = this.getClosestIndex(direction$axis1, p_197760_4_);
      int k = this.part.lastFilled(p_197760_1_, i, j);
      return k <= 0 ? Double.NEGATIVE_INFINITY : this.getValueUnchecked(p_197760_1_, k);
   }

   protected int getClosestIndex(Direction.Axis axis, double position) {
      return MathHelper.binarySearch(0, this.part.getSize(axis) + 1, (p_197761_4_) -> {
         if (p_197761_4_ < 0) {
            return false;
         } else if (p_197761_4_ > this.part.getSize(axis)) {
            return true;
         } else {
            return position < this.getValueUnchecked(axis, p_197761_4_);
         }
      }) - 1;
   }

   protected boolean contains(double x, double y, double z) {
      return this.part.contains(this.getClosestIndex(Direction.Axis.X, x), this.getClosestIndex(Direction.Axis.Y, y), this.getClosestIndex(Direction.Axis.Z, z));
   }

   @Nullable
   public BlockRayTraceResult rayTrace(Vec3d p_212433_1_, Vec3d p_212433_2_, BlockPos p_212433_3_) {
      if (this.isEmpty()) {
         return null;
      } else {
         Vec3d vec3d = p_212433_2_.subtract(p_212433_1_);
         if (vec3d.lengthSquared() < 1.0E-7D) {
            return null;
         } else {
            Vec3d vec3d1 = p_212433_1_.add(vec3d.scale(0.001D));
            return this.contains(vec3d1.x - (double)p_212433_3_.getX(), vec3d1.y - (double)p_212433_3_.getY(), vec3d1.z - (double)p_212433_3_.getZ()) ? new BlockRayTraceResult(vec3d1, Direction.getFacingFromVector(vec3d.x, vec3d.y, vec3d.z).getOpposite(), p_212433_3_, true) : AxisAlignedBB.rayTrace(this.toBoundingBoxList(), p_212433_1_, p_212433_2_, p_212433_3_);
         }
      }
   }

   /**
    * "Projects" this shape onto the given side. For each box in the shape, if it does not touch the given side, it is
    * eliminated. Otherwise, the box is extended in the given axis to cover the entire range [0, 1].
    */
   public VoxelShape project(Direction side) {
      if (!this.isEmpty() && this != VoxelShapes.fullCube()) {
         if (this.projectionCache != null) {
            VoxelShape voxelshape = this.projectionCache[side.ordinal()];
            if (voxelshape != null) {
               return voxelshape;
            }
         } else {
            this.projectionCache = new VoxelShape[6];
         }

         VoxelShape voxelshape1 = this.doProject(side);
         this.projectionCache[side.ordinal()] = voxelshape1;
         return voxelshape1;
      } else {
         return this;
      }
   }

   private VoxelShape doProject(Direction side) {
      Direction.Axis direction$axis = side.getAxis();
      Direction.AxisDirection direction$axisdirection = side.getAxisDirection();
      DoubleList doublelist = this.getValues(direction$axis);
      if (doublelist.size() == 2 && DoubleMath.fuzzyEquals(doublelist.getDouble(0), 0.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(doublelist.getDouble(1), 1.0D, 1.0E-7D)) {
         return this;
      } else {
         int i = this.getClosestIndex(direction$axis, direction$axisdirection == Direction.AxisDirection.POSITIVE ? 0.9999999D : 1.0E-7D);
         return new SplitVoxelShape(this, direction$axis, i);
      }
   }

   public double getAllowedOffset(Direction.Axis movementAxis, AxisAlignedBB collisionBox, double desiredOffset) {
      return this.getAllowedOffset(AxisRotation.from(movementAxis, Direction.Axis.X), collisionBox, desiredOffset);
   }

   protected double getAllowedOffset(AxisRotation movementAxis, AxisAlignedBB collisionBox, double desiredOffset) {
      if (this.isEmpty()) {
         return desiredOffset;
      } else if (Math.abs(desiredOffset) < 1.0E-7D) {
         return 0.0D;
      } else {
         AxisRotation axisrotation = movementAxis.reverse();
         Direction.Axis direction$axis = axisrotation.rotate(Direction.Axis.X);
         Direction.Axis direction$axis1 = axisrotation.rotate(Direction.Axis.Y);
         Direction.Axis direction$axis2 = axisrotation.rotate(Direction.Axis.Z);
         double d0 = collisionBox.getMax(direction$axis);
         double d1 = collisionBox.getMin(direction$axis);
         int i = this.getClosestIndex(direction$axis, d1 + 1.0E-7D);
         int j = this.getClosestIndex(direction$axis, d0 - 1.0E-7D);
         int k = Math.max(0, this.getClosestIndex(direction$axis1, collisionBox.getMin(direction$axis1) + 1.0E-7D));
         int l = Math.min(this.part.getSize(direction$axis1), this.getClosestIndex(direction$axis1, collisionBox.getMax(direction$axis1) - 1.0E-7D) + 1);
         int i1 = Math.max(0, this.getClosestIndex(direction$axis2, collisionBox.getMin(direction$axis2) + 1.0E-7D));
         int j1 = Math.min(this.part.getSize(direction$axis2), this.getClosestIndex(direction$axis2, collisionBox.getMax(direction$axis2) - 1.0E-7D) + 1);
         int k1 = this.part.getSize(direction$axis);
         if (desiredOffset > 0.0D) {
            for(int l1 = j + 1; l1 < k1; ++l1) {
               for(int i2 = k; i2 < l; ++i2) {
                  for(int j2 = i1; j2 < j1; ++j2) {
                     if (this.part.containsWithRotation(axisrotation, l1, i2, j2)) {
                        double d2 = this.getValueUnchecked(direction$axis, l1) - d0;
                        if (d2 >= -1.0E-7D) {
                           desiredOffset = Math.min(desiredOffset, d2);
                        }

                        return desiredOffset;
                     }
                  }
               }
            }
         } else if (desiredOffset < 0.0D) {
            for(int k2 = i - 1; k2 >= 0; --k2) {
               for(int l2 = k; l2 < l; ++l2) {
                  for(int i3 = i1; i3 < j1; ++i3) {
                     if (this.part.containsWithRotation(axisrotation, k2, l2, i3)) {
                        double d3 = this.getValueUnchecked(direction$axis, k2 + 1) - d1;
                        if (d3 <= 1.0E-7D) {
                           desiredOffset = Math.max(desiredOffset, d3);
                        }

                        return desiredOffset;
                     }
                  }
               }
            }
         }

         return desiredOffset;
      }
   }

   public String toString() {
      return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.getBoundingBox() + "]";
   }
}