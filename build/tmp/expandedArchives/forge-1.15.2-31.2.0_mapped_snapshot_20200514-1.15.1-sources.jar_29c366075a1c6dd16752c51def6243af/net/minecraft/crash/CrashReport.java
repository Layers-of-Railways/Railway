package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String description;
   private final Throwable cause;
   private final CrashReportCategory systemDetailsCategory = new CrashReportCategory(this, "System Details");
   private final List<CrashReportCategory> crashReportSections = Lists.newArrayList();
   private File crashReportFile;
   private boolean firstCategoryInCrashReport = true;
   private StackTraceElement[] stacktrace = new StackTraceElement[0];

   public CrashReport(String descriptionIn, Throwable causeThrowable) {
      this.description = descriptionIn;
      this.cause = causeThrowable;
      this.populateEnvironment();
   }

   /**
    * Populates this crash report with initial information about the running server and operating system / java
    * environment
    */
   private void populateEnvironment() {
      this.systemDetailsCategory.addDetail("Minecraft Version", () -> {
         return SharedConstants.getVersion().getName();
      });
      this.systemDetailsCategory.addDetail("Minecraft Version ID", () -> {
         return SharedConstants.getVersion().getId();
      });
      this.systemDetailsCategory.addDetail("Operating System", () -> {
         return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
      });
      this.systemDetailsCategory.addDetail("Java Version", () -> {
         return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
      });
      this.systemDetailsCategory.addDetail("Java VM Version", () -> {
         return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
      });
      this.systemDetailsCategory.addDetail("Memory", () -> {
         Runtime runtime = Runtime.getRuntime();
         long i = runtime.maxMemory();
         long j = runtime.totalMemory();
         long k = runtime.freeMemory();
         long l = i / 1024L / 1024L;
         long i1 = j / 1024L / 1024L;
         long j1 = k / 1024L / 1024L;
         return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
      });
      this.systemDetailsCategory.addDetail("CPUs", Runtime.getRuntime().availableProcessors());
      this.systemDetailsCategory.addDetail("JVM Flags", () -> {
         List<String> list = Util.getJvmFlags().collect(Collectors.toList());
         return String.format("%d total; %s", list.size(), list.stream().collect(Collectors.joining(" ")));
      });
      net.minecraftforge.fml.CrashReportExtender.enhanceCrashReport(this, this.systemDetailsCategory);
   }

   /**
    * Returns the description of the Crash Report.
    */
   public String getDescription() {
      return this.description;
   }

   /**
    * Returns the Throwable object that is the cause for the crash and Crash Report.
    */
   public Throwable getCrashCause() {
      return this.cause;
   }

   /**
    * Gets the various sections of the crash report into the given StringBuilder
    */
   public void getSectionsInStringBuilder(StringBuilder builder) {
      if ((this.stacktrace == null || this.stacktrace.length <= 0) && !this.crashReportSections.isEmpty()) {
         this.stacktrace = ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);
      }

      if (this.stacktrace != null && this.stacktrace.length > 0) {
         builder.append("-- Head --\n");
         builder.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
         builder.append("Stacktrace:\n");

         for(StackTraceElement stacktraceelement : this.stacktrace) {
            builder.append("\t").append("at ").append((Object)stacktraceelement);
            builder.append("\n");
         }

         builder.append("\n");
      }

      for(CrashReportCategory crashreportcategory : this.crashReportSections) {
         crashreportcategory.appendToStringBuilder(builder);
         builder.append("\n\n");
      }

      this.systemDetailsCategory.appendToStringBuilder(builder);
   }

   /**
    * Gets the stack trace of the Throwable that caused this crash report, or if that fails, the cause .toString().
    */
   public String getCauseStackTraceOrString() {
      StringWriter stringwriter = null;
      PrintWriter printwriter = null;
      Throwable throwable = this.cause;
      if (throwable.getMessage() == null) {
         if (throwable instanceof NullPointerException) {
            throwable = new NullPointerException(this.description);
         } else if (throwable instanceof StackOverflowError) {
            throwable = new StackOverflowError(this.description);
         } else if (throwable instanceof OutOfMemoryError) {
            throwable = new OutOfMemoryError(this.description);
         }

         throwable.setStackTrace(this.cause.getStackTrace());
      }

      return net.minecraftforge.fml.CrashReportExtender.generateEnhancedStackTrace(throwable);
   }

   /**
    * Gets the complete report with headers, stack trace, and different sections as a string.
    */
   public String getCompleteReport() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("---- Minecraft Crash Report ----\n");
      net.minecraftforge.fml.CrashReportExtender.addCrashReportHeader(stringbuilder, this);
      stringbuilder.append("// ");
      stringbuilder.append(getWittyComment());
      stringbuilder.append("\n\n");
      stringbuilder.append("Time: ");
      stringbuilder.append((new SimpleDateFormat()).format(new Date()));
      stringbuilder.append("\n");
      stringbuilder.append("Description: ");
      stringbuilder.append(this.description);
      stringbuilder.append("\n\n");
      stringbuilder.append(this.getCauseStackTraceOrString());
      stringbuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int i = 0; i < 87; ++i) {
         stringbuilder.append("-");
      }

      stringbuilder.append("\n\n");
      this.getSectionsInStringBuilder(stringbuilder);
      return stringbuilder.toString();
   }

   /**
    * Gets the file this crash report is saved into.
    */
   @OnlyIn(Dist.CLIENT)
   public File getFile() {
      return this.crashReportFile;
   }

   /**
    * Saves this CrashReport to the given file and returns a value indicating whether we were successful at doing so.
    */
   public boolean saveToFile(File toFile) {
      if (this.crashReportFile != null) {
         return false;
      } else {
         if (toFile.getParentFile() != null) {
            toFile.getParentFile().mkdirs();
         }

         Writer writer = null;

         boolean flag1;
         try {
            writer = new OutputStreamWriter(new FileOutputStream(toFile), StandardCharsets.UTF_8);
            writer.write(this.getCompleteReport());
            this.crashReportFile = toFile;
            boolean lvt_3_1_ = true;
            return lvt_3_1_;
         } catch (Throwable throwable) {
            LOGGER.error("Could not save crash report to {}", toFile, throwable);
            flag1 = false;
         } finally {
            IOUtils.closeQuietly(writer);
         }

         return flag1;
      }
   }

   public CrashReportCategory getCategory() {
      return this.systemDetailsCategory;
   }

   /**
    * Creates a CrashReportCategory
    */
   public CrashReportCategory makeCategory(String name) {
      return this.makeCategoryDepth(name, 1);
   }

   /**
    * Creates a CrashReportCategory for the given stack trace depth
    */
   public CrashReportCategory makeCategoryDepth(String categoryName, int stacktraceLength) {
      CrashReportCategory crashreportcategory = new CrashReportCategory(this, categoryName);
      if (this.firstCategoryInCrashReport) {
         int i = crashreportcategory.getPrunedStackTrace(stacktraceLength);
         StackTraceElement[] astacktraceelement = this.cause.getStackTrace();
         StackTraceElement stacktraceelement = null;
         StackTraceElement stacktraceelement1 = null;
         int j = astacktraceelement.length - i;
         if (j < 0) {
            System.out.println("Negative index in crash report handler (" + astacktraceelement.length + "/" + i + ")");
         }

         if (astacktraceelement != null && 0 <= j && j < astacktraceelement.length) {
            stacktraceelement = astacktraceelement[j];
            if (astacktraceelement.length + 1 - i < astacktraceelement.length) {
               stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - i];
            }
         }

         this.firstCategoryInCrashReport = crashreportcategory.firstTwoElementsOfStackTraceMatch(stacktraceelement, stacktraceelement1);
         if (i > 0 && !this.crashReportSections.isEmpty()) {
            CrashReportCategory crashreportcategory1 = this.crashReportSections.get(this.crashReportSections.size() - 1);
            crashreportcategory1.trimStackTraceEntriesFromBottom(i);
         } else if (astacktraceelement != null && astacktraceelement.length >= i && 0 <= j && j < astacktraceelement.length) {
            this.stacktrace = new StackTraceElement[j];
            System.arraycopy(astacktraceelement, 0, this.stacktrace, 0, this.stacktrace.length);
         } else {
            this.firstCategoryInCrashReport = false;
         }
      }

      this.crashReportSections.add(crashreportcategory);
      return crashreportcategory;
   }

   /**
    * Gets a random witty comment for inclusion in this CrashReport
    */
   private static String getWittyComment() {
      String[] astring = new String[]{"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

      try {
         return astring[(int)(Util.nanoTime() % (long)astring.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   /**
    * Creates a crash report for the exception
    */
   public static CrashReport makeCrashReport(Throwable causeIn, String descriptionIn) {
      while(causeIn instanceof CompletionException && causeIn.getCause() != null) {
         causeIn = causeIn.getCause();
      }

      CrashReport crashreport;
      if (causeIn instanceof ReportedException) {
         crashreport = ((ReportedException)causeIn).getCrashReport();
      } else {
         crashreport = new CrashReport(descriptionIn, causeIn);
      }

      return crashreport;
   }

   public static void func_230188_h_() {
      (new CrashReport("Don't panic!", new Throwable())).getCompleteReport();
   }
}