package net.minecraft.network.handshake;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.status.ServerStatusNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ServerHandshakeNetHandler implements IHandshakeNetHandler {
   private final MinecraftServer server;
   private final NetworkManager networkManager;

   public ServerHandshakeNetHandler(MinecraftServer serverIn, NetworkManager netManager) {
      this.server = serverIn;
      this.networkManager = netManager;
   }

   /**
    * There are two recognized intentions for initiating a handshake: logging in and acquiring server status. The
    * NetworkManager's protocol will be reconfigured according to the specified intention, although a login-intention
    * must pass a versioncheck or receive a disconnect otherwise
    */
   public void processHandshake(CHandshakePacket packetIn) {
      if (!net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerLogin(packetIn, this.networkManager)) return;
      switch(packetIn.getRequestedState()) {
      case LOGIN:
         this.networkManager.setConnectionState(ProtocolType.LOGIN);
         if (packetIn.getProtocolVersion() > SharedConstants.getVersion().getProtocolVersion()) {
            ITextComponent itextcomponent = new TranslationTextComponent("multiplayer.disconnect.outdated_server", SharedConstants.getVersion().getName());
            this.networkManager.sendPacket(new SDisconnectLoginPacket(itextcomponent));
            this.networkManager.closeChannel(itextcomponent);
         } else if (packetIn.getProtocolVersion() < SharedConstants.getVersion().getProtocolVersion()) {
            ITextComponent itextcomponent1 = new TranslationTextComponent("multiplayer.disconnect.outdated_client", SharedConstants.getVersion().getName());
            this.networkManager.sendPacket(new SDisconnectLoginPacket(itextcomponent1));
            this.networkManager.closeChannel(itextcomponent1);
         } else {
            this.networkManager.setNetHandler(new ServerLoginNetHandler(this.server, this.networkManager));
         }
         break;
      case STATUS:
         this.networkManager.setConnectionState(ProtocolType.STATUS);
         this.networkManager.setNetHandler(new ServerStatusNetHandler(this.server, this.networkManager));
         break;
      default:
         throw new UnsupportedOperationException("Invalid intention " + packetIn.getRequestedState());
      }

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
}