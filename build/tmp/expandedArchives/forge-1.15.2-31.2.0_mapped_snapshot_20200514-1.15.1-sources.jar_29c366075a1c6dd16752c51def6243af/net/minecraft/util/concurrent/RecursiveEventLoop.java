package net.minecraft.util.concurrent;

public abstract class RecursiveEventLoop<R extends Runnable> extends ThreadTaskExecutor<R> {
   private int running;

   public RecursiveEventLoop(String name) {
      super(name);
   }

   protected boolean shouldDeferTasks() {
      return this.isTaskRunning() || super.shouldDeferTasks();
   }

   protected boolean isTaskRunning() {
      return this.running != 0;
   }

   protected void run(R taskIn) {
      ++this.running;

      try {
         super.run(taskIn);
      } finally {
         --this.running;
      }

   }
}