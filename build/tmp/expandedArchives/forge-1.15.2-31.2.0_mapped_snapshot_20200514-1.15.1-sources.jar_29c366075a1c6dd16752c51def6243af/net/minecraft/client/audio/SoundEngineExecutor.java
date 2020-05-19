package net.minecraft.client.audio;

import java.util.concurrent.locks.LockSupport;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundEngineExecutor extends ThreadTaskExecutor<Runnable> {
   private Thread executionThread = this.createExecutionThread();
   private volatile boolean stopped;

   public SoundEngineExecutor() {
      super("Sound executor");
   }

   private Thread createExecutionThread() {
      Thread thread = new Thread(this::run);
      thread.setDaemon(true);
      thread.setName("Sound engine");
      thread.start();
      return thread;
   }

   protected Runnable wrapTask(Runnable runnable) {
      return runnable;
   }

   protected boolean canRun(Runnable runnable) {
      return !this.stopped;
   }

   protected Thread getExecutionThread() {
      return this.executionThread;
   }

   private void run() {
      while(!this.stopped) {
         this.driveUntil(() -> {
            return this.stopped;
         });
      }

   }

   protected void threadYieldPark() {
      LockSupport.park("waiting for tasks");
   }

   public void restart() {
      this.stopped = true;
      this.executionThread.interrupt();

      try {
         this.executionThread.join();
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
      }

      this.dropTasks();
      this.stopped = false;
      this.executionThread = this.createExecutionThread();
   }
}