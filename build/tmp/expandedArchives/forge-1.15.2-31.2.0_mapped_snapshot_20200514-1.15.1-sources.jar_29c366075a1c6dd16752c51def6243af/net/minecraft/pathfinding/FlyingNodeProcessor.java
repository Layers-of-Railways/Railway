package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public class FlyingNodeProcessor extends WalkNodeProcessor {
   public void func_225578_a_(Region p_225578_1_, MobEntity p_225578_2_) {
      super.func_225578_a_(p_225578_1_, p_225578_2_);
      this.avoidsWater = p_225578_2_.getPathPriority(PathNodeType.WATER);
   }

   /**
    * This method is called when all nodes have been processed and PathEntity is created.
    * {@link net.minecraft.world.pathfinder.WalkNodeProcessor WalkNodeProcessor} uses this to change its field {@link
    * net.minecraft.world.pathfinder.WalkNodeProcessor#avoidsWater avoidsWater}
    */
   public void postProcess() {
      this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
      super.postProcess();
   }

   public PathPoint getStart() {
      int i;
      if (this.getCanSwim() && this.entity.isInWater()) {
         i = MathHelper.floor(this.entity.getPosY());
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.entity.getPosX(), (double)i, this.entity.getPosZ());

         for(Block block = this.blockaccess.getBlockState(blockpos$mutable).getBlock(); block == Blocks.WATER; block = this.blockaccess.getBlockState(blockpos$mutable).getBlock()) {
            ++i;
            blockpos$mutable.setPos(this.entity.getPosX(), (double)i, this.entity.getPosZ());
         }
      } else {
         i = MathHelper.floor(this.entity.getPosY() + 0.5D);
      }

      BlockPos blockpos1 = new BlockPos(this.entity);
      PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, blockpos1.getX(), i, blockpos1.getZ());
      if (this.entity.getPathPriority(pathnodetype1) < 0.0F) {
         Set<BlockPos> set = Sets.newHashSet();
         set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().maxZ));
         set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().maxZ));

         for(BlockPos blockpos : set) {
            PathNodeType pathnodetype = this.getPathNodeType(this.entity, blockpos);
            if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
               return super.openPoint(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
         }
      }

      return super.openPoint(blockpos1.getX(), i, blockpos1.getZ());
   }

   public FlaggedPathPoint func_224768_a(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new FlaggedPathPoint(super.openPoint(MathHelper.floor(p_224768_1_), MathHelper.floor(p_224768_3_), MathHelper.floor(p_224768_5_)));
   }

   public int func_222859_a(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
      int i = 0;
      PathPoint pathpoint = this.openPoint(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z + 1);
      if (this.func_227477_b_(pathpoint)) {
         p_222859_1_[i++] = pathpoint;
      }

      PathPoint pathpoint1 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z);
      if (this.func_227477_b_(pathpoint1)) {
         p_222859_1_[i++] = pathpoint1;
      }

      PathPoint pathpoint2 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z);
      if (this.func_227477_b_(pathpoint2)) {
         p_222859_1_[i++] = pathpoint2;
      }

      PathPoint pathpoint3 = this.openPoint(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z - 1);
      if (this.func_227477_b_(pathpoint3)) {
         p_222859_1_[i++] = pathpoint3;
      }

      PathPoint pathpoint4 = this.openPoint(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z);
      if (this.func_227477_b_(pathpoint4)) {
         p_222859_1_[i++] = pathpoint4;
      }

      PathPoint pathpoint5 = this.openPoint(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z);
      if (this.func_227477_b_(pathpoint5)) {
         p_222859_1_[i++] = pathpoint5;
      }

      PathPoint pathpoint6 = this.openPoint(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(pathpoint6) && this.func_227476_a_(pathpoint) && this.func_227476_a_(pathpoint4)) {
         p_222859_1_[i++] = pathpoint6;
      }

      PathPoint pathpoint7 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y + 1, p_222859_2_.z);
      if (this.func_227477_b_(pathpoint7) && this.func_227476_a_(pathpoint1) && this.func_227476_a_(pathpoint4)) {
         p_222859_1_[i++] = pathpoint7;
      }

      PathPoint pathpoint8 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y + 1, p_222859_2_.z);
      if (this.func_227477_b_(pathpoint8) && this.func_227476_a_(pathpoint2) && this.func_227476_a_(pathpoint4)) {
         p_222859_1_[i++] = pathpoint8;
      }

      PathPoint pathpoint9 = this.openPoint(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(pathpoint9) && this.func_227476_a_(pathpoint3) && this.func_227476_a_(pathpoint4)) {
         p_222859_1_[i++] = pathpoint9;
      }

      PathPoint pathpoint10 = this.openPoint(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(pathpoint10) && this.func_227476_a_(pathpoint) && this.func_227476_a_(pathpoint5)) {
         p_222859_1_[i++] = pathpoint10;
      }

      PathPoint pathpoint11 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y - 1, p_222859_2_.z);
      if (this.func_227477_b_(pathpoint11) && this.func_227476_a_(pathpoint1) && this.func_227476_a_(pathpoint5)) {
         p_222859_1_[i++] = pathpoint11;
      }

      PathPoint pathpoint12 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y - 1, p_222859_2_.z);
      if (this.func_227477_b_(pathpoint12) && this.func_227476_a_(pathpoint2) && this.func_227476_a_(pathpoint5)) {
         p_222859_1_[i++] = pathpoint12;
      }

      PathPoint pathpoint13 = this.openPoint(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(pathpoint13) && this.func_227476_a_(pathpoint3) && this.func_227476_a_(pathpoint5)) {
         p_222859_1_[i++] = pathpoint13;
      }

      PathPoint pathpoint14 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z - 1);
      if (this.func_227477_b_(pathpoint14) && this.func_227476_a_(pathpoint3) && this.func_227476_a_(pathpoint2)) {
         p_222859_1_[i++] = pathpoint14;
      }

      PathPoint pathpoint15 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z + 1);
      if (this.func_227477_b_(pathpoint15) && this.func_227476_a_(pathpoint) && this.func_227476_a_(pathpoint2)) {
         p_222859_1_[i++] = pathpoint15;
      }

      PathPoint pathpoint16 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z - 1);
      if (this.func_227477_b_(pathpoint16) && this.func_227476_a_(pathpoint3) && this.func_227476_a_(pathpoint1)) {
         p_222859_1_[i++] = pathpoint16;
      }

      PathPoint pathpoint17 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z + 1);
      if (this.func_227477_b_(pathpoint17) && this.func_227476_a_(pathpoint) && this.func_227476_a_(pathpoint1)) {
         p_222859_1_[i++] = pathpoint17;
      }

      PathPoint pathpoint18 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y + 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(pathpoint18) && this.func_227476_a_(pathpoint14) && this.func_227476_a_(pathpoint9) && this.func_227476_a_(pathpoint8)) {
         p_222859_1_[i++] = pathpoint18;
      }

      PathPoint pathpoint19 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y + 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(pathpoint19) && this.func_227476_a_(pathpoint15) && this.func_227476_a_(pathpoint6) && this.func_227476_a_(pathpoint8)) {
         p_222859_1_[i++] = pathpoint19;
      }

      PathPoint pathpoint20 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y + 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(pathpoint20) && this.func_227476_a_(pathpoint16) && this.func_227476_a_(pathpoint9) && this.func_227476_a_(pathpoint7)) {
         p_222859_1_[i++] = pathpoint20;
      }

      PathPoint pathpoint21 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y + 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(pathpoint21) && this.func_227476_a_(pathpoint17) && this.func_227476_a_(pathpoint6) && this.func_227476_a_(pathpoint7)) {
         p_222859_1_[i++] = pathpoint21;
      }

      PathPoint pathpoint22 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y - 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(pathpoint22) && this.func_227476_a_(pathpoint14) && this.func_227476_a_(pathpoint13) && this.func_227476_a_(pathpoint12)) {
         p_222859_1_[i++] = pathpoint22;
      }

      PathPoint pathpoint23 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y - 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(pathpoint23) && this.func_227476_a_(pathpoint15) && this.func_227476_a_(pathpoint10) && this.func_227476_a_(pathpoint12)) {
         p_222859_1_[i++] = pathpoint23;
      }

      PathPoint pathpoint24 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y - 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(pathpoint24) && this.func_227476_a_(pathpoint16) && this.func_227476_a_(pathpoint13) && this.func_227476_a_(pathpoint11)) {
         p_222859_1_[i++] = pathpoint24;
      }

      PathPoint pathpoint25 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y - 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(pathpoint25) && this.func_227476_a_(pathpoint17) && this.func_227476_a_(pathpoint10) && this.func_227476_a_(pathpoint11)) {
         p_222859_1_[i++] = pathpoint25;
      }

      return i;
   }

   private boolean func_227476_a_(@Nullable PathPoint p_227476_1_) {
      return p_227476_1_ != null && p_227476_1_.costMalus >= 0.0F;
   }

   private boolean func_227477_b_(@Nullable PathPoint p_227477_1_) {
      return p_227477_1_ != null && !p_227477_1_.visited;
   }

   /**
    * Returns a mapped point or creates and adds one
    */
   @Nullable
   protected PathPoint openPoint(int x, int y, int z) {
      PathPoint pathpoint = null;
      PathNodeType pathnodetype = this.getPathNodeType(this.entity, x, y, z);
      float f = this.entity.getPathPriority(pathnodetype);
      if (f >= 0.0F) {
         pathpoint = super.openPoint(x, y, z);
         pathpoint.nodeType = pathnodetype;
         pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         if (pathnodetype == PathNodeType.WALKABLE) {
            ++pathpoint.costMalus;
         }
      }

      return pathnodetype != PathNodeType.OPEN && pathnodetype != PathNodeType.WALKABLE ? pathpoint : pathpoint;
   }

   public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z, MobEntity entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
      EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
      PathNodeType pathnodetype = PathNodeType.BLOCKED;
      BlockPos blockpos = new BlockPos(entitylivingIn);
      pathnodetype = this.getPathNodeType(blockaccessIn, x, y, z, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn, enumset, pathnodetype, blockpos);
      if (enumset.contains(PathNodeType.FENCE)) {
         return PathNodeType.FENCE;
      } else {
         PathNodeType pathnodetype1 = PathNodeType.BLOCKED;

         for(PathNodeType pathnodetype2 : enumset) {
            if (entitylivingIn.getPathPriority(pathnodetype2) < 0.0F) {
               return pathnodetype2;
            }

            if (entitylivingIn.getPathPriority(pathnodetype2) >= entitylivingIn.getPathPriority(pathnodetype1)) {
               pathnodetype1 = pathnodetype2;
            }
         }

         return pathnodetype == PathNodeType.OPEN && entitylivingIn.getPathPriority(pathnodetype1) == 0.0F ? PathNodeType.OPEN : pathnodetype1;
      }
   }

   public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z) {
      PathNodeType pathnodetype = getPathNodeTypeRaw(blockaccessIn, x, y, z);
      if (pathnodetype == PathNodeType.OPEN && y >= 1) {
         Block block = blockaccessIn.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
         PathNodeType pathnodetype1 = getPathNodeTypeRaw(blockaccessIn, x, y - 1, z);
         if (pathnodetype1 != PathNodeType.DAMAGE_FIRE && block != Blocks.MAGMA_BLOCK && pathnodetype1 != PathNodeType.LAVA && block != Blocks.CAMPFIRE) {
            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
               pathnodetype = PathNodeType.DAMAGE_CACTUS;
            } else if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
               pathnodetype = PathNodeType.DAMAGE_OTHER;
            } else if (pathnodetype1 == PathNodeType.COCOA) {
               pathnodetype = PathNodeType.COCOA;
            } else if (pathnodetype1 == PathNodeType.FENCE) {
               pathnodetype = PathNodeType.FENCE;
            } else {
               pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER ? PathNodeType.WALKABLE : PathNodeType.OPEN;
            }
         } else {
            pathnodetype = PathNodeType.DAMAGE_FIRE;
         }
      }

      if (pathnodetype == PathNodeType.WALKABLE || pathnodetype == PathNodeType.OPEN) {
         pathnodetype = checkNeighborBlocks(blockaccessIn, x, y, z, pathnodetype);
      }

      return pathnodetype;
   }

   private PathNodeType getPathNodeType(MobEntity p_192559_1_, BlockPos p_192559_2_) {
      return this.getPathNodeType(p_192559_1_, p_192559_2_.getX(), p_192559_2_.getY(), p_192559_2_.getZ());
   }

   private PathNodeType getPathNodeType(MobEntity p_192558_1_, int p_192558_2_, int p_192558_3_, int p_192558_4_) {
      return this.getPathNodeType(this.blockaccess, p_192558_2_, p_192558_3_, p_192558_4_, p_192558_1_, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
   }
}