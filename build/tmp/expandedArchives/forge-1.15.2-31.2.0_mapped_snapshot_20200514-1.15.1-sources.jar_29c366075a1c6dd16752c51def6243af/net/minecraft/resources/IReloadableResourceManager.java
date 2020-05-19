package net.minecraft.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IReloadableResourceManager extends IResourceManager {
   CompletableFuture<Unit> reloadResourcesAndThen(Executor p_219536_1_, Executor p_219536_2_, List<IResourcePack> p_219536_3_, CompletableFuture<Unit> p_219536_4_);

   @OnlyIn(Dist.CLIENT)
   IAsyncReloader reloadResources(Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> waitingFor, List<IResourcePack> p_219537_4_);

   void addReloadListener(IFutureReloadListener p_219534_1_);
}