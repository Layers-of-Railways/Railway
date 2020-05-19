package net.minecraft.network.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.dedicated.ServerProperties;

public class MainThread extends RConThread {
   private final int rconPort;
   private String hostname;
   private ServerSocket serverSocket;
   private final String rconPassword;
   private Map<SocketAddress, ClientThread> clientThreads;

   public MainThread(IServer p_i1538_1_) {
      super(p_i1538_1_, "RCON Listener");
      ServerProperties serverproperties = p_i1538_1_.getServerProperties();
      this.rconPort = serverproperties.rconPort;
      this.rconPassword = serverproperties.rconPassword;
      this.hostname = p_i1538_1_.getHostname();
      if (this.hostname.isEmpty()) {
         this.hostname = "0.0.0.0";
      }

      this.initClientThreadList();
      this.serverSocket = null;
   }

   private void initClientThreadList() {
      this.clientThreads = Maps.newHashMap();
   }

   /**
    * Cleans up the clientThreads map by removing client Threads that are not running
    */
   private void cleanClientThreadsMap() {
      Iterator<Entry<SocketAddress, ClientThread>> iterator = this.clientThreads.entrySet().iterator();

      while(iterator.hasNext()) {
         Entry<SocketAddress, ClientThread> entry = iterator.next();
         if (!entry.getValue().isRunning()) {
            iterator.remove();
         }
      }

   }

   public void run() {
      this.logInfo("RCON running on " + this.hostname + ":" + this.rconPort);

      try {
         while(this.running) {
            try {
               Socket socket = this.serverSocket.accept();
               socket.setSoTimeout(500);
               ClientThread clientthread = new ClientThread(this.server, this.rconPassword, socket);
               clientthread.startThread();
               this.clientThreads.put(socket.getRemoteSocketAddress(), clientthread);
               this.cleanClientThreadsMap();
            } catch (SocketTimeoutException var7) {
               this.cleanClientThreadsMap();
            } catch (IOException ioexception) {
               if (this.running) {
                  this.logInfo("IO: " + ioexception.getMessage());
               }
            }
         }
      } finally {
         this.closeServerSocket(this.serverSocket);
      }

   }

   /**
    * Creates a new Thread object from this class and starts running
    */
   public void startThread() {
      if (this.rconPassword.isEmpty()) {
         this.logWarning("No rcon password set in server.properties, rcon disabled!");
      } else if (0 < this.rconPort && 65535 >= this.rconPort) {
         if (!this.running) {
            try {
               this.serverSocket = new ServerSocket(this.rconPort, 0, InetAddress.getByName(this.hostname));
               this.serverSocket.setSoTimeout(500);
               super.startThread();
            } catch (IOException ioexception) {
               this.logWarning("Unable to initialise rcon on " + this.hostname + ":" + this.rconPort + " : " + ioexception.getMessage());
            }

         }
      } else {
         this.logWarning("Invalid rcon port " + this.rconPort + " found in server.properties, rcon disabled!");
      }
   }

   public void func_219591_b() {
      super.func_219591_b();

      for(Entry<SocketAddress, ClientThread> entry : this.clientThreads.entrySet()) {
         entry.getValue().func_219591_b();
      }

      this.closeServerSocket(this.serverSocket);
      this.initClientThreadList();
   }
}