package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class SitGoal extends Goal {
   private final TameableEntity tameable;
   private boolean isSitting;

   public SitGoal(TameableEntity entityIn) {
      this.tameable = entityIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.isSitting;
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      if (!this.tameable.isTamed()) {
         return false;
      } else if (this.tameable.isInWaterOrBubbleColumn()) {
         return false;
      } else if (!this.tameable.onGround) {
         return false;
      } else {
         LivingEntity livingentity = this.tameable.getOwner();
         if (livingentity == null) {
            return true;
         } else {
            return this.tameable.getDistanceSq(livingentity) < 144.0D && livingentity.getRevengeTarget() != null ? false : this.isSitting;
         }
      }
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.tameable.getNavigator().clearPath();
      this.tameable.setSitting(true);
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.tameable.setSitting(false);
   }

   /**
    * Sets the sitting flag.
    */
   public void setSitting(boolean sitting) {
      this.isSitting = sitting;
   }
}