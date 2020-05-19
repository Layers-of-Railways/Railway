package net.minecraft.util.concurrent;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public interface ITaskQueue<T, F> {
   @Nullable
   F poll();

   boolean enqueue(T value);

   boolean isEmpty();

   public static final class Priority implements ITaskQueue<ITaskQueue.RunnableWithPriority, Runnable> {
      private final List<Queue<Runnable>> queues;

      public Priority(int queueCount) {
         this.queues = IntStream.range(0, queueCount).mapToObj((p_219948_0_) -> {
            return Queues.<Runnable>newConcurrentLinkedQueue();
         }).collect(Collectors.toList());
      }

      @Nullable
      public Runnable poll() {
         for(Queue<Runnable> queue : this.queues) {
            Runnable runnable = queue.poll();
            if (runnable != null) {
               return runnable;
            }
         }

         return null;
      }

      public boolean enqueue(ITaskQueue.RunnableWithPriority value) {
         int i = value.getPriority();
         this.queues.get(i).add(value);
         return true;
      }

      public boolean isEmpty() {
         return this.queues.stream().allMatch(Collection::isEmpty);
      }
   }

   public static final class RunnableWithPriority implements Runnable {
      private final int priority;
      private final Runnable runnable;

      public RunnableWithPriority(int priorityIn, Runnable runnableIn) {
         this.priority = priorityIn;
         this.runnable = runnableIn;
      }

      public void run() {
         this.runnable.run();
      }

      public int getPriority() {
         return this.priority;
      }
   }

   public static final class Single<T> implements ITaskQueue<T, T> {
      private final Queue<T> queue;

      public Single(Queue<T> queueIn) {
         this.queue = queueIn;
      }

      @Nullable
      public T poll() {
         return this.queue.poll();
      }

      public boolean enqueue(T value) {
         return this.queue.add(value);
      }

      public boolean isEmpty() {
         return this.queue.isEmpty();
      }
   }
}