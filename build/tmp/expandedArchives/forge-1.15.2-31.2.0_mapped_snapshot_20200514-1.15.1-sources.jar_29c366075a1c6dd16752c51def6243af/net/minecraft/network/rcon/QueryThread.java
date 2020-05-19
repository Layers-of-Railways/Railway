package net.minecraft.network.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.Util;

public class QueryThread extends RConThread {
   private long lastAuthCheckTime;
   private final int queryPort;
   private final int serverPort;
   private final int maxPlayers;
   private final String serverMotd;
   private final String worldName;
   private DatagramSocket querySocket;
   private final byte[] buffer = new byte[1460];
   private DatagramPacket incomingPacket;
   private final Map<SocketAddress, String> idents;
   private String queryHostname;
   private String serverHostname;
   private final Map<SocketAddress, QueryThread.Auth> queryClients;
   private final long time;
   private final RConOutputStream output;
   private long lastQueryResponseTime;

   public QueryThread(IServer p_i1536_1_) {
      super(p_i1536_1_, "Query Listener");
      this.queryPort = p_i1536_1_.getServerProperties().queryPort;
      this.serverHostname = p_i1536_1_.getHostname();
      this.serverPort = p_i1536_1_.getPort();
      this.serverMotd = p_i1536_1_.getMotd();
      this.maxPlayers = p_i1536_1_.getMaxPlayers();
      this.worldName = p_i1536_1_.getFolderName();
      this.lastQueryResponseTime = 0L;
      this.queryHostname = "0.0.0.0";
      if (!this.serverHostname.isEmpty() && !this.queryHostname.equals(this.serverHostname)) {
         this.queryHostname = this.serverHostname;
      } else {
         this.serverHostname = "0.0.0.0";

         try {
            InetAddress inetaddress = InetAddress.getLocalHost();
            this.queryHostname = inetaddress.getHostAddress();
         } catch (UnknownHostException unknownhostexception) {
            this.logWarning("Unable to determine local host IP, please set server-ip in server.properties: " + unknownhostexception.getMessage());
         }
      }

      this.idents = Maps.newHashMap();
      this.output = new RConOutputStream(1460);
      this.queryClients = Maps.newHashMap();
      this.time = (new Date()).getTime();
   }

   /**
    * Sends a byte array as a DatagramPacket response to the client who sent the given DatagramPacket
    */
   private void sendResponsePacket(byte[] data, DatagramPacket requestPacket) throws IOException {
      this.querySocket.send(new DatagramPacket(data, data.length, requestPacket.getSocketAddress()));
   }

   /**
    * Parses an incoming DatagramPacket, returning true if the packet was valid
    */
   private boolean parseIncomingPacket(DatagramPacket requestPacket) throws IOException {
      byte[] abyte = requestPacket.getData();
      int i = requestPacket.getLength();
      SocketAddress socketaddress = requestPacket.getSocketAddress();
      this.logDebug("Packet len " + i + " [" + socketaddress + "]");
      if (3 <= i && -2 == abyte[0] && -3 == abyte[1]) {
         this.logDebug("Packet '" + RConUtils.getByteAsHexString(abyte[2]) + "' [" + socketaddress + "]");
         switch(abyte[2]) {
         case 0:
            if (!this.verifyClientAuth(requestPacket)) {
               this.logDebug("Invalid challenge [" + socketaddress + "]");
               return false;
            } else if (15 == i) {
               this.sendResponsePacket(this.createQueryResponse(requestPacket), requestPacket);
               this.logDebug("Rules [" + socketaddress + "]");
            } else {
               RConOutputStream rconoutputstream = new RConOutputStream(1460);
               rconoutputstream.writeInt(0);
               rconoutputstream.writeByteArray(this.getRequestID(requestPacket.getSocketAddress()));
               rconoutputstream.writeString(this.serverMotd);
               rconoutputstream.writeString("SMP");
               rconoutputstream.writeString(this.worldName);
               rconoutputstream.writeString(Integer.toString(this.getNumberOfPlayers()));
               rconoutputstream.writeString(Integer.toString(this.maxPlayers));
               rconoutputstream.writeShort((short)this.serverPort);
               rconoutputstream.writeString(this.queryHostname);
               this.sendResponsePacket(rconoutputstream.toByteArray(), requestPacket);
               this.logDebug("Status [" + socketaddress + "]");
            }
         default:
            return true;
         case 9:
            this.sendAuthChallenge(requestPacket);
            this.logDebug("Challenge [" + socketaddress + "]");
            return true;
         }
      } else {
         this.logDebug("Invalid packet [" + socketaddress + "]");
         return false;
      }
   }

   /**
    * Creates a query response as a byte array for the specified query DatagramPacket
    */
   private byte[] createQueryResponse(DatagramPacket requestPacket) throws IOException {
      long i = Util.milliTime();
      if (i < this.lastQueryResponseTime + 5000L) {
         byte[] abyte = this.output.toByteArray();
         byte[] abyte1 = this.getRequestID(requestPacket.getSocketAddress());
         abyte[1] = abyte1[0];
         abyte[2] = abyte1[1];
         abyte[3] = abyte1[2];
         abyte[4] = abyte1[3];
         return abyte;
      } else {
         this.lastQueryResponseTime = i;
         this.output.reset();
         this.output.writeInt(0);
         this.output.writeByteArray(this.getRequestID(requestPacket.getSocketAddress()));
         this.output.writeString("splitnum");
         this.output.writeInt(128);
         this.output.writeInt(0);
         this.output.writeString("hostname");
         this.output.writeString(this.serverMotd);
         this.output.writeString("gametype");
         this.output.writeString("SMP");
         this.output.writeString("game_id");
         this.output.writeString("MINECRAFT");
         this.output.writeString("version");
         this.output.writeString(this.server.getMinecraftVersion());
         this.output.writeString("plugins");
         this.output.writeString(this.server.getPlugins());
         this.output.writeString("map");
         this.output.writeString(this.worldName);
         this.output.writeString("numplayers");
         this.output.writeString("" + this.getNumberOfPlayers());
         this.output.writeString("maxplayers");
         this.output.writeString("" + this.maxPlayers);
         this.output.writeString("hostport");
         this.output.writeString("" + this.serverPort);
         this.output.writeString("hostip");
         this.output.writeString(this.queryHostname);
         this.output.writeInt(0);
         this.output.writeInt(1);
         this.output.writeString("player_");
         this.output.writeInt(0);
         String[] astring = this.server.getOnlinePlayerNames();

         for(String s : astring) {
            this.output.writeString(s);
         }

         this.output.writeInt(0);
         return this.output.toByteArray();
      }
   }

   /**
    * Returns the request ID provided by the authorized client
    */
   private byte[] getRequestID(SocketAddress address) {
      return this.queryClients.get(address).getRequestId();
   }

   /**
    * Returns true if the client has a valid auth, otherwise false
    */
   private Boolean verifyClientAuth(DatagramPacket requestPacket) {
      SocketAddress socketaddress = requestPacket.getSocketAddress();
      if (!this.queryClients.containsKey(socketaddress)) {
         return false;
      } else {
         byte[] abyte = requestPacket.getData();
         return this.queryClients.get(socketaddress).getRandomChallenge() != RConUtils.getBytesAsBEint(abyte, 7, requestPacket.getLength()) ? false : true;
      }
   }

   /**
    * Sends an auth challenge DatagramPacket to the client and adds the client to the queryClients map
    */
   private void sendAuthChallenge(DatagramPacket requestPacket) throws IOException {
      QueryThread.Auth querythread$auth = new QueryThread.Auth(requestPacket);
      this.queryClients.put(requestPacket.getSocketAddress(), querythread$auth);
      this.sendResponsePacket(querythread$auth.getChallengeValue(), requestPacket);
   }

   /**
    * Removes all clients whose auth is no longer valid
    */
   private void cleanQueryClientsMap() {
      if (this.running) {
         long i = Util.milliTime();
         if (i >= this.lastAuthCheckTime + 30000L) {
            this.lastAuthCheckTime = i;
            Iterator<Entry<SocketAddress, QueryThread.Auth>> iterator = this.queryClients.entrySet().iterator();

            while(iterator.hasNext()) {
               Entry<SocketAddress, QueryThread.Auth> entry = iterator.next();
               if (entry.getValue().hasExpired(i)) {
                  iterator.remove();
               }
            }

         }
      }
   }

   public void run() {
      this.logInfo("Query running on " + this.serverHostname + ":" + this.queryPort);
      this.lastAuthCheckTime = Util.milliTime();
      this.incomingPacket = new DatagramPacket(this.buffer, this.buffer.length);

      try {
         while(this.running) {
            try {
               this.querySocket.receive(this.incomingPacket);
               this.cleanQueryClientsMap();
               this.parseIncomingPacket(this.incomingPacket);
            } catch (SocketTimeoutException var7) {
               this.cleanQueryClientsMap();
            } catch (PortUnreachableException var8) {
               ;
            } catch (IOException ioexception) {
               this.stopWithException(ioexception);
            }
         }
      } finally {
         this.closeAllSockets();
      }

   }

   /**
    * Creates a new Thread object from this class and starts running
    */
   public void startThread() {
      if (!this.running) {
         if (0 < this.queryPort && 65535 >= this.queryPort) {
            if (this.initQuerySystem()) {
               super.startThread();
            }

         } else {
            this.logWarning("Invalid query port " + this.queryPort + " found in server.properties (queries disabled)");
         }
      }
   }

   /**
    * Stops the query server and reports the given Exception
    */
   private void stopWithException(Exception exception) {
      if (this.running) {
         this.logWarning("Unexpected exception, buggy JRE? (" + exception + ")");
         if (!this.initQuerySystem()) {
            this.logSevere("Failed to recover from buggy JRE, shutting down!");
            this.running = false;
         }

      }
   }

   /**
    * Initializes the query system by binding it to a port
    */
   private boolean initQuerySystem() {
      try {
         this.querySocket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.serverHostname));
         this.registerSocket(this.querySocket);
         this.querySocket.setSoTimeout(500);
         return true;
      } catch (SocketException socketexception) {
         this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Socket): " + socketexception.getMessage());
      } catch (UnknownHostException unknownhostexception) {
         this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Unknown Host): " + unknownhostexception.getMessage());
      } catch (Exception exception) {
         this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (E): " + exception.getMessage());
      }

      return false;
   }

   class Auth {
      private final long timestamp = (new Date()).getTime();
      private final int randomChallenge;
      private final byte[] requestId;
      private final byte[] challengeValue;
      private final String requestIdAsString;

      public Auth(DatagramPacket requestPacket) {
         byte[] abyte = requestPacket.getData();
         this.requestId = new byte[4];
         this.requestId[0] = abyte[3];
         this.requestId[1] = abyte[4];
         this.requestId[2] = abyte[5];
         this.requestId[3] = abyte[6];
         this.requestIdAsString = new String(this.requestId, StandardCharsets.UTF_8);
         this.randomChallenge = (new Random()).nextInt(16777216);
         this.challengeValue = String.format("\t%s%d\u0000", this.requestIdAsString, this.randomChallenge).getBytes(StandardCharsets.UTF_8);
      }

      /**
       * Returns true if the auth's creation timestamp is less than the given time, otherwise false
       */
      public Boolean hasExpired(long currentTime) {
         return this.timestamp < currentTime;
      }

      /**
       * Returns the random challenge number assigned to this auth
       */
      public int getRandomChallenge() {
         return this.randomChallenge;
      }

      /**
       * Returns the auth challenge value
       */
      public byte[] getChallengeValue() {
         return this.challengeValue;
      }

      /**
       * Returns the request ID provided by the client.
       */
      public byte[] getRequestId() {
         return this.requestId;
      }
   }
}