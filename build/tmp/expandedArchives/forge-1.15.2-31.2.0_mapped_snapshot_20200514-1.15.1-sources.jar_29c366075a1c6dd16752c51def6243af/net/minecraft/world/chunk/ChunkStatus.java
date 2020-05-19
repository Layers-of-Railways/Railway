package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.ServerWorldLightManager;

public class ChunkStatus extends net.minecraftforge.registries.ForgeRegistryEntry<ChunkStatus> {
   private static final EnumSet<Heightmap.Type> PRE_FEATURES = EnumSet.of(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
   private static final EnumSet<Heightmap.Type> POST_FEATURES = EnumSet.of(Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE, Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
   private static final ChunkStatus.ILoadingWorker NOOP_LOADING_WORKER = (p_222588_0_, p_222588_1_, p_222588_2_, p_222588_3_, p_222588_4_, p_222588_5_) -> {
      if (p_222588_5_ instanceof ChunkPrimer && !p_222588_5_.getStatus().isAtLeast(p_222588_0_)) {
         ((ChunkPrimer)p_222588_5_).setStatus(p_222588_0_);
      }

      return CompletableFuture.completedFuture(Either.left(p_222588_5_));
   };
   public static final ChunkStatus EMPTY = registerSelective("empty", (ChunkStatus)null, -1, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_223194_0_, p_223194_1_, p_223194_2_, p_223194_3_) -> {
   });
   public static final ChunkStatus STRUCTURE_STARTS = register("structure_starts", EMPTY, 0, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222586_0_, p_222586_1_, p_222586_2_, p_222586_3_, p_222586_4_, p_222586_5_, p_222586_6_, p_222586_7_) -> {
      if (!p_222586_7_.getStatus().isAtLeast(p_222586_0_)) {
         if (p_222586_1_.getWorldInfo().isMapFeaturesEnabled()) {
            p_222586_2_.generateStructures(p_222586_1_.getBiomeManager().copyWithProvider(p_222586_2_.getBiomeProvider()), p_222586_7_, p_222586_2_, p_222586_3_);
         }

         if (p_222586_7_ instanceof ChunkPrimer) {
            ((ChunkPrimer)p_222586_7_).setStatus(p_222586_0_);
         }
      }

      return CompletableFuture.completedFuture(Either.left(p_222586_7_));
   });
   public static final ChunkStatus STRUCTURE_REFERENCES = registerSelective("structure_references", STRUCTURE_STARTS, 8, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222587_0_, p_222587_1_, p_222587_2_, p_222587_3_) -> {
      p_222587_1_.generateStructureStarts(new WorldGenRegion(p_222587_0_, p_222587_2_), p_222587_3_);
   });
   public static final ChunkStatus BIOMES = registerSelective("biomes", STRUCTURE_REFERENCES, 0, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222594_0_, p_222594_1_, p_222594_2_, p_222594_3_) -> {
      p_222594_1_.generateBiomes(p_222594_3_);
   });
   public static final ChunkStatus NOISE = registerSelective("noise", BIOMES, 8, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222592_0_, p_222592_1_, p_222592_2_, p_222592_3_) -> {
      p_222592_1_.makeBase(new WorldGenRegion(p_222592_0_, p_222592_2_), p_222592_3_);
   });
   public static final ChunkStatus SURFACE = registerSelective("surface", NOISE, 0, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222589_0_, p_222589_1_, p_222589_2_, p_222589_3_) -> {
      p_222589_1_.generateSurface(new WorldGenRegion(p_222589_0_, p_222589_2_), p_222589_3_);
   });
   public static final ChunkStatus CARVERS = registerSelective("carvers", SURFACE, 0, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222590_0_, p_222590_1_, p_222590_2_, p_222590_3_) -> {
      p_222590_1_.func_225550_a_(p_222590_0_.getBiomeManager().copyWithProvider(p_222590_1_.getBiomeProvider()), p_222590_3_, GenerationStage.Carving.AIR);
   });
   public static final ChunkStatus LIQUID_CARVERS = registerSelective("liquid_carvers", CARVERS, 0, POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222601_0_, p_222601_1_, p_222601_2_, p_222601_3_) -> {
      p_222601_1_.func_225550_a_(p_222601_0_.getBiomeManager().copyWithProvider(p_222601_1_.getBiomeProvider()), p_222601_3_, GenerationStage.Carving.LIQUID);
   });
   public static final ChunkStatus FEATURES = register("features", LIQUID_CARVERS, 8, POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222605_0_, p_222605_1_, p_222605_2_, p_222605_3_, p_222605_4_, p_222605_5_, p_222605_6_, p_222605_7_) -> {
      ChunkPrimer chunkprimer = (ChunkPrimer)p_222605_7_;
      chunkprimer.setLightManager(p_222605_4_);
      if (!p_222605_7_.getStatus().isAtLeast(p_222605_0_)) {
         Heightmap.updateChunkHeightmaps(p_222605_7_, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE));
         p_222605_2_.decorate(new WorldGenRegion(p_222605_1_, p_222605_6_));
         chunkprimer.setStatus(p_222605_0_);
      }

      return CompletableFuture.completedFuture(Either.left(p_222605_7_));
   });
   public static final ChunkStatus LIGHT = register("light", FEATURES, 1, POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222604_0_, p_222604_1_, p_222604_2_, p_222604_3_, p_222604_4_, p_222604_5_, p_222604_6_, p_222604_7_) -> {
      return lightChunk(p_222604_0_, p_222604_4_, p_222604_7_);
   }, (p_223195_0_, p_223195_1_, p_223195_2_, p_223195_3_, p_223195_4_, p_223195_5_) -> {
      return lightChunk(p_223195_0_, p_223195_3_, p_223195_5_);
   });
   public static final ChunkStatus SPAWN = registerSelective("spawn", LIGHT, 0, POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222602_0_, p_222602_1_, p_222602_2_, p_222602_3_) -> {
      p_222602_1_.spawnMobs(new WorldGenRegion(p_222602_0_, p_222602_2_));
   });
   public static final ChunkStatus HEIGHTMAPS = registerSelective("heightmaps", SPAWN, 0, POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (p_222603_0_, p_222603_1_, p_222603_2_, p_222603_3_) -> {
   });
   public static final ChunkStatus FULL = register("full", HEIGHTMAPS, 0, POST_FEATURES, ChunkStatus.Type.LEVELCHUNK, (p_222598_0_, p_222598_1_, p_222598_2_, p_222598_3_, p_222598_4_, p_222598_5_, p_222598_6_, p_222598_7_) -> {
      return (CompletableFuture)p_222598_5_.apply(p_222598_7_);
   }, (p_223205_0_, p_223205_1_, p_223205_2_, p_223205_3_, p_223205_4_, p_223205_5_) -> {
      return (CompletableFuture)p_223205_4_.apply(p_223205_5_);
   });
   private static final List<ChunkStatus> STATUS_BY_RANGE = ImmutableList.of(FULL, FEATURES, LIQUID_CARVERS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS);
   private static final IntList RANGE_BY_STATUS = Util.make(new IntArrayList(getAll().size()), (p_223202_0_) -> {
      int i = 0;

      for(int j = getAll().size() - 1; j >= 0; --j) {
         while(i + 1 < STATUS_BY_RANGE.size() && j <= STATUS_BY_RANGE.get(i + 1).ordinal()) {
            ++i;
         }

         p_223202_0_.add(0, i);
      }

   });
   private final String name;
   private final int ordinal;
   private final ChunkStatus parent;
   private final ChunkStatus.IGenerationWorker generationWorker;
   private final ChunkStatus.ILoadingWorker loadingWorker;
   private final int taskRange;
   private final ChunkStatus.Type type;
   private final EnumSet<Heightmap.Type> heightmaps;

   private static CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> lightChunk(ChunkStatus p_223206_0_, ServerWorldLightManager p_223206_1_, IChunk p_223206_2_) {
      boolean flag = isLighted(p_223206_0_, p_223206_2_);
      if (!p_223206_2_.getStatus().isAtLeast(p_223206_0_)) {
         ((ChunkPrimer)p_223206_2_).setStatus(p_223206_0_);
      }

      return p_223206_1_.lightChunk(p_223206_2_, flag).thenApply(Either::left);
   }

   private static ChunkStatus registerSelective(String key, @Nullable ChunkStatus p_223203_1_, int p_223203_2_, EnumSet<Heightmap.Type> p_223203_3_, ChunkStatus.Type p_223203_4_, ChunkStatus.ISelectiveWorker p_223203_5_) {
      return register(key, p_223203_1_, p_223203_2_, p_223203_3_, p_223203_4_, p_223203_5_);
   }

   private static ChunkStatus register(String key, @Nullable ChunkStatus p_223207_1_, int p_223207_2_, EnumSet<Heightmap.Type> p_223207_3_, ChunkStatus.Type p_223207_4_, ChunkStatus.IGenerationWorker p_223207_5_) {
      return register(key, p_223207_1_, p_223207_2_, p_223207_3_, p_223207_4_, p_223207_5_, NOOP_LOADING_WORKER);
   }

   private static ChunkStatus register(String key, @Nullable ChunkStatus p_223196_1_, int p_223196_2_, EnumSet<Heightmap.Type> p_223196_3_, ChunkStatus.Type p_223196_4_, ChunkStatus.IGenerationWorker p_223196_5_, ChunkStatus.ILoadingWorker p_223196_6_) {
      return Registry.register(Registry.CHUNK_STATUS, key, new ChunkStatus(key, p_223196_1_, p_223196_2_, p_223196_3_, p_223196_4_, p_223196_5_, p_223196_6_));
   }

   public static List<ChunkStatus> getAll() {
      List<ChunkStatus> list = Lists.newArrayList();

      ChunkStatus chunkstatus;
      for(chunkstatus = FULL; chunkstatus.getParent() != chunkstatus; chunkstatus = chunkstatus.getParent()) {
         list.add(chunkstatus);
      }

      list.add(chunkstatus);
      Collections.reverse(list);
      return list;
   }

   private static boolean isLighted(ChunkStatus p_223197_0_, IChunk p_223197_1_) {
      return p_223197_1_.getStatus().isAtLeast(p_223197_0_) && p_223197_1_.hasLight();
   }

   public static ChunkStatus getStatus(int p_222581_0_) {
      if (p_222581_0_ >= STATUS_BY_RANGE.size()) {
         return EMPTY;
      } else {
         return p_222581_0_ < 0 ? FULL : STATUS_BY_RANGE.get(p_222581_0_);
      }
   }

   public static int maxDistance() {
      return STATUS_BY_RANGE.size();
   }

   public static int getDistance(ChunkStatus p_222599_0_) {
      return RANGE_BY_STATUS.getInt(p_222599_0_.ordinal());
   }

   public ChunkStatus(String nameIn, @Nullable ChunkStatus parentIn, int taskRangeIn, EnumSet<Heightmap.Type> heightmapsIn, ChunkStatus.Type typeIn, ChunkStatus.IGenerationWorker generationWorkerIn, ChunkStatus.ILoadingWorker loadingWorkerIn) {
      this.name = nameIn;
      this.parent = parentIn == null ? this : parentIn;
      this.generationWorker = generationWorkerIn;
      this.loadingWorker = loadingWorkerIn;
      this.taskRange = taskRangeIn;
      this.type = typeIn;
      this.heightmaps = heightmapsIn;
      this.ordinal = parentIn == null ? 0 : parentIn.ordinal() + 1;
   }

   public int ordinal() {
      return this.ordinal;
   }

   public String getName() {
      return this.name;
   }

   public ChunkStatus getParent() {
      return this.parent;
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doGenerationWork(ServerWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, TemplateManager templateManagerIn, ServerWorldLightManager lightManager, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_223198_5_, List<IChunk> chunks) {
      return this.generationWorker.doWork(this, worldIn, chunkGeneratorIn, templateManagerIn, lightManager, p_223198_5_, chunks, chunks.get(chunks.size() / 2));
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doLoadingWork(ServerWorld worldIn, TemplateManager templateManagerIn, ServerWorldLightManager lightManager, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_223201_4_, IChunk p_223201_5_) {
      return this.loadingWorker.doWork(this, worldIn, templateManagerIn, lightManager, p_223201_4_, p_223201_5_);
   }

   /**
    * Distance in chunks between the edge of the center chunk and the edge of the chunk region needed for the task. The
    * task will only affect the center chunk, only reading from the chunks in the margin.
    */
   public int getTaskRange() {
      return this.taskRange;
   }

   public ChunkStatus.Type getType() {
      return this.type;
   }

   public static ChunkStatus byName(String location) {
      return Registry.CHUNK_STATUS.getOrDefault(ResourceLocation.tryCreate(location));
   }

   public EnumSet<Heightmap.Type> getHeightMaps() {
      return this.heightmaps;
   }

   public boolean isAtLeast(ChunkStatus status) {
      return this.ordinal() >= status.ordinal();
   }

   public String toString() {
      return Registry.CHUNK_STATUS.getKey(this).toString();
   }

   interface IGenerationWorker {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doWork(ChunkStatus p_doWork_1_, ServerWorld p_doWork_2_, ChunkGenerator<?> p_doWork_3_, TemplateManager p_doWork_4_, ServerWorldLightManager p_doWork_5_, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_doWork_6_, List<IChunk> p_doWork_7_, IChunk p_doWork_8_);
   }

   interface ILoadingWorker {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doWork(ChunkStatus p_doWork_1_, ServerWorld p_doWork_2_, TemplateManager p_doWork_3_, ServerWorldLightManager p_doWork_4_, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_doWork_5_, IChunk p_doWork_6_);
   }

   interface ISelectiveWorker extends ChunkStatus.IGenerationWorker {
      default CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doWork(ChunkStatus p_doWork_1_, ServerWorld p_doWork_2_, ChunkGenerator<?> p_doWork_3_, TemplateManager p_doWork_4_, ServerWorldLightManager p_doWork_5_, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_doWork_6_, List<IChunk> p_doWork_7_, IChunk p_doWork_8_) {
         if (!p_doWork_8_.getStatus().isAtLeast(p_doWork_1_)) {
            this.doWork(p_doWork_2_, p_doWork_3_, p_doWork_7_, p_doWork_8_);
            if (p_doWork_8_ instanceof ChunkPrimer) {
               ((ChunkPrimer)p_doWork_8_).setStatus(p_doWork_1_);
            }
         }

         return CompletableFuture.completedFuture(Either.left(p_doWork_8_));
      }

      void doWork(ServerWorld p_doWork_1_, ChunkGenerator<?> p_doWork_2_, List<IChunk> p_doWork_3_, IChunk p_doWork_4_);
   }

   public static enum Type {
      PROTOCHUNK,
      LEVELCHUNK;
   }
}