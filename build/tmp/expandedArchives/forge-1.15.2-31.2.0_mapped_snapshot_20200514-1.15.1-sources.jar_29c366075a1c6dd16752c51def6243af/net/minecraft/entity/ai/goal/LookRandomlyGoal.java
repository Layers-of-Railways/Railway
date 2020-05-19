package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.MobEntity;

public class LookRandomlyGoal extends Goal {
   private final MobEntity idleEntity;
   private double lookX;
   private double lookZ;
   private int idleTime;

   public LookRandomlyGoal(MobEntity entitylivingIn) {
      this.idleEntity = entitylivingIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      return this.idleEntity.getRNG().nextFloat() < 0.02F;
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.idleTime >= 0;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      double d0 = (Math.PI * 2D) * this.idleEntity.getRNG().nextDouble();
      this.lookX = Math.cos(d0);
      this.lookZ = Math.sin(d0);
      this.idleTime = 20 + this.idleEntity.getRNG().nextInt(20);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      --this.idleTime;
      this.idleEntity.getLookController().setLookPosition(this.idleEntity.getPosX() + this.lookX, this.idleEntity.getPosYEye(), this.idleEntity.getPosZ() + this.lookZ);
   }
}