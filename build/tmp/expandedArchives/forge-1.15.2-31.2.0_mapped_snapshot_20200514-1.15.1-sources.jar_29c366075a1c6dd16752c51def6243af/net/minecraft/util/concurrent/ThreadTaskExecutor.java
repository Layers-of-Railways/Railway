package net.minecraft.util.concurrent;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadTaskExecutor<R extends Runnable> implements ITaskExecutor<R>, Executor {
   private final String name;
   private static final Logger LOGGER = LogManager.getLogger();
   private final Queue<R> queue = Queues.newConcurrentLinkedQueue();
   private int drivers;

   protected ThreadTaskExecutor(String nameIn) {
      this.name = nameIn;
   }

   protected abstract R wrapTask(Runnable runnable);

   protected abstract boolean canRun(R runnable);

   public boolean isOnExecutionThread() {
      return Thread.currentThread() == this.getExecutionThread();
   }

   protected abstract Thread getExecutionThread();

   protected boolean shouldDeferTasks() {
      return !this.isOnExecutionThread();
   }

   public int getQueueSize() {
      return this.queue.size();
   }

   public String getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public <V> CompletableFuture<V> supplyAsync(Supplier<V> supplier) {
      return this.shouldDeferTasks() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
   }

   public CompletableFuture<Void> deferTask(Runnable taskIn) {
      return CompletableFuture.supplyAsync(() -> {
         taskIn.run();
         return null;
      }, this);
   }

   public CompletableFuture<Void> runAsync(Runnable taskIn) {
      if (this.shouldDeferTasks()) {
         return this.deferTask(taskIn);
      } else {
         taskIn.run();
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   public void runImmediately(Runnable taskIn) {
      if (!this.isOnExecutionThread()) {
         this.deferTask(taskIn).join();
      } else {
         taskIn.run();
      }

   }

   public void enqueue(R taskIn) {
      this.queue.add(taskIn);
      LockSupport.unpark(this.getExecutionThread());
   }

   public void execute(Runnable p_execute_1_) {
      if (this.shouldDeferTasks()) {
         this.enqueue(this.wrapTask(p_execute_1_));
      } else {
         p_execute_1_.run();
      }

   }

   @OnlyIn(Dist.CLIENT)
   protected void dropTasks() {
      this.queue.clear();
   }

   protected void drainTasks() {
      while(this.driveOne()) {
         ;
      }

   }

   protected boolean driveOne() {
      R r = this.queue.peek();
      if (r == null) {
         return false;
      } else if (this.drivers == 0 && !this.canRun(r)) {
         return false;
      } else {
         this.run((R)(this.queue.remove()));
         return true;
      }
   }

   /**
    * Drive the executor until the given BooleanSupplier returns true
    */
   public void driveUntil(BooleanSupplier isDone) {
      ++this.drivers;

      try {
         while(!isDone.getAsBoolean()) {
            if (!this.driveOne()) {
               this.threadYieldPark();
            }
         }
      } finally {
         --this.drivers;
      }

   }

   protected void threadYieldPark() {
      Thread.yield();
      LockSupport.parkNanos("waiting for tasks", 100000L);
   }

   protected void run(R taskIn) {
      try {
         taskIn.run();
      } catch (Exception exception) {
         LOGGER.fatal("Error executing task on {}", this.getName(), exception);
      }

   }
}