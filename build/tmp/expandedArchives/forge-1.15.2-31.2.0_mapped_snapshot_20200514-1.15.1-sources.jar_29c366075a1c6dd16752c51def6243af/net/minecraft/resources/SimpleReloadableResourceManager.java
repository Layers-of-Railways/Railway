package net.minecraft.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, FallbackResourceManager> namespaceResourceManagers = Maps.newHashMap();
   private final List<IFutureReloadListener> reloadListeners = Lists.newArrayList();
   private final List<IFutureReloadListener> initTaskQueue = Lists.newArrayList();
   private final Set<String> resourceNamespaces = Sets.newLinkedHashSet();
   private final ResourcePackType type;
   private final Thread mainThread;

   public SimpleReloadableResourceManager(ResourcePackType side, Thread p_i50689_2_) {
      this.type = side;
      this.mainThread = p_i50689_2_;
   }

   public void addResourcePack(IResourcePack resourcePack) {
      for(String s : resourcePack.getResourceNamespaces(this.type)) {
         this.resourceNamespaces.add(s);
         FallbackResourceManager fallbackresourcemanager = this.namespaceResourceManagers.get(s);
         if (fallbackresourcemanager == null) {
            fallbackresourcemanager = new FallbackResourceManager(this.type, s);
            this.namespaceResourceManagers.put(s, fallbackresourcemanager);
         }

         fallbackresourcemanager.addResourcePack(resourcePack);
      }

   }

   public Set<String> getResourceNamespaces() {
      return this.resourceNamespaces;
   }

   public IResource getResource(ResourceLocation resourceLocationIn) throws IOException {
      IResourceManager iresourcemanager = this.namespaceResourceManagers.get(resourceLocationIn.getNamespace());
      if (iresourcemanager != null) {
         return iresourcemanager.getResource(resourceLocationIn);
      } else {
         throw new FileNotFoundException(resourceLocationIn.toString());
      }
   }

   public boolean hasResource(ResourceLocation p_219533_1_) {
      IResourceManager iresourcemanager = this.namespaceResourceManagers.get(p_219533_1_.getNamespace());
      return iresourcemanager != null ? iresourcemanager.hasResource(p_219533_1_) : false;
   }

   public List<IResource> getAllResources(ResourceLocation resourceLocationIn) throws IOException {
      IResourceManager iresourcemanager = this.namespaceResourceManagers.get(resourceLocationIn.getNamespace());
      if (iresourcemanager != null) {
         return iresourcemanager.getAllResources(resourceLocationIn);
      } else {
         throw new FileNotFoundException(resourceLocationIn.toString());
      }
   }

   public Collection<ResourceLocation> getAllResourceLocations(String pathIn, Predicate<String> filter) {
      Set<ResourceLocation> set = Sets.newHashSet();

      for(FallbackResourceManager fallbackresourcemanager : this.namespaceResourceManagers.values()) {
         set.addAll(fallbackresourcemanager.getAllResourceLocations(pathIn, filter));
      }

      List<ResourceLocation> list = Lists.newArrayList(set);
      Collections.sort(list);
      return list;
   }

   private void clearResourceNamespaces() {
      this.namespaceResourceManagers.clear();
      this.resourceNamespaces.clear();
   }

   public CompletableFuture<Unit> reloadResourcesAndThen(Executor p_219536_1_, Executor p_219536_2_, List<IResourcePack> p_219536_3_, CompletableFuture<Unit> p_219536_4_) {
      IAsyncReloader iasyncreloader = this.reloadResources(p_219536_1_, p_219536_2_, p_219536_4_, p_219536_3_);
      return iasyncreloader.onceDone();
   }

   public void addReloadListener(IFutureReloadListener p_219534_1_) {
      this.reloadListeners.add(p_219534_1_);
      this.initTaskQueue.add(p_219534_1_);
   }

   protected IAsyncReloader initializeAsyncReloader(Executor backgroundExecutor, Executor gameExecutor, List<IFutureReloadListener> listeners, CompletableFuture<Unit> waitingFor) {
      IAsyncReloader iasyncreloader;
      if (LOGGER.isDebugEnabled()) {
         iasyncreloader = new DebugAsyncReloader(this, Lists.newArrayList(listeners), backgroundExecutor, gameExecutor, waitingFor);
      } else {
         iasyncreloader = AsyncReloader.create(this, Lists.newArrayList(listeners), backgroundExecutor, gameExecutor, waitingFor);
      }

      this.initTaskQueue.clear();
      return iasyncreloader;
   }

   public IAsyncReloader reloadResources(Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> waitingFor, List<IResourcePack> p_219537_4_) {
      this.clearResourceNamespaces();
      LOGGER.info("Reloading ResourceManager: {}", p_219537_4_.stream().map(IResourcePack::getName).collect(Collectors.joining(", ")));

      for(IResourcePack iresourcepack : p_219537_4_) {
         try {
            this.addResourcePack(iresourcepack);
         } catch (Exception exception) {
            LOGGER.error("Failed to add resource pack {}", iresourcepack.getName(), exception);
            return new SimpleReloadableResourceManager.FailedPackReloader(new SimpleReloadableResourceManager.FailedPackException(iresourcepack, exception));
         }
      }

      return this.initializeAsyncReloader(backgroundExecutor, gameExecutor, this.reloadListeners, waitingFor);
   }

   public static class FailedPackException extends RuntimeException {
      private final IResourcePack field_230021_a_;

      public FailedPackException(IResourcePack p_i229962_1_, Throwable p_i229962_2_) {
         super(p_i229962_1_.getName(), p_i229962_2_);
         this.field_230021_a_ = p_i229962_1_;
      }

      @OnlyIn(Dist.CLIENT)
      public IResourcePack func_230028_a() {
         return this.field_230021_a_;
      }
   }

   static class FailedPackReloader implements IAsyncReloader {
      private final SimpleReloadableResourceManager.FailedPackException field_230019_a_;
      private final CompletableFuture<Unit> field_230020_b_;

      public FailedPackReloader(SimpleReloadableResourceManager.FailedPackException p_i229961_1_) {
         this.field_230019_a_ = p_i229961_1_;
         this.field_230020_b_ = new CompletableFuture<>();
         this.field_230020_b_.completeExceptionally(p_i229961_1_);
      }

      public CompletableFuture<Unit> onceDone() {
         return this.field_230020_b_;
      }

      @OnlyIn(Dist.CLIENT)
      public float estimateExecutionSpeed() {
         return 0.0F;
      }

      @OnlyIn(Dist.CLIENT)
      public boolean asyncPartDone() {
         return false;
      }

      @OnlyIn(Dist.CLIENT)
      public boolean fullyDone() {
         return true;
      }

      @OnlyIn(Dist.CLIENT)
      public void join() {
         throw this.field_230019_a_;
      }
   }
}