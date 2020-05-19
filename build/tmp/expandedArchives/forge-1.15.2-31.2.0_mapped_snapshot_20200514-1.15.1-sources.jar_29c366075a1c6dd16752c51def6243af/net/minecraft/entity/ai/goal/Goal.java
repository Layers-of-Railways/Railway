package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class Goal {
   private final EnumSet<Goal.Flag> flags = EnumSet.noneOf(Goal.Flag.class);

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public abstract boolean shouldExecute();

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.shouldExecute();
   }

   public boolean isPreemptible() {
      return true;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
   }

   public void setMutexFlags(EnumSet<Goal.Flag> flagSet) {
      this.flags.clear();
      this.flags.addAll(flagSet);
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   public EnumSet<Goal.Flag> getMutexFlags() {
      return this.flags;
   }

   public static enum Flag {
      MOVE,
      LOOK,
      JUMP,
      TARGET;
   }
}