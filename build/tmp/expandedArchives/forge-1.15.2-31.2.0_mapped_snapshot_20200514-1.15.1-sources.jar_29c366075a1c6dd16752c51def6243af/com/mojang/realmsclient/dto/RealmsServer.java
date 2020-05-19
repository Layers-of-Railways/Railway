package com.mojang.realmsclient.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServer extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public long id;
   public String remoteSubscriptionId;
   public String name;
   public String motd;
   public RealmsServer.Status state;
   public String owner;
   public String ownerUUID;
   public List<PlayerInfo> players;
   public Map<Integer, RealmsWorldOptions> slots;
   public boolean expired;
   public boolean expiredTrial;
   public int daysLeft;
   public RealmsServer.ServerType worldType;
   public int activeSlot;
   public String minigameName;
   public int minigameId;
   public String minigameImage;
   public RealmsServerPing serverPing = new RealmsServerPing();

   public String getDescription() {
      return this.motd;
   }

   public String getName() {
      return this.name;
   }

   public String getMinigameName() {
      return this.minigameName;
   }

   public void setName(String p_setName_1_) {
      this.name = p_setName_1_;
   }

   public void setDescription(String p_setDescription_1_) {
      this.motd = p_setDescription_1_;
   }

   public void updateServerPing(RealmsServerPlayerList p_updateServerPing_1_) {
      StringBuilder stringbuilder = new StringBuilder();
      int i = 0;

      for(String s : p_updateServerPing_1_.players) {
         if (!s.equals(Realms.getUUID())) {
            String s1 = "";

            try {
               s1 = RealmsUtil.func_225193_a(s);
            } catch (Exception exception) {
               LOGGER.error("Could not get name for " + s, (Throwable)exception);
               continue;
            }

            if (stringbuilder.length() > 0) {
               stringbuilder.append("\n");
            }

            stringbuilder.append(s1);
            ++i;
         }
      }

      this.serverPing.nrOfPlayers = String.valueOf(i);
      this.serverPing.playerList = stringbuilder.toString();
   }

   public static RealmsServer parse(JsonObject p_parse_0_) {
      RealmsServer realmsserver = new RealmsServer();

      try {
         realmsserver.id = JsonUtils.func_225169_a("id", p_parse_0_, -1L);
         realmsserver.remoteSubscriptionId = JsonUtils.func_225171_a("remoteSubscriptionId", p_parse_0_, (String)null);
         realmsserver.name = JsonUtils.func_225171_a("name", p_parse_0_, (String)null);
         realmsserver.motd = JsonUtils.func_225171_a("motd", p_parse_0_, (String)null);
         realmsserver.state = getState(JsonUtils.func_225171_a("state", p_parse_0_, RealmsServer.Status.CLOSED.name()));
         realmsserver.owner = JsonUtils.func_225171_a("owner", p_parse_0_, (String)null);
         if (p_parse_0_.get("players") != null && p_parse_0_.get("players").isJsonArray()) {
            realmsserver.players = parseInvited(p_parse_0_.get("players").getAsJsonArray());
            sortInvited(realmsserver);
         } else {
            realmsserver.players = Lists.newArrayList();
         }

         realmsserver.daysLeft = JsonUtils.func_225172_a("daysLeft", p_parse_0_, 0);
         realmsserver.expired = JsonUtils.func_225170_a("expired", p_parse_0_, false);
         realmsserver.expiredTrial = JsonUtils.func_225170_a("expiredTrial", p_parse_0_, false);
         realmsserver.worldType = getWorldType(JsonUtils.func_225171_a("worldType", p_parse_0_, RealmsServer.ServerType.NORMAL.name()));
         realmsserver.ownerUUID = JsonUtils.func_225171_a("ownerUUID", p_parse_0_, "");
         if (p_parse_0_.get("slots") != null && p_parse_0_.get("slots").isJsonArray()) {
            realmsserver.slots = parseSlots(p_parse_0_.get("slots").getAsJsonArray());
         } else {
            realmsserver.slots = getEmptySlots();
         }

         realmsserver.minigameName = JsonUtils.func_225171_a("minigameName", p_parse_0_, (String)null);
         realmsserver.activeSlot = JsonUtils.func_225172_a("activeSlot", p_parse_0_, -1);
         realmsserver.minigameId = JsonUtils.func_225172_a("minigameId", p_parse_0_, -1);
         realmsserver.minigameImage = JsonUtils.func_225171_a("minigameImage", p_parse_0_, (String)null);
      } catch (Exception exception) {
         LOGGER.error("Could not parse McoServer: " + exception.getMessage());
      }

      return realmsserver;
   }

   private static void sortInvited(RealmsServer p_sortInvited_0_) {
      p_sortInvited_0_.players.sort((p_229951_0_, p_229951_1_) -> {
         return ComparisonChain.start().compareFalseFirst(p_229951_1_.getAccepted(), p_229951_0_.getAccepted()).compare(p_229951_0_.getName().toLowerCase(Locale.ROOT), p_229951_1_.getName().toLowerCase(Locale.ROOT)).result();
      });
   }

   private static List<PlayerInfo> parseInvited(JsonArray p_parseInvited_0_) {
      List<PlayerInfo> list = Lists.newArrayList();

      for(JsonElement jsonelement : p_parseInvited_0_) {
         try {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            PlayerInfo playerinfo = new PlayerInfo();
            playerinfo.setName(JsonUtils.func_225171_a("name", jsonobject, (String)null));
            playerinfo.setUuid(JsonUtils.func_225171_a("uuid", jsonobject, (String)null));
            playerinfo.setOperator(JsonUtils.func_225170_a("operator", jsonobject, false));
            playerinfo.setAccepted(JsonUtils.func_225170_a("accepted", jsonobject, false));
            playerinfo.setOnline(JsonUtils.func_225170_a("online", jsonobject, false));
            list.add(playerinfo);
         } catch (Exception var6) {
            ;
         }
      }

      return list;
   }

   private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray p_parseSlots_0_) {
      Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();

      for(JsonElement jsonelement : p_parseSlots_0_) {
         try {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            JsonParser jsonparser = new JsonParser();
            JsonElement jsonelement1 = jsonparser.parse(jsonobject.get("options").getAsString());
            RealmsWorldOptions realmsworldoptions;
            if (jsonelement1 == null) {
               realmsworldoptions = RealmsWorldOptions.getDefaults();
            } else {
               realmsworldoptions = RealmsWorldOptions.parse(jsonelement1.getAsJsonObject());
            }

            int i = JsonUtils.func_225172_a("slotId", jsonobject, -1);
            map.put(i, realmsworldoptions);
         } catch (Exception var9) {
            ;
         }
      }

      for(int j = 1; j <= 3; ++j) {
         if (!map.containsKey(j)) {
            map.put(j, RealmsWorldOptions.getEmptyDefaults());
         }
      }

      return map;
   }

   private static Map<Integer, RealmsWorldOptions> getEmptySlots() {
      Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();
      map.put(1, RealmsWorldOptions.getEmptyDefaults());
      map.put(2, RealmsWorldOptions.getEmptyDefaults());
      map.put(3, RealmsWorldOptions.getEmptyDefaults());
      return map;
   }

   public static RealmsServer parse(String p_parse_0_) {
      RealmsServer realmsserver = new RealmsServer();

      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_parse_0_).getAsJsonObject();
         realmsserver = parse(jsonobject);
      } catch (Exception exception) {
         LOGGER.error("Could not parse McoServer: " + exception.getMessage());
      }

      return realmsserver;
   }

   private static RealmsServer.Status getState(String p_getState_0_) {
      try {
         return RealmsServer.Status.valueOf(p_getState_0_);
      } catch (Exception var2) {
         return RealmsServer.Status.CLOSED;
      }
   }

   private static RealmsServer.ServerType getWorldType(String p_getWorldType_0_) {
      try {
         return RealmsServer.ServerType.valueOf(p_getWorldType_0_);
      } catch (Exception var2) {
         return RealmsServer.ServerType.NORMAL;
      }
   }

   public int hashCode() {
      return (new HashCodeBuilder(17, 37)).append(this.id).append((Object)this.name).append((Object)this.motd).append((Object)this.state).append((Object)this.owner).append(this.expired).toHashCode();
   }

   public boolean equals(Object p_equals_1_) {
      if (p_equals_1_ == null) {
         return false;
      } else if (p_equals_1_ == this) {
         return true;
      } else if (p_equals_1_.getClass() != this.getClass()) {
         return false;
      } else {
         RealmsServer realmsserver = (RealmsServer)p_equals_1_;
         return (new EqualsBuilder()).append(this.id, realmsserver.id).append((Object)this.name, (Object)realmsserver.name).append((Object)this.motd, (Object)realmsserver.motd).append((Object)this.state, (Object)realmsserver.state).append((Object)this.owner, (Object)realmsserver.owner).append(this.expired, realmsserver.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
      }
   }

   public RealmsServer clone() {
      RealmsServer realmsserver = new RealmsServer();
      realmsserver.id = this.id;
      realmsserver.remoteSubscriptionId = this.remoteSubscriptionId;
      realmsserver.name = this.name;
      realmsserver.motd = this.motd;
      realmsserver.state = this.state;
      realmsserver.owner = this.owner;
      realmsserver.players = this.players;
      realmsserver.slots = this.cloneSlots(this.slots);
      realmsserver.expired = this.expired;
      realmsserver.expiredTrial = this.expiredTrial;
      realmsserver.daysLeft = this.daysLeft;
      realmsserver.serverPing = new RealmsServerPing();
      realmsserver.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
      realmsserver.serverPing.playerList = this.serverPing.playerList;
      realmsserver.worldType = this.worldType;
      realmsserver.ownerUUID = this.ownerUUID;
      realmsserver.minigameName = this.minigameName;
      realmsserver.activeSlot = this.activeSlot;
      realmsserver.minigameId = this.minigameId;
      realmsserver.minigameImage = this.minigameImage;
      return realmsserver;
   }

   public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> p_cloneSlots_1_) {
      Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();

      for(Entry<Integer, RealmsWorldOptions> entry : p_cloneSlots_1_.entrySet()) {
         map.put(entry.getKey(), entry.getValue().clone());
      }

      return map;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerComparator implements Comparator<RealmsServer> {
      private final String field_223701_a;

      public ServerComparator(String p_i51687_1_) {
         this.field_223701_a = p_i51687_1_;
      }

      public int compare(RealmsServer p_compare_1_, RealmsServer p_compare_2_) {
         return ComparisonChain.start().compareTrueFirst(p_compare_1_.state.equals(RealmsServer.Status.UNINITIALIZED), p_compare_2_.state.equals(RealmsServer.Status.UNINITIALIZED)).compareTrueFirst(p_compare_1_.expiredTrial, p_compare_2_.expiredTrial).compareTrueFirst(p_compare_1_.owner.equals(this.field_223701_a), p_compare_2_.owner.equals(this.field_223701_a)).compareFalseFirst(p_compare_1_.expired, p_compare_2_.expired).compareTrueFirst(p_compare_1_.state.equals(RealmsServer.Status.OPEN), p_compare_2_.state.equals(RealmsServer.Status.OPEN)).compare(p_compare_1_.id, p_compare_2_.id).result();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ServerType {
      NORMAL,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Status {
      CLOSED,
      OPEN,
      UNINITIALIZED;
   }
}