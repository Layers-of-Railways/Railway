package net.minecraft.world.storage;

import javax.annotation.Nullable;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DerivedWorldInfo extends WorldInfo {
   private final WorldInfo delegate;

   public DerivedWorldInfo(WorldInfo worldInfoIn) {
      this.delegate = worldInfoIn;
   }

   /**
    * Creates a new NBTTagCompound for the world, with the given NBTTag as the "Player"
    */
   public CompoundNBT cloneNBTCompound(@Nullable CompoundNBT nbt) {
      return this.delegate.cloneNBTCompound(nbt);
   }

   /**
    * Returns the seed of current world.
    */
   public long getSeed() {
      return this.delegate.getSeed();
   }

   /**
    * Returns the x spawn position
    */
   public int getSpawnX() {
      return this.delegate.getSpawnX();
   }

   /**
    * Return the Y axis spawning point of the player.
    */
   public int getSpawnY() {
      return this.delegate.getSpawnY();
   }

   /**
    * Returns the z spawn position
    */
   public int getSpawnZ() {
      return this.delegate.getSpawnZ();
   }

   public long getGameTime() {
      return this.delegate.getGameTime();
   }

   /**
    * Get current world time
    */
   public long getDayTime() {
      return this.delegate.getDayTime();
   }

   /**
    * Returns the player's NBTTagCompound to be loaded
    */
   public CompoundNBT getPlayerNBTTagCompound() {
      return this.delegate.getPlayerNBTTagCompound();
   }

   /**
    * Get current world name
    */
   public String getWorldName() {
      return this.delegate.getWorldName();
   }

   /**
    * Returns the save version of this world
    */
   public int getSaveVersion() {
      return this.delegate.getSaveVersion();
   }

   /**
    * Return the last time the player was in this world.
    */
   @OnlyIn(Dist.CLIENT)
   public long getLastTimePlayed() {
      return this.delegate.getLastTimePlayed();
   }

   /**
    * Returns true if it is thundering, false otherwise.
    */
   public boolean isThundering() {
      return this.delegate.isThundering();
   }

   /**
    * Returns the number of ticks until next thunderbolt.
    */
   public int getThunderTime() {
      return this.delegate.getThunderTime();
   }

   /**
    * Returns true if it is raining, false otherwise.
    */
   public boolean isRaining() {
      return this.delegate.isRaining();
   }

   /**
    * Return the number of ticks until rain.
    */
   public int getRainTime() {
      return this.delegate.getRainTime();
   }

   /**
    * Gets the GameType.
    */
   public GameType getGameType() {
      return this.delegate.getGameType();
   }

   /**
    * Set the x spawn position to the passed in value
    */
   @OnlyIn(Dist.CLIENT)
   public void setSpawnX(int x) {
   }

   /**
    * Sets the y spawn position
    */
   @OnlyIn(Dist.CLIENT)
   public void setSpawnY(int y) {
   }

   /**
    * Set the z spawn position to the passed in value
    */
   @OnlyIn(Dist.CLIENT)
   public void setSpawnZ(int z) {
   }

   public void setGameTime(long time) {
   }

   /**
    * Set current world time
    */
   public void setDayTime(long time) {
   }

   public void setSpawn(BlockPos spawnPoint) {
   }

   public void setWorldName(String worldName) {
   }

   /**
    * Sets the save version of the world
    */
   public void setSaveVersion(int version) {
   }

   /**
    * Sets whether it is thundering or not.
    */
   public void setThundering(boolean thunderingIn) {
   }

   /**
    * Defines the number of ticks until next thunderbolt.
    */
   public void setThunderTime(int time) {
   }

   /**
    * Sets whether it is raining or not.
    */
   public void setRaining(boolean isRaining) {
   }

   /**
    * Sets the number of ticks until rain.
    */
   public void setRainTime(int time) {
   }

   /**
    * Get whether the map features (e.g. strongholds) generation is enabled or disabled.
    */
   public boolean isMapFeaturesEnabled() {
      return this.delegate.isMapFeaturesEnabled();
   }

   /**
    * Returns true if hardcore mode is enabled, otherwise false
    */
   public boolean isHardcore() {
      return this.delegate.isHardcore();
   }

   public WorldType getGenerator() {
      return this.delegate.getGenerator();
   }

   public void setGenerator(WorldType type) {
   }

   /**
    * Returns true if commands are allowed on this World.
    */
   public boolean areCommandsAllowed() {
      return this.delegate.areCommandsAllowed();
   }

   public void setAllowCommands(boolean allow) {
   }

   /**
    * Returns true if the World is initialized.
    */
   public boolean isInitialized() {
      return this.delegate.isInitialized();
   }

   /**
    * Sets the initialization status of the World.
    */
   public void setInitialized(boolean initializedIn) {
   }

   /**
    * Gets the GameRules class Instance.
    */
   public GameRules getGameRulesInstance() {
      return this.delegate.getGameRulesInstance();
   }

   public Difficulty getDifficulty() {
      return this.delegate.getDifficulty();
   }

   public void setDifficulty(Difficulty newDifficulty) {
   }

   public boolean isDifficultyLocked() {
      return this.delegate.isDifficultyLocked();
   }

   public void setDifficultyLocked(boolean locked) {
   }

   public TimerCallbackManager<MinecraftServer> getScheduledEvents() {
      return this.delegate.getScheduledEvents();
   }

   public void setDimensionData(DimensionType dimensionIn, CompoundNBT compound) {
      this.delegate.setDimensionData(dimensionIn, compound);
   }

   public CompoundNBT getDimensionData(DimensionType dimensionIn) {
      return this.delegate.getDimensionData(dimensionIn);
   }

   /**
    * Adds this WorldInfo instance to the crash report.
    */
   public void addToCrashReport(CrashReportCategory category) {
      category.addDetail("Derived", true);
      this.delegate.addToCrashReport(category);
   }
}