package net.minecraft.world.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.INPC;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.merchant.IReputationTracking;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SSpawnGlobalEntityPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Explosion;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.spawner.WanderingTraderSpawner;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapIdTracker;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld extends World implements net.minecraftforge.common.extensions.IForgeWorldServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<Entity> globalEntities = Lists.newArrayList();
   private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap<>();
   private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
   private final Queue<Entity> entitiesToAdd = Queues.newArrayDeque();
   private final List<ServerPlayerEntity> players = Lists.newArrayList();
   boolean tickingEntities;
   private final MinecraftServer server;
   private final SaveHandler saveHandler;
   public boolean disableLevelSaving;
   private boolean allPlayersSleeping;
   private int updateEntityTick;
   private final Teleporter worldTeleporter;
   private final ServerTickList<Block> pendingBlockTicks = new ServerTickList<>(this, (p_205341_0_) -> {
      return p_205341_0_ == null || p_205341_0_.getDefaultState().isAir();
   }, Registry.BLOCK::getKey, Registry.BLOCK::getOrDefault, this::tickBlock);
   private final ServerTickList<Fluid> pendingFluidTicks = new ServerTickList<>(this, (p_205774_0_) -> {
      return p_205774_0_ == null || p_205774_0_ == Fluids.EMPTY;
   }, Registry.FLUID::getKey, Registry.FLUID::getOrDefault, this::tickFluid);
   private final Set<PathNavigator> navigations = Sets.newHashSet();
   protected final RaidManager raids;
   private final ObjectLinkedOpenHashSet<BlockEventData> blockEventQueue = new ObjectLinkedOpenHashSet<>();
   private boolean insideTick;
   @Nullable
   private final WanderingTraderSpawner wanderingTraderSpawner;
   /** Stores the recently processed (lighting) chunks */
   protected java.util.Set<ChunkPos> doneChunks = Sets.newHashSet();
   private net.minecraftforge.common.util.WorldCapabilityData capabilityData;

   public ServerWorld(MinecraftServer serverIn, Executor executorIn, SaveHandler saveHandlerIn, WorldInfo worldInfoIn, DimensionType dimType, IProfiler profilerIn, IChunkStatusListener listenerIn) {
      super(worldInfoIn, dimType, (p_217442_4_, p_217442_5_) -> {
         return new ServerChunkProvider((ServerWorld)p_217442_4_, saveHandlerIn.getWorldDirectory(), saveHandlerIn.getFixer(), saveHandlerIn.getStructureTemplateManager(), executorIn, p_217442_4_.getWorldType().createChunkGenerator(p_217442_4_), serverIn.getPlayerList().getViewDistance(), listenerIn, () -> {
            return serverIn.getWorld(DimensionType.OVERWORLD).getSavedData();
         });
      }, profilerIn, false);
      this.saveHandler = saveHandlerIn;
      this.server = serverIn;
      this.worldTeleporter = new Teleporter(this);
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      this.getWorldBorder().setSize(serverIn.getMaxWorldSize());
      this.raids = this.getSavedData().getOrCreate(() -> {
         return new RaidManager(this);
      }, RaidManager.getFileId(this.dimension));
      if (!serverIn.isSinglePlayer()) {
         this.getWorldInfo().setGameType(serverIn.getGameType());
      }

      this.wanderingTraderSpawner = this.dimension.getType() == DimensionType.OVERWORLD ? new WanderingTraderSpawner(this) : null;
      this.initCapabilities();
   }

   public Biome getNoiseBiomeRaw(int x, int y, int z) {
      return this.getChunkProvider().getChunkGenerator().getBiomeProvider().getNoiseBiome(x, y, z);
   }

   /**
    * Runs a single tick for the world
    */
   public void tick(BooleanSupplier hasTimeLeft) {
      IProfiler iprofiler = this.getProfiler();
      this.insideTick = true;
      iprofiler.startSection("world border");
      this.getWorldBorder().tick();
      iprofiler.endStartSection("weather");
      boolean flag = this.isRaining();
      this.dimension.updateWeather(() -> {
      if (this.dimension.hasSkyLight()) {
         if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
            int i = this.worldInfo.getClearWeatherTime();
            int j = this.worldInfo.getThunderTime();
            int k = this.worldInfo.getRainTime();
            boolean flag1 = this.worldInfo.isThundering();
            boolean flag2 = this.worldInfo.isRaining();
            if (i > 0) {
               --i;
               j = flag1 ? 0 : 1;
               k = flag2 ? 0 : 1;
               flag1 = false;
               flag2 = false;
            } else {
               if (j > 0) {
                  --j;
                  if (j == 0) {
                     flag1 = !flag1;
                  }
               } else if (flag1) {
                  j = this.rand.nextInt(12000) + 3600;
               } else {
                  j = this.rand.nextInt(168000) + 12000;
               }

               if (k > 0) {
                  --k;
                  if (k == 0) {
                     flag2 = !flag2;
                  }
               } else if (flag2) {
                  k = this.rand.nextInt(12000) + 12000;
               } else {
                  k = this.rand.nextInt(168000) + 12000;
               }
            }

            this.worldInfo.setThunderTime(j);
            this.worldInfo.setRainTime(k);
            this.worldInfo.setClearWeatherTime(i);
            this.worldInfo.setThundering(flag1);
            this.worldInfo.setRaining(flag2);
         }

         this.prevThunderingStrength = this.thunderingStrength;
         if (this.worldInfo.isThundering()) {
            this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
         } else {
            this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
         }

         this.thunderingStrength = MathHelper.clamp(this.thunderingStrength, 0.0F, 1.0F);
         this.prevRainingStrength = this.rainingStrength;
         if (this.worldInfo.isRaining()) {
            this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
         } else {
            this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
         }

         this.rainingStrength = MathHelper.clamp(this.rainingStrength, 0.0F, 1.0F);
      }
      }); //Forge: End weatherTick delegate

      if (this.prevRainingStrength != this.rainingStrength) {
         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(7, this.rainingStrength), this.dimension.getType());
      }

      if (this.prevThunderingStrength != this.thunderingStrength) {
         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(8, this.thunderingStrength), this.dimension.getType());
      }

      /* The function in use here has been replaced in order to only send the weather info to players in the correct dimension,
       * rather than to all players on the server. This is what causes the client-side rain, as the
       * client believes that it has started raining locally, rather than in another dimension.
       */
      if (flag != this.isRaining()) {
         if (flag) {
            this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(2, 0.0F), this.dimension.getType());
         } else {
            this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(1, 0.0F), this.dimension.getType());
         }

         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(7, this.rainingStrength), this.dimension.getType());
         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(8, this.thunderingStrength), this.dimension.getType());
      }

      if (this.getWorldInfo().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
         this.getWorldInfo().setDifficulty(Difficulty.HARD);
      }

      if (this.allPlayersSleeping && this.players.stream().noneMatch((p_217449_0_) -> {
         return !p_217449_0_.isSpectator() && !p_217449_0_.isPlayerFullyAsleep();
      })) {
         this.allPlayersSleeping = false;
         if (this.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            long l = this.getDayTime() + 24000L;
            this.setDayTime(net.minecraftforge.event.ForgeEventFactory.onSleepFinished(this, l - l % 24000L, this.getDayTime()));
         }

         this.wakeUpAllPlayers();
         if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
            this.resetRainAndThunder();
         }
      }

      this.calculateInitialSkylight();
      this.advanceTime();
      iprofiler.endStartSection("chunkSource");
      this.getChunkProvider().tick(hasTimeLeft);
      iprofiler.endStartSection("tickPending");
      if (this.worldInfo.getGenerator() != WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.pendingBlockTicks.tick();
         this.pendingFluidTicks.tick();
      }

      iprofiler.endStartSection("raid");
      this.raids.tick();
      if (this.wanderingTraderSpawner != null) {
         this.wanderingTraderSpawner.tick();
      }

      iprofiler.endStartSection("blockEvents");
      this.sendQueuedBlockEvents();
      this.insideTick = false;
      iprofiler.endStartSection("entities");
      boolean flag3 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
      if (flag3) {
         this.resetUpdateEntityTick();
      }

      if (flag3 || this.updateEntityTick++ < 300) {
         this.dimension.tick();
         iprofiler.startSection("global");

         for(int i1 = 0; i1 < this.globalEntities.size(); ++i1) {
            Entity entity = this.globalEntities.get(i1);
            this.guardEntityTick((p_229858_0_) -> {
               ++p_229858_0_.ticksExisted;
               if (p_229858_0_.canUpdate())
               p_229858_0_.tick();
            }, entity);
            if (entity.removed) {
               this.globalEntities.remove(i1--);
            }
         }

         iprofiler.endStartSection("regular");
         this.tickingEntities = true;
         ObjectIterator<Entry<Entity>> objectiterator = this.entitiesById.int2ObjectEntrySet().iterator();

         label174:
         while(true) {
            Entity entity2;
            while(true) {
               if (!objectiterator.hasNext()) {
                  this.tickingEntities = false;

                  Entity entity1;
                  while((entity1 = this.entitiesToAdd.poll()) != null) {
                     this.onEntityAdded(entity1);
                  }

                  iprofiler.endSection();
                  this.tickBlockEntities();
                  break label174;
               }

               Entry<Entity> entry = objectiterator.next();
               entity2 = entry.getValue();
               Entity entity3 = entity2.getRidingEntity();
               if (!this.server.getCanSpawnAnimals() && (entity2 instanceof AnimalEntity || entity2 instanceof WaterMobEntity)) {
                  entity2.remove();
               }

               if (!this.server.getCanSpawnNPCs() && entity2 instanceof INPC) {
                  entity2.remove();
               }

               iprofiler.startSection("checkDespawn");
               if (!entity2.removed) {
                  entity2.checkDespawn();
               }

               iprofiler.endSection();
               if (entity3 == null) {
                  break;
               }

               if (entity3.removed || !entity3.isPassenger(entity2)) {
                  entity2.stopRiding();
                  break;
               }
            }

            iprofiler.startSection("tick");
            if (!entity2.removed && !(entity2 instanceof EnderDragonPartEntity)) {
               this.guardEntityTick(this::updateEntity, entity2);
            }

            iprofiler.endSection();
            iprofiler.startSection("remove");
            if (entity2.removed) {
               this.removeFromChunk(entity2);
               objectiterator.remove();
               this.removeEntityComplete(entity2, entity2 instanceof ServerPlayerEntity); //Forge: Keep cap data until revive. Every other entity removes directly.
            }

            iprofiler.endSection();
         }
      }

      iprofiler.endSection();
   }

   private void wakeUpAllPlayers() {
      this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach((p_229857_0_) -> {
         p_229857_0_.stopSleepInBed(false, false);
      });
   }

   public void tickEnvironment(Chunk chunkIn, int randomTickSpeed) {
      ChunkPos chunkpos = chunkIn.getPos();
      boolean flag = this.isRaining();
      int i = chunkpos.getXStart();
      int j = chunkpos.getZStart();
      IProfiler iprofiler = this.getProfiler();
      iprofiler.startSection("thunder");
      if (this.dimension.canDoLightning(chunkIn) && flag && this.isThundering() && this.rand.nextInt(100000) == 0) {
         BlockPos blockpos = this.adjustPosToNearbyEntity(this.getBlockRandomPos(i, 0, j, 15));
         if (this.isRainingAt(blockpos)) {
            DifficultyInstance difficultyinstance = this.getDifficultyForLocation(blockpos);
            boolean flag1 = this.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && this.rand.nextDouble() < (double)difficultyinstance.getAdditionalDifficulty() * 0.01D;
            if (flag1) {
               SkeletonHorseEntity skeletonhorseentity = EntityType.SKELETON_HORSE.create(this);
               skeletonhorseentity.setTrap(true);
               skeletonhorseentity.setGrowingAge(0);
               skeletonhorseentity.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
               this.addEntity(skeletonhorseentity);
            }

            this.addLightningBolt(new LightningBoltEntity(this, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, flag1));
         }
      }

      iprofiler.endStartSection("iceandsnow");
      if (this.dimension.canDoRainSnowIce(chunkIn) && this.rand.nextInt(16) == 0) {
         BlockPos blockpos2 = this.getHeight(Heightmap.Type.MOTION_BLOCKING, this.getBlockRandomPos(i, 0, j, 15));
         BlockPos blockpos3 = blockpos2.down();
         Biome biome = this.getBiome(blockpos2);
         if (this.isAreaLoaded(blockpos2, 1)) // Forge: check area to avoid loading neighbors in unloaded chunks
         if (biome.doesWaterFreeze(this, blockpos3)) {
            this.setBlockState(blockpos3, Blocks.ICE.getDefaultState());
         }

         if (flag && biome.doesSnowGenerate(this, blockpos2)) {
            this.setBlockState(blockpos2, Blocks.SNOW.getDefaultState());
         }

         if (flag && this.getBiome(blockpos3).getPrecipitation() == Biome.RainType.RAIN) {
            this.getBlockState(blockpos3).getBlock().fillWithRain(this, blockpos3);
         }
      }

      iprofiler.endStartSection("tickBlocks");
      if (randomTickSpeed > 0) {
         for(ChunkSection chunksection : chunkIn.getSections()) {
            if (chunksection != Chunk.EMPTY_SECTION && chunksection.needsRandomTickAny()) {
               int k = chunksection.getYLocation();

               for(int l = 0; l < randomTickSpeed; ++l) {
                  BlockPos blockpos1 = this.getBlockRandomPos(i, k, j, 15);
                  iprofiler.startSection("randomTick");
                  BlockState blockstate = chunksection.getBlockState(blockpos1.getX() - i, blockpos1.getY() - k, blockpos1.getZ() - j);
                  if (blockstate.ticksRandomly()) {
                     blockstate.randomTick(this, blockpos1, this.rand);
                  }

                  IFluidState ifluidstate = blockstate.getFluidState();
                  if (ifluidstate.ticksRandomly()) {
                     ifluidstate.randomTick(this, blockpos1, this.rand);
                  }

                  iprofiler.endSection();
               }
            }
         }
      }

      iprofiler.endSection();
   }

   protected BlockPos adjustPosToNearbyEntity(BlockPos pos) {
      BlockPos blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING, pos);
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos, new BlockPos(blockpos.getX(), this.getHeight(), blockpos.getZ()))).grow(3.0D);
      List<LivingEntity> list = this.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb, (p_217463_1_) -> {
         return p_217463_1_ != null && p_217463_1_.isAlive() && this.canSeeSky(p_217463_1_.getPosition());
      });
      if (!list.isEmpty()) {
         return list.get(this.rand.nextInt(list.size())).getPosition();
      } else {
         if (blockpos.getY() == -1) {
            blockpos = blockpos.up(2);
         }

         return blockpos;
      }
   }

   public boolean isInsideTick() {
      return this.insideTick;
   }

   /**
    * Updates the flag that indicates whether or not all players in the world are sleeping.
    */
   public void updateAllPlayersSleepingFlag() {
      this.allPlayersSleeping = false;
      if (!this.players.isEmpty()) {
         int i = 0;
         int j = 0;

         for(ServerPlayerEntity serverplayerentity : this.players) {
            if (serverplayerentity.isSpectator()) {
               ++i;
            } else if (serverplayerentity.isSleeping()) {
               ++j;
            }
         }

         this.allPlayersSleeping = j > 0 && j >= this.players.size() - i;
      }

   }

   public ServerScoreboard getScoreboard() {
      return this.server.getScoreboard();
   }

   /**
    * Clears the current rain and thunder weather states.
    */
   private void resetRainAndThunder() {
      this.dimension.resetRainAndThunder();
   }

   /**
    * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
    */
   @OnlyIn(Dist.CLIENT)
   public void setInitialSpawnLocation() {
      if (this.worldInfo.getSpawnY() <= 0) {
         this.worldInfo.setSpawnY(this.getSeaLevel() + 1);
      }

      int i = this.worldInfo.getSpawnX();
      int j = this.worldInfo.getSpawnZ();
      int k = 0;

      while(this.getGroundAboveSeaLevel(new BlockPos(i, 0, j)).isAir(this, new BlockPos(i, 0, j))) {
         i += this.rand.nextInt(8) - this.rand.nextInt(8);
         j += this.rand.nextInt(8) - this.rand.nextInt(8);
         ++k;
         if (k == 10000) {
            break;
         }
      }

      this.worldInfo.setSpawnX(i);
      this.worldInfo.setSpawnZ(j);
   }

   /**
    * Resets the updateEntityTick field to 0
    */
   public void resetUpdateEntityTick() {
      this.updateEntityTick = 0;
   }

   private void tickFluid(NextTickListEntry<Fluid> fluidTickEntry) {
      IFluidState ifluidstate = this.getFluidState(fluidTickEntry.position);
      if (ifluidstate.getFluid() == fluidTickEntry.getTarget()) {
         ifluidstate.tick(this, fluidTickEntry.position);
      }

   }

   private void tickBlock(NextTickListEntry<Block> blockTickEntry) {
      BlockState blockstate = this.getBlockState(blockTickEntry.position);
      if (blockstate.getBlock() == blockTickEntry.getTarget()) {
         blockstate.tick(this, blockTickEntry.position, this.rand);
      }

   }

   public void updateEntity(Entity entityIn) {
      if (entityIn instanceof PlayerEntity || this.getChunkProvider().isChunkLoaded(entityIn)) {
         entityIn.forceSetPosition(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
         entityIn.prevRotationYaw = entityIn.rotationYaw;
         entityIn.prevRotationPitch = entityIn.rotationPitch;
         if (entityIn.addedToChunk) {
            ++entityIn.ticksExisted;
            IProfiler iprofiler = this.getProfiler();
            iprofiler.startSection(() -> {
               return entityIn.getType().getRegistryName() == null ? entityIn.getType().toString() : entityIn.getType().getRegistryName().toString();
            });
            if (entityIn.canUpdate())
            iprofiler.func_230035_c_("tickNonPassenger");
            entityIn.tick();
            iprofiler.endSection();
         }

         this.chunkCheck(entityIn);
         if (entityIn.addedToChunk) {
            for(Entity entity : entityIn.getPassengers()) {
               this.tickPassenger(entityIn, entity);
            }
         }

      }
   }

   public void tickPassenger(Entity ridingEntity, Entity passengerEntity) {
      if (!passengerEntity.removed && passengerEntity.getRidingEntity() == ridingEntity) {
         if (passengerEntity instanceof PlayerEntity || this.getChunkProvider().isChunkLoaded(passengerEntity)) {
            passengerEntity.forceSetPosition(passengerEntity.getPosX(), passengerEntity.getPosY(), passengerEntity.getPosZ());
            passengerEntity.prevRotationYaw = passengerEntity.rotationYaw;
            passengerEntity.prevRotationPitch = passengerEntity.rotationPitch;
            if (passengerEntity.addedToChunk) {
               ++passengerEntity.ticksExisted;
               IProfiler iprofiler = this.getProfiler();
               iprofiler.startSection(() -> {
                  return Registry.ENTITY_TYPE.getKey(passengerEntity.getType()).toString();
               });
               iprofiler.func_230035_c_("tickPassenger");
               passengerEntity.updateRidden();
               iprofiler.endSection();
            }

            this.chunkCheck(passengerEntity);
            if (passengerEntity.addedToChunk) {
               for(Entity entity : passengerEntity.getPassengers()) {
                  this.tickPassenger(passengerEntity, entity);
               }
            }

         }
      } else {
         passengerEntity.stopRiding();
      }
   }

   public void chunkCheck(Entity entityIn) {
      this.getProfiler().startSection("chunkCheck");
      int i = MathHelper.floor(entityIn.getPosX() / 16.0D);
      int j = MathHelper.floor(entityIn.getPosY() / 16.0D);
      int k = MathHelper.floor(entityIn.getPosZ() / 16.0D);
      if (!entityIn.addedToChunk || entityIn.chunkCoordX != i || entityIn.chunkCoordY != j || entityIn.chunkCoordZ != k) {
         if (entityIn.addedToChunk && this.chunkExists(entityIn.chunkCoordX, entityIn.chunkCoordZ)) {
            this.getChunk(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
         }

         if (!entityIn.setPositionNonDirty() && !this.chunkExists(i, k)) {
            entityIn.addedToChunk = false;
         } else {
            this.getChunk(i, k).addEntity(entityIn);
         }
      }

      this.getProfiler().endSection();
   }

   public boolean isBlockModifiable(PlayerEntity player, BlockPos pos) {
       return super.isBlockModifiable(player, pos);
   }

   @Override
   public boolean canMineBlockBody(PlayerEntity player, BlockPos pos) {
      return !this.server.isBlockProtected(this, pos, player) && this.getWorldBorder().contains(pos);
   }

   /**
    * creates a spawn position at random within 256 blocks of 0,0
    */
   public void createSpawnPosition(WorldSettings settings) {
      if (!this.dimension.canRespawnHere()) {
         this.worldInfo.setSpawn(BlockPos.ZERO.up(this.getChunkProvider().getChunkGenerator().getGroundHeight()));
      } else if (this.worldInfo.getGenerator() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.worldInfo.setSpawn(BlockPos.ZERO.up());
      } else {
         if (net.minecraftforge.event.ForgeEventFactory.onCreateWorldSpawn(this, settings)) return;
         BiomeProvider biomeprovider = this.getChunkProvider().getChunkGenerator().getBiomeProvider();
         List<Biome> list = biomeprovider.getBiomesToSpawnIn();
         Random random = new Random(this.getSeed());
         BlockPos blockpos = biomeprovider.func_225531_a_(0, this.getSeaLevel(), 0, 256, list, random);
         ChunkPos chunkpos = blockpos == null ? new ChunkPos(0, 0) : new ChunkPos(blockpos);
         if (blockpos == null) {
            LOGGER.warn("Unable to find spawn biome");
         }

         boolean flag = false;

         for(Block block : BlockTags.VALID_SPAWN.getAllElements()) {
            if (biomeprovider.getSurfaceBlocks().contains(block.getDefaultState())) {
               flag = true;
               break;
            }
         }

         this.worldInfo.setSpawn(chunkpos.asBlockPos().add(8, this.getChunkProvider().getChunkGenerator().getGroundHeight(), 8));
         int i1 = 0;
         int j1 = 0;
         int i = 0;
         int j = -1;
         int k = 32;

         for(int l = 0; l < 1024; ++l) {
            if (i1 > -16 && i1 <= 16 && j1 > -16 && j1 <= 16) {
               BlockPos blockpos1 = this.dimension.findSpawn(new ChunkPos(chunkpos.x + i1, chunkpos.z + j1), flag);
               if (blockpos1 != null) {
                  this.worldInfo.setSpawn(blockpos1);
                  break;
               }
            }

            if (i1 == j1 || i1 < 0 && i1 == -j1 || i1 > 0 && i1 == 1 - j1) {
               int k1 = i;
               i = -j;
               j = k1;
            }

            i1 += i;
            j1 += j;
         }

         if (settings.isBonusChestEnabled()) {
            this.createBonusChest();
         }

      }
   }

   /**
    * Creates the bonus chest in the world.
    */
   protected void createBonusChest() {
      ConfiguredFeature<?, ?> configuredfeature = Feature.BONUS_CHEST.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG);
      configuredfeature.place(this, this.getChunkProvider().getChunkGenerator(), this.rand, new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ()));
   }

   /**
    * Returns null for anything other than the End
    */
   @Nullable
   public BlockPos getSpawnCoordinate() {
      return this.dimension.getSpawnCoordinate();
   }

   public void save(@Nullable IProgressUpdate progress, boolean flush, boolean skipSave) throws SessionLockException {
      ServerChunkProvider serverchunkprovider = this.getChunkProvider();
      if (!skipSave) {
         if (progress != null) {
            progress.displaySavingString(new TranslationTextComponent("menu.savingLevel"));
         }

         this.saveLevel();
         if (progress != null) {
            progress.displayLoadingString(new TranslationTextComponent("menu.savingChunks"));
         }

         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Save(this));
         serverchunkprovider.save(flush);
      }
   }

   /**
    * Saves the chunks to disk.
    */
   protected void saveLevel() throws SessionLockException {
      this.checkSessionLock();
      this.dimension.onWorldSave();
      this.getChunkProvider().getSavedData().save();
   }

   public List<Entity> getEntities(@Nullable EntityType<?> entityTypeIn, Predicate<? super Entity> predicateIn) {
      List<Entity> list = Lists.newArrayList();
      ServerChunkProvider serverchunkprovider = this.getChunkProvider();

      for(Entity entity : this.entitiesById.values()) {
         if ((entityTypeIn == null || entity.getType() == entityTypeIn) && serverchunkprovider.chunkExists(MathHelper.floor(entity.getPosX()) >> 4, MathHelper.floor(entity.getPosZ()) >> 4) && predicateIn.test(entity)) {
            list.add(entity);
         }
      }

      return list;
   }

   public List<EnderDragonEntity> getDragons() {
      List<EnderDragonEntity> list = Lists.newArrayList();

      for(Entity entity : this.entitiesById.values()) {
         if (entity instanceof EnderDragonEntity && entity.isAlive()) {
            list.add((EnderDragonEntity)entity);
         }
      }

      return list;
   }

   public List<ServerPlayerEntity> getPlayers(Predicate<? super ServerPlayerEntity> predicateIn) {
      List<ServerPlayerEntity> list = Lists.newArrayList();

      for(ServerPlayerEntity serverplayerentity : this.players) {
         if (predicateIn.test(serverplayerentity)) {
            list.add(serverplayerentity);
         }
      }

      return list;
   }

   @Nullable
   public ServerPlayerEntity getRandomPlayer() {
      List<ServerPlayerEntity> list = this.getPlayers(LivingEntity::isAlive);
      return list.isEmpty() ? null : list.get(this.rand.nextInt(list.size()));
   }

   public Object2IntMap<EntityClassification> countEntities() {
      Object2IntMap<EntityClassification> object2intmap = new Object2IntOpenHashMap<>();
      ObjectIterator objectiterator = this.entitiesById.values().iterator();

      while(true) {
         Entity entity;
         while(true) {
            if (!objectiterator.hasNext()) {
               return object2intmap;
            }

            entity = (Entity)objectiterator.next();
            if (!(entity instanceof MobEntity)) {
               break;
            }

            MobEntity mobentity = (MobEntity)entity;
            if (!mobentity.isNoDespawnRequired() && !mobentity.preventDespawn()) {
               break;
            }
         }

         EntityClassification entityclassification = entity.getClassification(true);
         if (entityclassification != EntityClassification.MISC && this.getChunkProvider().func_223435_b(entity)) {
            object2intmap.mergeInt(entityclassification, 1, Integer::sum);
         }
      }
   }

   public boolean addEntity(Entity entityIn) {
      return this.addEntity0(entityIn);
   }

   /**
    * Used for "unnatural" ways of entities appearing in the world, e.g. summon command, interdimensional teleports
    */
   public boolean summonEntity(Entity entityIn) {
      return this.addEntity0(entityIn);
   }

   public void addFromAnotherDimension(Entity entityIn) {
      boolean flag = entityIn.forceSpawn;
      entityIn.forceSpawn = true;
      this.summonEntity(entityIn);
      entityIn.forceSpawn = flag;
      this.chunkCheck(entityIn);
   }

   public void addDuringCommandTeleport(ServerPlayerEntity playerIn) {
      this.addPlayer(playerIn);
      this.chunkCheck(playerIn);
   }

   public void addDuringPortalTeleport(ServerPlayerEntity playerIn) {
      this.addPlayer(playerIn);
      this.chunkCheck(playerIn);
   }

   public void addNewPlayer(ServerPlayerEntity player) {
      this.addPlayer(player);
   }

   public void addRespawnedPlayer(ServerPlayerEntity player) {
      this.addPlayer(player);
   }

   private void addPlayer(ServerPlayerEntity player) {
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(player, this))) return;
      Entity entity = this.entitiesByUuid.get(player.getUniqueID());
      if (entity != null) {
         LOGGER.warn("Force-added player with duplicate UUID {}", (Object)player.getUniqueID().toString());
         entity.detach();
         this.removePlayer((ServerPlayerEntity)entity);
      }

      this.players.add(player);
      this.updateAllPlayersSleepingFlag();
      IChunk ichunk = this.getChunk(MathHelper.floor(player.getPosX() / 16.0D), MathHelper.floor(player.getPosZ() / 16.0D), ChunkStatus.FULL, true);
      if (ichunk instanceof Chunk) {
         ichunk.addEntity(player);
      }

      this.onEntityAdded(player);
   }

   /**
    * Called when an entity is spawned in the world. This includes players.
    */
   private boolean addEntity0(Entity entityIn) {
      if (entityIn.removed) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityType.getKey(entityIn.getType()));
         return false;
      } else if (this.hasDuplicateEntity(entityIn)) {
         return false;
      } else {
         if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(entityIn, this))) return false;
         IChunk ichunk = this.getChunk(MathHelper.floor(entityIn.getPosX() / 16.0D), MathHelper.floor(entityIn.getPosZ() / 16.0D), ChunkStatus.FULL, entityIn.forceSpawn);
         if (!(ichunk instanceof Chunk)) {
            return false;
         } else {
            ichunk.addEntity(entityIn);
            this.onEntityAdded(entityIn);
            return true;
         }
      }
   }

   public boolean addEntityIfNotDuplicate(Entity entityIn) {
      if (this.hasDuplicateEntity(entityIn)) {
         return false;
      } else {
         if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(entityIn, this))) return false;
         this.onEntityAdded(entityIn);
         return true;
      }
   }

   private boolean hasDuplicateEntity(Entity entityIn) {
      Entity entity = this.entitiesByUuid.get(entityIn.getUniqueID());
      if (entity == null) {
         return false;
      } else {
         LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getKey(entity.getType()), entityIn.getUniqueID().toString());
         return true;
      }
   }

   public void onChunkUnloading(Chunk chunkIn) {
      this.tileEntitiesToBeRemoved.addAll(chunkIn.getTileEntityMap().values());
      ClassInheritanceMultiMap<Entity>[] aclassinheritancemultimap = chunkIn.getEntityLists();
      int i = aclassinheritancemultimap.length;

      for(int j = 0; j < i; ++j) {
         for(Entity entity : aclassinheritancemultimap[j]) {
            if (!(entity instanceof ServerPlayerEntity)) {
               if (this.tickingEntities) {
                  throw (IllegalStateException)Util.pauseDevMode(new IllegalStateException("Removing entity while ticking!"));
               }

               this.entitiesById.remove(entity.getEntityId());
               this.onEntityRemoved(entity);
            }
         }
      }

   }

   @Deprecated //Forge: Use removeEntityComplete(entity,boolean)
   public void onEntityRemoved(Entity entityIn) {
      removeEntityComplete(entityIn, false);
   }
   public void removeEntityComplete(Entity entityIn, boolean keepData) {
      if (entityIn instanceof EnderDragonEntity) {
         for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entityIn).getDragonParts()) {
            enderdragonpartentity.remove(keepData);
         }
      }
      entityIn.remove(keepData);

      this.entitiesByUuid.remove(entityIn.getUniqueID());
      this.getChunkProvider().untrack(entityIn);
      if (entityIn instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityIn;
         this.players.remove(serverplayerentity);
      }

      this.getScoreboard().removeEntity(entityIn);
      if (entityIn instanceof MobEntity) {
         this.navigations.remove(((MobEntity)entityIn).getNavigator());
      }

      entityIn.onRemovedFromWorld();
   }

   private void onEntityAdded(Entity entityIn) {
      if (this.tickingEntities) {
         this.entitiesToAdd.add(entityIn);
      } else {
         this.entitiesById.put(entityIn.getEntityId(), entityIn);
         if (entityIn instanceof EnderDragonEntity) {
            for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entityIn).getDragonParts()) {
               this.entitiesById.put(enderdragonpartentity.getEntityId(), enderdragonpartentity);
            }
         }

         this.entitiesByUuid.put(entityIn.getUniqueID(), entityIn);
         this.getChunkProvider().track(entityIn);
         if (entityIn instanceof MobEntity) {
            this.navigations.add(((MobEntity)entityIn).getNavigator());
         }
      }

      entityIn.onAddedToWorld();
   }

   public void removeEntity(Entity entityIn) {
      removeEntity(entityIn, false);
   }
   public void removeEntity(Entity entityIn, boolean keepData) {
      if (this.tickingEntities) {
         throw (IllegalStateException)Util.pauseDevMode(new IllegalStateException("Removing entity while ticking!"));
      } else {
         this.removeFromChunk(entityIn);
         this.entitiesById.remove(entityIn.getEntityId());
         this.removeEntityComplete(entityIn, keepData);
      }
   }

   private void removeFromChunk(Entity entityIn) {
      IChunk ichunk = this.getChunk(entityIn.chunkCoordX, entityIn.chunkCoordZ, ChunkStatus.FULL, false);
      if (ichunk instanceof Chunk) {
         ((Chunk)ichunk).removeEntity(entityIn);
      }

   }

   public void removePlayer(ServerPlayerEntity player) {
      removePlayer(player, false);
   }
   public void removePlayer(ServerPlayerEntity player, boolean keepData) {
      player.remove(keepData);
      this.removeEntity(player, keepData);
      this.updateAllPlayersSleepingFlag();
   }

   public void addLightningBolt(LightningBoltEntity entityIn) {
      this.globalEntities.add(entityIn);
      this.server.getPlayerList().sendToAllNearExcept((PlayerEntity)null, entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), 512.0D, this.dimension.getType(), new SSpawnGlobalEntityPacket(entityIn));
   }

   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      for(ServerPlayerEntity serverplayerentity : this.server.getPlayerList().getPlayers()) {
         if (serverplayerentity != null && serverplayerentity.world == this && serverplayerentity.getEntityId() != breakerId) {
            double d0 = (double)pos.getX() - serverplayerentity.getPosX();
            double d1 = (double)pos.getY() - serverplayerentity.getPosY();
            double d2 = (double)pos.getZ() - serverplayerentity.getPosZ();
            if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
               serverplayerentity.connection.sendPacket(new SAnimateBlockBreakPacket(breakerId, pos, progress));
            }
         }
      }

   }

   public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(player, soundIn, category, volume, pitch);
      if (event.isCanceled() || event.getSound() == null) return;
      soundIn = event.getSound();
      category = event.getCategory();
      volume = event.getVolume();
      this.server.getPlayerList().sendToAllNearExcept(player, x, y, z, volume > 1.0F ? (double)(16.0F * volume) : 16.0D, this.dimension.getType(), new SPlaySoundEffectPacket(soundIn, category, x, y, z, volume, pitch));
   }

   public void playMovingSound(@Nullable PlayerEntity playerIn, Entity entityIn, SoundEvent eventIn, SoundCategory categoryIn, float volume, float pitch) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(playerIn, eventIn, categoryIn, volume, pitch);
      if (event.isCanceled() || event.getSound() == null) return;
      eventIn = event.getSound();
      categoryIn = event.getCategory();
      volume = event.getVolume();
      this.server.getPlayerList().sendToAllNearExcept(playerIn, entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), volume > 1.0F ? (double)(16.0F * volume) : 16.0D, this.dimension.getType(), new SSpawnMovingSoundEffectPacket(eventIn, categoryIn, entityIn, volume, pitch));
   }

   public void playBroadcastSound(int id, BlockPos pos, int data) {
      this.server.getPlayerList().sendPacketToAllPlayers(new SPlaySoundEventPacket(id, pos, data, true));
   }

   public void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data) {
      this.server.getPlayerList().sendToAllNearExcept(player, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 64.0D, this.dimension.getType(), new SPlaySoundEventPacket(type, pos, data, false));
   }

   /**
    * Flags are as in setBlockState
    */
   public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
      this.getChunkProvider().markBlockChanged(pos);
      VoxelShape voxelshape = oldState.getCollisionShape(this, pos);
      VoxelShape voxelshape1 = newState.getCollisionShape(this, pos);
      if (VoxelShapes.compare(voxelshape, voxelshape1, IBooleanFunction.NOT_SAME)) {
         for(PathNavigator pathnavigator : this.navigations) {
            if (!pathnavigator.canUpdatePathOnTimeout()) {
               pathnavigator.func_220970_c(pos);
            }
         }

      }
   }

   /**
    * sends a Packet 38 (Entity Status) to all tracked players of that entity
    */
   public void setEntityState(Entity entityIn, byte state) {
      this.getChunkProvider().sendToTrackingAndSelf(entityIn, new SEntityStatusPacket(entityIn, state));
   }

   /**
    * Gets the world's chunk provider
    */
   public ServerChunkProvider getChunkProvider() {
      return (ServerChunkProvider)super.getChunkProvider();
   }

   public Explosion createExplosion(@Nullable Entity entityIn, @Nullable DamageSource damageSourceIn, double xIn, double yIn, double zIn, float explosionRadius, boolean causesFire, Explosion.Mode modeIn) {
      Explosion explosion = new Explosion(this, entityIn, xIn, yIn, zIn, explosionRadius, causesFire, modeIn);
      if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this, explosion)) return explosion;
      if (damageSourceIn != null) {
         explosion.setDamageSource(damageSourceIn);
      }

      explosion.doExplosionA();
      explosion.doExplosionB(false);
      if (modeIn == Explosion.Mode.NONE) {
         explosion.clearAffectedBlockPositions();
      }

      for(ServerPlayerEntity serverplayerentity : this.players) {
         if (serverplayerentity.getDistanceSq(xIn, yIn, zIn) < 4096.0D) {
            serverplayerentity.connection.sendPacket(new SExplosionPacket(xIn, yIn, zIn, explosionRadius, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(serverplayerentity)));
         }
      }

      return explosion;
   }

   public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
      this.blockEventQueue.add(new BlockEventData(pos, blockIn, eventID, eventParam));
   }

   private void sendQueuedBlockEvents() {
      while(!this.blockEventQueue.isEmpty()) {
         BlockEventData blockeventdata = this.blockEventQueue.removeFirst();
         if (this.fireBlockEvent(blockeventdata)) {
            this.server.getPlayerList().sendToAllNearExcept((PlayerEntity)null, (double)blockeventdata.getPosition().getX(), (double)blockeventdata.getPosition().getY(), (double)blockeventdata.getPosition().getZ(), 64.0D, this.dimension.getType(), new SBlockActionPacket(blockeventdata.getPosition(), blockeventdata.getBlock(), blockeventdata.getEventID(), blockeventdata.getEventParameter()));
         }
      }

   }

   private boolean fireBlockEvent(BlockEventData event) {
      BlockState blockstate = this.getBlockState(event.getPosition());
      return blockstate.getBlock() == event.getBlock() ? blockstate.onBlockEventReceived(this, event.getPosition(), event.getEventID(), event.getEventParameter()) : false;
   }

   public ServerTickList<Block> getPendingBlockTicks() {
      return this.pendingBlockTicks;
   }

   public ServerTickList<Fluid> getPendingFluidTicks() {
      return this.pendingFluidTicks;
   }

   @Nonnull
   public MinecraftServer getServer() {
      return this.server;
   }

   public Teleporter getDefaultTeleporter() {
      return this.worldTeleporter;
   }

   public TemplateManager getStructureTemplateManager() {
      return this.saveHandler.getStructureTemplateManager();
   }

   public <T extends IParticleData> int spawnParticle(T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
      SSpawnParticlePacket sspawnparticlepacket = new SSpawnParticlePacket(type, false, posX, posY, posZ, (float)xOffset, (float)yOffset, (float)zOffset, (float)speed, particleCount);
      int i = 0;

      for(int j = 0; j < this.players.size(); ++j) {
         ServerPlayerEntity serverplayerentity = this.players.get(j);
         if (this.sendPacketWithinDistance(serverplayerentity, false, posX, posY, posZ, sspawnparticlepacket)) {
            ++i;
         }
      }

      return i;
   }

   public <T extends IParticleData> boolean spawnParticle(ServerPlayerEntity player, T type, boolean longDistance, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
      IPacket<?> ipacket = new SSpawnParticlePacket(type, longDistance, posX, posY, posZ, (float)xOffset, (float)yOffset, (float)zOffset, (float)speed, particleCount);
      return this.sendPacketWithinDistance(player, longDistance, posX, posY, posZ, ipacket);
   }

   private boolean sendPacketWithinDistance(ServerPlayerEntity player, boolean longDistance, double posX, double posY, double posZ, IPacket<?> packet) {
      if (player.getServerWorld() != this) {
         return false;
      } else {
         BlockPos blockpos = player.getPosition();
         if (blockpos.withinDistance(new Vec3d(posX, posY, posZ), longDistance ? 512.0D : 32.0D)) {
            player.connection.sendPacket(packet);
            return true;
         } else {
            return false;
         }
      }
   }

   /**
    * Returns the Entity with the given ID, or null if it doesn't exist in this World.
    */
   @Nullable
   public Entity getEntityByID(int id) {
      return this.entitiesById.get(id);
   }

   @Nullable
   public Entity getEntityByUuid(UUID uniqueId) {
      return this.entitiesByUuid.get(uniqueId);
   }

   @Nullable
   public BlockPos findNearestStructure(String name, BlockPos pos, int radius, boolean skipExistingChunks) {
      return this.getChunkProvider().getChunkGenerator().findNearestStructure(this, name, pos, radius, skipExistingChunks);
   }

   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public NetworkTagManager getTags() {
      return this.server.getNetworkTagManager();
   }

   public void setGameTime(long worldTime) {
      super.setGameTime(worldTime);
      this.worldInfo.getScheduledEvents().run(this.server, worldTime);
   }

   public boolean isSaveDisabled() {
      return this.disableLevelSaving;
   }

   public void checkSessionLock() throws SessionLockException {
      this.saveHandler.checkSessionLock();
   }

   public SaveHandler getSaveHandler() {
      return this.saveHandler;
   }

   public DimensionSavedDataManager getSavedData() {
      return this.getChunkProvider().getSavedData();
   }

   @Nullable
   public MapData getMapData(String mapName) {
      return this.getServer().getWorld(DimensionType.OVERWORLD).getSavedData().get(() -> {
         return new MapData(mapName);
      }, mapName);
   }

   public void registerMapData(MapData mapDataIn) {
      this.getServer().getWorld(DimensionType.OVERWORLD).getSavedData().set(mapDataIn);
   }

   public int getNextMapId() {
      return this.getServer().getWorld(DimensionType.OVERWORLD).getSavedData().getOrCreate(MapIdTracker::new, "idcounts").getNextId();
   }

   public void setSpawnPoint(BlockPos pos) {
      ChunkPos chunkpos = new ChunkPos(new BlockPos(this.worldInfo.getSpawnX(), 0, this.worldInfo.getSpawnZ()));
      super.setSpawnPoint(pos);
      this.getChunkProvider().releaseTicket(TicketType.START, chunkpos, 11, Unit.INSTANCE);
      this.getChunkProvider().registerTicket(TicketType.START, new ChunkPos(pos), 11, Unit.INSTANCE);
   }

   public LongSet getForcedChunks() {
      ForcedChunksSaveData forcedchunkssavedata = this.getSavedData().get(ForcedChunksSaveData::new, "chunks");
      return (LongSet)(forcedchunkssavedata != null ? LongSets.unmodifiable(forcedchunkssavedata.getChunks()) : LongSets.EMPTY_SET);
   }

   public boolean forceChunk(int chunkX, int chunkZ, boolean add) {
      ForcedChunksSaveData forcedchunkssavedata = this.getSavedData().getOrCreate(ForcedChunksSaveData::new, "chunks");
      ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
      long i = chunkpos.asLong();
      boolean flag;
      if (add) {
         flag = forcedchunkssavedata.getChunks().add(i);
         if (flag) {
            this.getChunk(chunkX, chunkZ);
         }
      } else {
         flag = forcedchunkssavedata.getChunks().remove(i);
      }

      forcedchunkssavedata.setDirty(flag);
      if (flag) {
         this.getChunkProvider().forceChunk(chunkpos, add);
      }

      return flag;
   }

   public List<ServerPlayerEntity> getPlayers() {
      return this.players;
   }

   public void onBlockStateChange(BlockPos pos, BlockState blockStateIn, BlockState newState) {
      Optional<PointOfInterestType> optional = PointOfInterestType.forState(blockStateIn);
      Optional<PointOfInterestType> optional1 = PointOfInterestType.forState(newState);
      if (!Objects.equals(optional, optional1)) {
         BlockPos blockpos = pos.toImmutable();
         optional.ifPresent((p_217476_2_) -> {
            this.getServer().execute(() -> {
               this.getPointOfInterestManager().remove(blockpos);
               DebugPacketSender.func_218805_b(this, blockpos);
            });
         });
         optional1.ifPresent((p_217457_2_) -> {
            this.getServer().execute(() -> {
               this.getPointOfInterestManager().add(blockpos, p_217457_2_);
               DebugPacketSender.func_218799_a(this, blockpos);
            });
         });
      }
   }

   public PointOfInterestManager getPointOfInterestManager() {
      return this.getChunkProvider().getPointOfInterestManager();
   }

   public boolean isVillage(BlockPos pos) {
      return this.isCloseToVillage(pos, 1);
   }

   public boolean isVillage(SectionPos pos) {
      return this.isVillage(pos.getCenter());
   }

   public boolean isCloseToVillage(BlockPos pos, int p_217471_2_) {
      if (p_217471_2_ > 6) {
         return false;
      } else {
         return this.sectionsToVillage(SectionPos.from(pos)) <= p_217471_2_;
      }
   }

   public int sectionsToVillage(SectionPos pos) {
      return this.getPointOfInterestManager().sectionsToVillage(pos);
   }

   public RaidManager getRaids() {
      return this.raids;
   }

   @Nullable
   public Raid findRaid(BlockPos pos) {
      return this.raids.findRaid(pos, 9216);
   }

   public boolean hasRaid(BlockPos pos) {
      return this.findRaid(pos) != null;
   }

   public void updateReputation(IReputationType type, Entity target, IReputationTracking host) {
      host.updateReputation(type, target);
   }

   public void writeDebugInfo(Path pathIn) throws IOException {
      ChunkManager chunkmanager = this.getChunkProvider().chunkManager;

      try (Writer writer = Files.newBufferedWriter(pathIn.resolve("stats.txt"))) {
         writer.write(String.format("spawning_chunks: %d\n", chunkmanager.getTicketManager().getSpawningChunksCount()));

         for(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<EntityClassification> entry : this.countEntities().object2IntEntrySet()) {
            writer.write(String.format("spawn_count.%s: %d\n", entry.getKey().getName(), entry.getIntValue()));
         }

         writer.write(String.format("entities: %d\n", this.entitiesById.size()));
         writer.write(String.format("block_entities: %d\n", this.loadedTileEntityList.size()));
         writer.write(String.format("block_ticks: %d\n", this.getPendingBlockTicks().func_225420_a()));
         writer.write(String.format("fluid_ticks: %d\n", this.getPendingFluidTicks().func_225420_a()));
         writer.write("distance_manager: " + chunkmanager.getTicketManager().func_225412_c() + "\n");
         writer.write(String.format("pending_tasks: %d\n", this.getChunkProvider().func_225314_f()));
      }

      CrashReport crashreport = new CrashReport("Level dump", new Exception("dummy"));
      this.fillCrashReport(crashreport);

      try (Writer writer1 = Files.newBufferedWriter(pathIn.resolve("example_crash.txt"))) {
         writer1.write(crashreport.getCompleteReport());
      }

      Path path = pathIn.resolve("chunks.csv");

      try (Writer writer2 = Files.newBufferedWriter(path)) {
         chunkmanager.func_225406_a(writer2);
      }

      Path path1 = pathIn.resolve("entities.csv");

      try (Writer writer3 = Files.newBufferedWriter(path1)) {
         dumpEntities(writer3, this.entitiesById.values());
      }

      Path path2 = pathIn.resolve("global_entities.csv");

      try (Writer writer4 = Files.newBufferedWriter(path2)) {
         dumpEntities(writer4, this.globalEntities);
      }

      Path path3 = pathIn.resolve("block_entities.csv");

      try (Writer writer5 = Files.newBufferedWriter(path3)) {
         this.dumpBlockEntities(writer5);
      }

   }

   private static void dumpEntities(Writer writerIn, Iterable<Entity> entities) throws IOException {
      CSVWriter csvwriter = CSVWriter.func_225428_a().func_225423_a("x").func_225423_a("y").func_225423_a("z").func_225423_a("uuid").func_225423_a("type").func_225423_a("alive").func_225423_a("display_name").func_225423_a("custom_name").func_225422_a(writerIn);

      for(Entity entity : entities) {
         ITextComponent itextcomponent = entity.getCustomName();
         ITextComponent itextcomponent1 = entity.getDisplayName();
         csvwriter.func_225426_a(entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.getUniqueID(), Registry.ENTITY_TYPE.getKey(entity.getType()), entity.isAlive(), itextcomponent1.getString(), itextcomponent != null ? itextcomponent.getString() : null);
      }

   }

   private void dumpBlockEntities(Writer writerIn) throws IOException {
      CSVWriter csvwriter = CSVWriter.func_225428_a().func_225423_a("x").func_225423_a("y").func_225423_a("z").func_225423_a("type").func_225422_a(writerIn);

      for(TileEntity tileentity : this.loadedTileEntityList) {
         BlockPos blockpos = tileentity.getPos();
         csvwriter.func_225426_a(blockpos.getX(), blockpos.getY(), blockpos.getZ(), Registry.BLOCK_ENTITY_TYPE.getKey(tileentity.getType()));
      }

   }

   @VisibleForTesting
   public void clearBlockEvents(MutableBoundingBox boundingBox) {
      this.blockEventQueue.removeIf((p_229855_1_) -> {
         return boundingBox.isVecInside(p_229855_1_.getPosition());
      });
   }

   protected void initCapabilities() {
      net.minecraftforge.common.capabilities.ICapabilityProvider parent = dimension.initCapabilities();
      this.gatherCapabilities(parent);
      capabilityData = this.getSavedData().getOrCreate(() -> new net.minecraftforge.common.util.WorldCapabilityData(getCapabilities()), net.minecraftforge.common.util.WorldCapabilityData.ID);
      capabilityData.setCapabilities(dimension, getCapabilities());
   }

   public java.util.stream.Stream<Entity> getEntities() {
       return entitiesById.values().stream();
   }
}