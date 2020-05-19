package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.server.management.PlayerList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerList extends PlayerList {
   private static final Logger LOGGER = LogManager.getLogger();

   public DedicatedPlayerList(DedicatedServer server) {
      super(server, server.getServerProperties().maxPlayers);
      ServerProperties serverproperties = server.getServerProperties();
      this.setViewDistance(serverproperties.viewDistance);
      super.setWhiteListEnabled(serverproperties.whitelistEnabled.get());
      if (!server.isSinglePlayer()) {
         this.getBannedPlayers().setLanServer(true);
         this.getBannedIPs().setLanServer(true);
      }

      this.loadPlayerBanList();
      this.savePlayerBanList();
      this.loadIPBanList();
      this.saveIPBanList();
      this.loadOpsList();
      this.readWhiteList();
      this.saveOpsList();
      if (!this.getWhitelistedPlayers().getSaveFile().exists()) {
         this.saveWhiteList();
      }

   }

   public void setWhiteListEnabled(boolean whitelistEnabled) {
      super.setWhiteListEnabled(whitelistEnabled);
      this.getServer().func_213223_o(whitelistEnabled);
   }

   public void addOp(GameProfile profile) {
      super.addOp(profile);
      this.saveOpsList();
   }

   public void removeOp(GameProfile profile) {
      super.removeOp(profile);
      this.saveOpsList();
   }

   public void reloadWhitelist() {
      this.readWhiteList();
   }

   private void saveIPBanList() {
      try {
         this.getBannedIPs().writeChanges();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to save ip banlist: ", (Throwable)ioexception);
      }

   }

   private void savePlayerBanList() {
      try {
         this.getBannedPlayers().writeChanges();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to save user banlist: ", (Throwable)ioexception);
      }

   }

   private void loadIPBanList() {
      try {
         this.getBannedIPs().readSavedFile();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to load ip banlist: ", (Throwable)ioexception);
      }

   }

   private void loadPlayerBanList() {
      try {
         this.getBannedPlayers().readSavedFile();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to load user banlist: ", (Throwable)ioexception);
      }

   }

   private void loadOpsList() {
      try {
         this.getOppedPlayers().readSavedFile();
      } catch (Exception exception) {
         LOGGER.warn("Failed to load operators list: ", (Throwable)exception);
      }

   }

   private void saveOpsList() {
      try {
         this.getOppedPlayers().writeChanges();
      } catch (Exception exception) {
         LOGGER.warn("Failed to save operators list: ", (Throwable)exception);
      }

   }

   private void readWhiteList() {
      try {
         this.getWhitelistedPlayers().readSavedFile();
      } catch (Exception exception) {
         LOGGER.warn("Failed to load white-list: ", (Throwable)exception);
      }

   }

   private void saveWhiteList() {
      try {
         this.getWhitelistedPlayers().writeChanges();
      } catch (Exception exception) {
         LOGGER.warn("Failed to save white-list: ", (Throwable)exception);
      }

   }

   public boolean canJoin(GameProfile profile) {
      return !this.isWhiteListEnabled() || this.canSendCommands(profile) || this.getWhitelistedPlayers().isWhitelisted(profile);
   }

   public DedicatedServer getServer() {
      return (DedicatedServer)super.getServer();
   }

   public boolean bypassesPlayerLimit(GameProfile profile) {
      return this.getOppedPlayers().bypassesPlayerLimit(profile);
   }
}