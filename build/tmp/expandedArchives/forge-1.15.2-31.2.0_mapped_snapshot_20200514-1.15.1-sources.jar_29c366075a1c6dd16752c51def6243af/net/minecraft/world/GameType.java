package net.minecraft.world;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum GameType {
   NOT_SET(-1, ""),
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure"),
   SPECTATOR(3, "spectator");

   private final int id;
   private final String name;

   private GameType(int gameTypeId, String gameTypeName) {
      this.id = gameTypeId;
      this.name = gameTypeName;
   }

   /**
    * Returns the ID of this game type
    */
   public int getID() {
      return this.id;
   }

   /**
    * Returns the name of this game type
    */
   public String getName() {
      return this.name;
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent("gameMode." + this.name);
   }

   /**
    * Configures the player capabilities based on the game type
    */
   public void configurePlayerCapabilities(PlayerAbilities capabilities) {
      if (this == CREATIVE) {
         capabilities.allowFlying = true;
         capabilities.isCreativeMode = true;
         capabilities.disableDamage = true;
      } else if (this == SPECTATOR) {
         capabilities.allowFlying = true;
         capabilities.isCreativeMode = false;
         capabilities.disableDamage = true;
         capabilities.isFlying = true;
      } else {
         capabilities.allowFlying = false;
         capabilities.isCreativeMode = false;
         capabilities.disableDamage = false;
         capabilities.isFlying = false;
      }

      capabilities.allowEdit = !this.hasLimitedInteractions();
   }

   /**
    * Returns true if this is the ADVENTURE game type
    */
   public boolean hasLimitedInteractions() {
      return this == ADVENTURE || this == SPECTATOR;
   }

   /**
    * Returns true if this is the CREATIVE game type
    */
   public boolean isCreative() {
      return this == CREATIVE;
   }

   /**
    * Returns true if this is the SURVIVAL or ADVENTURE game type
    */
   public boolean isSurvivalOrAdventure() {
      return this == SURVIVAL || this == ADVENTURE;
   }

   /**
    * Gets the game type by it's ID. Will be survival if none was found.
    */
   public static GameType getByID(int idIn) {
      return parseGameTypeWithDefault(idIn, SURVIVAL);
   }

   public static GameType parseGameTypeWithDefault(int targetId, GameType fallback) {
      for(GameType gametype : values()) {
         if (gametype.id == targetId) {
            return gametype;
         }
      }

      return fallback;
   }

   /**
    * Gets the game type registered with the specified name. If no matches were found, survival will be returned.
    */
   public static GameType getByName(String gamemodeName) {
      return parseGameTypeWithDefault(gamemodeName, SURVIVAL);
   }

   public static GameType parseGameTypeWithDefault(String targetName, GameType fallback) {
      for(GameType gametype : values()) {
         if (gametype.name.equals(targetName)) {
            return gametype;
         }
      }

      return fallback;
   }
}