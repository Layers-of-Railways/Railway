package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class PrioritizedGoal extends Goal {
   private final Goal inner;
   private final int priority;
   private boolean running;

   public PrioritizedGoal(int priorityIn, Goal goalIn) {
      this.priority = priorityIn;
      this.inner = goalIn;
   }

   public boolean isPreemptedBy(PrioritizedGoal other) {
      return this.isPreemptible() && other.getPriority() < this.getPriority();
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      return this.inner.shouldExecute();
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.inner.shouldContinueExecuting();
   }

   public boolean isPreemptible() {
      return this.inner.isPreemptible();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      if (!this.running) {
         this.running = true;
         this.inner.startExecuting();
      }
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      if (this.running) {
         this.running = false;
         this.inner.resetTask();
      }
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      this.inner.tick();
   }

   public void setMutexFlags(EnumSet<Goal.Flag> flagSet) {
      this.inner.setMutexFlags(flagSet);
   }

   public EnumSet<Goal.Flag> getMutexFlags() {
      return this.inner.getMutexFlags();
   }

   public boolean isRunning() {
      return this.running;
   }

   public int getPriority() {
      return this.priority;
   }

   /**
    * "Gets the private goal enclosed by this PrioritizedGoal. Call this rather than use an access transformer"
    */
   public Goal getGoal() {
      return this.inner;
   }

   public boolean equals(@Nullable Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ != null && this.getClass() == p_equals_1_.getClass() ? this.inner.equals(((PrioritizedGoal)p_equals_1_).inner) : false;
      }
   }

   public int hashCode() {
      return this.inner.hashCode();
   }
}