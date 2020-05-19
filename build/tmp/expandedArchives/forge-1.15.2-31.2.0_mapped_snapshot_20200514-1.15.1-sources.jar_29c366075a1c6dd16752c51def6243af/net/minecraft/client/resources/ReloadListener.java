package net.minecraft.client.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;

public abstract class ReloadListener<T> implements IFutureReloadListener {
   public final CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
      return CompletableFuture.supplyAsync(() -> {
         return (T)this.prepare(resourceManager, preparationsProfiler);
      }, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync((p_215269_3_) -> {
         this.apply(p_215269_3_, resourceManager, reloadProfiler);
      }, gameExecutor);
   }

   /**
    * Performs any reloading that can be done off-thread, such as file IO
    */
   protected abstract T prepare(IResourceManager resourceManagerIn, IProfiler profilerIn);

   protected abstract void apply(T objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn);
}