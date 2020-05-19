package net.minecraft.util.math.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class VoxelShapes {
   private static final VoxelShape FULL_CUBE = Util.make(() -> {
      VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(1, 1, 1);
      voxelshapepart.setFilled(0, 0, 0, true, true);
      return new VoxelShapeCube(voxelshapepart);
   });
   public static final VoxelShape INFINITY = create(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
   private static final VoxelShape EMPTY = new VoxelShapeArray(new BitSetVoxelShapePart(0, 0, 0), (DoubleList)(new DoubleArrayList(new double[]{0.0D})), (DoubleList)(new DoubleArrayList(new double[]{0.0D})), (DoubleList)(new DoubleArrayList(new double[]{0.0D})));

   public static VoxelShape empty() {
      return EMPTY;
   }

   public static VoxelShape fullCube() {
      return FULL_CUBE;
   }

   public static VoxelShape create(double x1, double y1, double z1, double x2, double y2, double z2) {
      return create(new AxisAlignedBB(x1, y1, z1, x2, y2, z2));
   }

   public static VoxelShape create(AxisAlignedBB aabb) {
      int i = getPrecisionBits(aabb.minX, aabb.maxX);
      int j = getPrecisionBits(aabb.minY, aabb.maxY);
      int k = getPrecisionBits(aabb.minZ, aabb.maxZ);
      if (i >= 0 && j >= 0 && k >= 0) {
         if (i == 0 && j == 0 && k == 0) {
            return aabb.contains(0.5D, 0.5D, 0.5D) ? fullCube() : empty();
         } else {
            int l = 1 << i;
            int i1 = 1 << j;
            int j1 = 1 << k;
            int k1 = (int)Math.round(aabb.minX * (double)l);
            int l1 = (int)Math.round(aabb.maxX * (double)l);
            int i2 = (int)Math.round(aabb.minY * (double)i1);
            int j2 = (int)Math.round(aabb.maxY * (double)i1);
            int k2 = (int)Math.round(aabb.minZ * (double)j1);
            int l2 = (int)Math.round(aabb.maxZ * (double)j1);
            BitSetVoxelShapePart bitsetvoxelshapepart = new BitSetVoxelShapePart(l, i1, j1, k1, i2, k2, l1, j2, l2);

            for(long i3 = (long)k1; i3 < (long)l1; ++i3) {
               for(long j3 = (long)i2; j3 < (long)j2; ++j3) {
                  for(long k3 = (long)k2; k3 < (long)l2; ++k3) {
                     bitsetvoxelshapepart.setFilled((int)i3, (int)j3, (int)k3, false, true);
                  }
               }
            }

            return new VoxelShapeCube(bitsetvoxelshapepart);
         }
      } else {
         return new VoxelShapeArray(FULL_CUBE.part, new double[]{aabb.minX, aabb.maxX}, new double[]{aabb.minY, aabb.maxY}, new double[]{aabb.minZ, aabb.maxZ});
      }
   }

   private static int getPrecisionBits(double p_197885_0_, double p_197885_2_) {
      if (!(p_197885_0_ < -1.0E-7D) && !(p_197885_2_ > 1.0000001D)) {
         for(int i = 0; i <= 3; ++i) {
            double d0 = p_197885_0_ * (double)(1 << i);
            double d1 = p_197885_2_ * (double)(1 << i);
            boolean flag = Math.abs(d0 - Math.floor(d0)) < 1.0E-7D;
            boolean flag1 = Math.abs(d1 - Math.floor(d1)) < 1.0E-7D;
            if (flag && flag1) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   protected static long lcm(int aa, int bb) {
      return (long)aa * (long)(bb / IntMath.gcd(aa, bb));
   }

   public static VoxelShape or(VoxelShape shape1, VoxelShape shape2) {
      return combineAndSimplify(shape1, shape2, IBooleanFunction.OR);
   }

   public static VoxelShape or(VoxelShape p_216384_0_, VoxelShape... p_216384_1_) {
      return Arrays.stream(p_216384_1_).reduce(p_216384_0_, VoxelShapes::or);
   }

   public static VoxelShape combineAndSimplify(VoxelShape shape1, VoxelShape shape2, IBooleanFunction function) {
      return combine(shape1, shape2, function).simplify();
   }

   public static VoxelShape combine(VoxelShape shape1, VoxelShape shape2, IBooleanFunction function) {
      if (function.apply(false, false)) {
         throw (IllegalArgumentException)Util.pauseDevMode(new IllegalArgumentException());
      } else if (shape1 == shape2) {
         return function.apply(true, true) ? shape1 : empty();
      } else {
         boolean flag = function.apply(true, false);
         boolean flag1 = function.apply(false, true);
         if (shape1.isEmpty()) {
            return flag1 ? shape2 : empty();
         } else if (shape2.isEmpty()) {
            return flag ? shape1 : empty();
         } else {
            IDoubleListMerger idoublelistmerger = makeListMerger(1, shape1.getValues(Direction.Axis.X), shape2.getValues(Direction.Axis.X), flag, flag1);
            IDoubleListMerger idoublelistmerger1 = makeListMerger(idoublelistmerger.func_212435_a().size() - 1, shape1.getValues(Direction.Axis.Y), shape2.getValues(Direction.Axis.Y), flag, flag1);
            IDoubleListMerger idoublelistmerger2 = makeListMerger((idoublelistmerger.func_212435_a().size() - 1) * (idoublelistmerger1.func_212435_a().size() - 1), shape1.getValues(Direction.Axis.Z), shape2.getValues(Direction.Axis.Z), flag, flag1);
            BitSetVoxelShapePart bitsetvoxelshapepart = BitSetVoxelShapePart.func_197852_a(shape1.part, shape2.part, idoublelistmerger, idoublelistmerger1, idoublelistmerger2, function);
            return (VoxelShape)(idoublelistmerger instanceof DoubleCubeMergingList && idoublelistmerger1 instanceof DoubleCubeMergingList && idoublelistmerger2 instanceof DoubleCubeMergingList ? new VoxelShapeCube(bitsetvoxelshapepart) : new VoxelShapeArray(bitsetvoxelshapepart, idoublelistmerger.func_212435_a(), idoublelistmerger1.func_212435_a(), idoublelistmerger2.func_212435_a()));
         }
      }
   }

   public static boolean compare(VoxelShape shape1, VoxelShape shape2, IBooleanFunction function) {
      if (function.apply(false, false)) {
         throw (IllegalArgumentException)Util.pauseDevMode(new IllegalArgumentException());
      } else if (shape1 == shape2) {
         return function.apply(true, true);
      } else if (shape1.isEmpty()) {
         return function.apply(false, !shape2.isEmpty());
      } else if (shape2.isEmpty()) {
         return function.apply(!shape1.isEmpty(), false);
      } else {
         boolean flag = function.apply(true, false);
         boolean flag1 = function.apply(false, true);

         for(Direction.Axis direction$axis : AxisRotation.AXES) {
            if (shape1.getEnd(direction$axis) < shape2.getStart(direction$axis) - 1.0E-7D) {
               return flag || flag1;
            }

            if (shape2.getEnd(direction$axis) < shape1.getStart(direction$axis) - 1.0E-7D) {
               return flag || flag1;
            }
         }

         IDoubleListMerger idoublelistmerger = makeListMerger(1, shape1.getValues(Direction.Axis.X), shape2.getValues(Direction.Axis.X), flag, flag1);
         IDoubleListMerger idoublelistmerger1 = makeListMerger(idoublelistmerger.func_212435_a().size() - 1, shape1.getValues(Direction.Axis.Y), shape2.getValues(Direction.Axis.Y), flag, flag1);
         IDoubleListMerger idoublelistmerger2 = makeListMerger((idoublelistmerger.func_212435_a().size() - 1) * (idoublelistmerger1.func_212435_a().size() - 1), shape1.getValues(Direction.Axis.Z), shape2.getValues(Direction.Axis.Z), flag, flag1);
         return join(idoublelistmerger, idoublelistmerger1, idoublelistmerger2, shape1.part, shape2.part, function);
      }
   }

   private static boolean join(IDoubleListMerger p_197874_0_, IDoubleListMerger p_197874_1_, IDoubleListMerger p_197874_2_, VoxelShapePart p_197874_3_, VoxelShapePart p_197874_4_, IBooleanFunction p_197874_5_) {
      return !p_197874_0_.forMergedIndexes((p_199861_5_, p_199861_6_, p_199861_7_) -> {
         return p_197874_1_.forMergedIndexes((p_199860_6_, p_199860_7_, p_199860_8_) -> {
            return p_197874_2_.forMergedIndexes((p_199862_7_, p_199862_8_, p_199862_9_) -> {
               return !p_197874_5_.apply(p_197874_3_.contains(p_199861_5_, p_199860_6_, p_199862_7_), p_197874_4_.contains(p_199861_6_, p_199860_7_, p_199862_8_));
            });
         });
      });
   }

   public static double getAllowedOffset(Direction.Axis movementAxis, AxisAlignedBB collisionBox, Stream<VoxelShape> possibleHits, double desiredOffset) {
      for(Iterator<VoxelShape> iterator = possibleHits.iterator(); iterator.hasNext(); desiredOffset = iterator.next().getAllowedOffset(movementAxis, collisionBox, desiredOffset)) {
         if (Math.abs(desiredOffset) < 1.0E-7D) {
            return 0.0D;
         }
      }

      return desiredOffset;
   }

   public static double getAllowedOffset(Direction.Axis movementAxis, AxisAlignedBB collisionBox, IWorldReader worldReader, double desiredOffset, ISelectionContext selectionContext, Stream<VoxelShape> possibleHits) {
      return getAllowedOffset(collisionBox, worldReader, desiredOffset, selectionContext, AxisRotation.from(movementAxis, Direction.Axis.Z), possibleHits);
   }

   private static double getAllowedOffset(AxisAlignedBB collisionBox, IWorldReader worldReader, double desiredOffset, ISelectionContext selectionContext, AxisRotation rotationAxis, Stream<VoxelShape> possibleHits) {
      if (!(collisionBox.getXSize() < 1.0E-6D) && !(collisionBox.getYSize() < 1.0E-6D) && !(collisionBox.getZSize() < 1.0E-6D)) {
         if (Math.abs(desiredOffset) < 1.0E-7D) {
            return 0.0D;
         } else {
            AxisRotation axisrotation = rotationAxis.reverse();
            Direction.Axis direction$axis = axisrotation.rotate(Direction.Axis.X);
            Direction.Axis direction$axis1 = axisrotation.rotate(Direction.Axis.Y);
            Direction.Axis direction$axis2 = axisrotation.rotate(Direction.Axis.Z);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            int i = MathHelper.floor(collisionBox.getMin(direction$axis) - 1.0E-7D) - 1;
            int j = MathHelper.floor(collisionBox.getMax(direction$axis) + 1.0E-7D) + 1;
            int k = MathHelper.floor(collisionBox.getMin(direction$axis1) - 1.0E-7D) - 1;
            int l = MathHelper.floor(collisionBox.getMax(direction$axis1) + 1.0E-7D) + 1;
            double d0 = collisionBox.getMin(direction$axis2) - 1.0E-7D;
            double d1 = collisionBox.getMax(direction$axis2) + 1.0E-7D;
            boolean flag = desiredOffset > 0.0D;
            int i1 = flag ? MathHelper.floor(collisionBox.getMax(direction$axis2) - 1.0E-7D) - 1 : MathHelper.floor(collisionBox.getMin(direction$axis2) + 1.0E-7D) + 1;
            int j1 = getDifferenceFloored(desiredOffset, d0, d1);
            int k1 = flag ? 1 : -1;
            int l1 = i1;

            while(true) {
               if (flag) {
                  if (l1 > j1) {
                     break;
                  }
               } else if (l1 < j1) {
                  break;
               }

               for(int i2 = i; i2 <= j; ++i2) {
                  for(int j2 = k; j2 <= l; ++j2) {
                     int k2 = 0;
                     if (i2 == i || i2 == j) {
                        ++k2;
                     }

                     if (j2 == k || j2 == l) {
                        ++k2;
                     }

                     if (l1 == i1 || l1 == j1) {
                        ++k2;
                     }

                     if (k2 < 3) {
                        blockpos$mutable.func_218295_a(axisrotation, i2, j2, l1);
                        BlockState blockstate = worldReader.getBlockState(blockpos$mutable);
                        if ((k2 != 1 || blockstate.isCollisionShapeLargerThanFullBlock()) && (k2 != 2 || blockstate.getBlock() == Blocks.MOVING_PISTON)) {
                           desiredOffset = blockstate.getCollisionShape(worldReader, blockpos$mutable, selectionContext).getAllowedOffset(direction$axis2, collisionBox.offset((double)(-blockpos$mutable.getX()), (double)(-blockpos$mutable.getY()), (double)(-blockpos$mutable.getZ())), desiredOffset);
                           if (Math.abs(desiredOffset) < 1.0E-7D) {
                              return 0.0D;
                           }

                           j1 = getDifferenceFloored(desiredOffset, d0, d1);
                        }
                     }
                  }
               }

               l1 += k1;
            }

            double[] adouble = new double[]{desiredOffset};
            possibleHits.forEach((p_216388_3_) -> {
               adouble[0] = p_216388_3_.getAllowedOffset(direction$axis2, collisionBox, adouble[0]);
            });
            return adouble[0];
         }
      } else {
         return desiredOffset;
      }
   }

   private static int getDifferenceFloored(double desiredOffset, double min, double max) {
      return desiredOffset > 0.0D ? MathHelper.floor(max + desiredOffset) + 1 : MathHelper.floor(min + desiredOffset) - 1;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isCubeSideCovered(VoxelShape shape, VoxelShape adjacentShape, Direction side) {
      if (shape == fullCube() && adjacentShape == fullCube()) {
         return true;
      } else if (adjacentShape.isEmpty()) {
         return false;
      } else {
         Direction.Axis direction$axis = side.getAxis();
         Direction.AxisDirection direction$axisdirection = side.getAxisDirection();
         VoxelShape voxelshape = direction$axisdirection == Direction.AxisDirection.POSITIVE ? shape : adjacentShape;
         VoxelShape voxelshape1 = direction$axisdirection == Direction.AxisDirection.POSITIVE ? adjacentShape : shape;
         IBooleanFunction ibooleanfunction = direction$axisdirection == Direction.AxisDirection.POSITIVE ? IBooleanFunction.ONLY_FIRST : IBooleanFunction.ONLY_SECOND;
         return DoubleMath.fuzzyEquals(voxelshape.getEnd(direction$axis), 1.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(voxelshape1.getStart(direction$axis), 0.0D, 1.0E-7D) && !compare(new SplitVoxelShape(voxelshape, direction$axis, voxelshape.part.getSize(direction$axis) - 1), new SplitVoxelShape(voxelshape1, direction$axis, 0), ibooleanfunction);
      }
   }

   public static VoxelShape getFaceShape(VoxelShape voxelShapeIn, Direction directionIn) {
      if (voxelShapeIn == fullCube()) {
         return fullCube();
      } else {
         Direction.Axis direction$axis = directionIn.getAxis();
         boolean flag;
         int i;
         if (directionIn.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            flag = DoubleMath.fuzzyEquals(voxelShapeIn.getEnd(direction$axis), 1.0D, 1.0E-7D);
            i = voxelShapeIn.part.getSize(direction$axis) - 1;
         } else {
            flag = DoubleMath.fuzzyEquals(voxelShapeIn.getStart(direction$axis), 0.0D, 1.0E-7D);
            i = 0;
         }

         return (VoxelShape)(!flag ? empty() : new SplitVoxelShape(voxelShapeIn, direction$axis, i));
      }
   }

   public static boolean doAdjacentCubeSidesFillSquare(VoxelShape shape, VoxelShape adjacentShape, Direction side) {
      if (shape != fullCube() && adjacentShape != fullCube()) {
         Direction.Axis direction$axis = side.getAxis();
         Direction.AxisDirection direction$axisdirection = side.getAxisDirection();
         VoxelShape voxelshape = direction$axisdirection == Direction.AxisDirection.POSITIVE ? shape : adjacentShape;
         VoxelShape voxelshape1 = direction$axisdirection == Direction.AxisDirection.POSITIVE ? adjacentShape : shape;
         if (!DoubleMath.fuzzyEquals(voxelshape.getEnd(direction$axis), 1.0D, 1.0E-7D)) {
            voxelshape = empty();
         }

         if (!DoubleMath.fuzzyEquals(voxelshape1.getStart(direction$axis), 0.0D, 1.0E-7D)) {
            voxelshape1 = empty();
         }

         return !compare(fullCube(), combine(new SplitVoxelShape(voxelshape, direction$axis, voxelshape.part.getSize(direction$axis) - 1), new SplitVoxelShape(voxelshape1, direction$axis, 0), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
      } else {
         return true;
      }
   }

   public static boolean faceShapeCovers(VoxelShape voxelShape1, VoxelShape voxelShape2) {
      if (voxelShape1 != fullCube() && voxelShape2 != fullCube()) {
         if (voxelShape1.isEmpty() && voxelShape2.isEmpty()) {
            return false;
         } else {
            return !compare(fullCube(), combine(voxelShape1, voxelShape2, IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
         }
      } else {
         return true;
      }
   }

   @VisibleForTesting
   protected static IDoubleListMerger makeListMerger(int p_199410_0_, DoubleList list1, DoubleList list2, boolean p_199410_3_, boolean p_199410_4_) {
      int i = list1.size() - 1;
      int j = list2.size() - 1;
      if (list1 instanceof DoubleRangeList && list2 instanceof DoubleRangeList) {
         long k = lcm(i, j);
         if ((long)p_199410_0_ * k <= 256L) {
            return new DoubleCubeMergingList(i, j);
         }
      }

      if (list1.getDouble(i) < list2.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(list1, list2, false);
      } else if (list2.getDouble(j) < list1.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(list2, list1, true);
      } else if (i == j && Objects.equals(list1, list2)) {
         if (list1 instanceof SimpleDoubleMerger) {
            return (IDoubleListMerger)list1;
         } else {
            return (IDoubleListMerger)(list2 instanceof SimpleDoubleMerger ? (IDoubleListMerger)list2 : new SimpleDoubleMerger(list1));
         }
      } else {
         return new IndirectMerger(list1, list2, p_199410_3_, p_199410_4_);
      }
   }

   public interface ILineConsumer {
      void consume(double p_consume_1_, double p_consume_3_, double p_consume_5_, double p_consume_7_, double p_consume_9_, double p_consume_11_);
   }
}