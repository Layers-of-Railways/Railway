package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.util.math.Vec3d;

public class LlamaFollowCaravanGoal extends Goal {
   public final LlamaEntity llama;
   private double speedModifier;
   private int distCheckCounter;

   public LlamaFollowCaravanGoal(LlamaEntity llamaIn, double speedModifierIn) {
      this.llama = llamaIn;
      this.speedModifier = speedModifierIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      if (!this.llama.getLeashed() && !this.llama.inCaravan()) {
         List<Entity> list = this.llama.world.getEntitiesInAABBexcluding(this.llama, this.llama.getBoundingBox().grow(9.0D, 4.0D, 9.0D), (p_220719_0_) -> {
            EntityType<?> entitytype = p_220719_0_.getType();
            return entitytype == EntityType.LLAMA || entitytype == EntityType.TRADER_LLAMA;
         });
         LlamaEntity llamaentity = null;
         double d0 = Double.MAX_VALUE;

         for(Entity entity : list) {
            LlamaEntity llamaentity1 = (LlamaEntity)entity;
            if (llamaentity1.inCaravan() && !llamaentity1.hasCaravanTrail()) {
               double d1 = this.llama.getDistanceSq(llamaentity1);
               if (!(d1 > d0)) {
                  d0 = d1;
                  llamaentity = llamaentity1;
               }
            }
         }

         if (llamaentity == null) {
            for(Entity entity1 : list) {
               LlamaEntity llamaentity2 = (LlamaEntity)entity1;
               if (llamaentity2.getLeashed() && !llamaentity2.hasCaravanTrail()) {
                  double d2 = this.llama.getDistanceSq(llamaentity2);
                  if (!(d2 > d0)) {
                     d0 = d2;
                     llamaentity = llamaentity2;
                  }
               }
            }
         }

         if (llamaentity == null) {
            return false;
         } else if (d0 < 4.0D) {
            return false;
         } else if (!llamaentity.getLeashed() && !this.firstIsLeashed(llamaentity, 1)) {
            return false;
         } else {
            this.llama.joinCaravan(llamaentity);
            return true;
         }
      } else {
         return false;
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      if (this.llama.inCaravan() && this.llama.getCaravanHead().isAlive() && this.firstIsLeashed(this.llama, 0)) {
         double d0 = this.llama.getDistanceSq(this.llama.getCaravanHead());
         if (d0 > 676.0D) {
            if (this.speedModifier <= 3.0D) {
               this.speedModifier *= 1.2D;
               this.distCheckCounter = 40;
               return true;
            }

            if (this.distCheckCounter == 0) {
               return false;
            }
         }

         if (this.distCheckCounter > 0) {
            --this.distCheckCounter;
         }

         return true;
      } else {
         return false;
      }
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.llama.leaveCaravan();
      this.speedModifier = 2.1D;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (this.llama.inCaravan()) {
         LlamaEntity llamaentity = this.llama.getCaravanHead();
         double d0 = (double)this.llama.getDistance(llamaentity);
         float f = 2.0F;
         Vec3d vec3d = (new Vec3d(llamaentity.getPosX() - this.llama.getPosX(), llamaentity.getPosY() - this.llama.getPosY(), llamaentity.getPosZ() - this.llama.getPosZ())).normalize().scale(Math.max(d0 - 2.0D, 0.0D));
         this.llama.getNavigator().tryMoveToXYZ(this.llama.getPosX() + vec3d.x, this.llama.getPosY() + vec3d.y, this.llama.getPosZ() + vec3d.z, this.speedModifier);
      }
   }

   private boolean firstIsLeashed(LlamaEntity p_190858_1_, int p_190858_2_) {
      if (p_190858_2_ > 8) {
         return false;
      } else if (p_190858_1_.inCaravan()) {
         if (p_190858_1_.getCaravanHead().getLeashed()) {
            return true;
         } else {
            LlamaEntity llamaentity = p_190858_1_.getCaravanHead();
            ++p_190858_2_;
            return this.firstIsLeashed(llamaentity, p_190858_2_);
         }
      } else {
         return false;
      }
   }
}