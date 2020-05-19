package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class FollowOwnerGoal extends Goal {
   private final TameableEntity tameable;
   private LivingEntity owner;
   private final IWorldReader world;
   private final double followSpeed;
   private final PathNavigator navigator;
   private int timeToRecalcPath;
   private final float maxDist;
   private final float minDist;
   private float oldWaterCost;
   private final boolean field_226326_j_;

   public FollowOwnerGoal(TameableEntity p_i225711_1_, double p_i225711_2_, float p_i225711_4_, float p_i225711_5_, boolean p_i225711_6_) {
      this.tameable = p_i225711_1_;
      this.world = p_i225711_1_.world;
      this.followSpeed = p_i225711_2_;
      this.navigator = p_i225711_1_.getNavigator();
      this.minDist = p_i225711_4_;
      this.maxDist = p_i225711_5_;
      this.field_226326_j_ = p_i225711_6_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if (!(p_i225711_1_.getNavigator() instanceof GroundPathNavigator) && !(p_i225711_1_.getNavigator() instanceof FlyingPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      LivingEntity livingentity = this.tameable.getOwner();
      if (livingentity == null) {
         return false;
      } else if (livingentity.isSpectator()) {
         return false;
      } else if (this.tameable.isSitting()) {
         return false;
      } else if (this.tameable.getDistanceSq(livingentity) < (double)(this.minDist * this.minDist)) {
         return false;
      } else {
         this.owner = livingentity;
         return true;
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      if (this.navigator.noPath()) {
         return false;
      } else if (this.tameable.isSitting()) {
         return false;
      } else {
         return !(this.tameable.getDistanceSq(this.owner) <= (double)(this.maxDist * this.maxDist));
      }
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.tameable.getPathPriority(PathNodeType.WATER);
      this.tameable.setPathPriority(PathNodeType.WATER, 0.0F);
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.owner = null;
      this.navigator.clearPath();
      this.tameable.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      this.tameable.getLookController().setLookPositionWithEntity(this.owner, 10.0F, (float)this.tameable.getVerticalFaceSpeed());
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = 10;
         if (!this.tameable.getLeashed() && !this.tameable.isPassenger()) {
            if (this.tameable.getDistanceSq(this.owner) >= 144.0D) {
               this.func_226330_g_();
            } else {
               this.navigator.tryMoveToEntityLiving(this.owner, this.followSpeed);
            }

         }
      }
   }

   private void func_226330_g_() {
      BlockPos blockpos = new BlockPos(this.owner);

      for(int i = 0; i < 10; ++i) {
         int j = this.func_226327_a_(-3, 3);
         int k = this.func_226327_a_(-1, 1);
         int l = this.func_226327_a_(-3, 3);
         boolean flag = this.func_226328_a_(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
         if (flag) {
            return;
         }
      }

   }

   private boolean func_226328_a_(int p_226328_1_, int p_226328_2_, int p_226328_3_) {
      if (Math.abs((double)p_226328_1_ - this.owner.getPosX()) < 2.0D && Math.abs((double)p_226328_3_ - this.owner.getPosZ()) < 2.0D) {
         return false;
      } else if (!this.func_226329_a_(new BlockPos(p_226328_1_, p_226328_2_, p_226328_3_))) {
         return false;
      } else {
         this.tameable.setLocationAndAngles((double)((float)p_226328_1_ + 0.5F), (double)p_226328_2_, (double)((float)p_226328_3_ + 0.5F), this.tameable.rotationYaw, this.tameable.rotationPitch);
         this.navigator.clearPath();
         return true;
      }
   }

   private boolean func_226329_a_(BlockPos p_226329_1_) {
      PathNodeType pathnodetype = WalkNodeProcessor.func_227480_b_(this.world, p_226329_1_.getX(), p_226329_1_.getY(), p_226329_1_.getZ());
      if (pathnodetype != PathNodeType.WALKABLE) {
         return false;
      } else {
         BlockState blockstate = this.world.getBlockState(p_226329_1_.down());
         if (!this.field_226326_j_ && blockstate.getBlock() instanceof LeavesBlock) {
            return false;
         } else {
            BlockPos blockpos = p_226329_1_.subtract(new BlockPos(this.tameable));
            return this.world.hasNoCollisions(this.tameable, this.tameable.getBoundingBox().offset(blockpos));
         }
      }
   }

   private int func_226327_a_(int p_226327_1_, int p_226327_2_) {
      return this.tameable.getRNG().nextInt(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
   }
}