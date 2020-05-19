package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private final PriorityQueue<ChunkRenderDispatcher.ChunkRender.ChunkRenderTask> renderTasks = Queues.newPriorityQueue();
   private final Queue<RegionRenderCacheBuilder> freeBuilders;
   private final Queue<Runnable> uploadTasks = Queues.newConcurrentLinkedQueue();
   private volatile int countRenderTasks;
   private volatile int countFreeBuilders;
   private final RegionRenderCacheBuilder fixedBuilder;
   private final DelegatedTaskExecutor<Runnable> delegatedTaskExecutor;
   private final Executor executor;
   private World world;
   private final WorldRenderer worldRenderer;
   private Vec3d renderPosition = Vec3d.ZERO;

   public ChunkRenderDispatcher(World worldIn, WorldRenderer worldRendererIn, Executor executorIn, boolean java64bit, RegionRenderCacheBuilder fixedBuilderIn) {
      this(worldIn, worldRendererIn, executorIn, java64bit, fixedBuilderIn, -1);
   }
   public ChunkRenderDispatcher(World worldIn, WorldRenderer worldRendererIn, Executor executorIn, boolean java64bit, RegionRenderCacheBuilder fixedBuilderIn, int countRenderBuilders) {
      this.world = worldIn;
      this.worldRenderer = worldRendererIn;
      int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / (RenderType.getBlockRenderTypes().stream().mapToInt(RenderType::getBufferSize).sum() * 4) - 1);
      int j = Runtime.getRuntime().availableProcessors();
      int k = java64bit ? j : Math.min(j, 4);
      int l = countRenderBuilders < 0 ? Math.max(1, Math.min(k, i)) : countRenderBuilders;
      this.fixedBuilder = fixedBuilderIn;
      List<RegionRenderCacheBuilder> list = Lists.newArrayListWithExpectedSize(l);

      try {
         for(int i1 = 0; i1 < l; ++i1) {
            list.add(new RegionRenderCacheBuilder());
         }
      } catch (OutOfMemoryError var14) {
         LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
         int j1 = Math.min(list.size() * 2 / 3, list.size() - 1);

         for(int k1 = 0; k1 < j1; ++k1) {
            list.remove(list.size() - 1);
         }

         System.gc();
      }

      this.freeBuilders = Queues.newArrayDeque(list);
      this.countFreeBuilders = this.freeBuilders.size();
      this.executor = executorIn;
      this.delegatedTaskExecutor = DelegatedTaskExecutor.create(executorIn, "Chunk Renderer");
      this.delegatedTaskExecutor.enqueue(this::runTask);
   }

   public void setWorld(World worldIn) {
      this.world = worldIn;
   }

   private void runTask() {
      if (!this.freeBuilders.isEmpty()) {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.renderTasks.poll();
         if (chunkrenderdispatcher$chunkrender$chunkrendertask != null) {
            RegionRenderCacheBuilder regionrendercachebuilder = this.freeBuilders.poll();
            this.countRenderTasks = this.renderTasks.size();
            this.countFreeBuilders = this.freeBuilders.size();
            CompletableFuture.runAsync(() -> {
            }, this.executor).thenCompose((p_228901_2_) -> {
               return chunkrenderdispatcher$chunkrender$chunkrendertask.execute(regionrendercachebuilder);
            }).whenComplete((p_228898_2_, p_228898_3_) -> {
               if (p_228898_3_ != null) {
                  CrashReport crashreport = CrashReport.makeCrashReport(p_228898_3_, "Batching chunks");
                  Minecraft.getInstance().crashed(Minecraft.getInstance().addGraphicsAndWorldToCrashReport(crashreport));
               } else {
                  this.delegatedTaskExecutor.enqueue(() -> {
                     if (p_228898_2_ == ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL) {
                        regionrendercachebuilder.resetBuilders();
                     } else {
                        regionrendercachebuilder.discardBuilders();
                     }

                     this.freeBuilders.add(regionrendercachebuilder);
                     this.countFreeBuilders = this.freeBuilders.size();
                     this.runTask();
                  });
               }
            });
         }
      }
   }

   public String getDebugInfo() {
      return String.format("pC: %03d, pU: %02d, aB: %02d", this.countRenderTasks, this.uploadTasks.size(), this.countFreeBuilders);
   }

   public void setRenderPosition(Vec3d posIn) {
      this.renderPosition = posIn;
   }

   public Vec3d getRenderPosition() {
      return this.renderPosition;
   }

   public boolean runChunkUploads() {
      boolean flag;
      Runnable runnable;
      for(flag = false; (runnable = this.uploadTasks.poll()) != null; flag = true) {
         runnable.run();
      }

      return flag;
   }

   public void rebuildChunk(ChunkRenderDispatcher.ChunkRender chunkRenderIn) {
      chunkRenderIn.rebuildChunk();
   }

   public void stopChunkUpdates() {
      this.clearChunkUpdates();
   }

   public void schedule(ChunkRenderDispatcher.ChunkRender.ChunkRenderTask renderTaskIn) {
      this.delegatedTaskExecutor.enqueue(() -> {
         this.renderTasks.offer(renderTaskIn);
         this.countRenderTasks = this.renderTasks.size();
         this.runTask();
      });
   }

   public CompletableFuture<Void> uploadChunkLayer(BufferBuilder bufferBuilderIn, VertexBuffer vertexBufferIn) {
      return CompletableFuture.runAsync(() -> {
      }, this.uploadTasks::add).thenCompose((p_228897_3_) -> {
         return this.uploadChunkLayerRaw(bufferBuilderIn, vertexBufferIn);
      });
   }

   private CompletableFuture<Void> uploadChunkLayerRaw(BufferBuilder bufferBuilderIn, VertexBuffer vertexBufferIn) {
      return vertexBufferIn.uploadLater(bufferBuilderIn);
   }

   private void clearChunkUpdates() {
      while(!this.renderTasks.isEmpty()) {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.renderTasks.poll();
         if (chunkrenderdispatcher$chunkrender$chunkrendertask != null) {
            chunkrenderdispatcher$chunkrender$chunkrendertask.cancel();
         }
      }

      this.countRenderTasks = 0;
   }

   public boolean hasNoChunkUpdates() {
      return this.countRenderTasks == 0 && this.uploadTasks.isEmpty();
   }

   public void stopWorkerThreads() {
      this.clearChunkUpdates();
      this.delegatedTaskExecutor.close();
      this.freeBuilders.clear();
   }

   @OnlyIn(Dist.CLIENT)
   public class ChunkRender implements net.minecraftforge.client.extensions.IForgeRenderChunk {
      public final AtomicReference<ChunkRenderDispatcher.CompiledChunk> compiledChunk = new AtomicReference<>(ChunkRenderDispatcher.CompiledChunk.DUMMY);
      @Nullable
      private ChunkRenderDispatcher.ChunkRender.RebuildTask lastRebuildTask;
      @Nullable
      private ChunkRenderDispatcher.ChunkRender.SortTransparencyTask lastResortTransparencyTask;
      private final Set<TileEntity> globalTileEntities = Sets.newHashSet();
      private final Map<RenderType, VertexBuffer> vertexBuffers = RenderType.getBlockRenderTypes().stream().collect(Collectors.toMap((p_228934_0_) -> {
         return p_228934_0_;
      }, (p_228933_0_) -> {
         return new VertexBuffer(DefaultVertexFormats.BLOCK);
      }));
      public AxisAlignedBB boundingBox;
      private int frameIndex = -1;
      private boolean needsUpdate = true;
      private final BlockPos.Mutable position = new BlockPos.Mutable(-1, -1, -1);
      private final BlockPos.Mutable[] mapEnumFacing = Util.make(new BlockPos.Mutable[6], (p_228932_0_) -> {
         for(int i = 0; i < p_228932_0_.length; ++i) {
            p_228932_0_[i] = new BlockPos.Mutable();
         }

      });
      private boolean needsImmediateUpdate;

      private boolean isChunkLoaded(BlockPos blockPosIn) {
         return ChunkRenderDispatcher.this.world.getChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4, ChunkStatus.FULL, false) != null;
      }

      public boolean shouldStayLoaded() {
         int i = 24;
         if (!(this.getDistanceSq() > 576.0D)) {
            return true;
         } else {
            return this.isChunkLoaded(this.mapEnumFacing[Direction.WEST.ordinal()]) && this.isChunkLoaded(this.mapEnumFacing[Direction.NORTH.ordinal()]) && this.isChunkLoaded(this.mapEnumFacing[Direction.EAST.ordinal()]) && this.isChunkLoaded(this.mapEnumFacing[Direction.SOUTH.ordinal()]);
         }
      }

      public boolean setFrameIndex(int frameIndexIn) {
         if (this.frameIndex == frameIndexIn) {
            return false;
         } else {
            this.frameIndex = frameIndexIn;
            return true;
         }
      }

      public VertexBuffer getVertexBuffer(RenderType renderTypeIn) {
         return this.vertexBuffers.get(renderTypeIn);
      }

      /**
       * Sets the RenderChunk base position
       */
      public void setPosition(int x, int y, int z) {
         if (x != this.position.getX() || y != this.position.getY() || z != this.position.getZ()) {
            this.stopCompileTask();
            this.position.setPos(x, y, z);
            this.boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 16), (double)(y + 16), (double)(z + 16));

            for(Direction direction : Direction.values()) {
               this.mapEnumFacing[direction.ordinal()].setPos(this.position).move(direction, 16);
            }

         }
      }

      protected double getDistanceSq() {
         ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
         double d0 = this.boundingBox.minX + 8.0D - activerenderinfo.getProjectedView().x;
         double d1 = this.boundingBox.minY + 8.0D - activerenderinfo.getProjectedView().y;
         double d2 = this.boundingBox.minZ + 8.0D - activerenderinfo.getProjectedView().z;
         return d0 * d0 + d1 * d1 + d2 * d2;
      }

      private void beginLayer(BufferBuilder bufferBuilderIn) {
         bufferBuilderIn.begin(7, DefaultVertexFormats.BLOCK);
      }

      public ChunkRenderDispatcher.CompiledChunk getCompiledChunk() {
         return this.compiledChunk.get();
      }

      private void stopCompileTask() {
         this.stopTasks();
         this.compiledChunk.set(ChunkRenderDispatcher.CompiledChunk.DUMMY);
         this.needsUpdate = true;
      }

      public void deleteGlResources() {
         this.stopCompileTask();
         this.vertexBuffers.values().forEach(VertexBuffer::close);
      }

      public BlockPos getPosition() {
         return this.position;
      }

      public void setNeedsUpdate(boolean immediate) {
         boolean flag = this.needsUpdate;
         this.needsUpdate = true;
         this.needsImmediateUpdate = immediate | (flag && this.needsImmediateUpdate);
      }

      public void clearNeedsUpdate() {
         this.needsUpdate = false;
         this.needsImmediateUpdate = false;
      }

      public boolean needsUpdate() {
         return this.needsUpdate;
      }

      public boolean needsImmediateUpdate() {
         return this.needsUpdate && this.needsImmediateUpdate;
      }

      public BlockPos getBlockPosOffset16(Direction facing) {
         return this.mapEnumFacing[facing.ordinal()];
      }

      public boolean resortTransparency(RenderType renderTypeIn, ChunkRenderDispatcher renderDispatcherIn) {
         ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = this.getCompiledChunk();
         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
         }

         if (!chunkrenderdispatcher$compiledchunk.layersStarted.contains(renderTypeIn)) {
            return false;
         } else {
            this.lastResortTransparencyTask = new ChunkRenderDispatcher.ChunkRender.SortTransparencyTask(new net.minecraft.util.math.ChunkPos(getPosition()), this.getDistanceSq(), chunkrenderdispatcher$compiledchunk);
            renderDispatcherIn.schedule(this.lastResortTransparencyTask);
            return true;
         }
      }

      protected void stopTasks() {
         if (this.lastRebuildTask != null) {
            this.lastRebuildTask.cancel();
            this.lastRebuildTask = null;
         }

         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
            this.lastResortTransparencyTask = null;
         }

      }

      public ChunkRenderDispatcher.ChunkRender.ChunkRenderTask makeCompileTaskChunk() {
         this.stopTasks();
         BlockPos blockpos = this.position.toImmutable();
         int i = 1;
         ChunkRenderCache chunkrendercache = createRegionRenderCache(ChunkRenderDispatcher.this.world, blockpos.add(-1, -1, -1), blockpos.add(16, 16, 16), 1);
         this.lastRebuildTask = new ChunkRenderDispatcher.ChunkRender.RebuildTask(new net.minecraft.util.math.ChunkPos(getPosition()), this.getDistanceSq(), chunkrendercache);
         return this.lastRebuildTask;
      }

      public void rebuildChunkLater(ChunkRenderDispatcher dispatcherIn) {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.makeCompileTaskChunk();
         dispatcherIn.schedule(chunkrenderdispatcher$chunkrender$chunkrendertask);
      }

      private void updateGlobalTileEntities(Set<TileEntity> globalEntitiesIn) {
         Set<TileEntity> set = Sets.newHashSet(globalEntitiesIn);
         Set<TileEntity> set1 = Sets.newHashSet(this.globalTileEntities);
         set.removeAll(this.globalTileEntities);
         set1.removeAll(globalEntitiesIn);
         this.globalTileEntities.clear();
         this.globalTileEntities.addAll(globalEntitiesIn);
         ChunkRenderDispatcher.this.worldRenderer.updateTileEntities(set1, set);
      }

      public void rebuildChunk() {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.makeCompileTaskChunk();
         chunkrenderdispatcher$chunkrender$chunkrendertask.execute(ChunkRenderDispatcher.this.fixedBuilder);
      }

      @OnlyIn(Dist.CLIENT)
      abstract class ChunkRenderTask implements Comparable<ChunkRenderDispatcher.ChunkRender.ChunkRenderTask> {
         protected final double distanceSq;
         protected final AtomicBoolean finished = new AtomicBoolean(false);
         protected java.util.Map<net.minecraft.util.math.BlockPos, net.minecraftforge.client.model.data.IModelData> modelData;

         public ChunkRenderTask(double distanceSqIn) {
            this(null, distanceSqIn);
         }

         public ChunkRenderTask(@Nullable net.minecraft.util.math.ChunkPos pos, double distanceSqIn) {
            this.distanceSq = distanceSqIn;
            if (pos == null) {
                this.modelData = java.util.Collections.emptyMap();
            } else {
                this.modelData = net.minecraftforge.client.model.ModelDataManager.getModelData(net.minecraft.client.Minecraft.getInstance().world, pos);
            }
         }

         public abstract CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> execute(RegionRenderCacheBuilder builderIn);

         public abstract void cancel();

         public int compareTo(ChunkRenderDispatcher.ChunkRender.ChunkRenderTask p_compareTo_1_) {
            return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
         }

         public net.minecraftforge.client.model.data.IModelData getModelData(net.minecraft.util.math.BlockPos pos) {
            return modelData.getOrDefault(pos, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
         }
      }

      @OnlyIn(Dist.CLIENT)
      class RebuildTask extends ChunkRenderDispatcher.ChunkRender.ChunkRenderTask {
         @Nullable
         protected ChunkRenderCache chunkRenderCache;

         @Deprecated
         public RebuildTask(double distanceSqIn, @Nullable ChunkRenderCache renderCacheIn) {
            this(null, distanceSqIn, renderCacheIn);
         }

         public RebuildTask(@Nullable net.minecraft.util.math.ChunkPos pos, double distanceSqIn, @Nullable ChunkRenderCache renderCacheIn) {
            super(pos, distanceSqIn);
            this.chunkRenderCache = renderCacheIn;
         }

         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> execute(RegionRenderCacheBuilder builderIn) {
            if (this.finished.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!ChunkRender.this.shouldStayLoaded()) {
               this.chunkRenderCache = null;
               ChunkRender.this.setNeedsUpdate(false);
               this.finished.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.finished.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vec3d vec3d = ChunkRenderDispatcher.this.getRenderPosition();
               float f = (float)vec3d.x;
               float f1 = (float)vec3d.y;
               float f2 = (float)vec3d.z;
               ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = new ChunkRenderDispatcher.CompiledChunk();
               Set<TileEntity> set = this.compile(f, f1, f2, chunkrenderdispatcher$compiledchunk, builderIn);
               ChunkRender.this.updateGlobalTileEntities(set);
               if (this.finished.get()) {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               } else {
                  List<CompletableFuture<Void>> list = Lists.newArrayList();
                  chunkrenderdispatcher$compiledchunk.layersStarted.forEach((p_228943_3_) -> {
                     list.add(ChunkRenderDispatcher.this.uploadChunkLayer(builderIn.getBuilder(p_228943_3_), ChunkRender.this.getVertexBuffer(p_228943_3_)));
                  });
                  return Util.gather(list).handle((p_228941_2_, p_228941_3_) -> {
                     if (p_228941_3_ != null && !(p_228941_3_ instanceof CancellationException) && !(p_228941_3_ instanceof InterruptedException)) {
                        Minecraft.getInstance().crashed(CrashReport.makeCrashReport(p_228941_3_, "Rendering chunk"));
                     }

                     if (this.finished.get()) {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     } else {
                        ChunkRender.this.compiledChunk.set(chunkrenderdispatcher$compiledchunk);
                        return ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     }
                  });
               }
            }
         }

         private Set<TileEntity> compile(float xIn, float yIn, float zIn, ChunkRenderDispatcher.CompiledChunk compiledChunkIn, RegionRenderCacheBuilder builderIn) {
            int i = 1;
            BlockPos blockpos = ChunkRender.this.position.toImmutable();
            BlockPos blockpos1 = blockpos.add(15, 15, 15);
            VisGraph visgraph = new VisGraph();
            Set<TileEntity> set = Sets.newHashSet();
            ChunkRenderCache chunkrendercache = this.chunkRenderCache;
            this.chunkRenderCache = null;
            MatrixStack matrixstack = new MatrixStack();
            if (chunkrendercache != null) {
               BlockModelRenderer.enableCache();
               Random random = new Random();
               BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

               for(BlockPos blockpos2 : BlockPos.getAllInBoxMutable(blockpos, blockpos1)) {
                  BlockState blockstate = chunkrendercache.getBlockState(blockpos2);
                  Block block = blockstate.getBlock();
                  if (blockstate.isOpaqueCube(chunkrendercache, blockpos2)) {
                     visgraph.setOpaqueCube(blockpos2);
                  }

                  if (blockstate.hasTileEntity()) {
                     TileEntity tileentity = chunkrendercache.getTileEntity(blockpos2, Chunk.CreateEntityType.CHECK);
                     if (tileentity != null) {
                        this.handleTileEntity(compiledChunkIn, set, tileentity);
                     }
                  }

                  IFluidState ifluidstate = chunkrendercache.getFluidState(blockpos2);
                  net.minecraftforge.client.model.data.IModelData modelData = getModelData(blockpos2);
                  for (RenderType rendertype : RenderType.getBlockRenderTypes()) {
                      net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype);
                  if (!ifluidstate.isEmpty() && RenderTypeLookup.canRenderInLayer(ifluidstate, rendertype)) {
                     BufferBuilder bufferbuilder = builderIn.getBuilder(rendertype);
                     if (compiledChunkIn.layersStarted.add(rendertype)) {
                        ChunkRender.this.beginLayer(bufferbuilder);
                     }

                     if (blockrendererdispatcher.renderFluid(blockpos2, chunkrendercache, bufferbuilder, ifluidstate)) {
                        compiledChunkIn.empty = false;
                        compiledChunkIn.layersUsed.add(rendertype);
                     }
                  }

                  if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && RenderTypeLookup.canRenderInLayer(blockstate, rendertype)) {
                     RenderType rendertype1 = rendertype;
                     BufferBuilder bufferbuilder2 = builderIn.getBuilder(rendertype1);
                     if (compiledChunkIn.layersStarted.add(rendertype1)) {
                        ChunkRender.this.beginLayer(bufferbuilder2);
                     }

                     matrixstack.push();
                     matrixstack.translate((double)(blockpos2.getX() & 15), (double)(blockpos2.getY() & 15), (double)(blockpos2.getZ() & 15));
                     if (blockrendererdispatcher.renderModel(blockstate, blockpos2, chunkrendercache, matrixstack, bufferbuilder2, true, random, modelData)) {
                        compiledChunkIn.empty = false;
                        compiledChunkIn.layersUsed.add(rendertype1);
                     }

                     matrixstack.pop();
                  }
               }
               }
               net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);

               if (compiledChunkIn.layersUsed.contains(RenderType.getTranslucent())) {
                  BufferBuilder bufferbuilder1 = builderIn.getBuilder(RenderType.getTranslucent());
                  bufferbuilder1.sortVertexData(xIn - (float)blockpos.getX(), yIn - (float)blockpos.getY(), zIn - (float)blockpos.getZ());
                  compiledChunkIn.state = bufferbuilder1.getVertexState();
               }

               compiledChunkIn.layersStarted.stream().map(builderIn::getBuilder).forEach(BufferBuilder::finishDrawing);
               BlockModelRenderer.disableCache();
            }

            compiledChunkIn.setVisibility = visgraph.computeVisibility();
            return set;
         }

         private <E extends TileEntity> void handleTileEntity(ChunkRenderDispatcher.CompiledChunk compiledChunkIn, Set<TileEntity> tileEntitiesIn, E tileEntityIn) {
            TileEntityRenderer<E> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileEntityIn);
            if (tileentityrenderer != null) {
               if (tileentityrenderer.isGlobalRenderer(tileEntityIn)) {
                  tileEntitiesIn.add(tileEntityIn);
               }
               else compiledChunkIn.tileEntities.add(tileEntityIn); //FORGE: Fix MC-112730
            }

         }

         public void cancel() {
            this.chunkRenderCache = null;
            if (this.finished.compareAndSet(false, true)) {
               ChunkRender.this.setNeedsUpdate(false);
            }

         }
      }

      @OnlyIn(Dist.CLIENT)
      class SortTransparencyTask extends ChunkRenderDispatcher.ChunkRender.ChunkRenderTask {
         private final ChunkRenderDispatcher.CompiledChunk sortCompiledChunk;

         @Deprecated
         public SortTransparencyTask(double distanceSqIn, ChunkRenderDispatcher.CompiledChunk compiledChunkIn) {
            this(null, distanceSqIn, compiledChunkIn);
         }

         public SortTransparencyTask(@Nullable net.minecraft.util.math.ChunkPos pos, double distanceSqIn, ChunkRenderDispatcher.CompiledChunk compiledChunkIn) {
            super(pos, distanceSqIn);
            this.sortCompiledChunk = compiledChunkIn;
         }

         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> execute(RegionRenderCacheBuilder builderIn) {
            if (this.finished.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!ChunkRender.this.shouldStayLoaded()) {
               this.finished.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.finished.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vec3d vec3d = ChunkRenderDispatcher.this.getRenderPosition();
               float f = (float)vec3d.x;
               float f1 = (float)vec3d.y;
               float f2 = (float)vec3d.z;
               BufferBuilder.State bufferbuilder$state = this.sortCompiledChunk.state;
               if (bufferbuilder$state != null && this.sortCompiledChunk.layersUsed.contains(RenderType.getTranslucent())) {
                  BufferBuilder bufferbuilder = builderIn.getBuilder(RenderType.getTranslucent());
                  ChunkRender.this.beginLayer(bufferbuilder);
                  bufferbuilder.setVertexState(bufferbuilder$state);
                  bufferbuilder.sortVertexData(f - (float)ChunkRender.this.position.getX(), f1 - (float)ChunkRender.this.position.getY(), f2 - (float)ChunkRender.this.position.getZ());
                  this.sortCompiledChunk.state = bufferbuilder.getVertexState();
                  bufferbuilder.finishDrawing();
                  if (this.finished.get()) {
                     return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                  } else {
                     CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> completablefuture = ChunkRenderDispatcher.this.uploadChunkLayer(builderIn.getBuilder(RenderType.getTranslucent()), ChunkRender.this.getVertexBuffer(RenderType.getTranslucent())).thenApply((p_228947_0_) -> {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     });
                     return completablefuture.handle((p_228946_1_, p_228946_2_) -> {
                        if (p_228946_2_ != null && !(p_228946_2_ instanceof CancellationException) && !(p_228946_2_ instanceof InterruptedException)) {
                           Minecraft.getInstance().crashed(CrashReport.makeCrashReport(p_228946_2_, "Rendering chunk"));
                        }

                        return this.finished.get() ? ChunkRenderDispatcher.ChunkTaskResult.CANCELLED : ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     });
                  }
               } else {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               }
            }
         }

         public void cancel() {
            this.finished.set(true);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum ChunkTaskResult {
      SUCCESSFUL,
      CANCELLED;
   }

   @OnlyIn(Dist.CLIENT)
   public static class CompiledChunk {
      public static final ChunkRenderDispatcher.CompiledChunk DUMMY = new ChunkRenderDispatcher.CompiledChunk() {
         public boolean isVisible(Direction facing, Direction facing2) {
            return false;
         }
      };
      private final Set<RenderType> layersUsed = new ObjectArraySet<>();
      private final Set<RenderType> layersStarted = new ObjectArraySet<>();
      private boolean empty = true;
      private final List<TileEntity> tileEntities = Lists.newArrayList();
      private SetVisibility setVisibility = new SetVisibility();
      @Nullable
      private BufferBuilder.State state;

      public boolean isEmpty() {
         return this.empty;
      }

      public boolean isLayerEmpty(RenderType renderTypeIn) {
         return !this.layersUsed.contains(renderTypeIn);
      }

      public List<TileEntity> getTileEntities() {
         return this.tileEntities;
      }

      public boolean isVisible(Direction facing, Direction facing2) {
         return this.setVisibility.isVisible(facing, facing2);
      }
   }
}