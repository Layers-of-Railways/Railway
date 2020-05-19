package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public class WalkAndSwimNodeProcessor extends WalkNodeProcessor {
   private float field_203247_k;
   private float field_203248_l;

   public void func_225578_a_(Region p_225578_1_, MobEntity p_225578_2_) {
      super.func_225578_a_(p_225578_1_, p_225578_2_);
      p_225578_2_.setPathPriority(PathNodeType.WATER, 0.0F);
      this.field_203247_k = p_225578_2_.getPathPriority(PathNodeType.WALKABLE);
      p_225578_2_.setPathPriority(PathNodeType.WALKABLE, 6.0F);
      this.field_203248_l = p_225578_2_.getPathPriority(PathNodeType.WATER_BORDER);
      p_225578_2_.setPathPriority(PathNodeType.WATER_BORDER, 4.0F);
   }

   /**
    * This method is called when all nodes have been processed and PathEntity is created.
    * {@link net.minecraft.world.pathfinder.WalkNodeProcessor WalkNodeProcessor} uses this to change its field {@link
    * net.minecraft.world.pathfinder.WalkNodeProcessor#avoidsWater avoidsWater}
    */
   public void postProcess() {
      this.entity.setPathPriority(PathNodeType.WALKABLE, this.field_203247_k);
      this.entity.setPathPriority(PathNodeType.WATER_BORDER, this.field_203248_l);
      super.postProcess();
   }

   public PathPoint getStart() {
      return this.openPoint(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getBoundingBox().minZ));
   }

   public FlaggedPathPoint func_224768_a(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new FlaggedPathPoint(this.openPoint(MathHelper.floor(p_224768_1_), MathHelper.floor(p_224768_3_ + 0.5D), MathHelper.floor(p_224768_5_)));
   }

   public int func_222859_a(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
      int i = 0;
      int j = 1;
      BlockPos blockpos = new BlockPos(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z);
      double d0 = this.func_203246_a(blockpos);
      PathPoint pathpoint = this.func_203245_a(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z + 1, 1, d0);
      PathPoint pathpoint1 = this.func_203245_a(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z, 1, d0);
      PathPoint pathpoint2 = this.func_203245_a(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z, 1, d0);
      PathPoint pathpoint3 = this.func_203245_a(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z - 1, 1, d0);
      PathPoint pathpoint4 = this.func_203245_a(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z, 0, d0);
      PathPoint pathpoint5 = this.func_203245_a(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z, 1, d0);
      if (pathpoint != null && !pathpoint.visited) {
         p_222859_1_[i++] = pathpoint;
      }

      if (pathpoint1 != null && !pathpoint1.visited) {
         p_222859_1_[i++] = pathpoint1;
      }

      if (pathpoint2 != null && !pathpoint2.visited) {
         p_222859_1_[i++] = pathpoint2;
      }

      if (pathpoint3 != null && !pathpoint3.visited) {
         p_222859_1_[i++] = pathpoint3;
      }

      if (pathpoint4 != null && !pathpoint4.visited) {
         p_222859_1_[i++] = pathpoint4;
      }

      if (pathpoint5 != null && !pathpoint5.visited) {
         p_222859_1_[i++] = pathpoint5;
      }

      boolean flag = pathpoint3 == null || pathpoint3.nodeType == PathNodeType.OPEN || pathpoint3.costMalus != 0.0F;
      boolean flag1 = pathpoint == null || pathpoint.nodeType == PathNodeType.OPEN || pathpoint.costMalus != 0.0F;
      boolean flag2 = pathpoint2 == null || pathpoint2.nodeType == PathNodeType.OPEN || pathpoint2.costMalus != 0.0F;
      boolean flag3 = pathpoint1 == null || pathpoint1.nodeType == PathNodeType.OPEN || pathpoint1.costMalus != 0.0F;
      if (flag && flag3) {
         PathPoint pathpoint6 = this.func_203245_a(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z - 1, 1, d0);
         if (pathpoint6 != null && !pathpoint6.visited) {
            p_222859_1_[i++] = pathpoint6;
         }
      }

      if (flag && flag2) {
         PathPoint pathpoint7 = this.func_203245_a(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z - 1, 1, d0);
         if (pathpoint7 != null && !pathpoint7.visited) {
            p_222859_1_[i++] = pathpoint7;
         }
      }

      if (flag1 && flag3) {
         PathPoint pathpoint8 = this.func_203245_a(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z + 1, 1, d0);
         if (pathpoint8 != null && !pathpoint8.visited) {
            p_222859_1_[i++] = pathpoint8;
         }
      }

      if (flag1 && flag2) {
         PathPoint pathpoint9 = this.func_203245_a(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z + 1, 1, d0);
         if (pathpoint9 != null && !pathpoint9.visited) {
            p_222859_1_[i++] = pathpoint9;
         }
      }

      return i;
   }

   private double func_203246_a(BlockPos p_203246_1_) {
      if (!this.entity.isInWater()) {
         BlockPos blockpos = p_203246_1_.down();
         VoxelShape voxelshape = this.blockaccess.getBlockState(blockpos).getCollisionShape(this.blockaccess, blockpos);
         return (double)blockpos.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.getEnd(Direction.Axis.Y));
      } else {
         return (double)p_203246_1_.getY() + 0.5D;
      }
   }

   @Nullable
   private PathPoint func_203245_a(int p_203245_1_, int p_203245_2_, int p_203245_3_, int p_203245_4_, double p_203245_5_) {
      PathPoint pathpoint = null;
      BlockPos blockpos = new BlockPos(p_203245_1_, p_203245_2_, p_203245_3_);
      double d0 = this.func_203246_a(blockpos);
      if (d0 - p_203245_5_ > 1.125D) {
         return null;
      } else {
         PathNodeType pathnodetype = this.getPathNodeType(this.blockaccess, p_203245_1_, p_203245_2_, p_203245_3_, this.entity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, false, false);
         float f = this.entity.getPathPriority(pathnodetype);
         double d1 = (double)this.entity.getWidth() / 2.0D;
         if (f >= 0.0F) {
            pathpoint = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
            pathpoint.nodeType = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         }

         if (pathnodetype != PathNodeType.WATER && pathnodetype != PathNodeType.WALKABLE) {
            if (pathpoint == null && p_203245_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
               pathpoint = this.func_203245_a(p_203245_1_, p_203245_2_ + 1, p_203245_3_, p_203245_4_ - 1, p_203245_5_);
            }

            if (pathnodetype == PathNodeType.OPEN) {
               AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)p_203245_1_ - d1 + 0.5D, (double)p_203245_2_ + 0.001D, (double)p_203245_3_ - d1 + 0.5D, (double)p_203245_1_ + d1 + 0.5D, (double)((float)p_203245_2_ + this.entity.getHeight()), (double)p_203245_3_ + d1 + 0.5D);
               if (!this.entity.world.hasNoCollisions(this.entity, axisalignedbb)) {
                  return null;
               }

               PathNodeType pathnodetype1 = this.getPathNodeType(this.blockaccess, p_203245_1_, p_203245_2_ - 1, p_203245_3_, this.entity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, false, false);
               if (pathnodetype1 == PathNodeType.BLOCKED) {
                  pathpoint = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
                  pathpoint.nodeType = PathNodeType.WALKABLE;
                  pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                  return pathpoint;
               }

               if (pathnodetype1 == PathNodeType.WATER) {
                  pathpoint = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
                  pathpoint.nodeType = PathNodeType.WATER;
                  pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                  return pathpoint;
               }

               int i = 0;

               while(p_203245_2_ > 0 && pathnodetype == PathNodeType.OPEN) {
                  --p_203245_2_;
                  if (i++ >= this.entity.getMaxFallHeight()) {
                     return null;
                  }

                  pathnodetype = this.getPathNodeType(this.blockaccess, p_203245_1_, p_203245_2_, p_203245_3_, this.entity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, false, false);
                  f = this.entity.getPathPriority(pathnodetype);
                  if (pathnodetype != PathNodeType.OPEN && f >= 0.0F) {
                     pathpoint = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
                     pathpoint.nodeType = pathnodetype;
                     pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                     break;
                  }

                  if (f < 0.0F) {
                     return null;
                  }
               }
            }

            return pathpoint;
         } else {
            if (p_203245_2_ < this.entity.world.getSeaLevel() - 10 && pathpoint != null) {
               ++pathpoint.costMalus;
            }

            return pathpoint;
         }
      }
   }

   protected PathNodeType func_215744_a(IBlockReader p_215744_1_, boolean p_215744_2_, boolean p_215744_3_, BlockPos p_215744_4_, PathNodeType p_215744_5_) {
      if (p_215744_5_ == PathNodeType.RAIL && !(p_215744_1_.getBlockState(p_215744_4_).getBlock() instanceof AbstractRailBlock) && !(p_215744_1_.getBlockState(p_215744_4_.down()).getBlock() instanceof AbstractRailBlock)) {
         p_215744_5_ = PathNodeType.FENCE;
      }

      if (p_215744_5_ == PathNodeType.DOOR_OPEN || p_215744_5_ == PathNodeType.DOOR_WOOD_CLOSED || p_215744_5_ == PathNodeType.DOOR_IRON_CLOSED) {
         p_215744_5_ = PathNodeType.BLOCKED;
      }

      if (p_215744_5_ == PathNodeType.LEAVES) {
         p_215744_5_ = PathNodeType.BLOCKED;
      }

      return p_215744_5_;
   }

   public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z) {
      PathNodeType pathnodetype = getPathNodeTypeRaw(blockaccessIn, x, y, z);
      if (pathnodetype == PathNodeType.WATER) {
         for(Direction direction : Direction.values()) {
            PathNodeType pathnodetype2 = getPathNodeTypeRaw(blockaccessIn, x + direction.getXOffset(), y + direction.getYOffset(), z + direction.getZOffset());
            if (pathnodetype2 == PathNodeType.BLOCKED) {
               return PathNodeType.WATER_BORDER;
            }
         }

         return PathNodeType.WATER;
      } else {
         if (pathnodetype == PathNodeType.OPEN && y >= 1) {
            Block block = blockaccessIn.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
            PathNodeType pathnodetype1 = getPathNodeTypeRaw(blockaccessIn, x, y - 1, z);
            if (pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.LAVA) {
               pathnodetype = PathNodeType.WALKABLE;
            } else {
               pathnodetype = PathNodeType.OPEN;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || block == Blocks.CAMPFIRE) {
               pathnodetype = PathNodeType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
               pathnodetype = PathNodeType.DAMAGE_CACTUS;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
               pathnodetype = PathNodeType.DAMAGE_OTHER;
            }
         }

         if (pathnodetype == PathNodeType.WALKABLE) {
            pathnodetype = checkNeighborBlocks(blockaccessIn, x, y, z, pathnodetype);
         }

         return pathnodetype;
      }
   }
}