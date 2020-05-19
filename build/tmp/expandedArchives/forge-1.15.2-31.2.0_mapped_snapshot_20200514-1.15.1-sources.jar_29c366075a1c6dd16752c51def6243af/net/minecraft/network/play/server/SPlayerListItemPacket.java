package net.minecraft.network.play.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlayerListItemPacket implements IPacket<IClientPlayNetHandler> {
   private SPlayerListItemPacket.Action action;
   private final List<SPlayerListItemPacket.AddPlayerData> players = Lists.newArrayList();

   public SPlayerListItemPacket() {
   }

   public SPlayerListItemPacket(SPlayerListItemPacket.Action actionIn, ServerPlayerEntity... playersIn) {
      this.action = actionIn;

      for(ServerPlayerEntity serverplayerentity : playersIn) {
         this.players.add(new SPlayerListItemPacket.AddPlayerData(serverplayerentity.getGameProfile(), serverplayerentity.ping, serverplayerentity.interactionManager.getGameType(), serverplayerentity.getTabListDisplayName()));
      }

   }

   public SPlayerListItemPacket(SPlayerListItemPacket.Action actionIn, Iterable<ServerPlayerEntity> playersIn) {
      this.action = actionIn;

      for(ServerPlayerEntity serverplayerentity : playersIn) {
         this.players.add(new SPlayerListItemPacket.AddPlayerData(serverplayerentity.getGameProfile(), serverplayerentity.ping, serverplayerentity.interactionManager.getGameType(), serverplayerentity.getTabListDisplayName()));
      }

   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.action = buf.readEnumValue(SPlayerListItemPacket.Action.class);
      int i = buf.readVarInt();

      for(int j = 0; j < i; ++j) {
         GameProfile gameprofile = null;
         int k = 0;
         GameType gametype = null;
         ITextComponent itextcomponent = null;
         switch(this.action) {
         case ADD_PLAYER:
            gameprofile = new GameProfile(buf.readUniqueId(), buf.readString(16));
            int l = buf.readVarInt();
            int i1 = 0;

            for(; i1 < l; ++i1) {
               String s = buf.readString(32767);
               String s1 = buf.readString(32767);
               if (buf.readBoolean()) {
                  gameprofile.getProperties().put(s, new Property(s, s1, buf.readString(32767)));
               } else {
                  gameprofile.getProperties().put(s, new Property(s, s1));
               }
            }

            gametype = GameType.getByID(buf.readVarInt());
            k = buf.readVarInt();
            if (buf.readBoolean()) {
               itextcomponent = buf.readTextComponent();
            }
            break;
         case UPDATE_GAME_MODE:
            gameprofile = new GameProfile(buf.readUniqueId(), (String)null);
            gametype = GameType.getByID(buf.readVarInt());
            break;
         case UPDATE_LATENCY:
            gameprofile = new GameProfile(buf.readUniqueId(), (String)null);
            k = buf.readVarInt();
            break;
         case UPDATE_DISPLAY_NAME:
            gameprofile = new GameProfile(buf.readUniqueId(), (String)null);
            if (buf.readBoolean()) {
               itextcomponent = buf.readTextComponent();
            }
            break;
         case REMOVE_PLAYER:
            gameprofile = new GameProfile(buf.readUniqueId(), (String)null);
         }

         this.players.add(new SPlayerListItemPacket.AddPlayerData(gameprofile, k, gametype, itextcomponent));
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.action);
      buf.writeVarInt(this.players.size());

      for(SPlayerListItemPacket.AddPlayerData splayerlistitempacket$addplayerdata : this.players) {
         switch(this.action) {
         case ADD_PLAYER:
            buf.writeUniqueId(splayerlistitempacket$addplayerdata.getProfile().getId());
            buf.writeString(splayerlistitempacket$addplayerdata.getProfile().getName());
            buf.writeVarInt(splayerlistitempacket$addplayerdata.getProfile().getProperties().size());

            for(Property property : splayerlistitempacket$addplayerdata.getProfile().getProperties().values()) {
               buf.writeString(property.getName());
               buf.writeString(property.getValue());
               if (property.hasSignature()) {
                  buf.writeBoolean(true);
                  buf.writeString(property.getSignature());
               } else {
                  buf.writeBoolean(false);
               }
            }

            buf.writeVarInt(splayerlistitempacket$addplayerdata.getGameMode().getID());
            buf.writeVarInt(splayerlistitempacket$addplayerdata.getPing());
            if (splayerlistitempacket$addplayerdata.getDisplayName() == null) {
               buf.writeBoolean(false);
            } else {
               buf.writeBoolean(true);
               buf.writeTextComponent(splayerlistitempacket$addplayerdata.getDisplayName());
            }
            break;
         case UPDATE_GAME_MODE:
            buf.writeUniqueId(splayerlistitempacket$addplayerdata.getProfile().getId());
            buf.writeVarInt(splayerlistitempacket$addplayerdata.getGameMode().getID());
            break;
         case UPDATE_LATENCY:
            buf.writeUniqueId(splayerlistitempacket$addplayerdata.getProfile().getId());
            buf.writeVarInt(splayerlistitempacket$addplayerdata.getPing());
            break;
         case UPDATE_DISPLAY_NAME:
            buf.writeUniqueId(splayerlistitempacket$addplayerdata.getProfile().getId());
            if (splayerlistitempacket$addplayerdata.getDisplayName() == null) {
               buf.writeBoolean(false);
            } else {
               buf.writeBoolean(true);
               buf.writeTextComponent(splayerlistitempacket$addplayerdata.getDisplayName());
            }
            break;
         case REMOVE_PLAYER:
            buf.writeUniqueId(splayerlistitempacket$addplayerdata.getProfile().getId());
         }
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handlePlayerListItem(this);
   }

   @OnlyIn(Dist.CLIENT)
   public List<SPlayerListItemPacket.AddPlayerData> getEntries() {
      return this.players;
   }

   @OnlyIn(Dist.CLIENT)
   public SPlayerListItemPacket.Action getAction() {
      return this.action;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.players).toString();
   }

   public static enum Action {
      ADD_PLAYER,
      UPDATE_GAME_MODE,
      UPDATE_LATENCY,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER;
   }

   public class AddPlayerData {
      private final int ping;
      private final GameType gamemode;
      private final GameProfile profile;
      private final ITextComponent displayName;

      public AddPlayerData(GameProfile profileIn, int latencyIn, @Nullable GameType gameModeIn, @Nullable ITextComponent displayNameIn) {
         this.profile = profileIn;
         this.ping = latencyIn;
         this.gamemode = gameModeIn;
         this.displayName = displayNameIn;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getPing() {
         return this.ping;
      }

      public GameType getGameMode() {
         return this.gamemode;
      }

      @Nullable
      public ITextComponent getDisplayName() {
         return this.displayName;
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("latency", this.ping).add("gameMode", this.gamemode).add("profile", this.profile).add("displayName", this.displayName == null ? null : ITextComponent.Serializer.toJson(this.displayName)).toString();
      }
   }
}