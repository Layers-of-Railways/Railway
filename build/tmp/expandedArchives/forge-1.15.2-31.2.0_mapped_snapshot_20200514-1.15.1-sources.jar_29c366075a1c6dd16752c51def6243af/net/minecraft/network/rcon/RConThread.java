package net.minecraft.network.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.DefaultWithNameUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RConThread implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
   protected boolean running;
   protected final IServer server;
   protected final String threadName;
   protected Thread rconThread;
   protected final int maxStopWait = 5;
   protected final List<DatagramSocket> socketList = Lists.newArrayList();
   protected final List<ServerSocket> serverSocketList = Lists.newArrayList();

   protected RConThread(IServer serverIn, String threadName) {
      this.server = serverIn;
      this.threadName = threadName;
      if (this.server.isDebuggingEnabled()) {
         this.logWarning("Debugging is enabled, performance maybe reduced!");
      }

   }

   /**
    * Creates a new Thread object from this class and starts running
    */
   public synchronized void startThread() {
      this.rconThread = new Thread(this, this.threadName + " #" + THREAD_ID.incrementAndGet());
      this.rconThread.setUncaughtExceptionHandler(new DefaultWithNameUncaughtExceptionHandler(LOGGER));
      this.rconThread.start();
      this.running = true;
   }

   public synchronized void func_219591_b() {
      this.running = false;
      if (null != this.rconThread) {
         int i = 0;

         while(this.rconThread.isAlive()) {
            try {
               this.rconThread.join(1000L);
               ++i;
               if (5 <= i) {
                  this.logWarning("Waited " + i + " seconds attempting force stop!");
                  this.closeAllSockets_do(true);
               } else if (this.rconThread.isAlive()) {
                  this.logWarning("Thread " + this + " (" + this.rconThread.getState() + ") failed to exit after " + i + " second(s)");
                  this.logWarning("Stack:");

                  for(StackTraceElement stacktraceelement : this.rconThread.getStackTrace()) {
                     this.logWarning(stacktraceelement.toString());
                  }

                  this.rconThread.interrupt();
               }
            } catch (InterruptedException var6) {
               ;
            }
         }

         this.closeAllSockets_do(true);
         this.rconThread = null;
      }
   }

   /**
    * Returns true if the Thread is running, false otherwise
    */
   public boolean isRunning() {
      return this.running;
   }

   /**
    * Log debug message
    */
   protected void logDebug(String msg) {
      this.server.logDebug(msg);
   }

   /**
    * Log information message
    */
   protected void logInfo(String msg) {
      this.server.logInfo(msg);
   }

   /**
    * Log warning message
    */
   protected void logWarning(String msg) {
      this.server.logWarning(msg);
   }

   /**
    * Log severe error message
    */
   protected void logSevere(String msg) {
      this.server.logSevere(msg);
   }

   /**
    * Returns the number of players on the server
    */
   protected int getNumberOfPlayers() {
      return this.server.getCurrentPlayerCount();
   }

   /**
    * Registers a DatagramSocket with this thread
    */
   protected void registerSocket(DatagramSocket socket) {
      this.logDebug("registerSocket: " + socket);
      this.socketList.add(socket);
   }

   /**
    * Closes the specified DatagramSocket
    */
   protected boolean closeSocket(DatagramSocket socket, boolean removeFromList) {
      this.logDebug("closeSocket: " + socket);
      if (null == socket) {
         return false;
      } else {
         boolean flag = false;
         if (!socket.isClosed()) {
            socket.close();
            flag = true;
         }

         if (removeFromList) {
            this.socketList.remove(socket);
         }

         return flag;
      }
   }

   /**
    * Closes the specified ServerSocket
    */
   protected boolean closeServerSocket(ServerSocket socket) {
      return this.closeServerSocket_do(socket, true);
   }

   /**
    * Closes the specified ServerSocket
    */
   protected boolean closeServerSocket_do(ServerSocket socket, boolean removeFromList) {
      this.logDebug("closeSocket: " + socket);
      if (null == socket) {
         return false;
      } else {
         boolean flag = false;

         try {
            if (!socket.isClosed()) {
               socket.close();
               flag = true;
            }
         } catch (IOException ioexception) {
            this.logWarning("IO: " + ioexception.getMessage());
         }

         if (removeFromList) {
            this.serverSocketList.remove(socket);
         }

         return flag;
      }
   }

   /**
    * Closes all of the opened sockets
    */
   protected void closeAllSockets() {
      this.closeAllSockets_do(false);
   }

   /**
    * Closes all of the opened sockets
    */
   protected void closeAllSockets_do(boolean logWarning) {
      int i = 0;

      for(DatagramSocket datagramsocket : this.socketList) {
         if (this.closeSocket(datagramsocket, false)) {
            ++i;
         }
      }

      this.socketList.clear();

      for(ServerSocket serversocket : this.serverSocketList) {
         if (this.closeServerSocket_do(serversocket, false)) {
            ++i;
         }
      }

      this.serverSocketList.clear();
      if (logWarning && 0 < i) {
         this.logWarning("Force closed " + i + " sockets");
      }

   }
}