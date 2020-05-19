package net.minecraft.entity.ai.goal;

import net.minecraft.entity.MobEntity;

public class OpenDoorGoal extends InteractDoorGoal {
   private final boolean closeDoor;
   private int closeDoorTemporisation;

   public OpenDoorGoal(MobEntity entitylivingIn, boolean shouldClose) {
      super(entitylivingIn);
      this.entity = entitylivingIn;
      this.closeDoor = shouldClose;
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.closeDoor && this.closeDoorTemporisation > 0 && super.shouldContinueExecuting();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.closeDoorTemporisation = 20;
      this.toggleDoor(true);
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.toggleDoor(false);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      --this.closeDoorTemporisation;
      super.tick();
   }
}