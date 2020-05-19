package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Snooper {
   /** The snooper Map of stats */
   private final Map<String, Object> snooperStats = Maps.newHashMap();
   /** The client Map of stats */
   private final Map<String, Object> clientStats = Maps.newHashMap();
   private final String uniqueID = UUID.randomUUID().toString();
   private final URL serverUrl;
   private final ISnooperInfo playerStatsCollector;
   private final Timer timer = new Timer("Snooper Timer", true);
   private final Object syncLock = new Object();
   private final long minecraftStartTimeMilis;
   private boolean isRunning;

   public Snooper(String side, ISnooperInfo playerStatCollector, long startTime) {
      try {
         this.serverUrl = new URL("http://snoop.minecraft.net/" + side + "?version=" + 2);
      } catch (MalformedURLException var6) {
         throw new IllegalArgumentException();
      }

      this.playerStatsCollector = playerStatCollector;
      this.minecraftStartTimeMilis = startTime;
   }

   /**
    * Note issuing start multiple times is not an error.
    */
   public void start() {
      if (!this.isRunning) {
         ;
      }

   }

   public void addMemoryStatsToSnooper() {
      this.addStatToSnooper("memory_total", Runtime.getRuntime().totalMemory());
      this.addStatToSnooper("memory_max", Runtime.getRuntime().maxMemory());
      this.addStatToSnooper("memory_free", Runtime.getRuntime().freeMemory());
      this.addStatToSnooper("cpu_cores", Runtime.getRuntime().availableProcessors());
      this.playerStatsCollector.fillSnooper(this);
   }

   public void addClientStat(String statName, Object statValue) {
      synchronized(this.syncLock) {
         this.clientStats.put(statName, statValue);
      }
   }

   public void addStatToSnooper(String statName, Object statValue) {
      synchronized(this.syncLock) {
         this.snooperStats.put(statName, statValue);
      }
   }

   public boolean isSnooperRunning() {
      return this.isRunning;
   }

   public void stop() {
      this.timer.cancel();
   }

   @OnlyIn(Dist.CLIENT)
   public String getUniqueID() {
      return this.uniqueID;
   }

   /**
    * Returns the saved value of System#currentTimeMillis when the game started
    */
   public long getMinecraftStartTimeMillis() {
      return this.minecraftStartTimeMilis;
   }
}