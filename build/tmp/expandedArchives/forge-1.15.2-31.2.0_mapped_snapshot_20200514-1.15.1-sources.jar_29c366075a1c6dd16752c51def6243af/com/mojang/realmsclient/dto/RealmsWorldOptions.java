package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsWorldOptions extends ValueObject {
   public Boolean pvp;
   public Boolean spawnAnimals;
   public Boolean spawnMonsters;
   public Boolean spawnNPCs;
   public Integer spawnProtection;
   public Boolean commandBlocks;
   public Boolean forceGameMode;
   public Integer difficulty;
   public Integer gameMode;
   public String slotName;
   public long templateId;
   public String templateImage;
   public boolean adventureMap;
   public boolean empty;
   private static final boolean forceGameModeDefault = false;
   private static final boolean pvpDefault = true;
   private static final boolean spawnAnimalsDefault = true;
   private static final boolean spawnMonstersDefault = true;
   private static final boolean spawnNPCsDefault = true;
   private static final int spawnProtectionDefault = 0;
   private static final boolean commandBlocksDefault = false;
   private static final int difficultyDefault = 2;
   private static final int gameModeDefault = 0;
   private static final String slotNameDefault = "";
   private static final long templateIdDefault = -1L;
   private static final String templateImageDefault = null;
   private static final boolean adventureMapDefault = false;

   public RealmsWorldOptions(Boolean atlasTexturesIn, Boolean p_i51651_2_, Boolean p_i51651_3_, Boolean p_i51651_4_, Integer p_i51651_5_, Boolean p_i51651_6_, Integer p_i51651_7_, Integer p_i51651_8_, Boolean p_i51651_9_, String p_i51651_10_) {
      this.pvp = atlasTexturesIn;
      this.spawnAnimals = p_i51651_2_;
      this.spawnMonsters = p_i51651_3_;
      this.spawnNPCs = p_i51651_4_;
      this.spawnProtection = p_i51651_5_;
      this.commandBlocks = p_i51651_6_;
      this.difficulty = p_i51651_7_;
      this.gameMode = p_i51651_8_;
      this.forceGameMode = p_i51651_9_;
      this.slotName = p_i51651_10_;
   }

   public static RealmsWorldOptions getDefaults() {
      return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, "");
   }

   public static RealmsWorldOptions getEmptyDefaults() {
      RealmsWorldOptions realmsworldoptions = new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, "");
      realmsworldoptions.setEmpty(true);
      return realmsworldoptions;
   }

   public void setEmpty(boolean p_setEmpty_1_) {
      this.empty = p_setEmpty_1_;
   }

   public static RealmsWorldOptions parse(JsonObject p_parse_0_) {
      RealmsWorldOptions realmsworldoptions = new RealmsWorldOptions(JsonUtils.func_225170_a("pvp", p_parse_0_, true), JsonUtils.func_225170_a("spawnAnimals", p_parse_0_, true), JsonUtils.func_225170_a("spawnMonsters", p_parse_0_, true), JsonUtils.func_225170_a("spawnNPCs", p_parse_0_, true), JsonUtils.func_225172_a("spawnProtection", p_parse_0_, 0), JsonUtils.func_225170_a("commandBlocks", p_parse_0_, false), JsonUtils.func_225172_a("difficulty", p_parse_0_, 2), JsonUtils.func_225172_a("gameMode", p_parse_0_, 0), JsonUtils.func_225170_a("forceGameMode", p_parse_0_, false), JsonUtils.func_225171_a("slotName", p_parse_0_, ""));
      realmsworldoptions.templateId = JsonUtils.func_225169_a("worldTemplateId", p_parse_0_, -1L);
      realmsworldoptions.templateImage = JsonUtils.func_225171_a("worldTemplateImage", p_parse_0_, templateImageDefault);
      realmsworldoptions.adventureMap = JsonUtils.func_225170_a("adventureMap", p_parse_0_, false);
      return realmsworldoptions;
   }

   public String getSlotName(int p_getSlotName_1_) {
      if (this.slotName != null && !this.slotName.isEmpty()) {
         return this.slotName;
      } else {
         return this.empty ? RealmsScreen.getLocalizedString("mco.configure.world.slot.empty") : this.getDefaultSlotName(p_getSlotName_1_);
      }
   }

   public String getDefaultSlotName(int p_getDefaultSlotName_1_) {
      return RealmsScreen.getLocalizedString("mco.configure.world.slot", p_getDefaultSlotName_1_);
   }

   public String toJson() {
      JsonObject jsonobject = new JsonObject();
      if (!this.pvp) {
         jsonobject.addProperty("pvp", this.pvp);
      }

      if (!this.spawnAnimals) {
         jsonobject.addProperty("spawnAnimals", this.spawnAnimals);
      }

      if (!this.spawnMonsters) {
         jsonobject.addProperty("spawnMonsters", this.spawnMonsters);
      }

      if (!this.spawnNPCs) {
         jsonobject.addProperty("spawnNPCs", this.spawnNPCs);
      }

      if (this.spawnProtection != 0) {
         jsonobject.addProperty("spawnProtection", this.spawnProtection);
      }

      if (this.commandBlocks) {
         jsonobject.addProperty("commandBlocks", this.commandBlocks);
      }

      if (this.difficulty != 2) {
         jsonobject.addProperty("difficulty", this.difficulty);
      }

      if (this.gameMode != 0) {
         jsonobject.addProperty("gameMode", this.gameMode);
      }

      if (this.forceGameMode) {
         jsonobject.addProperty("forceGameMode", this.forceGameMode);
      }

      if (this.slotName != null && !this.slotName.equals("")) {
         jsonobject.addProperty("slotName", this.slotName);
      }

      return jsonobject.toString();
   }

   public RealmsWorldOptions clone() {
      return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
   }
}