package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.command.TimerCallbackSerializers;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldInfo {
   private String versionName;
   private int versionId;
   private boolean versionSnapshot;
   public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.NORMAL;
   private long randomSeed;
   private WorldType generator = WorldType.DEFAULT;
   private CompoundNBT generatorOptions = new CompoundNBT();
   @Nullable
   private String legacyCustomOptions;
   private int spawnX;
   private int spawnY;
   private int spawnZ;
   private long gameTime;
   private long dayTime;
   private long lastTimePlayed;
   private long sizeOnDisk;
   @Nullable
   private final DataFixer fixer;
   private final int dataVersion;
   private boolean playerDataFixed;
   private CompoundNBT playerData;
   private String levelName;
   private int saveVersion;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private GameType gameType;
   private boolean mapFeaturesEnabled;
   private boolean hardcore;
   private boolean allowCommands;
   private boolean initialized;
   private Difficulty difficulty;
   private boolean difficultyLocked;
   private double borderCenterX;
   private double borderCenterZ;
   private double borderSize = 6.0E7D;
   private long borderSizeLerpTime;
   private double borderSizeLerpTarget;
   private double borderSafeZone = 5.0D;
   private double borderDamagePerBlock = 0.2D;
   private int borderWarningBlocks = 5;
   private int borderWarningTime = 15;
   private final Set<String> disabledDataPacks = Sets.newHashSet();
   private final Set<String> enabledDataPacks = Sets.newLinkedHashSet();
   private final Map<DimensionType, CompoundNBT> dimensionData = Maps.newIdentityHashMap();
   private CompoundNBT customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   private UUID wanderingTraderId;
   private Set<String> field_230141_X_ = Sets.newLinkedHashSet();
   private boolean field_230142_Y_;
   private final GameRules gameRules = new GameRules();
   private final TimerCallbackManager<MinecraftServer> scheduledEvents = new TimerCallbackManager<>(TimerCallbackSerializers.field_216342_a);

   protected WorldInfo() {
      this.fixer = null;
      this.dataVersion = SharedConstants.getVersion().getWorldVersion();
      this.setGeneratorOptions(new CompoundNBT());
   }

   public WorldInfo(CompoundNBT nbt, DataFixer dataFixer, int dataVersionIn, @Nullable CompoundNBT playerDataIn) {
      this.fixer = dataFixer;
      ListNBT listnbt = nbt.getList("ServerBrands", 8);

      for(int i = 0; i < listnbt.size(); ++i) {
         this.field_230141_X_.add(listnbt.getString(i));
      }

      this.field_230142_Y_ = nbt.getBoolean("WasModded");
      if (nbt.contains("Version", 10)) {
         CompoundNBT compoundnbt = nbt.getCompound("Version");
         this.versionName = compoundnbt.getString("Name");
         this.versionId = compoundnbt.getInt("Id");
         this.versionSnapshot = compoundnbt.getBoolean("Snapshot");
      }

      this.randomSeed = nbt.getLong("RandomSeed");
      if (nbt.contains("generatorName", 8)) {
         String s1 = nbt.getString("generatorName");
         this.generator = WorldType.byName(s1);
         if (this.generator == null) {
            this.generator = WorldType.DEFAULT;
         } else if (this.generator == WorldType.CUSTOMIZED) {
            this.legacyCustomOptions = nbt.getString("generatorOptions");
         } else if (this.generator.isVersioned()) {
            int j = 0;
            if (nbt.contains("generatorVersion", 99)) {
               j = nbt.getInt("generatorVersion");
            }

            this.generator = this.generator.getWorldTypeForGeneratorVersion(j);
         }

         this.setGeneratorOptions(nbt.getCompound("generatorOptions"));
      }

      this.gameType = GameType.getByID(nbt.getInt("GameType"));
      if (nbt.contains("legacy_custom_options", 8)) {
         this.legacyCustomOptions = nbt.getString("legacy_custom_options");
      }

      if (nbt.contains("MapFeatures", 99)) {
         this.mapFeaturesEnabled = nbt.getBoolean("MapFeatures");
      } else {
         this.mapFeaturesEnabled = true;
      }

      this.spawnX = nbt.getInt("SpawnX");
      this.spawnY = nbt.getInt("SpawnY");
      this.spawnZ = nbt.getInt("SpawnZ");
      this.gameTime = nbt.getLong("Time");
      if (nbt.contains("DayTime", 99)) {
         this.dayTime = nbt.getLong("DayTime");
      } else {
         this.dayTime = this.gameTime;
      }

      this.lastTimePlayed = nbt.getLong("LastPlayed");
      this.sizeOnDisk = nbt.getLong("SizeOnDisk");
      this.levelName = nbt.getString("LevelName");
      this.saveVersion = nbt.getInt("version");
      this.clearWeatherTime = nbt.getInt("clearWeatherTime");
      this.rainTime = nbt.getInt("rainTime");
      this.raining = nbt.getBoolean("raining");
      this.thunderTime = nbt.getInt("thunderTime");
      this.thundering = nbt.getBoolean("thundering");
      this.hardcore = nbt.getBoolean("hardcore");
      if (nbt.contains("initialized", 99)) {
         this.initialized = nbt.getBoolean("initialized");
      } else {
         this.initialized = true;
      }

      if (nbt.contains("allowCommands", 99)) {
         this.allowCommands = nbt.getBoolean("allowCommands");
      } else {
         this.allowCommands = this.gameType == GameType.CREATIVE;
      }

      this.dataVersion = dataVersionIn;
      if (playerDataIn != null) {
         this.playerData = playerDataIn;
      }

      if (nbt.contains("GameRules", 10)) {
         this.gameRules.read(nbt.getCompound("GameRules"));
      }

      if (nbt.contains("Difficulty", 99)) {
         this.difficulty = Difficulty.byId(nbt.getByte("Difficulty"));
      }

      if (nbt.contains("DifficultyLocked", 1)) {
         this.difficultyLocked = nbt.getBoolean("DifficultyLocked");
      }

      if (nbt.contains("BorderCenterX", 99)) {
         this.borderCenterX = nbt.getDouble("BorderCenterX");
      }

      if (nbt.contains("BorderCenterZ", 99)) {
         this.borderCenterZ = nbt.getDouble("BorderCenterZ");
      }

      if (nbt.contains("BorderSize", 99)) {
         this.borderSize = nbt.getDouble("BorderSize");
      }

      if (nbt.contains("BorderSizeLerpTime", 99)) {
         this.borderSizeLerpTime = nbt.getLong("BorderSizeLerpTime");
      }

      if (nbt.contains("BorderSizeLerpTarget", 99)) {
         this.borderSizeLerpTarget = nbt.getDouble("BorderSizeLerpTarget");
      }

      if (nbt.contains("BorderSafeZone", 99)) {
         this.borderSafeZone = nbt.getDouble("BorderSafeZone");
      }

      if (nbt.contains("BorderDamagePerBlock", 99)) {
         this.borderDamagePerBlock = nbt.getDouble("BorderDamagePerBlock");
      }

      if (nbt.contains("BorderWarningBlocks", 99)) {
         this.borderWarningBlocks = nbt.getInt("BorderWarningBlocks");
      }

      if (nbt.contains("BorderWarningTime", 99)) {
         this.borderWarningTime = nbt.getInt("BorderWarningTime");
      }

      if (nbt.contains("DimensionData", 10)) {
         CompoundNBT compoundnbt1 = nbt.getCompound("DimensionData");

         for(String s : compoundnbt1.keySet()) {
            this.dimensionData.put(DimensionType.getById(Integer.parseInt(s)), compoundnbt1.getCompound(s));
         }
      }

      if (nbt.contains("DataPacks", 10)) {
         CompoundNBT compoundnbt2 = nbt.getCompound("DataPacks");
         ListNBT listnbt1 = compoundnbt2.getList("Disabled", 8);

         for(int l = 0; l < listnbt1.size(); ++l) {
            this.disabledDataPacks.add(listnbt1.getString(l));
         }

         ListNBT listnbt2 = compoundnbt2.getList("Enabled", 8);

         for(int k = 0; k < listnbt2.size(); ++k) {
            this.enabledDataPacks.add(listnbt2.getString(k));
         }
      }

      if (nbt.contains("CustomBossEvents", 10)) {
         this.customBossEvents = nbt.getCompound("CustomBossEvents");
      }

      if (nbt.contains("ScheduledEvents", 9)) {
         this.scheduledEvents.read(nbt.getList("ScheduledEvents", 10));
      }

      if (nbt.contains("WanderingTraderSpawnDelay", 99)) {
         this.wanderingTraderSpawnDelay = nbt.getInt("WanderingTraderSpawnDelay");
      }

      if (nbt.contains("WanderingTraderSpawnChance", 99)) {
         this.wanderingTraderSpawnChance = nbt.getInt("WanderingTraderSpawnChance");
      }

      if (nbt.contains("WanderingTraderId", 8)) {
         this.wanderingTraderId = UUID.fromString(nbt.getString("WanderingTraderId"));
      }

   }

   public WorldInfo(WorldSettings settings, String name) {
      this.fixer = null;
      this.dataVersion = SharedConstants.getVersion().getWorldVersion();
      this.populateFromWorldSettings(settings);
      this.levelName = name;
      this.difficulty = DEFAULT_DIFFICULTY;
      this.initialized = false;
   }

   public void populateFromWorldSettings(WorldSettings settings) {
      this.randomSeed = settings.getSeed();
      this.gameType = settings.getGameType();
      this.mapFeaturesEnabled = settings.isMapFeaturesEnabled();
      this.hardcore = settings.getHardcoreEnabled();
      this.generator = settings.getTerrainType();
      this.setGeneratorOptions((CompoundNBT)Dynamic.convert(JsonOps.INSTANCE, NBTDynamicOps.INSTANCE, settings.getGeneratorOptions()));
      this.allowCommands = settings.areCommandsAllowed();
   }

   /**
    * Creates a new NBTTagCompound for the world, with the given NBTTag as the "Player"
    */
   public CompoundNBT cloneNBTCompound(@Nullable CompoundNBT nbt) {
      this.fixPlayerData();
      if (nbt == null) {
         nbt = this.playerData;
      }

      CompoundNBT compoundnbt = new CompoundNBT();
      this.updateTagCompound(compoundnbt, nbt);
      return compoundnbt;
   }

   private void updateTagCompound(CompoundNBT nbt, CompoundNBT playerNbt) {
      ListNBT listnbt = new ListNBT();
      this.field_230141_X_.stream().map(StringNBT::valueOf).forEach(listnbt::add);
      nbt.put("ServerBrands", listnbt);
      nbt.putBoolean("WasModded", this.field_230142_Y_);
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", SharedConstants.getVersion().getName());
      compoundnbt.putInt("Id", SharedConstants.getVersion().getWorldVersion());
      compoundnbt.putBoolean("Snapshot", !SharedConstants.getVersion().isStable());
      nbt.put("Version", compoundnbt);
      nbt.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
      nbt.putLong("RandomSeed", this.randomSeed);
      nbt.putString("generatorName", this.generator.getSerialization());
      nbt.putInt("generatorVersion", this.generator.getVersion());
      if (!this.generatorOptions.isEmpty()) {
         nbt.put("generatorOptions", this.generatorOptions);
      }

      if (this.legacyCustomOptions != null) {
         nbt.putString("legacy_custom_options", this.legacyCustomOptions);
      }

      nbt.putInt("GameType", this.gameType.getID());
      nbt.putBoolean("MapFeatures", this.mapFeaturesEnabled);
      nbt.putInt("SpawnX", this.spawnX);
      nbt.putInt("SpawnY", this.spawnY);
      nbt.putInt("SpawnZ", this.spawnZ);
      nbt.putLong("Time", this.gameTime);
      nbt.putLong("DayTime", this.dayTime);
      nbt.putLong("SizeOnDisk", this.sizeOnDisk);
      nbt.putLong("LastPlayed", Util.millisecondsSinceEpoch());
      nbt.putString("LevelName", this.levelName);
      nbt.putInt("version", this.saveVersion);
      nbt.putInt("clearWeatherTime", this.clearWeatherTime);
      nbt.putInt("rainTime", this.rainTime);
      nbt.putBoolean("raining", this.raining);
      nbt.putInt("thunderTime", this.thunderTime);
      nbt.putBoolean("thundering", this.thundering);
      nbt.putBoolean("hardcore", this.hardcore);
      nbt.putBoolean("allowCommands", this.allowCommands);
      nbt.putBoolean("initialized", this.initialized);
      nbt.putDouble("BorderCenterX", this.borderCenterX);
      nbt.putDouble("BorderCenterZ", this.borderCenterZ);
      nbt.putDouble("BorderSize", this.borderSize);
      nbt.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
      nbt.putDouble("BorderSafeZone", this.borderSafeZone);
      nbt.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
      nbt.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
      nbt.putDouble("BorderWarningBlocks", (double)this.borderWarningBlocks);
      nbt.putDouble("BorderWarningTime", (double)this.borderWarningTime);
      if (this.difficulty != null) {
         nbt.putByte("Difficulty", (byte)this.difficulty.getId());
      }

      nbt.putBoolean("DifficultyLocked", this.difficultyLocked);
      nbt.put("GameRules", this.gameRules.write());
      CompoundNBT compoundnbt1 = new CompoundNBT();

      for(Entry<DimensionType, CompoundNBT> entry : this.dimensionData.entrySet()) {
         if (entry.getValue() == null || entry.getValue().isEmpty()) continue;
         compoundnbt1.put(String.valueOf(entry.getKey().getId()), entry.getValue());
      }

      nbt.put("DimensionData", compoundnbt1);
      if (playerNbt != null) {
         nbt.put("Player", playerNbt);
      }

      CompoundNBT compoundnbt2 = new CompoundNBT();
      ListNBT listnbt1 = new ListNBT();

      for(String s : this.enabledDataPacks) {
         listnbt1.add(StringNBT.valueOf(s));
      }

      compoundnbt2.put("Enabled", listnbt1);
      ListNBT listnbt2 = new ListNBT();

      for(String s1 : this.disabledDataPacks) {
         listnbt2.add(StringNBT.valueOf(s1));
      }

      compoundnbt2.put("Disabled", listnbt2);
      nbt.put("DataPacks", compoundnbt2);
      if (this.customBossEvents != null) {
         nbt.put("CustomBossEvents", this.customBossEvents);
      }

      nbt.put("ScheduledEvents", this.scheduledEvents.write());
      nbt.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
      nbt.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
      if (this.wanderingTraderId != null) {
         nbt.putString("WanderingTraderId", this.wanderingTraderId.toString());
      }

   }

   /**
    * Returns the seed of current world.
    */
   public long getSeed() {
      return this.randomSeed;
   }

   public static long byHashing(long p_227498_0_) {
      return Hashing.sha256().hashLong(p_227498_0_).asLong();
   }

   /**
    * Returns the x spawn position
    */
   public int getSpawnX() {
      return this.spawnX;
   }

   /**
    * Return the Y axis spawning point of the player.
    */
   public int getSpawnY() {
      return this.spawnY;
   }

   /**
    * Returns the z spawn position
    */
   public int getSpawnZ() {
      return this.spawnZ;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   /**
    * Get current world time
    */
   public long getDayTime() {
      return this.dayTime;
   }

   private void fixPlayerData() {
      if (!this.playerDataFixed && this.playerData != null) {
         if (this.dataVersion < SharedConstants.getVersion().getWorldVersion()) {
            if (this.fixer == null) {
               throw (NullPointerException)Util.pauseDevMode(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
            }

            this.playerData = NBTUtil.update(this.fixer, DefaultTypeReferences.PLAYER, this.playerData, this.dataVersion);
         }

         this.playerDataFixed = true;
      }
   }

   /**
    * Returns the player's NBTTagCompound to be loaded
    */
   public CompoundNBT getPlayerNBTTagCompound() {
      this.fixPlayerData();
      return this.playerData;
   }

   /**
    * Set the x spawn position to the passed in value
    */
   @OnlyIn(Dist.CLIENT)
   public void setSpawnX(int x) {
      this.spawnX = x;
   }

   /**
    * Sets the y spawn position
    */
   @OnlyIn(Dist.CLIENT)
   public void setSpawnY(int y) {
      this.spawnY = y;
   }

   /**
    * Set the z spawn position to the passed in value
    */
   @OnlyIn(Dist.CLIENT)
   public void setSpawnZ(int z) {
      this.spawnZ = z;
   }

   public void setGameTime(long time) {
      this.gameTime = time;
   }

   /**
    * Set current world time
    */
   public void setDayTime(long time) {
      this.dayTime = time;
   }

   public void setSpawn(BlockPos spawnPoint) {
      this.spawnX = spawnPoint.getX();
      this.spawnY = spawnPoint.getY();
      this.spawnZ = spawnPoint.getZ();
   }

   /**
    * Get current world name
    */
   public String getWorldName() {
      return this.levelName;
   }

   public void setWorldName(String worldName) {
      this.levelName = worldName;
   }

   /**
    * Returns the save version of this world
    */
   public int getSaveVersion() {
      return this.saveVersion;
   }

   /**
    * Sets the save version of the world
    */
   public void setSaveVersion(int version) {
      this.saveVersion = version;
   }

   /**
    * Return the last time the player was in this world.
    */
   @OnlyIn(Dist.CLIENT)
   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   public void setClearWeatherTime(int cleanWeatherTimeIn) {
      this.clearWeatherTime = cleanWeatherTimeIn;
   }

   /**
    * Returns true if it is thundering, false otherwise.
    */
   public boolean isThundering() {
      return this.thundering;
   }

   /**
    * Sets whether it is thundering or not.
    */
   public void setThundering(boolean thunderingIn) {
      this.thundering = thunderingIn;
   }

   /**
    * Returns the number of ticks until next thunderbolt.
    */
   public int getThunderTime() {
      return this.thunderTime;
   }

   /**
    * Defines the number of ticks until next thunderbolt.
    */
   public void setThunderTime(int time) {
      this.thunderTime = time;
   }

   /**
    * Returns true if it is raining, false otherwise.
    */
   public boolean isRaining() {
      return this.raining;
   }

   /**
    * Sets whether it is raining or not.
    */
   public void setRaining(boolean isRaining) {
      this.raining = isRaining;
   }

   /**
    * Return the number of ticks until rain.
    */
   public int getRainTime() {
      return this.rainTime;
   }

   /**
    * Sets the number of ticks until rain.
    */
   public void setRainTime(int time) {
      this.rainTime = time;
   }

   /**
    * Gets the GameType.
    */
   public GameType getGameType() {
      return this.gameType;
   }

   /**
    * Get whether the map features (e.g. strongholds) generation is enabled or disabled.
    */
   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public void setMapFeaturesEnabled(boolean enabled) {
      this.mapFeaturesEnabled = enabled;
   }

   /**
    * Sets the GameType.
    */
   public void setGameType(GameType type) {
      this.gameType = type;
   }

   /**
    * Returns true if hardcore mode is enabled, otherwise false
    */
   public boolean isHardcore() {
      return this.hardcore;
   }

   public void setHardcore(boolean hardcoreIn) {
      this.hardcore = hardcoreIn;
   }

   public WorldType getGenerator() {
      return this.generator;
   }

   public void setGenerator(WorldType type) {
      this.generator = type;
   }

   public CompoundNBT getGeneratorOptions() {
      return this.generatorOptions;
   }

   public void setGeneratorOptions(CompoundNBT p_212242_1_) {
      this.generatorOptions = p_212242_1_;
   }

   /**
    * Returns true if commands are allowed on this World.
    */
   public boolean areCommandsAllowed() {
      return this.allowCommands;
   }

   public void setAllowCommands(boolean allow) {
      this.allowCommands = allow;
   }

   /**
    * Returns true if the World is initialized.
    */
   public boolean isInitialized() {
      return this.initialized;
   }

   /**
    * Sets the initialization status of the World.
    */
   public void setInitialized(boolean initializedIn) {
      this.initialized = initializedIn;
   }

   /**
    * Gets the GameRules class Instance.
    */
   public GameRules getGameRulesInstance() {
      return this.gameRules;
   }

   /**
    * Returns the border center X position
    */
   public double getBorderCenterX() {
      return this.borderCenterX;
   }

   /**
    * Returns the border center Z position
    */
   public double getBorderCenterZ() {
      return this.borderCenterZ;
   }

   public double getBorderSize() {
      return this.borderSize;
   }

   /**
    * Sets the border size
    */
   public void setBorderSize(double size) {
      this.borderSize = size;
   }

   /**
    * Returns the border lerp time
    */
   public long getBorderSizeLerpTime() {
      return this.borderSizeLerpTime;
   }

   /**
    * Sets the border lerp time
    */
   public void setBorderSizeLerpTime(long time) {
      this.borderSizeLerpTime = time;
   }

   /**
    * Returns the border lerp target
    */
   public double getBorderSizeLerpTarget() {
      return this.borderSizeLerpTarget;
   }

   /**
    * Sets the border lerp target
    */
   public void setBorderSizeLerpTarget(double lerpSize) {
      this.borderSizeLerpTarget = lerpSize;
   }

   /**
    * Sets the border center Z position
    */
   public void setBorderCenterZ(double posZ) {
      this.borderCenterZ = posZ;
   }

   /**
    * Sets the border center X position
    */
   public void setBorderCenterX(double posX) {
      this.borderCenterX = posX;
   }

   /**
    * Returns the border safe zone
    */
   public double getBorderSafeZone() {
      return this.borderSafeZone;
   }

   /**
    * Sets the border safe zone
    */
   public void setBorderSafeZone(double amount) {
      this.borderSafeZone = amount;
   }

   /**
    * Returns the border damage per block
    */
   public double getBorderDamagePerBlock() {
      return this.borderDamagePerBlock;
   }

   /**
    * Sets the border damage per block
    */
   public void setBorderDamagePerBlock(double damage) {
      this.borderDamagePerBlock = damage;
   }

   /**
    * Returns the border warning distance
    */
   public int getBorderWarningBlocks() {
      return this.borderWarningBlocks;
   }

   /**
    * Returns the border warning time
    */
   public int getBorderWarningTime() {
      return this.borderWarningTime;
   }

   /**
    * Sets the border warning distance
    */
   public void setBorderWarningBlocks(int amountOfBlocks) {
      this.borderWarningBlocks = amountOfBlocks;
   }

   /**
    * Sets the border warning time
    */
   public void setBorderWarningTime(int ticks) {
      this.borderWarningTime = ticks;
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(Difficulty newDifficulty) {
      this.difficulty = newDifficulty;
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean locked) {
      this.difficultyLocked = locked;
   }

   public TimerCallbackManager<MinecraftServer> getScheduledEvents() {
      return this.scheduledEvents;
   }

   /**
    * Adds this WorldInfo instance to the crash report.
    */
   public void addToCrashReport(CrashReportCategory category) {
      category.addDetail("Level name", () -> {
         return this.levelName;
      });
      category.addDetail("Level seed", () -> {
         return String.valueOf(this.randomSeed);
      });
      category.addDetail("Level generator", () -> {
         return String.format("ID %02d - %s, ver %d. Features enabled: %b", this.generator.getId(), this.generator.getName(), this.generator.getVersion(), this.mapFeaturesEnabled);
      });
      category.addDetail("Level generator options", () -> {
         return this.generatorOptions.toString();
      });
      category.addDetail("Level spawn location", () -> {
         return CrashReportCategory.getCoordinateInfo(this.spawnX, this.spawnY, this.spawnZ);
      });
      category.addDetail("Level time", () -> {
         return String.format("%d game time, %d day time", this.gameTime, this.dayTime);
      });
      category.addDetail("Known server brands", () -> {
         return String.join(", ", this.field_230141_X_);
      });
      category.addDetail("Level was modded", () -> {
         return Boolean.toString(this.field_230142_Y_);
      });
      category.addDetail("Level storage version", () -> {
         String s = "Unknown?";

         try {
            switch(this.saveVersion) {
            case 19132:
               s = "McRegion";
               break;
            case 19133:
               s = "Anvil";
            }
         } catch (Throwable var3) {
            ;
         }

         return String.format("0x%05X - %s", this.saveVersion, s);
      });
      category.addDetail("Level weather", () -> {
         return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering);
      });
      category.addDetail("Level game mode", () -> {
         return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameType.getName(), this.gameType.getID(), this.hardcore, this.allowCommands);
      });
   }

   public CompoundNBT getDimensionData(DimensionType dimensionIn) {
      CompoundNBT compoundnbt = this.dimensionData.get(dimensionIn);
      return compoundnbt == null ? new CompoundNBT() : compoundnbt;
   }

   public void setDimensionData(DimensionType dimensionIn, CompoundNBT compound) {
      this.dimensionData.put(dimensionIn, compound);
   }

   @OnlyIn(Dist.CLIENT)
   public int getVersionId() {
      return this.versionId;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isVersionSnapshot() {
      return this.versionSnapshot;
   }

   @OnlyIn(Dist.CLIENT)
   public String getVersionName() {
      return this.versionName;
   }

   public Set<String> getDisabledDataPacks() {
      return this.disabledDataPacks;
   }

   public Set<String> getEnabledDataPacks() {
      return this.enabledDataPacks;
   }

   @Nullable
   public CompoundNBT getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable CompoundNBT p_201356_1_) {
      this.customBossEvents = p_201356_1_;
   }

   public int getWanderingTraderSpawnDelay() {
      return this.wanderingTraderSpawnDelay;
   }

   public void setWanderingTraderSpawnDelay(int p_215764_1_) {
      this.wanderingTraderSpawnDelay = p_215764_1_;
   }

   public int getWanderingTraderSpawnChance() {
      return this.wanderingTraderSpawnChance;
   }

   public void setWanderingTraderSpawnChance(int p_215762_1_) {
      this.wanderingTraderSpawnChance = p_215762_1_;
   }

   public void setWanderingTraderId(UUID p_215761_1_) {
      this.wanderingTraderId = p_215761_1_;
   }

   public void func_230145_a_(String p_230145_1_, boolean p_230145_2_) {
      this.field_230141_X_.add(p_230145_1_);
      this.field_230142_Y_ |= p_230145_2_;
   }
}