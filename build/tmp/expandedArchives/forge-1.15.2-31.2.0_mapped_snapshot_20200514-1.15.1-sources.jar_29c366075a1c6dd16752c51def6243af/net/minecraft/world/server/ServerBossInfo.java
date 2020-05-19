package net.minecraft.world.server;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

public class ServerBossInfo extends BossInfo {
   private final Set<ServerPlayerEntity> players = Sets.newHashSet();
   private final Set<ServerPlayerEntity> readOnlyPlayers = Collections.unmodifiableSet(this.players);
   private boolean visible = true;

   public ServerBossInfo(ITextComponent nameIn, BossInfo.Color colorIn, BossInfo.Overlay overlayIn) {
      super(MathHelper.getRandomUUID(), nameIn, colorIn, overlayIn);
   }

   public void setPercent(float percentIn) {
      if (percentIn != this.percent) {
         super.setPercent(percentIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PCT);
      }

   }

   public void setColor(BossInfo.Color colorIn) {
      if (colorIn != this.color) {
         super.setColor(colorIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_STYLE);
      }

   }

   public void setOverlay(BossInfo.Overlay overlayIn) {
      if (overlayIn != this.overlay) {
         super.setOverlay(overlayIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_STYLE);
      }

   }

   public BossInfo setDarkenSky(boolean darkenSkyIn) {
      if (darkenSkyIn != this.darkenSky) {
         super.setDarkenSky(darkenSkyIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossInfo setPlayEndBossMusic(boolean playEndBossMusicIn) {
      if (playEndBossMusicIn != this.playEndBossMusic) {
         super.setPlayEndBossMusic(playEndBossMusicIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossInfo setCreateFog(boolean createFogIn) {
      if (createFogIn != this.createFog) {
         super.setCreateFog(createFogIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public void setName(ITextComponent nameIn) {
      if (!Objects.equal(nameIn, this.name)) {
         super.setName(nameIn);
         this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_NAME);
      }

   }

   private void sendUpdate(SUpdateBossInfoPacket.Operation operationIn) {
      if (this.visible) {
         SUpdateBossInfoPacket supdatebossinfopacket = new SUpdateBossInfoPacket(operationIn, this);

         for(ServerPlayerEntity serverplayerentity : this.players) {
            serverplayerentity.connection.sendPacket(supdatebossinfopacket);
         }
      }

   }

   /**
    * Makes the boss visible to the given player.
    */
   public void addPlayer(ServerPlayerEntity player) {
      if (this.players.add(player) && this.visible) {
         player.connection.sendPacket(new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.ADD, this));
      }

   }

   /**
    * Makes the boss non-visible to the given player.
    */
   public void removePlayer(ServerPlayerEntity player) {
      if (this.players.remove(player) && this.visible) {
         player.connection.sendPacket(new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.REMOVE, this));
      }

   }

   public void removeAllPlayers() {
      if (!this.players.isEmpty()) {
         for(ServerPlayerEntity serverplayerentity : Lists.newArrayList(this.players)) {
            this.removePlayer(serverplayerentity);
         }
      }

   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visibleIn) {
      if (visibleIn != this.visible) {
         this.visible = visibleIn;

         for(ServerPlayerEntity serverplayerentity : this.players) {
            serverplayerentity.connection.sendPacket(new SUpdateBossInfoPacket(visibleIn ? SUpdateBossInfoPacket.Operation.ADD : SUpdateBossInfoPacket.Operation.REMOVE, this));
         }
      }

   }

   /**
    * The returned collection is unmodifiable
    */
   public Collection<ServerPlayerEntity> getPlayers() {
      return this.readOnlyPlayers;
   }
}