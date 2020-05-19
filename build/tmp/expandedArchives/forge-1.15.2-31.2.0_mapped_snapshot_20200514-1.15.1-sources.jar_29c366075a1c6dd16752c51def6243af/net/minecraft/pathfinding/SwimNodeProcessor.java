package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class SwimNodeProcessor extends NodeProcessor {
   private final boolean field_205202_j;

   public SwimNodeProcessor(boolean p_i48927_1_) {
      this.field_205202_j = p_i48927_1_;
   }

   public PathPoint getStart() {
      return super.openPoint(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getBoundingBox().minZ));
   }

   public FlaggedPathPoint func_224768_a(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new FlaggedPathPoint(super.openPoint(MathHelper.floor(p_224768_1_ - (double)(this.entity.getWidth() / 2.0F)), MathHelper.floor(p_224768_3_ + 0.5D), MathHelper.floor(p_224768_5_ - (double)(this.entity.getWidth() / 2.0F))));
   }

   public int func_222859_a(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
      int i = 0;

      for(Direction direction : Direction.values()) {
         PathPoint pathpoint = this.getWaterNode(p_222859_2_.x + direction.getXOffset(), p_222859_2_.y + direction.getYOffset(), p_222859_2_.z + direction.getZOffset());
         if (pathpoint != null && !pathpoint.visited) {
            p_222859_1_[i++] = pathpoint;
         }
      }

      return i;
   }

   public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z, MobEntity entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
      return this.getPathNodeType(blockaccessIn, x, y, z);
   }

   public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z) {
      BlockPos blockpos = new BlockPos(x, y, z);
      IFluidState ifluidstate = blockaccessIn.getFluidState(blockpos);
      BlockState blockstate = blockaccessIn.getBlockState(blockpos);
      if (ifluidstate.isEmpty() && blockstate.allowsMovement(blockaccessIn, blockpos.down(), PathType.WATER) && blockstate.isAir()) {
         return PathNodeType.BREACH;
      } else {
         return ifluidstate.isTagged(FluidTags.WATER) && blockstate.allowsMovement(blockaccessIn, blockpos, PathType.WATER) ? PathNodeType.WATER : PathNodeType.BLOCKED;
      }
   }

   @Nullable
   private PathPoint getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
      PathNodeType pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
      return (!this.field_205202_j || pathnodetype != PathNodeType.BREACH) && pathnodetype != PathNodeType.WATER ? null : this.openPoint(p_186328_1_, p_186328_2_, p_186328_3_);
   }

   /**
    * Returns a mapped point or creates and adds one
    */
   @Nullable
   protected PathPoint openPoint(int x, int y, int z) {
      PathPoint pathpoint = null;
      PathNodeType pathnodetype = this.getPathNodeType(this.entity.world, x, y, z);
      float f = this.entity.getPathPriority(pathnodetype);
      if (f >= 0.0F) {
         pathpoint = super.openPoint(x, y, z);
         pathpoint.nodeType = pathnodetype;
         pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         if (this.blockaccess.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
            pathpoint.costMalus += 8.0F;
         }
      }

      return pathnodetype == PathNodeType.OPEN ? pathpoint : pathpoint;
   }

   private PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = p_186327_1_; i < p_186327_1_ + this.entitySizeX; ++i) {
         for(int j = p_186327_2_; j < p_186327_2_ + this.entitySizeY; ++j) {
            for(int k = p_186327_3_; k < p_186327_3_ + this.entitySizeZ; ++k) {
               IFluidState ifluidstate = this.blockaccess.getFluidState(blockpos$mutable.setPos(i, j, k));
               BlockState blockstate = this.blockaccess.getBlockState(blockpos$mutable.setPos(i, j, k));
               if (ifluidstate.isEmpty() && blockstate.allowsMovement(this.blockaccess, blockpos$mutable.down(), PathType.WATER) && blockstate.isAir()) {
                  return PathNodeType.BREACH;
               }

               if (!ifluidstate.isTagged(FluidTags.WATER)) {
                  return PathNodeType.BLOCKED;
               }
            }
         }
      }

      BlockState blockstate1 = this.blockaccess.getBlockState(blockpos$mutable);
      return blockstate1.allowsMovement(this.blockaccess, blockpos$mutable, PathType.WATER) ? PathNodeType.WATER : PathNodeType.BLOCKED;
   }
}