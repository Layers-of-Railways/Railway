package net.minecraft.block.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorldReader;

public class BlockPattern {
   private final Predicate<CachedBlockInfo>[][][] blockMatches;
   private final int fingerLength;
   private final int thumbLength;
   private final int palmLength;

   public BlockPattern(Predicate<CachedBlockInfo>[][][] predicates) {
      this.blockMatches = predicates;
      this.fingerLength = predicates.length;
      if (this.fingerLength > 0) {
         this.thumbLength = predicates[0].length;
         if (this.thumbLength > 0) {
            this.palmLength = predicates[0][0].length;
         } else {
            this.palmLength = 0;
         }
      } else {
         this.thumbLength = 0;
         this.palmLength = 0;
      }

   }

   public int getFingerLength() {
      return this.fingerLength;
   }

   public int getThumbLength() {
      return this.thumbLength;
   }

   public int getPalmLength() {
      return this.palmLength;
   }

   /**
    * checks that the given pattern & rotation is at the block co-ordinates.
    */
   @Nullable
   private BlockPattern.PatternHelper checkPatternAt(BlockPos pos, Direction finger, Direction thumb, LoadingCache<BlockPos, CachedBlockInfo> lcache) {
      for(int i = 0; i < this.palmLength; ++i) {
         for(int j = 0; j < this.thumbLength; ++j) {
            for(int k = 0; k < this.fingerLength; ++k) {
               if (!this.blockMatches[k][j][i].test(lcache.getUnchecked(translateOffset(pos, finger, thumb, i, j, k)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.PatternHelper(pos, finger, thumb, lcache, this.palmLength, this.thumbLength, this.fingerLength);
   }

   /**
    * Calculates whether the given world position matches the pattern. Warning, fairly heavy function. @return a
    * BlockPattern.PatternHelper if found, null otherwise.
    */
   @Nullable
   public BlockPattern.PatternHelper match(IWorldReader worldIn, BlockPos pos) {
      LoadingCache<BlockPos, CachedBlockInfo> loadingcache = createLoadingCache(worldIn, false);
      int i = Math.max(Math.max(this.palmLength, this.thumbLength), this.fingerLength);

      for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos, pos.add(i - 1, i - 1, i - 1))) {
         for(Direction direction : Direction.values()) {
            for(Direction direction1 : Direction.values()) {
               if (direction1 != direction && direction1 != direction.getOpposite()) {
                  BlockPattern.PatternHelper blockpattern$patternhelper = this.checkPatternAt(blockpos, direction, direction1, loadingcache);
                  if (blockpattern$patternhelper != null) {
                     return blockpattern$patternhelper;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPos, CachedBlockInfo> createLoadingCache(IWorldReader worldIn, boolean forceLoadIn) {
      return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(worldIn, forceLoadIn));
   }

   /**
    * Offsets the position of pos in the direction of finger and thumb facing by offset amounts, follows the right-hand
    * rule for cross products (finger, thumb, palm) @return A new BlockPos offset in the facing directions
    */
   protected static BlockPos translateOffset(BlockPos pos, Direction finger, Direction thumb, int palmOffset, int thumbOffset, int fingerOffset) {
      if (finger != thumb && finger != thumb.getOpposite()) {
         Vec3i vec3i = new Vec3i(finger.getXOffset(), finger.getYOffset(), finger.getZOffset());
         Vec3i vec3i1 = new Vec3i(thumb.getXOffset(), thumb.getYOffset(), thumb.getZOffset());
         Vec3i vec3i2 = vec3i.crossProduct(vec3i1);
         return pos.add(vec3i1.getX() * -thumbOffset + vec3i2.getX() * palmOffset + vec3i.getX() * fingerOffset, vec3i1.getY() * -thumbOffset + vec3i2.getY() * palmOffset + vec3i.getY() * fingerOffset, vec3i1.getZ() * -thumbOffset + vec3i2.getZ() * palmOffset + vec3i.getZ() * fingerOffset);
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, CachedBlockInfo> {
      private final IWorldReader world;
      private final boolean forceLoad;

      public CacheLoader(IWorldReader worldIn, boolean forceLoadIn) {
         this.world = worldIn;
         this.forceLoad = forceLoadIn;
      }

      public CachedBlockInfo load(BlockPos p_load_1_) throws Exception {
         return new CachedBlockInfo(this.world, p_load_1_, this.forceLoad);
      }
   }

   public static class PatternHelper {
      private final BlockPos frontTopLeft;
      private final Direction forwards;
      private final Direction up;
      private final LoadingCache<BlockPos, CachedBlockInfo> lcache;
      private final int width;
      private final int height;
      private final int depth;

      public PatternHelper(BlockPos posIn, Direction fingerIn, Direction thumbIn, LoadingCache<BlockPos, CachedBlockInfo> lcacheIn, int widthIn, int heightIn, int depthIn) {
         this.frontTopLeft = posIn;
         this.forwards = fingerIn;
         this.up = thumbIn;
         this.lcache = lcacheIn;
         this.width = widthIn;
         this.height = heightIn;
         this.depth = depthIn;
      }

      /**
       * Return the BlockPos of the Pattern
       */
      public BlockPos getFrontTopLeft() {
         return this.frontTopLeft;
      }

      public Direction getForwards() {
         return this.forwards;
      }

      public Direction getUp() {
         return this.up;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public CachedBlockInfo translateOffset(int palmOffset, int thumbOffset, int fingerOffset) {
         return this.lcache.getUnchecked(BlockPattern.translateOffset(this.frontTopLeft, this.getForwards(), this.getUp(), palmOffset, thumbOffset, fingerOffset));
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
      }

      public BlockPattern.PortalInfo getPortalInfo(Direction p_222504_1_, BlockPos p_222504_2_, double p_222504_3_, Vec3d p_222504_5_, double p_222504_6_) {
         Direction direction = this.getForwards();
         Direction direction1 = direction.rotateY();
         double d1 = (double)(this.getFrontTopLeft().getY() + 1) - p_222504_3_ * (double)this.getHeight();
         double d0;
         double d2;
         if (direction1 == Direction.NORTH) {
            d0 = (double)p_222504_2_.getX() + 0.5D;
            d2 = (double)(this.getFrontTopLeft().getZ() + 1) - (1.0D - p_222504_6_) * (double)this.getWidth();
         } else if (direction1 == Direction.SOUTH) {
            d0 = (double)p_222504_2_.getX() + 0.5D;
            d2 = (double)this.getFrontTopLeft().getZ() + (1.0D - p_222504_6_) * (double)this.getWidth();
         } else if (direction1 == Direction.WEST) {
            d0 = (double)(this.getFrontTopLeft().getX() + 1) - (1.0D - p_222504_6_) * (double)this.getWidth();
            d2 = (double)p_222504_2_.getZ() + 0.5D;
         } else {
            d0 = (double)this.getFrontTopLeft().getX() + (1.0D - p_222504_6_) * (double)this.getWidth();
            d2 = (double)p_222504_2_.getZ() + 0.5D;
         }

         double d3;
         double d4;
         if (direction.getOpposite() == p_222504_1_) {
            d3 = p_222504_5_.x;
            d4 = p_222504_5_.z;
         } else if (direction.getOpposite() == p_222504_1_.getOpposite()) {
            d3 = -p_222504_5_.x;
            d4 = -p_222504_5_.z;
         } else if (direction.getOpposite() == p_222504_1_.rotateY()) {
            d3 = -p_222504_5_.z;
            d4 = p_222504_5_.x;
         } else {
            d3 = p_222504_5_.z;
            d4 = -p_222504_5_.x;
         }

         int i = (direction.getHorizontalIndex() - p_222504_1_.getOpposite().getHorizontalIndex()) * 90;
         return new BlockPattern.PortalInfo(new Vec3d(d0, d1, d2), new Vec3d(d3, p_222504_5_.y, d4), i);
      }
   }

   public static class PortalInfo {
      public final Vec3d pos;
      public final Vec3d motion;
      public final int rotation;

      public PortalInfo(Vec3d p_i50457_1_, Vec3d p_i50457_2_, int p_i50457_3_) {
         this.pos = p_i50457_1_;
         this.motion = p_i50457_2_;
         this.rotation = p_i50457_3_;
      }
   }
}