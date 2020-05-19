package net.minecraft.util.math;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AxisAlignedBB {
   public final double minX;
   public final double minY;
   public final double minZ;
   public final double maxX;
   public final double maxY;
   public final double maxZ;

   public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
      this.minX = Math.min(x1, x2);
      this.minY = Math.min(y1, y2);
      this.minZ = Math.min(z1, z2);
      this.maxX = Math.max(x1, x2);
      this.maxY = Math.max(y1, y2);
      this.maxZ = Math.max(z1, z2);
   }

   public AxisAlignedBB(BlockPos pos) {
      this((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1));
   }

   public AxisAlignedBB(BlockPos pos1, BlockPos pos2) {
      this((double)pos1.getX(), (double)pos1.getY(), (double)pos1.getZ(), (double)pos2.getX(), (double)pos2.getY(), (double)pos2.getZ());
   }

   public AxisAlignedBB(Vec3d min, Vec3d max) {
      this(min.x, min.y, min.z, max.x, max.y, max.z);
   }

   public static AxisAlignedBB toImmutable(MutableBoundingBox p_216363_0_) {
      return new AxisAlignedBB((double)p_216363_0_.minX, (double)p_216363_0_.minY, (double)p_216363_0_.minZ, (double)(p_216363_0_.maxX + 1), (double)(p_216363_0_.maxY + 1), (double)(p_216363_0_.maxZ + 1));
   }

   public double getMin(Direction.Axis axis) {
      return axis.getCoordinate(this.minX, this.minY, this.minZ);
   }

   public double getMax(Direction.Axis axis) {
      return axis.getCoordinate(this.maxX, this.maxY, this.maxZ);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof AxisAlignedBB)) {
         return false;
      } else {
         AxisAlignedBB axisalignedbb = (AxisAlignedBB)p_equals_1_;
         if (Double.compare(axisalignedbb.minX, this.minX) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.minY, this.minY) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.minZ, this.minZ) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.maxX, this.maxX) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.maxY, this.maxY) != 0) {
            return false;
         } else {
            return Double.compare(axisalignedbb.maxZ, this.maxZ) == 0;
         }
      }
   }

   public int hashCode() {
      long i = Double.doubleToLongBits(this.minX);
      int j = (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.minY);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.minZ);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxX);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxY);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxZ);
      j = 31 * j + (int)(i ^ i >>> 32);
      return j;
   }

   /**
    * Creates a new {@link AxisAlignedBB} that has been contracted by the given amount, with positive changes decreasing
    * max values and negative changes increasing min values.
    * <br/>
    * If the amount to contract by is larger than the length of a side, then the side will wrap (still creating a valid
    * AABB - see last sample).
    *  
    * <h3>Samples:</h3>
    * <table>
    * <tr><th>Input</th><th>Result</th></tr>
    * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 4, 4, 4).contract(2, 2, 2)</code></pre></td><td><pre><samp>box[0.0,
    * 0.0, 0.0 -> 2.0, 2.0, 2.0]</samp></pre></td></tr>
    * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 4, 4, 4).contract(-2, -2, -
    * 2)</code></pre></td><td><pre><samp>box[2.0, 2.0, 2.0 -> 4.0, 4.0, 4.0]</samp></pre></td></tr>
    * <tr><td><pre><code>new AxisAlignedBB(5, 5, 5, 7, 7, 7).contract(0, 1, -1)</code></pre></td><td><pre><samp>box[5.0,
    * 5.0, 6.0 -> 7.0, 6.0, 7.0]</samp></pre></td></tr>
    * <tr><td><pre><code>new AxisAlignedBB(-2, -2, -2, 2, 2, 2).contract(4, -4, 0)</code></pre></td><td><pre><samp>box[-
    * 8.0, 2.0, -2.0 -> -2.0, 8.0, 2.0]</samp></pre></td></tr>
    * </table>
    *  
    * <h3>See Also:</h3>
    * <ul>
    * <li>{@link #expand(double, double, double)} - like this, except for expanding.</li>
    * <li>{@link #grow(double, double, double)} and {@link #grow(double)} - expands in all directions.</li>
    * <li>{@link #shrink(double)} - contracts in all directions (like {@link #grow(double)})</li>
    * </ul>
    *  
    * @return A new modified bounding box.
    */
   public AxisAlignedBB contract(double x, double y, double z) {
      double d0 = this.minX;
      double d1 = this.minY;
      double d2 = this.minZ;
      double d3 = this.maxX;
      double d4 = this.maxY;
      double d5 = this.maxZ;
      if (x < 0.0D) {
         d0 -= x;
      } else if (x > 0.0D) {
         d3 -= x;
      }

      if (y < 0.0D) {
         d1 -= y;
      } else if (y > 0.0D) {
         d4 -= y;
      }

      if (z < 0.0D) {
         d2 -= z;
      } else if (z > 0.0D) {
         d5 -= z;
      }

      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public AxisAlignedBB expand(Vec3d p_216361_1_) {
      return this.expand(p_216361_1_.x, p_216361_1_.y, p_216361_1_.z);
   }

   /**
    * Creates a new {@link AxisAlignedBB} that has been expanded by the given amount, with positive changes increasing
    * max values and negative changes decreasing min values.
    * 
    * <h3>Samples:</h3>
    * <table>
    * <tr><th>Input</th><th>Result</th></tr>
    * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 1, 1, 1).expand(2, 2, 2)</code></pre></td><td><pre><samp>box[0, 0, 0
    * -> 3, 3, 3]</samp></pre></td><td>
    * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 1, 1, 1).expand(-2, -2, -2)</code></pre></td><td><pre><samp>box[-2,
    * -2, -2 -> 1, 1, 1]</samp></pre></td><td>
    * <tr><td><pre><code>new AxisAlignedBB(5, 5, 5, 7, 7, 7).expand(0, 1, -1)</code></pre></td><td><pre><samp>box[5, 5,
    * 4, 7, 8, 7]</samp></pre></td><td>
    * </table>
    * 
    * <h3>See Also:</h3>
    * <ul>
    * <li>{@link #contract(double, double, double)} - like this, except for shrinking.</li>
    * <li>{@link #grow(double, double, double)} and {@link #grow(double)} - expands in all directions.</li>
    * <li>{@link #shrink(double)} - contracts in all directions (like {@link #grow(double)})</li>
    * </ul>
    * 
    * @return A modified bounding box that will always be equal or greater in volume to this bounding box.
    */
   public AxisAlignedBB expand(double x, double y, double z) {
      double d0 = this.minX;
      double d1 = this.minY;
      double d2 = this.minZ;
      double d3 = this.maxX;
      double d4 = this.maxY;
      double d5 = this.maxZ;
      if (x < 0.0D) {
         d0 += x;
      } else if (x > 0.0D) {
         d3 += x;
      }

      if (y < 0.0D) {
         d1 += y;
      } else if (y > 0.0D) {
         d4 += y;
      }

      if (z < 0.0D) {
         d2 += z;
      } else if (z > 0.0D) {
         d5 += z;
      }

      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   /**
    * Creates a new {@link AxisAlignedBB} that has been contracted by the given amount in both directions. Negative
    * values will shrink the AABB instead of expanding it.
    * <br/>
    * Side lengths will be increased by 2 times the value of the parameters, since both min and max are changed.
    * <br/>
    * If contracting and the amount to contract by is larger than the length of a side, then the side will wrap (still
    * creating a valid AABB - see last ample).
    *  
    * <h3>Samples:</h3>
    * <table>
    * <tr><th>Input</th><th>Result</th></tr>
    * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(2, 2, 2)</code></pre></td><td><pre><samp>box[-2.0, -
    * 2.0, -2.0 -> 3.0, 3.0, 3.0]</samp></pre></td></tr>
    * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 6, 6, 6).grow(-2, -2, -2)</code></pre></td><td><pre><samp>box[2.0,
    * 2.0, 2.0 -> 4.0, 4.0, 4.0]</samp></pre></td></tr>
    * <tr><td><pre><code>new AxisAlignedBB(5, 5, 5, 7, 7, 7).grow(0, 1, -1)</code></pre></td><td><pre><samp>box[5.0,
    * 4.0, 6.0 -> 7.0, 8.0, 6.0]</samp></pre></td></tr>
    * <tr><td><pre><code>new AxisAlignedBB(1, 1, 1, 3, 3, 3).grow(-4, -2, -3)</code></pre></td><td><pre><samp>box[-1.0,
    * 1.0, 0.0 -> 5.0, 3.0, 4.0]</samp></pre></td></tr>
    * </table>
    *  
    * <h3>See Also:</h3>
    * <ul>
    * <li>{@link #expand(double, double, double)} - expands in only one direction.</li>
    * <li>{@link #contract(double, double, double)} - contracts in only one direction.</li>
    * <lu>{@link #grow(double)} - version of this that expands in all directions from one parameter.</li>
    * <li>{@link #shrink(double)} - contracts in all directions</li>
    * </ul>
    *  
    * @return A modified bounding box.
    */
   public AxisAlignedBB grow(double x, double y, double z) {
      double d0 = this.minX - x;
      double d1 = this.minY - y;
      double d2 = this.minZ - z;
      double d3 = this.maxX + x;
      double d4 = this.maxY + y;
      double d5 = this.maxZ + z;
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   /**
    * Creates a new {@link AxisAlignedBB} that is expanded by the given value in all directions. Equivalent to {@link
    * #grow(double, double, double)} with the given value for all 3 params. Negative values will shrink the AABB.
    * <br/>
    * Side lengths will be increased by 2 times the value of the parameter, since both min and max are changed.
    * <br/>
    * If contracting and the amount to contract by is larger than the length of a side, then the side will wrap (still
    * creating a valid AABB - see samples on {@link #grow(double, double, double)}).
    *  
    * @return A modified AABB.
    */
   public AxisAlignedBB grow(double value) {
      return this.grow(value, value, value);
   }

   public AxisAlignedBB intersect(AxisAlignedBB other) {
      double d0 = Math.max(this.minX, other.minX);
      double d1 = Math.max(this.minY, other.minY);
      double d2 = Math.max(this.minZ, other.minZ);
      double d3 = Math.min(this.maxX, other.maxX);
      double d4 = Math.min(this.maxY, other.maxY);
      double d5 = Math.min(this.maxZ, other.maxZ);
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public AxisAlignedBB union(AxisAlignedBB other) {
      double d0 = Math.min(this.minX, other.minX);
      double d1 = Math.min(this.minY, other.minY);
      double d2 = Math.min(this.minZ, other.minZ);
      double d3 = Math.max(this.maxX, other.maxX);
      double d4 = Math.max(this.maxY, other.maxY);
      double d5 = Math.max(this.maxZ, other.maxZ);
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   /**
    * Offsets the current bounding box by the specified amount.
    */
   public AxisAlignedBB offset(double x, double y, double z) {
      return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
   }

   public AxisAlignedBB offset(BlockPos pos) {
      return new AxisAlignedBB(this.minX + (double)pos.getX(), this.minY + (double)pos.getY(), this.minZ + (double)pos.getZ(), this.maxX + (double)pos.getX(), this.maxY + (double)pos.getY(), this.maxZ + (double)pos.getZ());
   }

   public AxisAlignedBB offset(Vec3d vec) {
      return this.offset(vec.x, vec.y, vec.z);
   }

   /**
    * Checks if the bounding box intersects with another.
    */
   public boolean intersects(AxisAlignedBB other) {
      return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
   }

   public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2) {
      return this.minX < x2 && this.maxX > x1 && this.minY < y2 && this.maxY > y1 && this.minZ < z2 && this.maxZ > z1;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean intersects(Vec3d min, Vec3d max) {
      return this.intersects(Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z), Math.max(min.x, max.x), Math.max(min.y, max.y), Math.max(min.z, max.z));
   }

   /**
    * Returns if the supplied Vec3D is completely inside the bounding box
    */
   public boolean contains(Vec3d vec) {
      return this.contains(vec.x, vec.y, vec.z);
   }

   public boolean contains(double x, double y, double z) {
      return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
   }

   /**
    * Returns the average length of the edges of the bounding box.
    */
   public double getAverageEdgeLength() {
      double d0 = this.getXSize();
      double d1 = this.getYSize();
      double d2 = this.getZSize();
      return (d0 + d1 + d2) / 3.0D;
   }

   public double getXSize() {
      return this.maxX - this.minX;
   }

   public double getYSize() {
      return this.maxY - this.minY;
   }

   public double getZSize() {
      return this.maxZ - this.minZ;
   }

   /**
    * Creates a new {@link AxisAlignedBB} that is expanded by the given value in all directions. Equivalent to {@link
    * #grow(double)} with value set to the negative of the value provided here. Passing a negative value to this method
    * values will grow the AABB.
    * <br/>
    * Side lengths will be decreased by 2 times the value of the parameter, since both min and max are changed.
    * <br/>
    * If contracting and the amount to contract by is larger than the length of a side, then the side will wrap (still
    * creating a valid AABB - see samples on {@link #grow(double, double, double)}).
    *  
    * @return A modified AABB.
    */
   public AxisAlignedBB shrink(double value) {
      return this.grow(-value);
   }

   public Optional<Vec3d> rayTrace(Vec3d p_216365_1_, Vec3d p_216365_2_) {
      double[] adouble = new double[]{1.0D};
      double d0 = p_216365_2_.x - p_216365_1_.x;
      double d1 = p_216365_2_.y - p_216365_1_.y;
      double d2 = p_216365_2_.z - p_216365_1_.z;
      Direction direction = func_197741_a(this, p_216365_1_, adouble, (Direction)null, d0, d1, d2);
      if (direction == null) {
         return Optional.empty();
      } else {
         double d3 = adouble[0];
         return Optional.of(p_216365_1_.add(d3 * d0, d3 * d1, d3 * d2));
      }
   }

   @Nullable
   public static BlockRayTraceResult rayTrace(Iterable<AxisAlignedBB> boxes, Vec3d start, Vec3d end, BlockPos pos) {
      double[] adouble = new double[]{1.0D};
      Direction direction = null;
      double d0 = end.x - start.x;
      double d1 = end.y - start.y;
      double d2 = end.z - start.z;

      for(AxisAlignedBB axisalignedbb : boxes) {
         direction = func_197741_a(axisalignedbb.offset(pos), start, adouble, direction, d0, d1, d2);
      }

      if (direction == null) {
         return null;
      } else {
         double d3 = adouble[0];
         return new BlockRayTraceResult(start.add(d3 * d0, d3 * d1, d3 * d2), direction, pos, false);
      }
   }

   @Nullable
   private static Direction func_197741_a(AxisAlignedBB aabb, Vec3d p_197741_1_, double[] p_197741_2_, @Nullable Direction facing, double p_197741_4_, double p_197741_6_, double p_197741_8_) {
      if (p_197741_4_ > 1.0E-7D) {
         facing = func_197740_a(p_197741_2_, facing, p_197741_4_, p_197741_6_, p_197741_8_, aabb.minX, aabb.minY, aabb.maxY, aabb.minZ, aabb.maxZ, Direction.WEST, p_197741_1_.x, p_197741_1_.y, p_197741_1_.z);
      } else if (p_197741_4_ < -1.0E-7D) {
         facing = func_197740_a(p_197741_2_, facing, p_197741_4_, p_197741_6_, p_197741_8_, aabb.maxX, aabb.minY, aabb.maxY, aabb.minZ, aabb.maxZ, Direction.EAST, p_197741_1_.x, p_197741_1_.y, p_197741_1_.z);
      }

      if (p_197741_6_ > 1.0E-7D) {
         facing = func_197740_a(p_197741_2_, facing, p_197741_6_, p_197741_8_, p_197741_4_, aabb.minY, aabb.minZ, aabb.maxZ, aabb.minX, aabb.maxX, Direction.DOWN, p_197741_1_.y, p_197741_1_.z, p_197741_1_.x);
      } else if (p_197741_6_ < -1.0E-7D) {
         facing = func_197740_a(p_197741_2_, facing, p_197741_6_, p_197741_8_, p_197741_4_, aabb.maxY, aabb.minZ, aabb.maxZ, aabb.minX, aabb.maxX, Direction.UP, p_197741_1_.y, p_197741_1_.z, p_197741_1_.x);
      }

      if (p_197741_8_ > 1.0E-7D) {
         facing = func_197740_a(p_197741_2_, facing, p_197741_8_, p_197741_4_, p_197741_6_, aabb.minZ, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY, Direction.NORTH, p_197741_1_.z, p_197741_1_.x, p_197741_1_.y);
      } else if (p_197741_8_ < -1.0E-7D) {
         facing = func_197740_a(p_197741_2_, facing, p_197741_8_, p_197741_4_, p_197741_6_, aabb.maxZ, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY, Direction.SOUTH, p_197741_1_.z, p_197741_1_.x, p_197741_1_.y);
      }

      return facing;
   }

   @Nullable
   private static Direction func_197740_a(double[] p_197740_0_, @Nullable Direction p_197740_1_, double p_197740_2_, double p_197740_4_, double p_197740_6_, double p_197740_8_, double p_197740_10_, double p_197740_12_, double p_197740_14_, double p_197740_16_, Direction p_197740_18_, double p_197740_19_, double p_197740_21_, double p_197740_23_) {
      double d0 = (p_197740_8_ - p_197740_19_) / p_197740_2_;
      double d1 = p_197740_21_ + d0 * p_197740_4_;
      double d2 = p_197740_23_ + d0 * p_197740_6_;
      if (0.0D < d0 && d0 < p_197740_0_[0] && p_197740_10_ - 1.0E-7D < d1 && d1 < p_197740_12_ + 1.0E-7D && p_197740_14_ - 1.0E-7D < d2 && d2 < p_197740_16_ + 1.0E-7D) {
         p_197740_0_[0] = d0;
         return p_197740_18_;
      } else {
         return p_197740_1_;
      }
   }

   public String toString() {
      return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasNaN() {
      return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
   }

   public Vec3d getCenter() {
      return new Vec3d(MathHelper.lerp(0.5D, this.minX, this.maxX), MathHelper.lerp(0.5D, this.minY, this.maxY), MathHelper.lerp(0.5D, this.minZ, this.maxZ));
   }
}