package net.minecraft.server.dedicated;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHangWatchdog implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DedicatedServer server;
   private final long maxTickTime;

   public ServerHangWatchdog(DedicatedServer server) {
      this.server = server;
      this.maxTickTime = server.getMaxTickTime();
   }

   public void run() {
      while(this.server.isServerRunning()) {
         long i = this.server.getServerTime();
         long j = Util.milliTime();
         long k = j - i;
         if (k > this.maxTickTime) {
            LOGGER.fatal("A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", (float)k / 1000.0F), String.format(Locale.ROOT, "%.2f", 0.05F));
            LOGGER.fatal("Considering it to be crashed, server will forcibly shutdown.");
            ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
            StringBuilder stringbuilder = new StringBuilder();
            Error error = new Error(String.format("ServerHangWatchdog detected that a single server tick took %.2f seconds (should be max 0.05)", k / 1000F)); // Forge: don't just make a crash report with a seemingly-inexplicable Error

            for(ThreadInfo threadinfo : athreadinfo) {
               if (threadinfo.getThreadId() == this.server.getExecutionThread().getId()) {
                  error.setStackTrace(threadinfo.getStackTrace());
               }

               stringbuilder.append((Object)threadinfo);
               stringbuilder.append("\n");
            }

            CrashReport crashreport = new CrashReport("Watching Server", error);
            this.server.addServerInfoToCrashReport(crashreport);
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Thread Dump");
            crashreportcategory.addDetail("Threads", stringbuilder);
            File file1 = new File(new File(this.server.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
            if (crashreport.saveToFile(file1)) {
               LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
            } else {
               LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.scheduleHalt();
         }

         try {
            Thread.sleep(i + this.maxTickTime - j);
         } catch (InterruptedException var15) {
            ;
         }
      }

   }

   private void scheduleHalt() {
      try {
         Timer timer = new Timer();
         timer.schedule(new TimerTask() {
            public void run() {
               Runtime.getRuntime().halt(1);
            }
         }, 10000L);
         System.exit(1);
      } catch (Throwable var2) {
         Runtime.getRuntime().halt(1);
      }

   }
}