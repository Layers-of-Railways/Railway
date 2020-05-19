package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.multiplayer.LanServerPingThread;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LanServerDetector {
   private static final AtomicInteger ATOMIC_COUNTER = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();

   @OnlyIn(Dist.CLIENT)
   public static class LanServerFindThread extends Thread {
      private final LanServerDetector.LanServerList localServerList;
      private final InetAddress broadcastAddress;
      private final MulticastSocket socket;

      public LanServerFindThread(LanServerDetector.LanServerList list) throws IOException {
         super("LanServerDetector #" + LanServerDetector.ATOMIC_COUNTER.incrementAndGet());
         this.localServerList = list;
         this.setDaemon(true);
         this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LanServerDetector.LOGGER));
         this.socket = new MulticastSocket(4445);
         this.broadcastAddress = InetAddress.getByName("224.0.2.60");
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.broadcastAddress);
      }

      public void run() {
         byte[] abyte = new byte[1024];

         while(!this.isInterrupted()) {
            DatagramPacket datagrampacket = new DatagramPacket(abyte, abyte.length);

            try {
               this.socket.receive(datagrampacket);
            } catch (SocketTimeoutException var5) {
               continue;
            } catch (IOException ioexception) {
               LanServerDetector.LOGGER.error("Couldn't ping server", (Throwable)ioexception);
               break;
            }

            String s = new String(datagrampacket.getData(), datagrampacket.getOffset(), datagrampacket.getLength(), StandardCharsets.UTF_8);
            LanServerDetector.LOGGER.debug("{}: {}", datagrampacket.getAddress(), s);
            this.localServerList.addServer(s, datagrampacket.getAddress());
         }

         try {
            this.socket.leaveGroup(this.broadcastAddress);
         } catch (IOException var4) {
            ;
         }

         this.socket.close();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LanServerList {
      private final List<LanServerInfo> listOfLanServers = Lists.newArrayList();
      private boolean wasUpdated;

      public synchronized boolean getWasUpdated() {
         return this.wasUpdated;
      }

      public synchronized void setWasNotUpdated() {
         this.wasUpdated = false;
      }

      public synchronized List<LanServerInfo> getLanServers() {
         return Collections.unmodifiableList(this.listOfLanServers);
      }

      public synchronized void addServer(String pingResponse, InetAddress ipAddress) {
         String s = LanServerPingThread.getMotdFromPingResponse(pingResponse);
         String s1 = LanServerPingThread.getAdFromPingResponse(pingResponse);
         if (s1 != null) {
            s1 = ipAddress.getHostAddress() + ":" + s1;
            boolean flag = false;

            for(LanServerInfo lanserverinfo : this.listOfLanServers) {
               if (lanserverinfo.getServerIpPort().equals(s1)) {
                  lanserverinfo.updateLastSeen();
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               this.listOfLanServers.add(new LanServerInfo(s, s1));
               this.wasUpdated = true;
            }

         }
      }
   }
}