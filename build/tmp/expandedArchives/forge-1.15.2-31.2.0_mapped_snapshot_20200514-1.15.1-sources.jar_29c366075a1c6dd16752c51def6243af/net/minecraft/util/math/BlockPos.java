package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Spliterator.OfInt;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.Rotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i implements IDynamicSerializable {
   private static final Logger LOGGER = LogManager.getLogger();
   /** An immutable block pos with zero as all coordinates. */
   public static final BlockPos ZERO = new BlockPos(0, 0, 0);
   private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
   private static final int NUM_Z_BITS = NUM_X_BITS;
   private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
   private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
   private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
   private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;
   private static final int field_218292_j = NUM_Y_BITS;
   private static final int field_218293_k = NUM_Y_BITS + NUM_Z_BITS;

   public BlockPos(int x, int y, int z) {
      super(x, y, z);
   }

   public BlockPos(double x, double y, double z) {
      super(x, y, z);
   }

   public BlockPos(Entity source) {
      this(source.getPosX(), source.getPosY(), source.getPosZ());
   }

   public BlockPos(Vec3d vec) {
      this(vec.x, vec.y, vec.z);
   }

   public BlockPos(IPosition p_i50799_1_) {
      this(p_i50799_1_.getX(), p_i50799_1_.getY(), p_i50799_1_.getZ());
   }

   public BlockPos(Vec3i source) {
      this(source.getX(), source.getY(), source.getZ());
   }

   public static <T> BlockPos deserialize(Dynamic<T> p_218286_0_) {
      OfInt ofint = p_218286_0_.asIntStream().spliterator();
      int[] aint = new int[3];
      if (ofint.tryAdvance((Integer p_218285_1_) -> {
         aint[0] = p_218285_1_;
      }) && ofint.tryAdvance((Integer p_218280_1_) -> {
         aint[1] = p_218280_1_;
      })) {
         ofint.tryAdvance((Integer p_218284_1_) -> {
            aint[2] = p_218284_1_;
         });
      }

      return new BlockPos(aint[0], aint[1], aint[2]);
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createIntList(IntStream.of(this.getX(), this.getY(), this.getZ()));
   }

   public static long offset(long pos, Direction p_218289_2_) {
      return offset(pos, p_218289_2_.getXOffset(), p_218289_2_.getYOffset(), p_218289_2_.getZOffset());
   }

   public static long offset(long pos, int dx, int dy, int dz) {
      return pack(unpackX(pos) + dx, unpackY(pos) + dy, unpackZ(pos) + dz);
   }

   public static int unpackX(long p_218290_0_) {
      return (int)(p_218290_0_ << 64 - field_218293_k - NUM_X_BITS >> 64 - NUM_X_BITS);
   }

   public static int unpackY(long p_218274_0_) {
      return (int)(p_218274_0_ << 64 - NUM_Y_BITS >> 64 - NUM_Y_BITS);
   }

   public static int unpackZ(long p_218282_0_) {
      return (int)(p_218282_0_ << 64 - field_218292_j - NUM_Z_BITS >> 64 - NUM_Z_BITS);
   }

   public static BlockPos fromLong(long p_218283_0_) {
      return new BlockPos(unpackX(p_218283_0_), unpackY(p_218283_0_), unpackZ(p_218283_0_));
   }

   public static long pack(int p_218276_0_, int p_218276_1_, int p_218276_2_) {
      long i = 0L;
      i = i | ((long)p_218276_0_ & X_MASK) << field_218293_k;
      i = i | ((long)p_218276_1_ & Y_MASK) << 0;
      i = i | ((long)p_218276_2_ & Z_MASK) << field_218292_j;
      return i;
   }

   public static long func_218288_f(long p_218288_0_) {
      return p_218288_0_ & -16L;
   }

   public long toLong() {
      return pack(this.getX(), this.getY(), this.getZ());
   }

   /**
    * Add the given coordinates to the coordinates of this BlockPos
    */
   public BlockPos add(double x, double y, double z) {
      return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
   }

   /**
    * Add the given coordinates to the coordinates of this BlockPos
    */
   public BlockPos add(int x, int y, int z) {
      return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
   }

   /**
    * Add the given Vector to this BlockPos
    */
   public BlockPos add(Vec3i vec) {
      return this.add(vec.getX(), vec.getY(), vec.getZ());
   }

   /**
    * Subtract the given Vector from this BlockPos
    */
   public BlockPos subtract(Vec3i vec) {
      return this.add(-vec.getX(), -vec.getY(), -vec.getZ());
   }

   /**
    * Offset this BlockPos 1 block up
    */
   public BlockPos up() {
      return this.offset(Direction.UP);
   }

   /**
    * Offset this BlockPos n blocks up
    */
   public BlockPos up(int n) {
      return this.offset(Direction.UP, n);
   }

   /**
    * Offset this BlockPos 1 block down
    */
   public BlockPos down() {
      return this.offset(Direction.DOWN);
   }

   /**
    * Offset this BlockPos n blocks down
    */
   public BlockPos down(int n) {
      return this.offset(Direction.DOWN, n);
   }

   /**
    * Offset this BlockPos 1 block in northern direction
    */
   public BlockPos north() {
      return this.offset(Direction.NORTH);
   }

   /**
    * Offset this BlockPos n blocks in northern direction
    */
   public BlockPos north(int n) {
      return this.offset(Direction.NORTH, n);
   }

   /**
    * Offset this BlockPos 1 block in southern direction
    */
   public BlockPos south() {
      return this.offset(Direction.SOUTH);
   }

   /**
    * Offset this BlockPos n blocks in southern direction
    */
   public BlockPos south(int n) {
      return this.offset(Direction.SOUTH, n);
   }

   /**
    * Offset this BlockPos 1 block in western direction
    */
   public BlockPos west() {
      return this.offset(Direction.WEST);
   }

   /**
    * Offset this BlockPos n blocks in western direction
    */
   public BlockPos west(int n) {
      return this.offset(Direction.WEST, n);
   }

   /**
    * Offset this BlockPos 1 block in eastern direction
    */
   public BlockPos east() {
      return this.offset(Direction.EAST);
   }

   /**
    * Offset this BlockPos n blocks in eastern direction
    */
   public BlockPos east(int n) {
      return this.offset(Direction.EAST, n);
   }

   /**
    * Offset this BlockPos 1 block in the given direction
    */
   public BlockPos offset(Direction facing) {
      return new BlockPos(this.getX() + facing.getXOffset(), this.getY() + facing.getYOffset(), this.getZ() + facing.getZOffset());
   }

   /**
    * Offsets this BlockPos n blocks in the given direction
    */
   public BlockPos offset(Direction facing, int n) {
      return n == 0 ? this : new BlockPos(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n);
   }

   public BlockPos rotate(Rotation rotationIn) {
      switch(rotationIn) {
      case NONE:
      default:
         return this;
      case CLOCKWISE_90:
         return new BlockPos(-this.getZ(), this.getY(), this.getX());
      case CLOCKWISE_180:
         return new BlockPos(-this.getX(), this.getY(), -this.getZ());
      case COUNTERCLOCKWISE_90:
         return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }

   /**
    * Calculate the cross product of this and the given Vector
    */
   public BlockPos crossProduct(Vec3i vec) {
      return new BlockPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
   }

   /**
    * Returns a version of this BlockPos that is guaranteed to be immutable.
    *  
    * <p>When storing a BlockPos given to you for an extended period of time, make sure you
    * use this in case the value is changed internally.</p>
    */
   public BlockPos toImmutable() {
      return this;
   }

   public static Iterable<BlockPos> getAllInBoxMutable(BlockPos firstPos, BlockPos secondPos) {
      return getAllInBoxMutable(Math.min(firstPos.getX(), secondPos.getX()), Math.min(firstPos.getY(), secondPos.getY()), Math.min(firstPos.getZ(), secondPos.getZ()), Math.max(firstPos.getX(), secondPos.getX()), Math.max(firstPos.getY(), secondPos.getY()), Math.max(firstPos.getZ(), secondPos.getZ()));
   }

   public static Stream<BlockPos> getAllInBox(BlockPos firstPos, BlockPos secondPos) {
      return getAllInBox(Math.min(firstPos.getX(), secondPos.getX()), Math.min(firstPos.getY(), secondPos.getY()), Math.min(firstPos.getZ(), secondPos.getZ()), Math.max(firstPos.getX(), secondPos.getX()), Math.max(firstPos.getY(), secondPos.getY()), Math.max(firstPos.getZ(), secondPos.getZ()));
   }

   public static Stream<BlockPos> getAllInBox(MutableBoundingBox p_229383_0_) {
      return getAllInBox(Math.min(p_229383_0_.minX, p_229383_0_.maxX), Math.min(p_229383_0_.minY, p_229383_0_.maxY), Math.min(p_229383_0_.minZ, p_229383_0_.maxZ), Math.max(p_229383_0_.minX, p_229383_0_.maxX), Math.max(p_229383_0_.minY, p_229383_0_.maxY), Math.max(p_229383_0_.minZ, p_229383_0_.maxZ));
   }

   public static Stream<BlockPos> getAllInBox(final int p_218287_0_, final int p_218287_1_, final int p_218287_2_, final int p_218287_3_, final int p_218287_4_, final int p_218287_5_) {
      return StreamSupport.stream(new AbstractSpliterator<BlockPos>((long)((p_218287_3_ - p_218287_0_ + 1) * (p_218287_4_ - p_218287_1_ + 1) * (p_218287_5_ - p_218287_2_ + 1)), 64) {
         final CubeCoordinateIterator iter = new CubeCoordinateIterator(p_218287_0_, p_218287_1_, p_218287_2_, p_218287_3_, p_218287_4_, p_218287_5_);
         final BlockPos.Mutable pos = new BlockPos.Mutable();

         public boolean tryAdvance(Consumer<? super BlockPos> p_tryAdvance_1_) {
            if (this.iter.hasNext()) {
               p_tryAdvance_1_.accept(this.pos.setPos(this.iter.getX(), this.iter.getY(), this.iter.getZ()));
               return true;
            } else {
               return false;
            }
         }
      }, false);
   }

   /**
    * Creates an Iterable that returns all positions in the box specified by the given corners. <strong>Coordinates must
    * be in order</strong>; e.g. x1 <= x2.
    *  
    * This method uses {@link BlockPos.MutableBlockPos MutableBlockPos} instead of regular BlockPos, which grants better
    * performance. However, the resulting BlockPos instances can only be used inside the iteration loop (as otherwise
    * the value will change), unless {@link #toImmutable()} is called. This method is ideal for searching large areas
    * and only storing a few locations.
    *  
    * @see #getAllInBox(BlockPos, BlockPos)
    * @see #getAllInBox(int, int, int, int, int, int)
    * @see #getAllInBoxMutable(BlockPos, BlockPos)
    */
   public static Iterable<BlockPos> getAllInBoxMutable(int x1, int y1, int z1, int x2, int y2, int z2) {
      return () -> {
         return new AbstractIterator<BlockPos>() {
            final CubeCoordinateIterator field_218298_a = new CubeCoordinateIterator(x1, y1, z1, x2, y2, z2);
            final BlockPos.Mutable field_218299_b = new BlockPos.Mutable();

            protected BlockPos computeNext() {
               return (BlockPos)(this.field_218298_a.hasNext() ? this.field_218299_b.setPos(this.field_218298_a.getX(), this.field_218298_a.getY(), this.field_218298_a.getZ()) : this.endOfData());
            }
         };
      };
   }

   public static class Mutable extends BlockPos {
      /** Mutable X Coordinate */
      protected int x;
      /** Mutable Y Coordinate */
      protected int y;
      /** Mutable Z Coordinate */
      protected int z;

      public Mutable() {
         this(0, 0, 0);
      }

      public Mutable(BlockPos pos) {
         this(pos.getX(), pos.getY(), pos.getZ());
      }

      public Mutable(int x_, int y_, int z_) {
         super(0, 0, 0);
         this.x = x_;
         this.y = y_;
         this.z = z_;
      }

      public Mutable(double p_i50824_1_, double p_i50824_3_, double p_i50824_5_) {
         this(MathHelper.floor(p_i50824_1_), MathHelper.floor(p_i50824_3_), MathHelper.floor(p_i50824_5_));
      }

      public Mutable(Entity p_i226062_1_) {
         this(p_i226062_1_.getPosX(), p_i226062_1_.getPosY(), p_i226062_1_.getPosZ());
      }

      /**
       * Add the given coordinates to the coordinates of this BlockPos
       */
      public BlockPos add(double x, double y, double z) {
         return super.add(x, y, z).toImmutable();
      }

      /**
       * Add the given coordinates to the coordinates of this BlockPos
       */
      public BlockPos add(int x, int y, int z) {
         return super.add(x, y, z).toImmutable();
      }

      /**
       * Offsets this BlockPos n blocks in the given direction
       */
      public BlockPos offset(Direction facing, int n) {
         return super.offset(facing, n).toImmutable();
      }

      public BlockPos rotate(Rotation rotationIn) {
         return super.rotate(rotationIn).toImmutable();
      }

      /**
       * Gets the X coordinate.
       */
      public int getX() {
         return this.x;
      }

      /**
       * Gets the Y coordinate.
       */
      public int getY() {
         return this.y;
      }

      /**
       * Gets the Z coordinate.
       */
      public int getZ() {
         return this.z;
      }

      /**
       * Sets position
       */
      public BlockPos.Mutable setPos(int xIn, int yIn, int zIn) {
         this.x = xIn;
         this.y = yIn;
         this.z = zIn;
         return this;
      }

      public BlockPos.Mutable setPos(Entity entityIn) {
         return this.setPos(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
      }

      public BlockPos.Mutable setPos(double xIn, double yIn, double zIn) {
         return this.setPos(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
      }

      public BlockPos.Mutable setPos(Vec3i vec) {
         return this.setPos(vec.getX(), vec.getY(), vec.getZ());
      }

      public BlockPos.Mutable setPos(long p_218294_1_) {
         return this.setPos(unpackX(p_218294_1_), unpackY(p_218294_1_), unpackZ(p_218294_1_));
      }

      public BlockPos.Mutable func_218295_a(AxisRotation p_218295_1_, int p_218295_2_, int p_218295_3_, int p_218295_4_) {
         return this.setPos(p_218295_1_.getCoordinate(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.X), p_218295_1_.getCoordinate(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.Y), p_218295_1_.getCoordinate(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.Z));
      }

      public BlockPos.Mutable move(Direction facing) {
         return this.move(facing, 1);
      }

      public BlockPos.Mutable move(Direction facing, int n) {
         return this.setPos(this.x + facing.getXOffset() * n, this.y + facing.getYOffset() * n, this.z + facing.getZOffset() * n);
      }

      public BlockPos.Mutable move(int xIn, int yIn, int zIn) {
         return this.setPos(this.x + xIn, this.y + yIn, this.z + zIn);
      }

      /**
       * Sets the X coordinate.
       */
      public void setX(int xIn) {
         this.x = xIn;
      }

      public void setY(int yIn) {
         this.y = yIn;
      }

      /**
       * Sets the Z coordinate.
       */
      public void setZ(int zIn) {
         this.z = zIn;
      }

      /**
       * Returns a version of this BlockPos that is guaranteed to be immutable.
       *  
       * <p>When storing a BlockPos given to you for an extended period of time, make sure you
       * use this in case the value is changed internally.</p>
       */
      public BlockPos toImmutable() {
         return new BlockPos(this);
      }
   }

   public static final class PooledMutable extends BlockPos.Mutable implements AutoCloseable {
      private boolean released;
      private static final List<BlockPos.PooledMutable> POOL = Lists.newArrayList();

      private PooledMutable(int xIn, int yIn, int zIn) {
         super(xIn, yIn, zIn);
      }

      public static BlockPos.PooledMutable retain() {
         return retain(0, 0, 0);
      }

      public static BlockPos.PooledMutable retain(Entity entityIn) {
         return retain(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
      }

      public static BlockPos.PooledMutable retain(double xIn, double yIn, double zIn) {
         return retain(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
      }

      public static BlockPos.PooledMutable retain(int xIn, int yIn, int zIn) {
         synchronized(POOL) {
            if (!POOL.isEmpty()) {
               BlockPos.PooledMutable blockpos$pooledmutable = POOL.remove(POOL.size() - 1);
               if (blockpos$pooledmutable != null && blockpos$pooledmutable.released) {
                  blockpos$pooledmutable.released = false;
                  blockpos$pooledmutable.setPos(xIn, yIn, zIn);
                  return blockpos$pooledmutable;
               }
            }
         }

         return new BlockPos.PooledMutable(xIn, yIn, zIn);
      }

      /**
       * Sets position
       */
      public BlockPos.PooledMutable setPos(int xIn, int yIn, int zIn) {
         return (BlockPos.PooledMutable)super.setPos(xIn, yIn, zIn);
      }

      public BlockPos.PooledMutable setPos(Entity entityIn) {
         return (BlockPos.PooledMutable)super.setPos(entityIn);
      }

      public BlockPos.PooledMutable setPos(double xIn, double yIn, double zIn) {
         return (BlockPos.PooledMutable)super.setPos(xIn, yIn, zIn);
      }

      public BlockPos.PooledMutable setPos(Vec3i vec) {
         return (BlockPos.PooledMutable)super.setPos(vec);
      }

      public BlockPos.PooledMutable move(Direction facing) {
         return (BlockPos.PooledMutable)super.move(facing);
      }

      public BlockPos.PooledMutable move(Direction facing, int n) {
         return (BlockPos.PooledMutable)super.move(facing, n);
      }

      public BlockPos.PooledMutable move(int xIn, int yIn, int zIn) {
         return (BlockPos.PooledMutable)super.move(xIn, yIn, zIn);
      }

      public void close() {
         synchronized(POOL) {
            if (POOL.size() < 100) {
               POOL.add(this);
            }

            this.released = true;
         }
      }
   }
}