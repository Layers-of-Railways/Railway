package net.minecraft.network.status;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ServerStatusNetHandler implements IServerStatusNetHandler {
   private static final ITextComponent EXIT_MESSAGE = new TranslationTextComponent("multiplayer.status.request_handled");
   private final MinecraftServer server;
   private final NetworkManager networkManager;
   private boolean handled;

   public ServerStatusNetHandler(MinecraftServer serverIn, NetworkManager netManager) {
      this.server = serverIn;
      this.networkManager = netManager;
   }

   /**
    * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
    */
   public void onDisconnect(ITextComponent reason) {
   }

   /**
    * Returns this the NetworkManager instance registered with this NetworkHandlerPlayClient
    */
   public NetworkManager getNetworkManager() {
      return this.networkManager;
   }

   public void processServerQuery(CServerQueryPacket packetIn) {
      if (this.handled) {
         this.networkManager.closeChannel(EXIT_MESSAGE);
      } else {
         this.handled = true;
         this.networkManager.sendPacket(new SServerInfoPacket(this.server.getServerStatusResponse()));
      }
   }

   public void processPing(CPingPacket packetIn) {
      this.networkManager.sendPacket(new SPongPacket(packetIn.getClientTime()));
      this.networkManager.closeChannel(EXIT_MESSAGE);
   }
}