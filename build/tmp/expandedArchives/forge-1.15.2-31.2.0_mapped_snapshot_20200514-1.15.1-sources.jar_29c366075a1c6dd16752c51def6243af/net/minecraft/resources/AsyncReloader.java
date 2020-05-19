package net.minecraft.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AsyncReloader<S> implements IAsyncReloader {
   protected final IResourceManager resourceManager;
   protected final CompletableFuture<Unit> allAsyncCompleted = new CompletableFuture<>();
   protected final CompletableFuture<List<S>> resultListFuture;
   private final Set<IFutureReloadListener> taskSet;
   private final int taskCount;
   private int syncScheduled;
   private int syncCompleted;
   private final AtomicInteger asyncScheduled = new AtomicInteger();
   private final AtomicInteger asyncCompleted = new AtomicInteger();

   public static AsyncReloader<Void> create(IResourceManager resourceManager, List<IFutureReloadListener> listeners, Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> alsoWaitedFor) {
      return new AsyncReloader<>(backgroundExecutor, gameExecutor, resourceManager, listeners, (p_219561_1_, p_219561_2_, p_219561_3_, p_219561_4_, p_219561_5_) -> {
         return p_219561_3_.reload(p_219561_1_, p_219561_2_, EmptyProfiler.INSTANCE, EmptyProfiler.INSTANCE, backgroundExecutor, p_219561_5_);
      }, alsoWaitedFor);
   }

   protected AsyncReloader(Executor backgroundExecutor, final Executor gameExecutor, IResourceManager resourceManager, List<IFutureReloadListener> listeners, AsyncReloader.IStateFactory<S> stateFactory, CompletableFuture<Unit> alsoWaitedFor) {
      this.resourceManager = resourceManager;
      this.taskCount = listeners.size();
      this.asyncScheduled.incrementAndGet();
      alsoWaitedFor.thenRun(this.asyncCompleted::incrementAndGet);
      List<CompletableFuture<S>> list = Lists.newArrayList();
      CompletableFuture<?> completablefuture = alsoWaitedFor;
      this.taskSet = Sets.newHashSet(listeners);

      for(final IFutureReloadListener ifuturereloadlistener : listeners) {
         final CompletableFuture<?> completablefuture1 = completablefuture;
         CompletableFuture<S> completablefuture2 = stateFactory.create(new IFutureReloadListener.IStage() {
            public <T> CompletableFuture<T> markCompleteAwaitingOthers(T backgroundResult) {
               gameExecutor.execute(() -> {
                  AsyncReloader.this.taskSet.remove(ifuturereloadlistener);
                  if (AsyncReloader.this.taskSet.isEmpty()) {
                     AsyncReloader.this.allAsyncCompleted.complete(Unit.INSTANCE);
                  }

               });
               return AsyncReloader.this.allAsyncCompleted.thenCombine(completablefuture1, (p_216874_1_, p_216874_2_) -> {
                  return backgroundResult;
               });
            }
         }, resourceManager, ifuturereloadlistener, (p_219564_2_) -> {
            this.asyncScheduled.incrementAndGet();
            backgroundExecutor.execute(() -> {
               p_219564_2_.run();
               this.asyncCompleted.incrementAndGet();
            });
         }, (p_219560_2_) -> {
            ++this.syncScheduled;
            gameExecutor.execute(() -> {
               p_219560_2_.run();
               ++this.syncCompleted;
            });
         });
         list.add(completablefuture2);
         completablefuture = completablefuture2;
      }

      this.resultListFuture = Util.gather(list);
   }

   public CompletableFuture<Unit> onceDone() {
      return this.resultListFuture.thenApply((p_219558_0_) -> {
         return Unit.INSTANCE;
      });
   }

   @OnlyIn(Dist.CLIENT)
   public float estimateExecutionSpeed() {
      int i = this.taskCount - this.taskSet.size();
      float f = (float)(this.asyncCompleted.get() * 2 + this.syncCompleted * 2 + i * 1);
      float f1 = (float)(this.asyncScheduled.get() * 2 + this.syncScheduled * 2 + this.taskCount * 1);
      return f / f1;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean asyncPartDone() {
      return this.allAsyncCompleted.isDone();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean fullyDone() {
      return this.resultListFuture.isDone();
   }

   @OnlyIn(Dist.CLIENT)
   public void join() {
      if (this.resultListFuture.isCompletedExceptionally()) {
         this.resultListFuture.join();
      }

   }

   public interface IStateFactory<S> {
      CompletableFuture<S> create(IFutureReloadListener.IStage p_create_1_, IResourceManager p_create_2_, IFutureReloadListener p_create_3_, Executor p_create_4_, Executor p_create_5_);
   }
}