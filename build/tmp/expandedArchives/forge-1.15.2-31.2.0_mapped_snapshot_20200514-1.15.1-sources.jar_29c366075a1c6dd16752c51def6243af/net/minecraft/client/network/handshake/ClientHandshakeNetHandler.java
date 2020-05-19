package net.minecraft.client.network.handshake;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.IHandshakeNetHandler;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientHandshakeNetHandler implements IHandshakeNetHandler {
   private final MinecraftServer server;
   private final NetworkManager networkManager;

   public ClientHandshakeNetHandler(MinecraftServer mcServerIn, NetworkManager networkManagerIn) {
      this.server = mcServerIn;
      this.networkManager = networkManagerIn;
   }

   /**
    * There are two recognized intentions for initiating a handshake: logging in and acquiring server status. The
    * NetworkManager's protocol will be reconfigured according to the specified intention, although a login-intention
    * must pass a versioncheck or receive a disconnect otherwise
    */
   public void processHandshake(CHandshakePacket packetIn) {
      if (!net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerLogin(packetIn, this.networkManager)) return;
      this.networkManager.setConnectionState(packetIn.getRequestedState());
      this.networkManager.setNetHandler(new ServerLoginNetHandler(this.server, this.networkManager));
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