package net.minecraft.world.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.GameRules;
import net.minecraft.world.TrackedEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkTaskPriorityQueue;
import net.minecraft.world.chunk.ChunkTaskPriorityQueueSorter;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.PlayerGenerationTracker;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.chunk.storage.ChunkSerializer;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SessionLockException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkManager extends ChunkLoader implements ChunkHolder.IPlayerProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final int MAX_LOADED_LEVEL = 33 + ChunkStatus.maxDistance();
   /** Chunks in memory. This should only ever be manipulated by the main thread. */
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> loadedChunks = new Long2ObjectLinkedOpenHashMap<>();
   /** Same as {@link #loadedChunks}, but immutable for access from other threads. <em>This should never be mutated.</em> */
   private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> immutableLoadedChunks = this.loadedChunks.clone();
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> chunksToUnload = new Long2ObjectLinkedOpenHashMap<>();
   /** Chunk positions in this set have fully loaded and their TE's and entities are accessible from the world */
   private final LongSet loadedPositions = new LongOpenHashSet();
   private final ServerWorld world;
   private final ServerWorldLightManager lightManager;
   private final ThreadTaskExecutor<Runnable> mainThread;
   private final ChunkGenerator<?> generator;
   private final Supplier<DimensionSavedDataManager> field_219259_m;
   private final PointOfInterestManager pointOfInterestManager;
   /** Chunks that have been requested to be unloaded, but haven't been unloaded yet. */
   private final LongSet unloadableChunks = new LongOpenHashSet();
   /**
    * True if changes have been made to {@link #loadedChunks} and thus a new copy of the collection has to be made into
    * {@link #immutableLoadedChunks}.
    */
   private boolean immutableLoadedChunksDirty;
   private final ChunkTaskPriorityQueueSorter field_219263_q;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> field_219264_r;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> field_219265_s;
   private final IChunkStatusListener field_219266_t;
   private final ChunkManager.ProxyTicketManager ticketManager;
   private final AtomicInteger field_219268_v = new AtomicInteger();
   private final TemplateManager templateManager;
   private final File dimensionDirectory;
   private final PlayerGenerationTracker playerGenerationTracker = new PlayerGenerationTracker();
   private final Int2ObjectMap<ChunkManager.EntityTracker> entities = new Int2ObjectOpenHashMap<>();
   private final Queue<Runnable> saveTasks = Queues.newConcurrentLinkedQueue();
   private int viewDistance;

   public ChunkManager(ServerWorld worldIn, File worldDirectory, DataFixer p_i51538_3_, TemplateManager templateManagerIn, Executor p_i51538_5_, ThreadTaskExecutor<Runnable> mainThreadIn, IChunkLightProvider p_i51538_7_, ChunkGenerator<?> generatorIn, IChunkStatusListener p_i51538_9_, Supplier<DimensionSavedDataManager> p_i51538_10_, int p_i51538_11_) {
      super(new File(worldIn.getDimension().getType().getDirectory(worldDirectory), "region"), p_i51538_3_);
      this.templateManager = templateManagerIn;
      this.dimensionDirectory = worldIn.getDimension().getType().getDirectory(worldDirectory);
      this.world = worldIn;
      this.generator = generatorIn;
      this.mainThread = mainThreadIn;
      DelegatedTaskExecutor<Runnable> delegatedtaskexecutor = DelegatedTaskExecutor.create(p_i51538_5_, "worldgen");
      ITaskExecutor<Runnable> itaskexecutor = ITaskExecutor.inline("main", mainThreadIn::enqueue);
      this.field_219266_t = p_i51538_9_;
      DelegatedTaskExecutor<Runnable> delegatedtaskexecutor1 = DelegatedTaskExecutor.create(p_i51538_5_, "light");
      this.field_219263_q = new ChunkTaskPriorityQueueSorter(ImmutableList.of(delegatedtaskexecutor, itaskexecutor, delegatedtaskexecutor1), p_i51538_5_, Integer.MAX_VALUE);
      this.field_219264_r = this.field_219263_q.func_219087_a(delegatedtaskexecutor, false);
      this.field_219265_s = this.field_219263_q.func_219087_a(itaskexecutor, false);
      this.lightManager = new ServerWorldLightManager(p_i51538_7_, this, this.world.getDimension().hasSkyLight(), delegatedtaskexecutor1, this.field_219263_q.func_219087_a(delegatedtaskexecutor1, false));
      this.ticketManager = new ChunkManager.ProxyTicketManager(p_i51538_5_, mainThreadIn);
      this.field_219259_m = p_i51538_10_;
      this.pointOfInterestManager = new PointOfInterestManager(new File(this.dimensionDirectory, "poi"), p_i51538_3_);
      this.setViewDistance(p_i51538_11_);
   }

   /**
    * Returns the squared distance to the center of the chunk.
    */
   private static double getDistanceSquaredToChunk(ChunkPos chunkPosIn, Entity entityIn) {
      double d0 = (double)(chunkPosIn.x * 16 + 8);
      double d1 = (double)(chunkPosIn.z * 16 + 8);
      double d2 = d0 - entityIn.getPosX();
      double d3 = d1 - entityIn.getPosZ();
      return d2 * d2 + d3 * d3;
   }

   private static int func_219215_b(ChunkPos pos, ServerPlayerEntity player, boolean p_219215_2_) {
      int i;
      int j;
      if (p_219215_2_) {
         SectionPos sectionpos = player.getManagedSectionPos();
         i = sectionpos.getSectionX();
         j = sectionpos.getSectionZ();
      } else {
         i = MathHelper.floor(player.getPosX() / 16.0D);
         j = MathHelper.floor(player.getPosZ() / 16.0D);
      }

      return getChunkDistance(pos, i, j);
   }

   private static int getChunkDistance(ChunkPos chunkPosIn, int x, int y) {
      int i = chunkPosIn.x - x;
      int j = chunkPosIn.z - y;
      return Math.max(Math.abs(i), Math.abs(j));
   }

   protected ServerWorldLightManager getLightManager() {
      return this.lightManager;
   }

   @Nullable
   protected ChunkHolder func_219220_a(long chunkPosIn) {
      return this.loadedChunks.get(chunkPosIn);
   }

   @Nullable
   protected ChunkHolder func_219219_b(long chunkPosIn) {
      return this.immutableLoadedChunks.get(chunkPosIn);
   }

   protected IntSupplier func_219191_c(long chunkPosIn) {
      return () -> {
         ChunkHolder chunkholder = this.func_219219_b(chunkPosIn);
         return chunkholder == null ? ChunkTaskPriorityQueue.field_219419_a - 1 : Math.min(chunkholder.func_219281_j(), ChunkTaskPriorityQueue.field_219419_a - 1);
      };
   }

   @OnlyIn(Dist.CLIENT)
   public String func_219170_a(ChunkPos pos) {
      ChunkHolder chunkholder = this.func_219219_b(pos.asLong());
      if (chunkholder == null) {
         return "null";
      } else {
         String s = chunkholder.getChunkLevel() + "\n";
         ChunkStatus chunkstatus = chunkholder.func_219285_d();
         IChunk ichunk = chunkholder.func_219287_e();
         if (chunkstatus != null) {
            s = s + "St: \u00a7" + chunkstatus.ordinal() + chunkstatus + '\u00a7' + "r\n";
         }

         if (ichunk != null) {
            s = s + "Ch: \u00a7" + ichunk.getStatus().ordinal() + ichunk.getStatus() + '\u00a7' + "r\n";
         }

         ChunkHolder.LocationType chunkholder$locationtype = chunkholder.func_219300_g();
         s = s + "\u00a7" + chunkholder$locationtype.ordinal() + chunkholder$locationtype;
         return s + '\u00a7' + "r";
      }
   }

   private CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> func_219236_a(ChunkPos pos, int p_219236_2_, IntFunction<ChunkStatus> p_219236_3_) {
      List<CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> list = Lists.newArrayList();
      int i = pos.x;
      int j = pos.z;

      for(int k = -p_219236_2_; k <= p_219236_2_; ++k) {
         for(int l = -p_219236_2_; l <= p_219236_2_; ++l) {
            int i1 = Math.max(Math.abs(l), Math.abs(k));
            final ChunkPos chunkpos = new ChunkPos(i + l, j + k);
            long j1 = chunkpos.asLong();
            ChunkHolder chunkholder = this.func_219220_a(j1);
            if (chunkholder == null) {
               return CompletableFuture.completedFuture(Either.right(new ChunkHolder.IChunkLoadingError() {
                  public String toString() {
                     return "Unloaded " + chunkpos.toString();
                  }
               }));
            }

            ChunkStatus chunkstatus = p_219236_3_.apply(i1);
            CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = chunkholder.func_219276_a(chunkstatus, this);
            list.add(completablefuture);
         }
      }

      CompletableFuture<List<Either<IChunk, ChunkHolder.IChunkLoadingError>>> completablefuture1 = Util.gather(list);
      return completablefuture1.thenApply((p_219227_4_) -> {
         List<IChunk> list1 = Lists.newArrayList();
         int k1 = 0;

         for(final Either<IChunk, ChunkHolder.IChunkLoadingError> either : p_219227_4_) {
            Optional<IChunk> optional = either.left();
            if (!optional.isPresent()) {
               final int l1 = k1;
               return Either.right(new ChunkHolder.IChunkLoadingError() {
                  public String toString() {
                     return "Unloaded " + new ChunkPos(i + l1 % (p_219236_2_ * 2 + 1), j + l1 / (p_219236_2_ * 2 + 1)) + " " + either.right().get().toString();
                  }
               });
            }

            list1.add(optional.get());
            ++k1;
         }

         return Either.left(list1);
      });
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_219188_b(ChunkPos p_219188_1_) {
      return this.func_219236_a(p_219188_1_, 2, (p_219218_0_) -> {
         return ChunkStatus.FULL;
      }).thenApplyAsync((p_219242_0_) -> {
         return p_219242_0_.mapLeft((p_219238_0_) -> {
            return (Chunk)p_219238_0_.get(p_219238_0_.size() / 2);
         });
      }, this.mainThread);
   }

   /**
    * Sets level and loads/unloads chunk. Used by {@link net.minecraft.world.server.ChunkManager.ProxyTicketManager} to
    * set chunk level.
    *  
    * @param holder The {@link net.minecraft.world.server.ChunkHolder} of the chunk if it is loaded, and null otherwise.
    */
   @Nullable
   private ChunkHolder setChunkLevel(long chunkPosIn, int newLevel, @Nullable ChunkHolder holder, int oldLevel) {
      if (oldLevel > MAX_LOADED_LEVEL && newLevel > MAX_LOADED_LEVEL) {
         return holder;
      } else {
         if (holder != null) {
            holder.setChunkLevel(newLevel);
         }

         if (holder != null) {
            if (newLevel > MAX_LOADED_LEVEL) {
               this.unloadableChunks.add(chunkPosIn);
            } else {
               this.unloadableChunks.remove(chunkPosIn);
            }
         }

         if (newLevel <= MAX_LOADED_LEVEL && holder == null) {
            holder = this.chunksToUnload.remove(chunkPosIn);
            if (holder != null) {
               holder.setChunkLevel(newLevel);
            } else {
               holder = new ChunkHolder(new ChunkPos(chunkPosIn), newLevel, this.lightManager, this.field_219263_q, this);
            }

            this.loadedChunks.put(chunkPosIn, holder);
            this.immutableLoadedChunksDirty = true;
         }

         return holder;
      }
   }

   public void close() throws IOException {
      try {
         this.field_219263_q.close();
         this.pointOfInterestManager.close();
      } finally {
         super.close();
      }

   }

   protected void save(boolean flush) {
      if (flush) {
         List<ChunkHolder> list = this.immutableLoadedChunks.values().stream().filter(ChunkHolder::isAccessible).peek(ChunkHolder::updateAccessible).collect(Collectors.toList());
         MutableBoolean mutableboolean = new MutableBoolean();

         while(true) {
            mutableboolean.setFalse();
            list.stream().map((p_222974_1_) -> {
               CompletableFuture<IChunk> completablefuture;
               while(true) {
                  completablefuture = p_222974_1_.func_219302_f();
                  this.mainThread.driveUntil(completablefuture::isDone);
                  if (completablefuture == p_222974_1_.func_219302_f()) {
                     break;
                  }
               }

               return completablefuture.join();
            }).filter((p_222952_0_) -> {
               return p_222952_0_ instanceof ChunkPrimerWrapper || p_222952_0_ instanceof Chunk;
            }).filter(this::func_219229_a).forEach((p_222959_1_) -> {
               mutableboolean.setTrue();
            });
            if (!mutableboolean.isTrue()) {
               break;
            }
         }

         this.scheduleUnloads(() -> {
            return true;
         });
         this.func_227079_i_();
         LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.dimensionDirectory.getName());
      } else {
         this.immutableLoadedChunks.values().stream().filter(ChunkHolder::isAccessible).forEach((p_222965_1_) -> {
            IChunk ichunk = p_222965_1_.func_219302_f().getNow((IChunk)null);
            if (ichunk instanceof ChunkPrimerWrapper || ichunk instanceof Chunk) {
               this.func_219229_a(ichunk);
               p_222965_1_.updateAccessible();
            }

         });
      }

   }

   protected void tick(BooleanSupplier hasMoreTime) {
      IProfiler iprofiler = this.world.getProfiler();
      iprofiler.startSection("poi");
      this.pointOfInterestManager.tick(hasMoreTime);
      iprofiler.endStartSection("chunk_unload");
      if (!this.world.isSaveDisabled()) {
         this.scheduleUnloads(hasMoreTime);
         if (this.loadedChunks.isEmpty()) net.minecraftforge.common.DimensionManager.unloadWorld(this.world);
      }

      iprofiler.endSection();
   }

   private void scheduleUnloads(BooleanSupplier hasMoreTime) {
      LongIterator longiterator = this.unloadableChunks.iterator();

      for(int i = 0; longiterator.hasNext() && (hasMoreTime.getAsBoolean() || i < 200 || this.unloadableChunks.size() > 2000); longiterator.remove()) {
         long j = longiterator.nextLong();
         ChunkHolder chunkholder = this.loadedChunks.remove(j);
         if (chunkholder != null) {
            this.chunksToUnload.put(j, chunkholder);
            this.immutableLoadedChunksDirty = true;
            ++i;
            this.scheduleSave(j, chunkholder);
         }
      }

      Runnable runnable;
      while((hasMoreTime.getAsBoolean() || this.saveTasks.size() > 2000) && (runnable = this.saveTasks.poll()) != null) {
         runnable.run();
      }

   }

   private void scheduleSave(long chunkPosIn, ChunkHolder chunkHolderIn) {
      CompletableFuture<IChunk> completablefuture = chunkHolderIn.func_219302_f();
      completablefuture.thenAcceptAsync((p_219185_5_) -> {
         CompletableFuture<IChunk> completablefuture1 = chunkHolderIn.func_219302_f();
         if (completablefuture1 != completablefuture) {
            this.scheduleSave(chunkPosIn, chunkHolderIn);
         } else {
            if (this.chunksToUnload.remove(chunkPosIn, chunkHolderIn) && p_219185_5_ != null) {
               if (p_219185_5_ instanceof Chunk) {
                  ((Chunk)p_219185_5_).setLoaded(false);
                  net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload((Chunk)p_219185_5_));
               }

               this.func_219229_a(p_219185_5_);
               if (this.loadedPositions.remove(chunkPosIn) && p_219185_5_ instanceof Chunk) {
                  Chunk chunk = (Chunk)p_219185_5_;
                  this.world.onChunkUnloading(chunk);
               }

               this.lightManager.updateChunkStatus(p_219185_5_.getPos());
               this.lightManager.func_215588_z_();
               this.field_219266_t.statusChanged(p_219185_5_.getPos(), (ChunkStatus)null);
            }

         }
      }, this.saveTasks::add).whenComplete((p_223171_1_, p_223171_2_) -> {
         if (p_223171_2_ != null) {
            LOGGER.error("Failed to save chunk " + chunkHolderIn.getPosition(), p_223171_2_);
         }

      });
   }

   protected boolean refreshOffThreadCache() {
      if (!this.immutableLoadedChunksDirty) {
         return false;
      } else {
         this.immutableLoadedChunks = this.loadedChunks.clone();
         this.immutableLoadedChunksDirty = false;
         return true;
      }
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219244_a(ChunkHolder p_219244_1_, ChunkStatus p_219244_2_) {
      ChunkPos chunkpos = p_219244_1_.getPosition();
      if (p_219244_2_ == ChunkStatus.EMPTY) {
         return this.func_223172_f(chunkpos);
      } else {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = p_219244_1_.func_219276_a(p_219244_2_.getParent(), this);
         return completablefuture.thenComposeAsync((p_223180_4_) -> {
            Optional<IChunk> optional = p_223180_4_.left();
            if (!optional.isPresent()) {
               return CompletableFuture.completedFuture(p_223180_4_);
            } else {
               if (p_219244_2_ == ChunkStatus.LIGHT) {
                  this.ticketManager.registerWithLevel(TicketType.LIGHT, chunkpos, 33 + ChunkStatus.getDistance(ChunkStatus.FEATURES), chunkpos);
               }

               IChunk ichunk = optional.get();
               if (ichunk.getStatus().isAtLeast(p_219244_2_)) {
                  CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1;
                  if (p_219244_2_ == ChunkStatus.LIGHT) {
                     completablefuture1 = this.func_223156_b(p_219244_1_, p_219244_2_);
                  } else {
                     completablefuture1 = p_219244_2_.doLoadingWork(this.world, this.templateManager, this.lightManager, (p_223175_2_) -> {
                        return this.func_219200_b(p_219244_1_);
                     }, ichunk);
                  }

                  this.field_219266_t.statusChanged(chunkpos, p_219244_2_);
                  return completablefuture1;
               } else {
                  return this.func_223156_b(p_219244_1_, p_219244_2_);
               }
            }
         }, this.mainThread);
      }
   }

   private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_223172_f(ChunkPos p_223172_1_) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            this.world.getProfiler().func_230035_c_("chunkLoad");
            CompoundNBT compoundnbt = this.loadChunkData(p_223172_1_);
            if (compoundnbt != null) {
               boolean flag = compoundnbt.contains("Level", 10) && compoundnbt.getCompound("Level").contains("Status", 8);
               if (flag) {
                  IChunk ichunk = ChunkSerializer.read(this.world, this.templateManager, this.pointOfInterestManager, p_223172_1_, compoundnbt);
                  ichunk.setLastSaveTime(this.world.getGameTime());
                  return Either.left(ichunk);
               }

               LOGGER.error("Chunk file at {} is missing level data, skipping", (Object)p_223172_1_);
            }
         } catch (ReportedException reportedexception) {
            Throwable throwable = reportedexception.getCause();
            if (!(throwable instanceof IOException)) {
               throw reportedexception;
            }

            LOGGER.error("Couldn't load chunk {}", p_223172_1_, throwable);
         } catch (Exception exception) {
            LOGGER.error("Couldn't load chunk {}", p_223172_1_, exception);
         }

         return Either.left(new ChunkPrimer(p_223172_1_, UpgradeData.EMPTY));
      }, this.mainThread);
   }

   private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_223156_b(ChunkHolder p_223156_1_, ChunkStatus p_223156_2_) {
      ChunkPos chunkpos = p_223156_1_.getPosition();
      CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219236_a(chunkpos, p_223156_2_.getTaskRange(), (p_219195_2_) -> {
         return this.func_219205_a(p_223156_2_, p_219195_2_);
      });
      this.world.getProfiler().func_230036_c_(() -> {
         return "chunkGenerate " + p_223156_2_.getName();
      });
      return completablefuture.thenComposeAsync((p_219235_4_) -> {
         return p_219235_4_.map((p_223148_4_) -> {
            try {
               CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = p_223156_2_.doGenerationWork(this.world, this.generator, this.templateManager, this.lightManager, (p_222954_2_) -> {
                  return this.func_219200_b(p_223156_1_);
               }, p_223148_4_);
               this.field_219266_t.statusChanged(chunkpos, p_223156_2_);
               return completablefuture1;
            } catch (Exception exception) {
               CrashReport crashreport = CrashReport.makeCrashReport(exception, "Exception generating new chunk");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
               crashreportcategory.addDetail("Location", String.format("%d,%d", chunkpos.x, chunkpos.z));
               crashreportcategory.addDetail("Position hash", ChunkPos.asLong(chunkpos.x, chunkpos.z));
               crashreportcategory.addDetail("Generator", this.generator);
               throw new ReportedException(crashreport);
            }
         }, (p_219211_2_) -> {
            this.func_219209_c(chunkpos);
            return CompletableFuture.completedFuture(Either.right(p_219211_2_));
         });
      }, (p_219216_2_) -> {
         this.field_219264_r.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_223156_1_, p_219216_2_));
      });
   }

   protected void func_219209_c(ChunkPos p_219209_1_) {
      this.mainThread.enqueue(Util.namedRunnable(() -> {
         this.ticketManager.releaseWithLevel(TicketType.LIGHT, p_219209_1_, 33 + ChunkStatus.getDistance(ChunkStatus.FEATURES), p_219209_1_);
      }, () -> {
         return "release light ticket " + p_219209_1_;
      }));
   }

   private ChunkStatus func_219205_a(ChunkStatus p_219205_1_, int p_219205_2_) {
      ChunkStatus chunkstatus;
      if (p_219205_2_ == 0) {
         chunkstatus = p_219205_1_.getParent();
      } else {
         chunkstatus = ChunkStatus.getStatus(ChunkStatus.getDistance(p_219205_1_) + p_219205_2_);
      }

      return chunkstatus;
   }

   private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219200_b(ChunkHolder p_219200_1_) {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = p_219200_1_.func_219301_a(ChunkStatus.FULL.getParent());
      return completablefuture.thenApplyAsync((p_219193_2_) -> {
         ChunkStatus chunkstatus = ChunkHolder.getChunkStatusFromLevel(p_219200_1_.getChunkLevel());
         return !chunkstatus.isAtLeast(ChunkStatus.FULL) ? ChunkHolder.MISSING_CHUNK : p_219193_2_.mapLeft((p_219237_2_) -> {
            ChunkPos chunkpos = p_219200_1_.getPosition();
            Chunk chunk;
            if (p_219237_2_ instanceof ChunkPrimerWrapper) {
               chunk = ((ChunkPrimerWrapper)p_219237_2_).func_217336_u();
            } else {
               chunk = new Chunk(this.world, (ChunkPrimer)p_219237_2_);
               p_219200_1_.func_219294_a(new ChunkPrimerWrapper(chunk));
            }

            chunk.setLocationType(() -> {
               return ChunkHolder.getLocationTypeFromLevel(p_219200_1_.getChunkLevel());
            });
            chunk.postLoad();
            if (this.loadedPositions.add(chunkpos.asLong())) {
               chunk.setLoaded(true);
               this.world.addTileEntities(chunk.getTileEntityMap().values());
               List<Entity> list = null;
               ClassInheritanceMultiMap<Entity>[] aclassinheritancemultimap = chunk.getEntityLists();
               int i = aclassinheritancemultimap.length;

               for(int j = 0; j < i; ++j) {
                  for(Entity entity : aclassinheritancemultimap[j]) {
                     if (!(entity instanceof PlayerEntity) && !this.world.addEntityIfNotDuplicate(entity)) {
                        if (list == null) {
                           list = Lists.newArrayList(entity);
                        } else {
                           list.add(entity);
                        }
                     }
                  }
               }

               if (list != null) {
                  list.forEach(chunk::removeEntity);
               }
               net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(chunk));
            }

            return chunk;
         });
      }, (p_219228_2_) -> {
         this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219069_a(p_219228_2_, p_219200_1_.getPosition().asLong(), p_219200_1_::getChunkLevel));
      });
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_219179_a(ChunkHolder p_219179_1_) {
      ChunkPos chunkpos = p_219179_1_.getPosition();
      CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219236_a(chunkpos, 1, (p_219172_0_) -> {
         return ChunkStatus.FULL;
      });
      CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = completablefuture.thenApplyAsync((p_219239_0_) -> {
         return p_219239_0_.flatMap((p_219208_0_) -> {
            Chunk chunk = (Chunk)p_219208_0_.get(p_219208_0_.size() / 2);
            chunk.postProcess();
            return Either.left(chunk);
         });
      }, (p_219230_2_) -> {
         this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_219179_1_, p_219230_2_));
      });
      completablefuture1.thenAcceptAsync((p_219176_2_) -> {
         p_219176_2_.mapLeft((p_219196_2_) -> {
            this.field_219268_v.getAndIncrement();
            IPacket<?>[] ipacket = new IPacket[2];
            this.getTrackingPlayers(chunkpos, false).forEach((p_219233_3_) -> {
               this.sendChunkData(p_219233_3_, ipacket, p_219196_2_);
            });
            return Either.left(p_219196_2_);
         });
      }, (p_219202_2_) -> {
         this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_219179_1_, p_219202_2_));
      });
      return completablefuture1;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_222961_b(ChunkHolder p_222961_1_) {
      return p_222961_1_.func_219276_a(ChunkStatus.FULL, this).thenApplyAsync((p_222976_0_) -> {
         return p_222976_0_.mapLeft((p_222955_0_) -> {
            Chunk chunk = (Chunk)p_222955_0_;
            chunk.rescheduleTicks();
            return chunk;
         });
      }, (p_222962_2_) -> {
         this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_222961_1_, p_222962_2_));
      });
   }

   public int func_219174_c() {
      return this.field_219268_v.get();
   }

   private boolean func_219229_a(IChunk chunkIn) {
      this.pointOfInterestManager.saveIfDirty(chunkIn.getPos());
      if (!chunkIn.isModified()) {
         return false;
      } else {
         try {
            this.world.checkSessionLock();
         } catch (SessionLockException sessionlockexception) {
            LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)sessionlockexception);
            return false;
         }

         chunkIn.setLastSaveTime(this.world.getGameTime());
         chunkIn.setModified(false);
         ChunkPos chunkpos = chunkIn.getPos();

         try {
            ChunkStatus chunkstatus = chunkIn.getStatus();
            if (chunkstatus.getType() != ChunkStatus.Type.LEVELCHUNK) {
               CompoundNBT compoundnbt = this.loadChunkData(chunkpos);
               if (compoundnbt != null && ChunkSerializer.getChunkStatus(compoundnbt) == ChunkStatus.Type.LEVELCHUNK) {
                  return false;
               }

               if (chunkstatus == ChunkStatus.EMPTY && chunkIn.getStructureStarts().values().stream().noneMatch(StructureStart::isValid)) {
                  return false;
               }
            }

            this.world.getProfiler().func_230035_c_("chunkSave");
            CompoundNBT compoundnbt1 = ChunkSerializer.write(this.world, chunkIn);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Save(chunkIn, chunkIn.getWorldForge() != null ? chunkIn.getWorldForge() : this.world, compoundnbt1));
            this.writeChunk(chunkpos, compoundnbt1);
            return true;
         } catch (Exception exception) {
            LOGGER.error("Failed to save chunk {},{}", chunkpos.x, chunkpos.z, exception);
            return false;
         }
      }
   }

   protected void setViewDistance(int viewDistance) {
      int i = MathHelper.clamp(viewDistance + 1, 3, 33);
      if (i != this.viewDistance) {
         int j = this.viewDistance;
         this.viewDistance = i;
         this.ticketManager.setViewDistance(this.viewDistance);

         for(ChunkHolder chunkholder : this.loadedChunks.values()) {
            ChunkPos chunkpos = chunkholder.getPosition();
            IPacket<?>[] ipacket = new IPacket[2];
            this.getTrackingPlayers(chunkpos, false).forEach((p_219224_4_) -> {
               int k = func_219215_b(chunkpos, p_219224_4_, true);
               boolean flag = k <= j;
               boolean flag1 = k <= this.viewDistance;
               this.setChunkLoadedAtClient(p_219224_4_, chunkpos, ipacket, flag, flag1);
            });
         }
      }

   }

   /**
    * Sends the chunk to the client, or tells it to unload it.
    */
   protected void setChunkLoadedAtClient(ServerPlayerEntity player, ChunkPos chunkPosIn, IPacket<?>[] packetCache, boolean wasLoaded, boolean load) {
      if (player.world == this.world) {
         net.minecraftforge.event.ForgeEventFactory.fireChunkWatch(wasLoaded, load, player, chunkPosIn, this.world);
         if (load && !wasLoaded) {
            ChunkHolder chunkholder = this.func_219219_b(chunkPosIn.asLong());
            if (chunkholder != null) {
               Chunk chunk = chunkholder.getChunkIfComplete();
               if (chunk != null) {
                  this.sendChunkData(player, packetCache, chunk);
               }

               DebugPacketSender.sendChuckPos(this.world, chunkPosIn);
            }
         }

         if (!load && wasLoaded) {
            player.sendChunkUnload(chunkPosIn);
         }

      }
   }

   public int getLoadedChunkCount() {
      return this.immutableLoadedChunks.size();
   }

   protected ChunkManager.ProxyTicketManager getTicketManager() {
      return this.ticketManager;
   }

   /**
    * Gets an unmodifiable iterable of all loaded chunks in the chunk manager
    */
   protected Iterable<ChunkHolder> getLoadedChunksIterable() {
      return Iterables.unmodifiableIterable(this.immutableLoadedChunks.values());
   }

   void func_225406_a(Writer p_225406_1_) throws IOException {
      CSVWriter csvwriter = CSVWriter.func_225428_a().func_225423_a("x").func_225423_a("z").func_225423_a("level").func_225423_a("in_memory").func_225423_a("status").func_225423_a("full_status").func_225423_a("accessible_ready").func_225423_a("ticking_ready").func_225423_a("entity_ticking_ready").func_225423_a("ticket").func_225423_a("spawning").func_225423_a("entity_count").func_225423_a("block_entity_count").func_225422_a(p_225406_1_);

      for(Entry<ChunkHolder> entry : this.immutableLoadedChunks.long2ObjectEntrySet()) {
         ChunkPos chunkpos = new ChunkPos(entry.getLongKey());
         ChunkHolder chunkholder = entry.getValue();
         Optional<IChunk> optional = Optional.ofNullable(chunkholder.func_219287_e());
         Optional<Chunk> optional1 = optional.flatMap((p_225407_0_) -> {
            return p_225407_0_ instanceof Chunk ? Optional.of((Chunk)p_225407_0_) : Optional.empty();
         });
         csvwriter.func_225426_a(chunkpos.x, chunkpos.z, chunkholder.getChunkLevel(), optional.isPresent(), optional.map(IChunk::getStatus).orElse((ChunkStatus)null), optional1.map(Chunk::getLocationType).orElse((ChunkHolder.LocationType)null), func_225402_a(chunkholder.getBorderFuture()), func_225402_a(chunkholder.getTickingFuture()), func_225402_a(chunkholder.getEntityTickingFuture()), this.ticketManager.func_225413_c(entry.getLongKey()), !this.isOutsideSpawningRadius(chunkpos), optional1.map((p_225401_0_) -> {
            return Stream.of(p_225401_0_.getEntityLists()).mapToInt(ClassInheritanceMultiMap::size).sum();
         }).orElse(0), optional1.map((p_225405_0_) -> {
            return p_225405_0_.getTileEntityMap().size();
         }).orElse(0));
      }

   }

   private static String func_225402_a(CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> p_225402_0_) {
      try {
         Either<Chunk, ChunkHolder.IChunkLoadingError> either = p_225402_0_.getNow((Either<Chunk, ChunkHolder.IChunkLoadingError>)null);
         return either != null ? either.map((p_225408_0_) -> {
            return "done";
         }, (p_225400_0_) -> {
            return "unloaded";
         }) : "not completed";
      } catch (CompletionException completionexception) {
         return "failed " + completionexception.getCause().getMessage();
      } catch (CancellationException var3) {
         return "cancelled";
      }
   }

   @Nullable
   private CompoundNBT loadChunkData(ChunkPos pos) throws IOException {
      CompoundNBT compoundnbt = this.readChunk(pos);
      return compoundnbt == null ? null : this.updateChunkData(this.world.getDimension().getType(), this.field_219259_m, compoundnbt);
   }

   boolean isOutsideSpawningRadius(ChunkPos chunkPosIn) {
      long i = chunkPosIn.asLong();
      return !this.ticketManager.isOutsideSpawningRadius(i) ? true : this.playerGenerationTracker.getGeneratingPlayers(i).noneMatch((p_219201_1_) -> {
         return !p_219201_1_.isSpectator() && getDistanceSquaredToChunk(chunkPosIn, p_219201_1_) < 16384.0D;
      });
   }

   private boolean cannotGenerateChunks(ServerPlayerEntity player) {
      return player.isSpectator() && !this.world.getGameRules().getBoolean(GameRules.SPECTATORS_GENERATE_CHUNKS);
   }

   void setPlayerTracking(ServerPlayerEntity player, boolean track) {
      boolean flag = this.cannotGenerateChunks(player);
      boolean flag1 = this.playerGenerationTracker.cannotGenerateChunks(player);
      int i = MathHelper.floor(player.getPosX()) >> 4;
      int j = MathHelper.floor(player.getPosZ()) >> 4;
      if (track) {
         this.playerGenerationTracker.addPlayer(ChunkPos.asLong(i, j), player, flag);
         this.func_223489_c(player);
         if (!flag) {
            this.ticketManager.updatePlayerPosition(SectionPos.from(player), player);
         }
      } else {
         SectionPos sectionpos = player.getManagedSectionPos();
         this.playerGenerationTracker.removePlayer(sectionpos.asChunkPos().asLong(), player);
         if (!flag1) {
            this.ticketManager.removePlayer(sectionpos, player);
         }
      }

      for(int l = i - this.viewDistance; l <= i + this.viewDistance; ++l) {
         for(int k = j - this.viewDistance; k <= j + this.viewDistance; ++k) {
            ChunkPos chunkpos = new ChunkPos(l, k);
            this.setChunkLoadedAtClient(player, chunkpos, new IPacket[2], !track, track);
         }
      }

   }

   private SectionPos func_223489_c(ServerPlayerEntity p_223489_1_) {
      SectionPos sectionpos = SectionPos.from(p_223489_1_);
      p_223489_1_.setManagedSectionPos(sectionpos);
      p_223489_1_.connection.sendPacket(new SUpdateChunkPositionPacket(sectionpos.getSectionX(), sectionpos.getSectionZ()));
      return sectionpos;
   }

   public void updatePlayerPosition(ServerPlayerEntity player) {
      for(ChunkManager.EntityTracker chunkmanager$entitytracker : this.entities.values()) {
         if (chunkmanager$entitytracker.entity == player) {
            chunkmanager$entitytracker.updateTrackingState(this.world.getPlayers());
         } else {
            chunkmanager$entitytracker.updateTrackingState(player);
         }
      }

      int l1 = MathHelper.floor(player.getPosX()) >> 4;
      int i2 = MathHelper.floor(player.getPosZ()) >> 4;
      SectionPos sectionpos = player.getManagedSectionPos();
      SectionPos sectionpos1 = SectionPos.from(player);
      long i = sectionpos.asChunkPos().asLong();
      long j = sectionpos1.asChunkPos().asLong();
      boolean flag = this.playerGenerationTracker.func_225419_d(player);
      boolean flag1 = this.cannotGenerateChunks(player);
      boolean flag2 = sectionpos.asLong() != sectionpos1.asLong();
      if (flag2 || flag != flag1) {
         this.func_223489_c(player);
         if (!flag) {
            this.ticketManager.removePlayer(sectionpos, player);
         }

         if (!flag1) {
            this.ticketManager.updatePlayerPosition(sectionpos1, player);
         }

         if (!flag && flag1) {
            this.playerGenerationTracker.disableGeneration(player);
         }

         if (flag && !flag1) {
            this.playerGenerationTracker.enableGeneration(player);
         }

         if (i != j) {
            this.playerGenerationTracker.updatePlayerPosition(i, j, player);
         }
      }

      int k = sectionpos.getSectionX();
      int l = sectionpos.getSectionZ();
      if (Math.abs(k - l1) <= this.viewDistance * 2 && Math.abs(l - i2) <= this.viewDistance * 2) {
         int k2 = Math.min(l1, k) - this.viewDistance;
         int i3 = Math.min(i2, l) - this.viewDistance;
         int j3 = Math.max(l1, k) + this.viewDistance;
         int k3 = Math.max(i2, l) + this.viewDistance;

         for(int l3 = k2; l3 <= j3; ++l3) {
            for(int k1 = i3; k1 <= k3; ++k1) {
               ChunkPos chunkpos1 = new ChunkPos(l3, k1);
               boolean flag5 = getChunkDistance(chunkpos1, k, l) <= this.viewDistance;
               boolean flag6 = getChunkDistance(chunkpos1, l1, i2) <= this.viewDistance;
               this.setChunkLoadedAtClient(player, chunkpos1, new IPacket[2], flag5, flag6);
            }
         }
      } else {
         for(int i1 = k - this.viewDistance; i1 <= k + this.viewDistance; ++i1) {
            for(int j1 = l - this.viewDistance; j1 <= l + this.viewDistance; ++j1) {
               ChunkPos chunkpos = new ChunkPos(i1, j1);
               boolean flag3 = true;
               boolean flag4 = false;
               this.setChunkLoadedAtClient(player, chunkpos, new IPacket[2], true, false);
            }
         }

         for(int j2 = l1 - this.viewDistance; j2 <= l1 + this.viewDistance; ++j2) {
            for(int l2 = i2 - this.viewDistance; l2 <= i2 + this.viewDistance; ++l2) {
               ChunkPos chunkpos2 = new ChunkPos(j2, l2);
               boolean flag7 = false;
               boolean flag8 = true;
               this.setChunkLoadedAtClient(player, chunkpos2, new IPacket[2], false, true);
            }
         }
      }

   }

   /**
    * Returns the players tracking the given chunk.
    */
   public Stream<ServerPlayerEntity> getTrackingPlayers(ChunkPos pos, boolean boundaryOnly) {
      return this.playerGenerationTracker.getGeneratingPlayers(pos.asLong()).filter((p_219192_3_) -> {
         int i = func_219215_b(pos, p_219192_3_, true);
         if (i > this.viewDistance) {
            return false;
         } else {
            return !boundaryOnly || i == this.viewDistance;
         }
      });
   }

   protected void track(Entity entityIn) {
      if (!(entityIn instanceof EnderDragonPartEntity)) {
         if (!(entityIn instanceof LightningBoltEntity)) {
            EntityType<?> entitytype = entityIn.getType();
            int i = entitytype.getTrackingRange() * 16;
            int j = entitytype.getUpdateFrequency();
            if (this.entities.containsKey(entityIn.getEntityId())) {
               throw (IllegalStateException)Util.pauseDevMode(new IllegalStateException("Entity is already tracked!"));
            } else {
               ChunkManager.EntityTracker chunkmanager$entitytracker = new ChunkManager.EntityTracker(entityIn, i, j, entitytype.shouldSendVelocityUpdates());
               this.entities.put(entityIn.getEntityId(), chunkmanager$entitytracker);
               chunkmanager$entitytracker.updateTrackingState(this.world.getPlayers());
               if (entityIn instanceof ServerPlayerEntity) {
                  ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityIn;
                  this.setPlayerTracking(serverplayerentity, true);

                  for(ChunkManager.EntityTracker chunkmanager$entitytracker1 : this.entities.values()) {
                     if (chunkmanager$entitytracker1.entity != serverplayerentity) {
                        chunkmanager$entitytracker1.updateTrackingState(serverplayerentity);
                     }
                  }
               }

            }
         }
      }
   }

   protected void untrack(Entity p_219231_1_) {
      if (p_219231_1_ instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_219231_1_;
         this.setPlayerTracking(serverplayerentity, false);

         for(ChunkManager.EntityTracker chunkmanager$entitytracker : this.entities.values()) {
            chunkmanager$entitytracker.func_219399_a(serverplayerentity);
         }
      }

      ChunkManager.EntityTracker chunkmanager$entitytracker1 = this.entities.remove(p_219231_1_.getEntityId());
      if (chunkmanager$entitytracker1 != null) {
         chunkmanager$entitytracker1.removeAllTrackers();
      }

   }

   protected void tickEntityTracker() {
      List<ServerPlayerEntity> list = Lists.newArrayList();
      List<ServerPlayerEntity> list1 = this.world.getPlayers();

      for(ChunkManager.EntityTracker chunkmanager$entitytracker : this.entities.values()) {
         SectionPos sectionpos = chunkmanager$entitytracker.pos;
         SectionPos sectionpos1 = SectionPos.from(chunkmanager$entitytracker.entity);
         if (!Objects.equals(sectionpos, sectionpos1)) {
            chunkmanager$entitytracker.updateTrackingState(list1);
            Entity entity = chunkmanager$entitytracker.entity;
            if (entity instanceof ServerPlayerEntity) {
               list.add((ServerPlayerEntity)entity);
            }

            chunkmanager$entitytracker.pos = sectionpos1;
         }

         chunkmanager$entitytracker.entry.tick();
      }

      if (!list.isEmpty()) {
         for(ChunkManager.EntityTracker chunkmanager$entitytracker1 : this.entities.values()) {
            chunkmanager$entitytracker1.updateTrackingState(list);
         }
      }

   }

   protected void sendToAllTracking(Entity p_219222_1_, IPacket<?> p_219222_2_) {
      ChunkManager.EntityTracker chunkmanager$entitytracker = this.entities.get(p_219222_1_.getEntityId());
      if (chunkmanager$entitytracker != null) {
         chunkmanager$entitytracker.sendToAllTracking(p_219222_2_);
      }

   }

   protected void sendToTrackingAndSelf(Entity p_219225_1_, IPacket<?> p_219225_2_) {
      ChunkManager.EntityTracker chunkmanager$entitytracker = this.entities.get(p_219225_1_.getEntityId());
      if (chunkmanager$entitytracker != null) {
         chunkmanager$entitytracker.sendToTrackingAndSelf(p_219225_2_);
      }

   }

   private void sendChunkData(ServerPlayerEntity player, IPacket<?>[] packetCache, Chunk chunkIn) {
      if (packetCache[0] == null) {
         packetCache[0] = new SChunkDataPacket(chunkIn, 65535);
         packetCache[1] = new SUpdateLightPacket(chunkIn.getPos(), this.lightManager);
      }

      player.sendChunkLoad(chunkIn.getPos(), packetCache[0], packetCache[1]);
      DebugPacketSender.sendChuckPos(this.world, chunkIn.getPos());
      List<Entity> list = Lists.newArrayList();
      List<Entity> list1 = Lists.newArrayList();

      for(ChunkManager.EntityTracker chunkmanager$entitytracker : this.entities.values()) {
         Entity entity = chunkmanager$entitytracker.entity;
         if (entity != player && entity.chunkCoordX == chunkIn.getPos().x && entity.chunkCoordZ == chunkIn.getPos().z) {
            chunkmanager$entitytracker.updateTrackingState(player);
            if (entity instanceof MobEntity && ((MobEntity)entity).getLeashHolder() != null) {
               list.add(entity);
            }

            if (!entity.getPassengers().isEmpty()) {
               list1.add(entity);
            }
         }
      }

      if (!list.isEmpty()) {
         for(Entity entity1 : list) {
            player.connection.sendPacket(new SMountEntityPacket(entity1, ((MobEntity)entity1).getLeashHolder()));
         }
      }

      if (!list1.isEmpty()) {
         for(Entity entity2 : list1) {
            player.connection.sendPacket(new SSetPassengersPacket(entity2));
         }
      }

   }

   protected PointOfInterestManager getPointOfInterestManager() {
      return this.pointOfInterestManager;
   }

   public CompletableFuture<Void> func_222973_a(Chunk p_222973_1_) {
      return this.mainThread.runAsync(() -> {
         p_222973_1_.saveScheduledTicks(this.world);
      });
   }

   class EntityTracker {
      private final TrackedEntity entry;
      private final Entity entity;
      private final int range;
      private SectionPos pos;
      private final Set<ServerPlayerEntity> trackingPlayers = Sets.newHashSet();

      public EntityTracker(Entity p_i50468_2_, int p_i50468_3_, int p_i50468_4_, boolean p_i50468_5_) {
         this.entry = new TrackedEntity(ChunkManager.this.world, p_i50468_2_, p_i50468_4_, p_i50468_5_, this::sendToAllTracking);
         this.entity = p_i50468_2_;
         this.range = p_i50468_3_;
         this.pos = SectionPos.from(p_i50468_2_);
      }

      public boolean equals(Object p_equals_1_) {
         if (p_equals_1_ instanceof ChunkManager.EntityTracker) {
            return ((ChunkManager.EntityTracker)p_equals_1_).entity.getEntityId() == this.entity.getEntityId();
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.entity.getEntityId();
      }

      public void sendToAllTracking(IPacket<?> p_219391_1_) {
         for(ServerPlayerEntity serverplayerentity : this.trackingPlayers) {
            serverplayerentity.connection.sendPacket(p_219391_1_);
         }

      }

      public void sendToTrackingAndSelf(IPacket<?> p_219392_1_) {
         this.sendToAllTracking(p_219392_1_);
         if (this.entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)this.entity).connection.sendPacket(p_219392_1_);
         }

      }

      public void removeAllTrackers() {
         for(ServerPlayerEntity serverplayerentity : this.trackingPlayers) {
            this.entry.untrack(serverplayerentity);
         }

      }

      public void func_219399_a(ServerPlayerEntity p_219399_1_) {
         if (this.trackingPlayers.remove(p_219399_1_)) {
            this.entry.untrack(p_219399_1_);
         }

      }

      public void updateTrackingState(ServerPlayerEntity p_219400_1_) {
         if (p_219400_1_ != this.entity) {
            Vec3d vec3d = p_219400_1_.getPositionVec().subtract(this.entry.func_219456_b());
            int i = Math.min(this.func_229843_b_(), (ChunkManager.this.viewDistance - 1) * 16);
            boolean flag = vec3d.x >= (double)(-i) && vec3d.x <= (double)i && vec3d.z >= (double)(-i) && vec3d.z <= (double)i && this.entity.isSpectatedByPlayer(p_219400_1_);
            if (flag) {
               boolean flag1 = this.entity.forceSpawn;
               if (!flag1) {
                  ChunkPos chunkpos = new ChunkPos(this.entity.chunkCoordX, this.entity.chunkCoordZ);
                  ChunkHolder chunkholder = ChunkManager.this.func_219219_b(chunkpos.asLong());
                  if (chunkholder != null && chunkholder.getChunkIfComplete() != null) {
                     flag1 = ChunkManager.func_219215_b(chunkpos, p_219400_1_, false) <= ChunkManager.this.viewDistance;
                  }
               }

               if (flag1 && this.trackingPlayers.add(p_219400_1_)) {
                  this.entry.track(p_219400_1_);
               }
            } else if (this.trackingPlayers.remove(p_219400_1_)) {
               this.entry.untrack(p_219400_1_);
            }

         }
      }

      private int func_229843_b_() {
         Collection<Entity> collection = this.entity.getRecursivePassengers();
         int i = this.range;

         for(Entity entity : collection) {
            int j = entity.getType().getTrackingRange() * 16;
            if (j > i) {
               i = j;
            }
         }

         return i;
      }

      public void updateTrackingState(List<ServerPlayerEntity> p_219397_1_) {
         for(ServerPlayerEntity serverplayerentity : p_219397_1_) {
            this.updateTrackingState(serverplayerentity);
         }

      }
   }

   class ProxyTicketManager extends TicketManager {
      protected ProxyTicketManager(Executor p_i50469_2_, Executor p_i50469_3_) {
         super(p_i50469_2_, p_i50469_3_);
      }

      protected boolean contains(long p_219371_1_) {
         return ChunkManager.this.unloadableChunks.contains(p_219371_1_);
      }

      @Nullable
      protected ChunkHolder func_219335_b(long chunkPosIn) {
         return ChunkManager.this.func_219220_a(chunkPosIn);
      }

      @Nullable
      protected ChunkHolder setChunkLevel(long chunkPosIn, int newLevel, @Nullable ChunkHolder holder, int oldLevel) {
         return ChunkManager.this.setChunkLevel(chunkPosIn, newLevel, holder, oldLevel);
      }
   }
}