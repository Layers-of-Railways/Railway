package net.minecraft.client;

import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientGameSession implements GameSession {
   private final int playerCount;
   private final boolean remoteServer;
   private final String difficulty;
   private final String gameMode;
   private final UUID sessionId;

   public ClientGameSession(ClientWorld p_i51152_1_, ClientPlayerEntity p_i51152_2_, ClientPlayNetHandler p_i51152_3_) {
      this.playerCount = p_i51152_3_.getPlayerInfoMap().size();
      this.remoteServer = !p_i51152_3_.getNetworkManager().isLocalChannel();
      this.difficulty = p_i51152_1_.getDifficulty().getTranslationKey();
      NetworkPlayerInfo networkplayerinfo = p_i51152_3_.getPlayerInfo(p_i51152_2_.getUniqueID());
      if (networkplayerinfo != null) {
         this.gameMode = networkplayerinfo.getGameType().getName();
      } else {
         this.gameMode = "unknown";
      }

      this.sessionId = p_i51152_3_.getSessionId();
   }

   public int getPlayerCount() {
      return this.playerCount;
   }

   public boolean isRemoteServer() {
      return this.remoteServer;
   }

   public String getDifficulty() {
      return this.difficulty;
   }

   public String getGameMode() {
      return this.gameMode;
   }

   public UUID getSessionId() {
      return this.sessionId;
   }
}