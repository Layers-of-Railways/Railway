package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.IBlockReader;

public class OcelotAttackGoal extends Goal {
   private final IBlockReader world;
   private final MobEntity entity;
   private LivingEntity target;
   private int attackCountdown;

   public OcelotAttackGoal(MobEntity theEntityIn) {
      this.entity = theEntityIn;
      this.world = theEntityIn.world;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      LivingEntity livingentity = this.entity.getAttackTarget();
      if (livingentity == null) {
         return false;
      } else {
         this.target = livingentity;
         return true;
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      if (!this.target.isAlive()) {
         return false;
      } else if (this.entity.getDistanceSq(this.target) > 225.0D) {
         return false;
      } else {
         return !this.entity.getNavigator().noPath() || this.shouldExecute();
      }
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.target = null;
      this.entity.getNavigator().clearPath();
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      this.entity.getLookController().setLookPositionWithEntity(this.target, 30.0F, 30.0F);
      double d0 = (double)(this.entity.getWidth() * 2.0F * this.entity.getWidth() * 2.0F);
      double d1 = this.entity.getDistanceSq(this.target.getPosX(), this.target.getPosY(), this.target.getPosZ());
      double d2 = 0.8D;
      if (d1 > d0 && d1 < 16.0D) {
         d2 = 1.33D;
      } else if (d1 < 225.0D) {
         d2 = 0.6D;
      }

      this.entity.getNavigator().tryMoveToEntityLiving(this.target, d2);
      this.attackCountdown = Math.max(this.attackCountdown - 1, 0);
      if (!(d1 > d0)) {
         if (this.attackCountdown <= 0) {
            this.attackCountdown = 20;
            this.entity.attackEntityAsMob(this.target);
         }
      }
   }
}