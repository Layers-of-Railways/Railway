package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.OpEntry;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.WhiteList;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.test.TestCollection;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.chunk.listener.LoggingChunkStatusListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerMultiWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraft.world.storage.CommandStorage;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataCallableSave;
import net.minecraft.world.storage.loot.LootPredicateManager;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends RecursiveEventLoop<TickDelayedTask> implements ISnooperInfo, ICommandSource, AutoCloseable, Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File USER_CACHE_FILE = new File("usercache.json");
   private static final CompletableFuture<Unit> field_223713_i = CompletableFuture.completedFuture(Unit.INSTANCE);
   public static final WorldSettings DEMO_WORLD_SETTINGS = (new WorldSettings((long)"North Carolina".hashCode(), GameType.SURVIVAL, true, false, WorldType.DEFAULT)).enableBonusChest();
   private final SaveFormat anvilConverterForAnvilFile;
   private final Snooper snooper = new Snooper("server", this, Util.milliTime());
   private final File anvilFile;
   private final List<Runnable> tickables = Lists.newArrayList();
   private final DebugProfiler profiler = new DebugProfiler(this::getTickCounter);
   private final NetworkSystem networkSystem;
   protected final IChunkStatusListenerFactory chunkStatusListenerFactory;
   private final ServerStatusResponse statusResponse = new ServerStatusResponse();
   private final Random random = new Random();
   private final DataFixer dataFixer;
   private String hostname;
   private int serverPort = -1;
   private final Map<DimensionType, ServerWorld> worlds = Maps.newIdentityHashMap();
   private PlayerList playerList;
   private volatile boolean serverRunning = true;
   private boolean serverStopped;
   private int tickCounter;
   protected final Proxy serverProxy;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean canSpawnAnimals;
   private boolean canSpawnNPCs;
   private boolean pvpEnabled;
   private boolean allowFlight;
   @Nullable
   private String motd;
   private int buildLimit;
   private int maxPlayerIdleMinutes;
   public final long[] tickTimeArray = new long[100];
   @Nullable
   private KeyPair serverKeyPair;
   @Nullable
   private String serverOwner;
   private final String folderName;
   @Nullable
   @OnlyIn(Dist.CLIENT)
   private String worldName;
   private boolean isDemo;
   private boolean enableBonusChest;
   /** The texture pack for the server */
   private String resourcePackUrl = "";
   private String resourcePackHash = "";
   private volatile boolean serverIsRunning;
   private long timeOfLastWarning;
   @Nullable
   private ITextComponent userMessage;
   private boolean startProfiling;
   private boolean isGamemodeForced;
   @Nullable
   private final YggdrasilAuthenticationService authService;
   private final MinecraftSessionService sessionService;
   private final GameProfileRepository profileRepo;
   private final PlayerProfileCache profileCache;
   private long nanoTimeSinceStatusRefresh;
   protected final Thread serverThread = Util.make(new Thread(net.minecraftforge.fml.common.thread.SidedThreadGroups.SERVER, this, "Server thread"), (p_213187_0_) -> {
      p_213187_0_.setUncaughtExceptionHandler((p_213206_0_, p_213206_1_) -> {
         LOGGER.error(p_213206_1_);
      });
   });
   protected long serverTime = Util.milliTime();
   private long runTasksUntil;
   private boolean isRunningScheduledTasks;
   @OnlyIn(Dist.CLIENT)
   private boolean worldIconSet;
   private final IReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA, this.serverThread);
   private final ResourcePackList<ResourcePackInfo> resourcePacks = new ResourcePackList<>(ResourcePackInfo::new);
   @Nullable
   private FolderPackFinder datapackFinder;
   private final Commands commandManager;
   private final RecipeManager recipeManager = new RecipeManager();
   private final NetworkTagManager networkTagManager = new NetworkTagManager();
   private final ServerScoreboard scoreboard = new ServerScoreboard(this);
   @Nullable
   private CommandStorage field_229733_al_;
   private final CustomServerBossInfoManager customBossEvents = new CustomServerBossInfoManager(this);
   private final LootPredicateManager field_229734_an_ = new LootPredicateManager();
   private final LootTableManager lootTableManager = new LootTableManager(this.field_229734_an_);
   private final AdvancementManager advancementManager = new AdvancementManager();
   private final FunctionManager functionManager = new FunctionManager(this);
   private final net.minecraftforge.common.loot.LootModifierManager lootManager = new net.minecraftforge.common.loot.LootModifierManager();
   private final FrameTimer frameTimer = new FrameTimer();
   private boolean whitelistEnabled;
   private boolean forceWorldUpgrade;
   private boolean eraseCache;
   private float tickTime;
   private final Executor backgroundExecutor;
   @Nullable
   private String serverId;

   public MinecraftServer(File p_i50590_1_, Proxy p_i50590_2_, DataFixer dataFixerIn, Commands p_i50590_4_, YggdrasilAuthenticationService p_i50590_5_, MinecraftSessionService p_i50590_6_, GameProfileRepository p_i50590_7_, PlayerProfileCache p_i50590_8_, IChunkStatusListenerFactory p_i50590_9_, String p_i50590_10_) {
      super("Server");
      this.serverProxy = p_i50590_2_;
      this.commandManager = p_i50590_4_;
      this.authService = p_i50590_5_;
      this.sessionService = p_i50590_6_;
      this.profileRepo = p_i50590_7_;
      this.profileCache = p_i50590_8_;
      this.anvilFile = p_i50590_1_;
      this.networkSystem = new NetworkSystem(this);
      this.chunkStatusListenerFactory = p_i50590_9_;
      this.anvilConverterForAnvilFile = new SaveFormat(p_i50590_1_.toPath(), p_i50590_1_.toPath().resolve("../backups"), dataFixerIn);
      this.dataFixer = dataFixerIn;
      this.resourceManager.addReloadListener(this.networkTagManager);
      this.resourceManager.addReloadListener(this.field_229734_an_);
      this.resourceManager.addReloadListener(this.recipeManager);
      this.resourceManager.addReloadListener(this.lootTableManager);
      this.resourceManager.addReloadListener(this.functionManager);
      this.resourceManager.addReloadListener(this.advancementManager);
      resourceManager.addReloadListener(lootManager);
      this.backgroundExecutor = Util.getServerExecutor();
      this.folderName = p_i50590_10_;
   }

   private void func_213204_a(DimensionSavedDataManager p_213204_1_) {
      ScoreboardSaveData scoreboardsavedata = p_213204_1_.getOrCreate(ScoreboardSaveData::new, "scoreboard");
      scoreboardsavedata.setScoreboard(this.getScoreboard());
      this.getScoreboard().addDirtyRunnable(new WorldSavedDataCallableSave(scoreboardsavedata));
   }

   /**
    * Initialises the server and starts it.
    */
   protected abstract boolean init() throws IOException;

   protected void convertMapIfNeeded(String worldNameIn) {
      if (this.getActiveAnvilConverter().isOldMapFormat(worldNameIn)) {
         LOGGER.info("Converting map!");
         this.setUserMessage(new TranslationTextComponent("menu.convertingLevel"));
         this.getActiveAnvilConverter().convertMapFormat(worldNameIn, new IProgressUpdate() {
            private long startTime = Util.milliTime();

            public void displaySavingString(ITextComponent component) {
            }

            @OnlyIn(Dist.CLIENT)
            public void resetProgressAndMessage(ITextComponent component) {
            }

            /**
             * Updates the progress bar on the loading screen to the specified amount.
             */
            public void setLoadingProgress(int progress) {
               if (Util.milliTime() - this.startTime >= 1000L) {
                  this.startTime = Util.milliTime();
                  MinecraftServer.LOGGER.info("Converting... {}%", (int)progress);
               }

            }

            @OnlyIn(Dist.CLIENT)
            public void setDoneWorking() {
            }

            public void displayLoadingString(ITextComponent component) {
            }
         });
      }

      if (this.forceWorldUpgrade) {
         LOGGER.info("Forcing world upgrade!");
         WorldInfo worldinfo = this.getActiveAnvilConverter().getWorldInfo(this.getFolderName());
         if (worldinfo != null) {
            WorldOptimizer worldoptimizer = new WorldOptimizer(this.getFolderName(), this.getActiveAnvilConverter(), worldinfo, this.eraseCache);
            ITextComponent itextcomponent = null;

            while(!worldoptimizer.isFinished()) {
               ITextComponent itextcomponent1 = worldoptimizer.getStatusText();
               if (itextcomponent != itextcomponent1) {
                  itextcomponent = itextcomponent1;
                  LOGGER.info(worldoptimizer.getStatusText().getString());
               }

               int i = worldoptimizer.getTotalChunks();
               if (i > 0) {
                  int j = worldoptimizer.getConverted() + worldoptimizer.getSkipped();
                  LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor((float)j / (float)i * 100.0F), j, i);
               }

               if (this.isServerStopped()) {
                  worldoptimizer.cancel();
               } else {
                  try {
                     Thread.sleep(1000L);
                  } catch (InterruptedException var8) {
                     ;
                  }
               }
            }
         }
      }

   }

   protected synchronized void setUserMessage(ITextComponent userMessageIn) {
      this.userMessage = userMessageIn;
   }

   protected void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, JsonElement generatorOptions) {
      this.convertMapIfNeeded(saveName);
      this.setUserMessage(new TranslationTextComponent("menu.loadingLevel"));
      SaveHandler savehandler = this.getActiveAnvilConverter().getSaveLoader(saveName, this);
      this.setResourcePackFromWorld(this.getFolderName(), savehandler);
      // Move factory creation earlier to prevent startupquery deadlock
      IChunkStatusListener ichunkstatuslistener = this.chunkStatusListenerFactory.create(11);
      WorldInfo worldinfo = savehandler.loadWorldInfo();
      WorldSettings worldsettings;
      if (worldinfo == null) {
         if (this.isDemo()) {
            worldsettings = DEMO_WORLD_SETTINGS;
         } else {
            worldsettings = new WorldSettings(seed, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), type);
            worldsettings.setGeneratorOptions(generatorOptions);
            if (this.enableBonusChest) {
               worldsettings.enableBonusChest();
            }
         }

         worldinfo = new WorldInfo(worldsettings, worldNameIn);
      } else {
         worldinfo.setWorldName(worldNameIn);
         worldsettings = new WorldSettings(worldinfo);
      }

      worldinfo.func_230145_a_(this.getServerModName(), this.func_230045_q_().isPresent());
      this.loadDataPacks(savehandler.getWorldDirectory(), worldinfo);
      this.loadWorlds(savehandler, worldinfo, worldsettings, ichunkstatuslistener);
      this.setDifficultyForAllWorlds(this.getDifficulty(), true);
      this.loadInitialChunks(ichunkstatuslistener);
   }

   protected void loadWorlds(SaveHandler saveHandlerIn, WorldInfo info, WorldSettings worldSettingsIn, IChunkStatusListener chunkStatusListenerIn) {
      net.minecraftforge.common.DimensionManager.fireRegister();
      if (this.isDemo()) {
         info.populateFromWorldSettings(DEMO_WORLD_SETTINGS);
      }

      ServerWorld serverworld = new ServerWorld(this, this.backgroundExecutor, saveHandlerIn, info, DimensionType.OVERWORLD, this.profiler, chunkStatusListenerIn);
      this.worlds.put(DimensionType.OVERWORLD, serverworld);
      DimensionSavedDataManager dimensionsaveddatamanager = serverworld.getSavedData();
      this.func_213204_a(dimensionsaveddatamanager);
      this.field_229733_al_ = new CommandStorage(dimensionsaveddatamanager);
      serverworld.getWorldBorder().copyFrom(info);
      ServerWorld serverworld1 = this.getWorld(DimensionType.OVERWORLD);
      if (!info.isInitialized()) {
         try {
            serverworld1.createSpawnPosition(worldSettingsIn);
            if (info.getGenerator() == WorldType.DEBUG_ALL_BLOCK_STATES) {
               this.applyDebugWorldInfo(info);
            }

            info.setInitialized(true);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception initializing level");

            try {
               serverworld1.fillCrashReport(crashreport);
            } catch (Throwable var11) {
               ;
            }

            throw new ReportedException(crashreport);
         }

         info.setInitialized(true);
      }

      this.getPlayerList().func_212504_a(serverworld1);
      if (info.getCustomBossEvents() != null) {
         this.getCustomBossEvents().read(info.getCustomBossEvents());
      }

      for(DimensionType dimensiontype : DimensionType.getAll()) {
         if (dimensiontype != DimensionType.OVERWORLD) {
            this.worlds.put(dimensiontype, new ServerMultiWorld(serverworld1, this, this.backgroundExecutor, saveHandlerIn, dimensiontype, this.profiler, chunkStatusListenerIn));
         }
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(worlds.get(dimensiontype)));
      }

   }

   private void applyDebugWorldInfo(WorldInfo worldInfoIn) {
      worldInfoIn.setMapFeaturesEnabled(false);
      worldInfoIn.setAllowCommands(true);
      worldInfoIn.setRaining(false);
      worldInfoIn.setThundering(false);
      worldInfoIn.setClearWeatherTime(1000000000);
      worldInfoIn.setDayTime(6000L);
      worldInfoIn.setGameType(GameType.SPECTATOR);
      worldInfoIn.setHardcore(false);
      worldInfoIn.setDifficulty(Difficulty.PEACEFUL);
      worldInfoIn.setDifficultyLocked(true);
      worldInfoIn.getGameRulesInstance().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, this);
   }

   protected void loadDataPacks(File p_195560_1_, WorldInfo p_195560_2_) {
      this.resourcePacks.addPackFinder(new ServerPackFinder());
      this.datapackFinder = new FolderPackFinder(new File(p_195560_1_, "datapacks"));
      this.resourcePacks.addPackFinder(this.datapackFinder);
      this.resourcePacks.reloadPacksFromFinders();
      List<ResourcePackInfo> list = Lists.newArrayList();

      for(String s : p_195560_2_.getEnabledDataPacks()) {
         ResourcePackInfo resourcepackinfo = this.resourcePacks.getPackInfo(s);
         if (resourcepackinfo != null) {
            list.add(resourcepackinfo);
         } else {
            LOGGER.warn("Missing data pack {}", (Object)s);
         }
      }

      this.resourcePacks.setEnabledPacks(list);
      this.loadDataPacks(p_195560_2_);
      this.func_229737_ba_();
   }

   /**
    * Loads the spawn chunks and any forced chunks
    */
   protected void loadInitialChunks(IChunkStatusListener p_213186_1_) {
      this.setUserMessage(new TranslationTextComponent("menu.generatingTerrain"));
      ServerWorld serverworld = this.getWorld(DimensionType.OVERWORLD);
      LOGGER.info("Preparing start region for dimension " + DimensionType.getKey(serverworld.dimension.getType()));
      BlockPos blockpos = serverworld.getSpawnPoint();
      p_213186_1_.start(new ChunkPos(blockpos));
      ServerChunkProvider serverchunkprovider = serverworld.getChunkProvider();
      serverchunkprovider.getLightManager().func_215598_a(500);
      this.serverTime = Util.milliTime();
      serverchunkprovider.registerTicket(TicketType.START, new ChunkPos(blockpos), 11, Unit.INSTANCE);

      while(serverchunkprovider.func_217229_b() != 441) {
         this.serverTime = Util.milliTime() + 10L;
         this.runScheduledTasks();
      }

      this.serverTime = Util.milliTime() + 10L;
      this.runScheduledTasks();

      for(DimensionType dimensiontype : DimensionType.getAll()) {
         ForcedChunksSaveData forcedchunkssavedata = this.getWorld(dimensiontype).getSavedData().get(ForcedChunksSaveData::new, "chunks");
         if (forcedchunkssavedata != null) {
            ServerWorld serverworld1 = this.getWorld(dimensiontype);
            LongIterator longiterator = forcedchunkssavedata.getChunks().iterator();

            while(longiterator.hasNext()) {
               long i = longiterator.nextLong();
               ChunkPos chunkpos = new ChunkPos(i);
               serverworld1.getChunkProvider().forceChunk(chunkpos, true);
            }
         }
      }

      this.serverTime = Util.milliTime() + 10L;
      this.runScheduledTasks();
      p_213186_1_.stop();
      serverchunkprovider.getLightManager().func_215598_a(5);
   }

   protected void setResourcePackFromWorld(String worldNameIn, SaveHandler saveHandlerIn) {
      File file1 = new File(saveHandlerIn.getWorldDirectory(), "resources.zip");
      if (file1.isFile()) {
         try {
            this.setResourcePack("level://" + URLEncoder.encode(worldNameIn, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
         } catch (UnsupportedEncodingException var5) {
            LOGGER.warn("Something went wrong url encoding {}", (Object)worldNameIn);
         }
      }

   }

   public abstract boolean canStructuresSpawn();

   public abstract GameType getGameType();

   /**
    * Get the server's difficulty
    */
   public abstract Difficulty getDifficulty();

   /**
    * Defaults to false.
    */
   public abstract boolean isHardcore();

   public abstract int getOpPermissionLevel();

   public abstract int getFunctionLevel();

   public abstract boolean allowLoggingRcon();

   public boolean save(boolean suppressLog, boolean flush, boolean forced) {
      boolean flag = false;

      for(ServerWorld serverworld : this.getWorlds()) {
         if (!suppressLog) {
            LOGGER.info("Saving chunks for level '{}'/{}", serverworld.getWorldInfo().getWorldName(), DimensionType.getKey(serverworld.dimension.getType()));
         }

         try {
            serverworld.save((IProgressUpdate)null, flush, serverworld.disableLevelSaving && !forced);
         } catch (SessionLockException sessionlockexception) {
            LOGGER.warn(sessionlockexception.getMessage());
         }

         flag = true;
      }

      ServerWorld serverworld1 = this.getWorld(DimensionType.OVERWORLD);
      WorldInfo worldinfo = serverworld1.getWorldInfo();
      serverworld1.getWorldBorder().copyTo(worldinfo);
      worldinfo.setCustomBossEvents(this.getCustomBossEvents().write());
      serverworld1.getSaveHandler().saveWorldInfoWithPlayer(worldinfo, this.getPlayerList().getHostPlayerData());
      return flag;
   }

   public void close() {
      this.stopServer();
   }

   /**
    * Saves all necessary data as preparation for stopping the server.
    */
   protected void stopServer() {
      LOGGER.info("Stopping server");
      if (this.getNetworkSystem() != null) {
         this.getNetworkSystem().terminateEndpoints();
      }

      if (this.playerList != null) {
         LOGGER.info("Saving players");
         this.playerList.saveAllPlayerData();
         this.playerList.removeAllPlayers();
      }

      LOGGER.info("Saving worlds");

      for(ServerWorld serverworld : this.getWorlds()) {
         if (serverworld != null) {
            serverworld.disableLevelSaving = false;
         }
      }

      this.save(false, true, false);

      for(ServerWorld serverworld1 : this.getWorlds()) {
         if (serverworld1 != null) {
            try {
               net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(serverworld1));
               serverworld1.close();
            } catch (IOException ioexception) {
               LOGGER.error("Exception closing the level", (Throwable)ioexception);
            }
         }
      }

      if (this.snooper.isSnooperRunning()) {
         this.snooper.stop();
      }

   }

   /**
    * "getHostname" is already taken, but both return the hostname.
    */
   public String getServerHostname() {
      return this.hostname;
   }

   public void setHostname(String host) {
      this.hostname = host;
   }

   public boolean isServerRunning() {
      return this.serverRunning;
   }

   /**
    * Sets the serverRunning variable to false, in order to get the server to shut down.
    */
   public void initiateShutdown(boolean waitForServer) {
      this.serverRunning = false;
      if (waitForServer) {
         try {
            this.serverThread.join();
         } catch (InterruptedException interruptedexception) {
            LOGGER.error("Error while shutting down", (Throwable)interruptedexception);
         }
      }

   }

   public void run() {
      try {
         if (this.init()) {
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStarted(this);
            this.serverTime = Util.milliTime();
            this.statusResponse.setServerDescription(new StringTextComponent(this.motd));
            this.statusResponse.setVersion(new ServerStatusResponse.Version(SharedConstants.getVersion().getName(), SharedConstants.getVersion().getProtocolVersion()));
            this.applyServerIconToResponse(this.statusResponse);

            while(this.serverRunning) {
               long i = Util.milliTime() - this.serverTime;
               if (i > 2000L && this.serverTime - this.timeOfLastWarning >= 15000L) {
                  long j = i / 50L;
                  LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", i, j);
                  this.serverTime += j * 50L;
                  this.timeOfLastWarning = this.serverTime;
               }

               this.serverTime += 50L;
               if (this.startProfiling) {
                  this.startProfiling = false;
                  this.profiler.getFixedProfiler().enable();
               }

               this.profiler.startTick();
               this.profiler.startSection("tick");
               this.tick(this::isAheadOfTime);
               this.profiler.endStartSection("nextTickWait");
               this.isRunningScheduledTasks = true;
               this.runTasksUntil = Math.max(Util.milliTime() + 50L, this.serverTime);
               this.runScheduledTasks();
               this.profiler.endSection();
               this.profiler.endTick();
               this.serverIsRunning = true;
            }
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStopping(this);
            net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
         } else {
            net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
            this.finalTick((CrashReport)null);
         }
      } catch (net.minecraftforge.fml.StartupQuery.AbortedException e) {
         // ignore silently
         net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
      } catch (Throwable throwable1) {
         LOGGER.error("Encountered an unexpected exception", throwable1);
         CrashReport crashreport;
         if (throwable1 instanceof ReportedException) {
            crashreport = this.addServerInfoToCrashReport(((ReportedException)throwable1).getCrashReport());
         } else {
            crashreport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable1));
         }

         File file1 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
         if (crashreport.saveToFile(file1)) {
            LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
         this.finalTick(crashreport);
      } finally {
         try {
            this.serverStopped = true;
            this.stopServer();
         } catch (Throwable throwable) {
            LOGGER.error("Exception stopping the server", throwable);
         } finally {
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStopped(this);
            this.systemExitNow();
         }

      }

   }

   private boolean isAheadOfTime() {
      return this.isTaskRunning() || Util.milliTime() < (this.isRunningScheduledTasks ? this.runTasksUntil : this.serverTime);
   }

   /**
    * Runs all pending tasks and waits for more tasks until serverTime is reached.
    */
   protected void runScheduledTasks() {
      this.drainTasks();
      this.driveUntil(() -> {
         return !this.isAheadOfTime();
      });
   }

   protected TickDelayedTask wrapTask(Runnable runnable) {
      return new TickDelayedTask(this.tickCounter, runnable);
   }

   protected boolean canRun(TickDelayedTask runnable) {
      return runnable.getScheduledTime() + 3 < this.tickCounter || this.isAheadOfTime();
   }

   public boolean driveOne() {
      boolean flag = this.driveOneInternal();
      this.isRunningScheduledTasks = flag;
      return flag;
   }

   private boolean driveOneInternal() {
      if (super.driveOne()) {
         return true;
      } else {
         if (this.isAheadOfTime()) {
            for(ServerWorld serverworld : this.getWorlds()) {
               if (serverworld.getChunkProvider().driveOneTask()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void run(TickDelayedTask taskIn) {
      this.getProfiler().func_230035_c_("runTask");
      super.run(taskIn);
   }

   public void applyServerIconToResponse(ServerStatusResponse response) {
      File file1 = this.getFile("server-icon.png");
      if (!file1.exists()) {
         file1 = this.getActiveAnvilConverter().getFile(this.getFolderName(), "icon.png");
      }

      if (file1.isFile()) {
         ByteBuf bytebuf = Unpooled.buffer();

         try {
            BufferedImage bufferedimage = ImageIO.read(file1);
            Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
            Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
            ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
            ByteBuffer bytebuffer = Base64.getEncoder().encode(bytebuf.nioBuffer());
            response.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(bytebuffer));
         } catch (Exception exception) {
            LOGGER.error("Couldn't load server icon", (Throwable)exception);
         } finally {
            bytebuf.release();
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isWorldIconSet() {
      this.worldIconSet = this.worldIconSet || this.getWorldIconFile().isFile();
      return this.worldIconSet;
   }

   @OnlyIn(Dist.CLIENT)
   public File getWorldIconFile() {
      return this.getActiveAnvilConverter().getFile(this.getFolderName(), "icon.png");
   }

   public File getDataDirectory() {
      return new File(".");
   }

   /**
    * Called on exit from the main run() loop.
    */
   protected void finalTick(CrashReport report) {
   }

   /**
    * Directly calls System.exit(0), instantly killing the program.
    */
   protected void systemExitNow() {
   }

   /**
    * Main function called by run() every loop.
    */
   protected void tick(BooleanSupplier hasTimeLeft) {
      long i = Util.nanoTime();
      net.minecraftforge.fml.hooks.BasicEventHooks.onPreServerTick();
      ++this.tickCounter;
      this.updateTimeLightAndEntities(hasTimeLeft);
      if (i - this.nanoTimeSinceStatusRefresh >= 5000000000L) {
         this.nanoTimeSinceStatusRefresh = i;
         this.statusResponse.setPlayers(new ServerStatusResponse.Players(this.getMaxPlayers(), this.getCurrentPlayerCount()));
         GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
         int j = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - agameprofile.length);

         for(int k = 0; k < agameprofile.length; ++k) {
            agameprofile[k] = this.playerList.getPlayers().get(j + k).getGameProfile();
         }

         Collections.shuffle(Arrays.asList(agameprofile));
         this.statusResponse.getPlayers().setPlayers(agameprofile);
         this.statusResponse.invalidateJson();
      }

      if (this.tickCounter % 6000 == 0) {
         LOGGER.debug("Autosave started");
         this.profiler.startSection("save");
         this.playerList.saveAllPlayerData();
         this.save(true, false, false);
         this.profiler.endSection();
         LOGGER.debug("Autosave finished");
      }

      this.profiler.startSection("snooper");
      if (!this.snooper.isSnooperRunning() && this.tickCounter > 100) {
         this.snooper.start();
      }

      if (this.tickCounter % 6000 == 0) {
         this.snooper.addMemoryStatsToSnooper();
      }

      this.profiler.endSection();
      this.profiler.startSection("tallying");
      long l = this.tickTimeArray[this.tickCounter % 100] = Util.nanoTime() - i;
      this.tickTime = this.tickTime * 0.8F + (float)l / 1000000.0F * 0.19999999F;
      long i1 = Util.nanoTime();
      this.frameTimer.addFrame(i1 - i);
      this.profiler.endSection();
      net.minecraftforge.fml.hooks.BasicEventHooks.onPostServerTick();
   }

   protected void updateTimeLightAndEntities(BooleanSupplier hasTimeLeft) {
      this.profiler.startSection("commandFunctions");
      this.getFunctionManager().tick();
      this.profiler.endStartSection("levels");

      for(ServerWorld serverworld : this.getWorldArray()) {
         long tickStart = Util.nanoTime();
         if (serverworld.dimension.getType() == DimensionType.OVERWORLD || this.getAllowNether()) {
            this.profiler.startSection(() -> {
               return serverworld.getWorldInfo().getWorldName() + " " + Registry.DIMENSION_TYPE.getKey(serverworld.dimension.getType());
            });
            if (this.tickCounter % 20 == 0) {
               this.profiler.startSection("timeSync");
               this.playerList.sendPacketToAllPlayersInDimension(new SUpdateTimePacket(serverworld.getGameTime(), serverworld.getDayTime(), serverworld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), serverworld.dimension.getType());
               this.profiler.endSection();
            }

            this.profiler.startSection("tick");
            net.minecraftforge.fml.hooks.BasicEventHooks.onPreWorldTick(serverworld);

            try {
               serverworld.tick(hasTimeLeft);
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception ticking world");
               serverworld.fillCrashReport(crashreport);
               throw new ReportedException(crashreport);
            }
            net.minecraftforge.fml.hooks.BasicEventHooks.onPostWorldTick(serverworld);

            this.profiler.endSection();
            this.profiler.endSection();
         }
         perWorldTickTimes.computeIfAbsent(serverworld.getDimension().getType(), k -> new long[100])[this.tickCounter % 100] = Util.nanoTime() - tickStart;
      }

      this.profiler.endStartSection("dim_unloading");
      net.minecraftforge.common.DimensionManager.unloadWorlds(this, this.tickCounter % 200 == 0);
      this.profiler.endStartSection("connection");
      this.getNetworkSystem().tick();
      this.profiler.endStartSection("players");
      this.playerList.tick();
      if (SharedConstants.developmentMode) {
         TestCollection.field_229570_a_.func_229574_b_();
      }

      this.profiler.endStartSection("server gui refresh");

      for(int i = 0; i < this.tickables.size(); ++i) {
         this.tickables.get(i).run();
      }

      this.profiler.endSection();
   }

   public boolean getAllowNether() {
      return true;
   }

   public void registerTickable(Runnable tickable) {
      this.tickables.add(tickable);
   }

   public static void main(String[] p_main_0_) {
      OptionParser optionparser = new OptionParser();
      OptionSpec<Void> optionspec = optionparser.accepts("nogui");
      OptionSpec<Void> optionspec1 = optionparser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
      OptionSpec<Void> optionspec2 = optionparser.accepts("demo");
      OptionSpec<Void> optionspec3 = optionparser.accepts("bonusChest");
      OptionSpec<Void> optionspec4 = optionparser.accepts("forceUpgrade");
      OptionSpec<Void> optionspec5 = optionparser.accepts("eraseCache");
      OptionSpec<Void> optionspec6 = optionparser.accepts("help").forHelp();
      OptionSpec<String> optionspec7 = optionparser.accepts("singleplayer").withRequiredArg();
      OptionSpec<String> optionspec8 = optionparser.accepts("universe").withRequiredArg().defaultsTo(".");
      OptionSpec<String> optionspec9 = optionparser.accepts("world").withRequiredArg();
      OptionSpec<Integer> optionspec10 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1);
      OptionSpec<String> optionspec11 = optionparser.accepts("serverId").withRequiredArg();
      OptionSpec<String> optionspec12 = optionparser.nonOptions();
      optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File(".")); //Forge: Consume this argument, we use it in the launcher, and the client side.

      try {
         OptionSet optionset = optionparser.parse(p_main_0_);
         if (optionset.has(optionspec6)) {
            optionparser.printHelpOn(System.err);
            return;
         }

         Path path = Paths.get("server.properties");
         ServerPropertiesProvider serverpropertiesprovider = new ServerPropertiesProvider(path);
         if (optionset.has(optionspec1) || !Files.exists(path)) serverpropertiesprovider.save();
         Path path1 = Paths.get("eula.txt");
         ServerEula servereula = new ServerEula(path1);
         if (optionset.has(optionspec1)) {
            LOGGER.info("Initialized '" + path.toAbsolutePath().toString() + "' and '" + path1.toAbsolutePath().toString() + "'");
            return;
         }

         if (!servereula.hasAcceptedEULA()) {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            return;
         }

         CrashReport.func_230188_h_();
         Bootstrap.register();
         Bootstrap.checkTranslations();
         String s = optionset.valueOf(optionspec8);
         YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
         MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(s, USER_CACHE_FILE.getName()));
         String s1 = Optional.ofNullable(optionset.valueOf(optionspec9)).orElse(serverpropertiesprovider.getProperties().worldName);
         if (s1 == null || s1.isEmpty() || new File(s, s1).getAbsolutePath().equals(new File(s).getAbsolutePath())) {
            LOGGER.error("Invalid world directory specified, must not be null, empty or the same directory as your universe! " + s1);
            return;
         }
         final DedicatedServer dedicatedserver = new DedicatedServer(new File(s), serverpropertiesprovider, DataFixesManager.getDataFixer(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache, LoggingChunkStatusListener::new, s1);
         dedicatedserver.setServerOwner(optionset.valueOf(optionspec7));
         dedicatedserver.setServerPort(optionset.valueOf(optionspec10));
         dedicatedserver.setDemo(optionset.has(optionspec2));
         dedicatedserver.canCreateBonusChest(optionset.has(optionspec3));
         dedicatedserver.setForceWorldUpgrade(optionset.has(optionspec4));
         dedicatedserver.setEraseCache(optionset.has(optionspec5));
         dedicatedserver.setServerId(optionset.valueOf(optionspec11));
         boolean flag = !optionset.has(optionspec) && !optionset.valuesOf(optionspec12).contains("nogui");
         if (flag && !GraphicsEnvironment.isHeadless()) {
            dedicatedserver.setGuiEnabled();
         }

         dedicatedserver.startServerThread();
         Thread thread = new Thread("Server Shutdown Thread") {
            public void run() {
               dedicatedserver.initiateShutdown(true);
               LogManager.shutdown(); // we're manually managing the logging shutdown on the server. Make sure we do it here at the end.
            }
         };
         thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(thread);
      } catch (Exception exception) {
         LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception);
      }

   }

   protected void setServerId(String serverIdIn) {
      this.serverId = serverIdIn;
   }

   protected void setForceWorldUpgrade(boolean forceWorldUpgradeIn) {
      this.forceWorldUpgrade = forceWorldUpgradeIn;
   }

   protected void setEraseCache(boolean eraseCacheIn) {
      this.eraseCache = eraseCacheIn;
   }

   public void startServerThread() {
      this.serverThread.start();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isThreadAlive() {
      return !this.serverThread.isAlive();
   }

   /**
    * Returns a File object from the specified string.
    */
   public File getFile(String fileName) {
      return new File(this.getDataDirectory(), fileName);
   }

   /**
    * Logs the message with a level of INFO.
    */
   public void logInfo(String msg) {
      LOGGER.info(msg);
   }

   /**
    * Logs the message with a level of WARN.
    */
   public void logWarning(String msg) {
      LOGGER.warn(msg);
   }

   /**
    * Gets the worldServer by the given dimension.
    */
   public ServerWorld getWorld(DimensionType dimension) {
      return net.minecraftforge.common.DimensionManager.getWorld(this, dimension, true, true);
   }

   public Iterable<ServerWorld> getWorlds() {
      return this.worlds.values();
   }

   /**
    * Returns the server's Minecraft version as string.
    */
   public String getMinecraftVersion() {
      return SharedConstants.getVersion().getName();
   }

   /**
    * Returns the number of players currently on the server.
    */
   public int getCurrentPlayerCount() {
      return this.playerList.getCurrentPlayerCount();
   }

   /**
    * Returns the maximum number of players allowed on the server.
    */
   public int getMaxPlayers() {
      return this.playerList.getMaxPlayers();
   }

   /**
    * Returns an array of the usernames of all the connected players.
    */
   public String[] getOnlinePlayerNames() {
      return this.playerList.getOnlinePlayerNames();
   }

   /**
    * Returns true if debugging is enabled, false otherwise.
    */
   public boolean isDebuggingEnabled() {
      return false;
   }

   /**
    * Logs the error message with a level of SEVERE.
    */
   public void logSevere(String msg) {
      LOGGER.error(msg);
   }

   /**
    * If isDebuggingEnabled(), logs the message with a level of INFO.
    */
   public void logDebug(String msg) {
      if (this.isDebuggingEnabled()) {
         LOGGER.info(msg);
      }

   }

   public String getServerModName() {
      return net.minecraftforge.fml.BrandingControl.getServerBranding();
   }

   /**
    * Adds the server info, including from theWorldServer, to the crash report.
    */
   public CrashReport addServerInfoToCrashReport(CrashReport report) {
      if (this.playerList != null) {
         report.getCategory().addDetail("Player Count", () -> {
            return this.playerList.getCurrentPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers();
         });
      }

      report.getCategory().addDetail("Data Packs", () -> {
         StringBuilder stringbuilder = new StringBuilder();

         for(ResourcePackInfo resourcepackinfo : this.resourcePacks.getEnabledPacks()) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(resourcepackinfo.getName());
            if (!resourcepackinfo.getCompatibility().isCompatible()) {
               stringbuilder.append(" (incompatible)");
            }
         }

         return stringbuilder.toString();
      });
      if (this.serverId != null) {
         report.getCategory().addDetail("Server Id", () -> {
            return this.serverId;
         });
      }

      return report;
   }

   public abstract Optional<String> func_230045_q_();

   public boolean isAnvilFileSet() {
      return this.anvilFile != null;
   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
      LOGGER.info(component.getString());
   }

   /**
    * Gets KeyPair instanced in MinecraftServer.
    */
   public KeyPair getKeyPair() {
      return this.serverKeyPair;
   }

   /**
    * Gets serverPort.
    */
   public int getServerPort() {
      return this.serverPort;
   }

   public void setServerPort(int port) {
      this.serverPort = port;
   }

   /**
    * Returns the username of the server owner (for integrated servers)
    */
   public String getServerOwner() {
      return this.serverOwner;
   }

   /**
    * Sets the username of the owner of this server (in the case of an integrated server)
    */
   public void setServerOwner(String owner) {
      this.serverOwner = owner;
   }

   public boolean isSinglePlayer() {
      return this.serverOwner != null;
   }

   public String getFolderName() {
      return this.folderName;
   }

   @OnlyIn(Dist.CLIENT)
   public void setWorldName(String worldNameIn) {
      this.worldName = worldNameIn;
   }

   @OnlyIn(Dist.CLIENT)
   public String getWorldName() {
      return this.worldName;
   }

   public void setKeyPair(KeyPair keyPair) {
      this.serverKeyPair = keyPair;
   }

   public void setDifficultyForAllWorlds(Difficulty difficulty, boolean p_147139_2_) {
      for(ServerWorld serverworld : this.getWorlds()) {
         WorldInfo worldinfo = serverworld.getWorldInfo();
         if (p_147139_2_ || !worldinfo.isDifficultyLocked()) {
            if (worldinfo.isHardcore()) {
               worldinfo.setDifficulty(Difficulty.HARD);
               serverworld.setAllowedSpawnTypes(true, true);
            } else if (this.isSinglePlayer()) {
               worldinfo.setDifficulty(difficulty);
               serverworld.setAllowedSpawnTypes(serverworld.getDifficulty() != Difficulty.PEACEFUL, true);
            } else {
               worldinfo.setDifficulty(difficulty);
               serverworld.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
            }
         }
      }

      this.getPlayerList().getPlayers().forEach(this::sendDifficultyToPlayer);
   }

   public void setDifficultyLocked(boolean locked) {
      for(ServerWorld serverworld : this.getWorlds()) {
         WorldInfo worldinfo = serverworld.getWorldInfo();
         worldinfo.setDifficultyLocked(locked);
      }

      this.getPlayerList().getPlayers().forEach(this::sendDifficultyToPlayer);
   }

   private void sendDifficultyToPlayer(ServerPlayerEntity playerIn) {
      WorldInfo worldinfo = playerIn.getServerWorld().getWorldInfo();
      playerIn.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
   }

   protected boolean allowSpawnMonsters() {
      return true;
   }

   /**
    * Gets whether this is a demo or not.
    */
   public boolean isDemo() {
      return this.isDemo;
   }

   /**
    * Sets whether this is a demo or not.
    */
   public void setDemo(boolean demo) {
      this.isDemo = demo;
   }

   public void canCreateBonusChest(boolean enable) {
      this.enableBonusChest = enable;
   }

   public SaveFormat getActiveAnvilConverter() {
      return this.anvilConverterForAnvilFile;
   }

   public String getResourcePackUrl() {
      return this.resourcePackUrl;
   }

   public String getResourcePackHash() {
      return this.resourcePackHash;
   }

   public void setResourcePack(String url, String hash) {
      this.resourcePackUrl = url;
      this.resourcePackHash = hash;
   }

   public void fillSnooper(Snooper snooper) {
      snooper.addClientStat("whitelist_enabled", false);
      snooper.addClientStat("whitelist_count", 0);
      if (this.playerList != null) {
         snooper.addClientStat("players_current", this.getCurrentPlayerCount());
         snooper.addClientStat("players_max", this.getMaxPlayers());
         snooper.addClientStat("players_seen", this.getWorld(DimensionType.OVERWORLD).getSaveHandler().func_215771_d().length);
      }

      snooper.addClientStat("uses_auth", this.onlineMode);
      snooper.addClientStat("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
      snooper.addClientStat("run_time", (Util.milliTime() - snooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
      snooper.addClientStat("avg_tick_ms", (int)(MathHelper.average(this.tickTimeArray) * 1.0E-6D));
      int i = 0;

      for(ServerWorld serverworld : this.getWorlds()) {
         if (serverworld != null) {
            WorldInfo worldinfo = serverworld.getWorldInfo();
            snooper.addClientStat("world[" + i + "][dimension]", serverworld.dimension.getType());
            snooper.addClientStat("world[" + i + "][mode]", worldinfo.getGameType());
            snooper.addClientStat("world[" + i + "][difficulty]", serverworld.getDifficulty());
            snooper.addClientStat("world[" + i + "][hardcore]", worldinfo.isHardcore());
            snooper.addClientStat("world[" + i + "][generator_name]", worldinfo.getGenerator().getName());
            snooper.addClientStat("world[" + i + "][generator_version]", worldinfo.getGenerator().getVersion());
            snooper.addClientStat("world[" + i + "][height]", this.buildLimit);
            snooper.addClientStat("world[" + i + "][chunks_loaded]", serverworld.getChunkProvider().getLoadedChunkCount());
            ++i;
         }
      }

      snooper.addClientStat("worlds", i);
   }

   public abstract boolean isDedicatedServer();

   public boolean isServerInOnlineMode() {
      return this.onlineMode;
   }

   public void setOnlineMode(boolean online) {
      this.onlineMode = online;
   }

   public boolean getPreventProxyConnections() {
      return this.preventProxyConnections;
   }

   public void setPreventProxyConnections(boolean p_190517_1_) {
      this.preventProxyConnections = p_190517_1_;
   }

   public boolean getCanSpawnAnimals() {
      return this.canSpawnAnimals;
   }

   public void setCanSpawnAnimals(boolean spawnAnimals) {
      this.canSpawnAnimals = spawnAnimals;
   }

   public boolean getCanSpawnNPCs() {
      return this.canSpawnNPCs;
   }

   /**
    * Get if native transport should be used. Native transport means linux server performance improvements and optimized
    * packet sending/receiving on linux
    */
   public abstract boolean shouldUseNativeTransport();

   public void setCanSpawnNPCs(boolean spawnNpcs) {
      this.canSpawnNPCs = spawnNpcs;
   }

   public boolean isPVPEnabled() {
      return this.pvpEnabled;
   }

   public void setAllowPvp(boolean allowPvp) {
      this.pvpEnabled = allowPvp;
   }

   public boolean isFlightAllowed() {
      return this.allowFlight;
   }

   public void setAllowFlight(boolean allow) {
      this.allowFlight = allow;
   }

   /**
    * Return whether command blocks are enabled.
    */
   public abstract boolean isCommandBlockEnabled();

   public String getMOTD() {
      return this.motd;
   }

   public void setMOTD(String motdIn) {
      this.motd = motdIn;
   }

   public int getBuildLimit() {
      return this.buildLimit;
   }

   public void setBuildLimit(int maxBuildHeight) {
      this.buildLimit = maxBuildHeight;
   }

   public boolean isServerStopped() {
      return this.serverStopped;
   }

   public PlayerList getPlayerList() {
      return this.playerList;
   }

   public void setPlayerList(PlayerList list) {
      this.playerList = list;
   }

   /**
    * Returns true if this integrated server is open to LAN
    */
   public abstract boolean getPublic();

   /**
    * Sets the game type for all worlds.
    */
   public void setGameType(GameType gameMode) {
      for(ServerWorld serverworld : this.getWorlds()) {
         serverworld.getWorldInfo().setGameType(gameMode);
      }

   }

   @Nullable
   public NetworkSystem getNetworkSystem() {
      return this.networkSystem;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean serverIsInRunLoop() {
      return this.serverIsRunning;
   }

   public boolean getGuiEnabled() {
      return false;
   }

   public abstract boolean shareToLAN(GameType gameMode, boolean cheats, int port);

   public int getTickCounter() {
      return this.tickCounter;
   }

   public void enableProfiling() {
      this.startProfiling = true;
   }

   @OnlyIn(Dist.CLIENT)
   public Snooper getSnooper() {
      return this.snooper;
   }

   /**
    * Return the spawn protection area's size.
    */
   public int getSpawnProtectionSize() {
      return 16;
   }

   public boolean isBlockProtected(World worldIn, BlockPos pos, PlayerEntity playerIn) {
      return false;
   }

   /**
    * Set the forceGamemode field (whether joining players will be put in their old gamemode or the default one)
    */
   public void setForceGamemode(boolean force) {
      this.isGamemodeForced = force;
   }

   /**
    * Get the forceGamemode field (whether joining players will be put in their old gamemode or the default one)
    */
   public boolean getForceGamemode() {
      return this.isGamemodeForced;
   }

   public int getMaxPlayerIdleMinutes() {
      return this.maxPlayerIdleMinutes;
   }

   public void setPlayerIdleTimeout(int idleTimeout) {
      this.maxPlayerIdleMinutes = idleTimeout;
   }

   public MinecraftSessionService getMinecraftSessionService() {
      return this.sessionService;
   }

   public GameProfileRepository getGameProfileRepository() {
      return this.profileRepo;
   }

   public PlayerProfileCache getPlayerProfileCache() {
      return this.profileCache;
   }

   public ServerStatusResponse getServerStatusResponse() {
      return this.statusResponse;
   }

   public void refreshStatusNextTick() {
      this.nanoTimeSinceStatusRefresh = 0L;
   }

   public int getMaxWorldSize() {
      return 29999984;
   }

   public boolean shouldDeferTasks() {
      return super.shouldDeferTasks() && !this.isServerStopped();
   }

   public Thread getExecutionThread() {
      return this.serverThread;
   }

   /**
    * The compression treshold. If the packet is larger than the specified amount of bytes, it will be compressed
    */
   public int getNetworkCompressionThreshold() {
      return 256;
   }

   public long getServerTime() {
      return this.serverTime;
   }

   public DataFixer getDataFixer() {
      return this.dataFixer;
   }

   public int getSpawnRadius(@Nullable ServerWorld worldIn) {
      return worldIn != null ? worldIn.getGameRules().getInt(GameRules.SPAWN_RADIUS) : 10;
   }

   public AdvancementManager getAdvancementManager() {
      return this.advancementManager;
   }

   public FunctionManager getFunctionManager() {
      return this.functionManager;
   }
   
   public net.minecraftforge.common.loot.LootModifierManager getLootModifierManager() {
	   return lootManager;
   }

   public void reload() {
      if (!this.isOnExecutionThread()) {
         this.execute(this::reload);
      } else {
         this.getPlayerList().saveAllPlayerData();
         this.resourcePacks.reloadPacksFromFinders();
         this.loadDataPacks(this.getWorld(DimensionType.OVERWORLD).getWorldInfo());
         this.getPlayerList().reloadResources();
         this.func_229737_ba_();
      }
   }

   private void loadDataPacks(WorldInfo worldInfoIn) {
      List<ResourcePackInfo> list = Lists.newArrayList(this.resourcePacks.getEnabledPacks());

      for(ResourcePackInfo resourcepackinfo : this.resourcePacks.getAllPacks()) {
         if (!worldInfoIn.getDisabledDataPacks().contains(resourcepackinfo.getName()) && !list.contains(resourcepackinfo)) {
            LOGGER.info("Found new data pack {}, loading it automatically", (Object)resourcepackinfo.getName());
            resourcepackinfo.getPriority().insert(list, resourcepackinfo, (p_200247_0_) -> {
               return p_200247_0_;
            }, false);
         }
      }

      this.resourcePacks.setEnabledPacks(list);
      List<IResourcePack> list1 = Lists.newArrayList();
      this.resourcePacks.getEnabledPacks().forEach((p_200244_1_) -> {
         list1.add(p_200244_1_.getResourcePack());
      });
      CompletableFuture<Unit> completablefuture = this.resourceManager.reloadResourcesAndThen(this.backgroundExecutor, this, list1, field_223713_i);
      this.driveUntil(completablefuture::isDone);

      try {
         completablefuture.get();
      } catch (Exception exception) {
         LOGGER.error("Failed to reload data packs", (Throwable)exception);
      }

      worldInfoIn.getEnabledDataPacks().clear();
      worldInfoIn.getDisabledDataPacks().clear();
      this.resourcePacks.getEnabledPacks().forEach((p_195562_1_) -> {
         worldInfoIn.getEnabledDataPacks().add(p_195562_1_.getName());
      });
      this.resourcePacks.getAllPacks().forEach((p_200248_2_) -> {
         if (!this.resourcePacks.getEnabledPacks().contains(p_200248_2_)) {
            worldInfoIn.getDisabledDataPacks().add(p_200248_2_.getName());
         }

      });
   }

   public void kickPlayersNotWhitelisted(CommandSource commandSourceIn) {
      if (this.isWhitelistEnabled()) {
         PlayerList playerlist = commandSourceIn.getServer().getPlayerList();
         WhiteList whitelist = playerlist.getWhitelistedPlayers();
         if (whitelist.isLanServer()) {
            for(ServerPlayerEntity serverplayerentity : Lists.newArrayList(playerlist.getPlayers())) {
               if (!whitelist.isWhitelisted(serverplayerentity.getGameProfile())) {
                  serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.not_whitelisted"));
               }
            }

         }
      }
   }

   public IReloadableResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public ResourcePackList<ResourcePackInfo> getResourcePacks() {
      return this.resourcePacks;
   }

   public Commands getCommandManager() {
      return this.commandManager;
   }

   public CommandSource getCommandSource() {
      return new CommandSource(this, this.getWorld(DimensionType.OVERWORLD) == null ? Vec3d.ZERO : new Vec3d(this.getWorld(DimensionType.OVERWORLD).getSpawnPoint()), Vec2f.ZERO, this.getWorld(DimensionType.OVERWORLD), 4, "Server", new StringTextComponent("Server"), this, (Entity)null);
   }

   public boolean shouldReceiveFeedback() {
      return true;
   }

   public boolean shouldReceiveErrors() {
      return true;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public NetworkTagManager getNetworkTagManager() {
      return this.networkTagManager;
   }

   public ServerScoreboard getScoreboard() {
      return this.scoreboard;
   }

   public CommandStorage func_229735_aN_() {
      if (this.field_229733_al_ == null) {
         throw new NullPointerException("Called before server init");
      } else {
         return this.field_229733_al_;
      }
   }

   public LootTableManager getLootTableManager() {
      return this.lootTableManager;
   }

   public LootPredicateManager func_229736_aP_() {
      return this.field_229734_an_;
   }

   public GameRules getGameRules() {
      return this.getWorld(DimensionType.OVERWORLD).getGameRules();
   }

   public CustomServerBossInfoManager getCustomBossEvents() {
      return this.customBossEvents;
   }

   public boolean isWhitelistEnabled() {
      return this.whitelistEnabled;
   }

   public void setWhitelistEnabled(boolean whitelistEnabledIn) {
      this.whitelistEnabled = whitelistEnabledIn;
   }

   public float getTickTime() {
      return this.tickTime;
   }

   public int getPermissionLevel(GameProfile profile) {
      if (this.getPlayerList().canSendCommands(profile)) {
         OpEntry opentry = this.getPlayerList().getOppedPlayers().getEntry(profile);
         if (opentry != null) {
            return opentry.getPermissionLevel();
         } else if (this.isServerOwner(profile)) {
            return 4;
         } else if (this.isSinglePlayer()) {
            return this.getPlayerList().commandsAllowedForAll() ? 4 : 0;
         } else {
            return this.getOpPermissionLevel();
         }
      } else {
         return 0;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public DebugProfiler getProfiler() {
      return this.profiler;
   }

   public Executor getBackgroundExecutor() {
      return this.backgroundExecutor;
   }

   public abstract boolean isServerOwner(GameProfile profileIn);

   private Map<DimensionType, long[]> perWorldTickTimes = Maps.newIdentityHashMap();
   @Nullable
   public long[] getTickTime(DimensionType dim) {
      return perWorldTickTimes.get(dim);
   }

   @Deprecated //Forge Internal use Only, You can screw up a lot of things if you mess with this map.
   public synchronized Map<DimensionType, ServerWorld> forgeGetWorldMap() {
      return this.worlds;
   }
   private int worldArrayMarker = 0;
   private int worldArrayLast = -1;
   private ServerWorld[] worldArray;
   @Deprecated //Forge Internal use Only, use to protect against concurrent modifications in the world tick loop.
   public synchronized void markWorldsDirty() {
      worldArrayMarker++;
   }
   private ServerWorld[] getWorldArray() {
      if (worldArrayMarker == worldArrayLast && worldArray != null)
         return worldArray;
      worldArray = this.worlds.values().stream().toArray(x -> new ServerWorld[x]);
      worldArrayLast = worldArrayMarker;
      return worldArray;
   }

   public void dumpDebugInfo(Path p_223711_1_) throws IOException {
      Path path = p_223711_1_.resolve("levels");

      for(Entry<DimensionType, ServerWorld> entry : this.worlds.entrySet()) {
         ResourceLocation resourcelocation = DimensionType.getKey(entry.getKey());
         Path path1 = path.resolve(resourcelocation.getNamespace()).resolve(resourcelocation.getPath());
         Files.createDirectories(path1);
         entry.getValue().writeDebugInfo(path1);
      }

      this.dumpGameRules(p_223711_1_.resolve("gamerules.txt"));
      this.dumpClasspath(p_223711_1_.resolve("classpath.txt"));
      this.dumpDummyCrashReport(p_223711_1_.resolve("example_crash.txt"));
      this.dumpStats(p_223711_1_.resolve("stats.txt"));
      this.dumpThreads(p_223711_1_.resolve("threads.txt"));
   }

   private void dumpStats(Path p_223710_1_) throws IOException {
      try (Writer writer = Files.newBufferedWriter(p_223710_1_)) {
         writer.write(String.format("pending_tasks: %d\n", this.getQueueSize()));
         writer.write(String.format("average_tick_time: %f\n", this.getTickTime()));
         writer.write(String.format("tick_times: %s\n", Arrays.toString(this.tickTimeArray)));
         writer.write(String.format("queue: %s\n", Util.getServerExecutor()));
      }

   }

   private void dumpDummyCrashReport(Path p_223709_1_) throws IOException {
      CrashReport crashreport = new CrashReport("Server dump", new Exception("dummy"));
      this.addServerInfoToCrashReport(crashreport);

      try (Writer writer = Files.newBufferedWriter(p_223709_1_)) {
         writer.write(crashreport.getCompleteReport());
      }

   }

   private void dumpGameRules(Path p_223708_1_) throws IOException {
      try (Writer writer = Files.newBufferedWriter(p_223708_1_)) {
         final List<String> list = Lists.newArrayList();
         final GameRules gamerules = this.getGameRules();
         GameRules.visitAll(new GameRules.IRuleEntryVisitor() {
            public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> key, GameRules.RuleType<T> type) {
               list.add(String.format("%s=%s\n", key.getName(), gamerules.<T>get(key).toString()));
            }
         });

         for(String s : list) {
            writer.write(s);
         }
      }

   }

   private void dumpClasspath(Path p_223706_1_) throws IOException {
      try (Writer writer = Files.newBufferedWriter(p_223706_1_)) {
         String s = System.getProperty("java.class.path");
         String s1 = System.getProperty("path.separator");

         for(String s2 : Splitter.on(s1).split(s)) {
            writer.write(s2);
            writer.write("\n");
         }
      }

   }

   private void dumpThreads(Path p_223712_1_) throws IOException {
      ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
      ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
      Arrays.sort(athreadinfo, Comparator.comparing(ThreadInfo::getThreadName));

      try (Writer writer = Files.newBufferedWriter(p_223712_1_)) {
         for(ThreadInfo threadinfo : athreadinfo) {
            writer.write(threadinfo.toString());
            writer.write(10);
         }
      }

   }

   private void func_229737_ba_() {
      Block.BLOCK_STATE_IDS.forEach(BlockState::cacheState);
   }
}