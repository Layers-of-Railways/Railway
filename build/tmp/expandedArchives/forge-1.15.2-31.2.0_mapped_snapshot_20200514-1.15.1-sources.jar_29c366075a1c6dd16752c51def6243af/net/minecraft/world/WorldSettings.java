package net.minecraft.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class WorldSettings {
   private final long seed;
   private final GameType gameType;
   private final boolean mapFeaturesEnabled;
   private final boolean hardcoreEnabled;
   private final WorldType terrainType;
   private boolean commandsAllowed;
   private boolean bonusChestEnabled;
   private JsonElement generatorOptions = new JsonObject();

   public WorldSettings(long seedIn, GameType gameType, boolean enableMapFeatures, boolean hardcoreMode, WorldType worldTypeIn) {
      this.seed = seedIn;
      this.gameType = gameType;
      this.mapFeaturesEnabled = enableMapFeatures;
      this.hardcoreEnabled = hardcoreMode;
      this.terrainType = worldTypeIn;
   }

   public WorldSettings(WorldInfo info) {
      this(info.getSeed(), info.getGameType(), info.isMapFeaturesEnabled(), info.isHardcore(), info.getGenerator());
   }

   /**
    * Enables the bonus chest.
    */
   public WorldSettings enableBonusChest() {
      this.bonusChestEnabled = true;
      return this;
   }

   /**
    * Enables Commands (cheats).
    */
   @OnlyIn(Dist.CLIENT)
   public WorldSettings enableCommands() {
      this.commandsAllowed = true;
      return this;
   }

   public WorldSettings setGeneratorOptions(JsonElement p_205390_1_) {
      this.generatorOptions = p_205390_1_;
      return this;
   }

   /**
    * Returns true if the Bonus Chest is enabled.
    */
   public boolean isBonusChestEnabled() {
      return this.bonusChestEnabled;
   }

   /**
    * Returns the seed for the world.
    */
   public long getSeed() {
      return this.seed;
   }

   /**
    * Gets the game type.
    */
   public GameType getGameType() {
      return this.gameType;
   }

   /**
    * Returns true if hardcore mode is enabled, otherwise false
    */
   public boolean getHardcoreEnabled() {
      return this.hardcoreEnabled;
   }

   /**
    * Get whether the map features (e.g. strongholds) generation is enabled or disabled.
    */
   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public WorldType getTerrainType() {
      return this.terrainType;
   }

   /**
    * Returns true if Commands (cheats) are allowed.
    */
   public boolean areCommandsAllowed() {
      return this.commandsAllowed;
   }

   public JsonElement getGeneratorOptions() {
      return this.generatorOptions;
   }
}