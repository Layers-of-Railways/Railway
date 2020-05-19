package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Unit;

/**
 * @deprecated Forge: {@link net.minecraftforge.resource.ISelectiveResourceReloadListener}, which selectively allows
 * individual resource types being reloaded should rather be used where possible.
 */
@Deprecated
public interface IResourceManagerReloadListener extends IFutureReloadListener {
   default CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
      return stage.markCompleteAwaitingOthers(Unit.INSTANCE).thenRunAsync(() -> {
         this.onResourceManagerReload(resourceManager);
      }, gameExecutor);
   }

   void onResourceManagerReload(IResourceManager resourceManager);

   @javax.annotation.Nullable
   default net.minecraftforge.resource.IResourceType getResourceType() {
      return null;
   }
}