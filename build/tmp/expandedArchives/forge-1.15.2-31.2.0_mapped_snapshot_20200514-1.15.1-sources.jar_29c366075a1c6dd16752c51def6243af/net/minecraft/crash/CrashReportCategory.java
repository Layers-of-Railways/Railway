package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrashReportCategory {
   private final CrashReport crashReport;
   private final String name;
   private final List<CrashReportCategory.Entry> children = Lists.newArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];

   public CrashReportCategory(CrashReport report, String name) {
      this.crashReport = report;
      this.name = name;
   }

   @OnlyIn(Dist.CLIENT)
   public static String getCoordinateInfo(double x, double y, double z) {
      return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", x, y, z, getCoordinateInfo(new BlockPos(x, y, z)));
   }

   public static String getCoordinateInfo(BlockPos pos) {
      return getCoordinateInfo(pos.getX(), pos.getY(), pos.getZ());
   }

   public static String getCoordinateInfo(int x, int y, int z) {
      StringBuilder stringbuilder = new StringBuilder();

      try {
         stringbuilder.append(String.format("World: (%d,%d,%d)", x, y, z));
      } catch (Throwable var16) {
         stringbuilder.append("(Error finding world loc)");
      }

      stringbuilder.append(", ");

      try {
         int i = x >> 4;
         int j = z >> 4;
         int k = x & 15;
         int l = y >> 4;
         int i1 = z & 15;
         int j1 = i << 4;
         int k1 = j << 4;
         int l1 = (i + 1 << 4) - 1;
         int i2 = (j + 1 << 4) - 1;
         stringbuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", k, l, i1, i, j, j1, k1, l1, i2));
      } catch (Throwable var15) {
         stringbuilder.append("(Error finding chunk loc)");
      }

      stringbuilder.append(", ");

      try {
         int k2 = x >> 9;
         int l2 = z >> 9;
         int i3 = k2 << 5;
         int j3 = l2 << 5;
         int k3 = (k2 + 1 << 5) - 1;
         int l3 = (l2 + 1 << 5) - 1;
         int i4 = k2 << 9;
         int j4 = l2 << 9;
         int k4 = (k2 + 1 << 9) - 1;
         int j2 = (l2 + 1 << 9) - 1;
         stringbuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", k2, l2, i3, j3, k3, l3, i4, j4, k4, j2));
      } catch (Throwable var14) {
         stringbuilder.append("(Error finding world loc)");
      }

      return stringbuilder.toString();
   }

   /**
    * Adds an additional section to this crash report category, resolved by calling the given callable.
    *  
    * If the given callable throws an exception, a detail containing that exception will be created instead.
    */
   public CrashReportCategory addDetail(String nameIn, ICrashReportDetail<String> detail) {
      try {
         this.addDetail(nameIn, detail.call());
      } catch (Throwable throwable) {
         this.addCrashSectionThrowable(nameIn, throwable);
      }

      return this;
   }

   /**
    * Adds a Crashreport section with the given name with the given value (convered .toString())
    */
   public CrashReportCategory addDetail(String sectionName, Object value) {
      this.children.add(new CrashReportCategory.Entry(sectionName, value));
      return this;
   }

   /**
    * Adds a Crashreport section with the given name with the given Throwable
    */
   public void addCrashSectionThrowable(String sectionName, Throwable throwable) {
      this.addDetail(sectionName, throwable);
   }

   /**
    * Resets our stack trace according to the current trace, pruning the deepest 3 entries.  The parameter indicates how
    * many additional deepest entries to prune.  Returns the number of entries in the resulting pruned stack trace.
    */
   public int getPrunedStackTrace(int size) {
      StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
      if (astacktraceelement.length <= 0) {
         return 0;
      } else {
         int len = astacktraceelement.length - 3 - size;
         if (len <= 0) len = astacktraceelement.length;
         this.stackTrace = new StackTraceElement[len];
         System.arraycopy(astacktraceelement, astacktraceelement.length - len, this.stackTrace, 0, this.stackTrace.length);
         return this.stackTrace.length;
      }
   }

   /**
    * Do the deepest two elements of our saved stack trace match the given elements, in order from the deepest?
    */
   public boolean firstTwoElementsOfStackTraceMatch(StackTraceElement s1, StackTraceElement s2) {
      if (this.stackTrace.length != 0 && s1 != null) {
         StackTraceElement stacktraceelement = this.stackTrace[0];
         if (stacktraceelement.isNativeMethod() == s1.isNativeMethod() && stacktraceelement.getClassName().equals(s1.getClassName()) && stacktraceelement.getFileName().equals(s1.getFileName()) && stacktraceelement.getMethodName().equals(s1.getMethodName())) {
            if (s2 != null != this.stackTrace.length > 1) {
               return false;
            } else if (s2 != null && !this.stackTrace[1].equals(s2)) {
               return false;
            } else {
               this.stackTrace[0] = s1;
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Removes the given number entries from the bottom of the stack trace.
    */
   public void trimStackTraceEntriesFromBottom(int amount) {
      StackTraceElement[] astacktraceelement = new StackTraceElement[this.stackTrace.length - amount];
      System.arraycopy(this.stackTrace, 0, astacktraceelement, 0, astacktraceelement.length);
      this.stackTrace = astacktraceelement;
   }

   public void appendToStringBuilder(StringBuilder builder) {
      builder.append("-- ").append(this.name).append(" --\n");
      builder.append("Details:");

      for(CrashReportCategory.Entry crashreportcategory$entry : this.children) {
         builder.append("\n\t");
         builder.append(crashreportcategory$entry.getKey());
         builder.append(": ");
         builder.append(crashreportcategory$entry.getValue());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         builder.append("\nStacktrace:");

         for(StackTraceElement stacktraceelement : this.stackTrace) {
            builder.append("\n\tat ");
            builder.append((Object)stacktraceelement);
         }
      }

   }

   public StackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public static void addBlockInfo(CrashReportCategory category, BlockPos pos, @Nullable BlockState state) {
      if (state != null) {
         category.addDetail("Block", state::toString);
      }

      category.addDetail("Block location", () -> {
         return getCoordinateInfo(pos);
      });
   }

   static class Entry {
      private final String key;
      private final String value;

      public Entry(String key, Object value) {
         this.key = key;
         if (value == null) {
            this.value = "~~NULL~~";
         } else if (value instanceof Throwable) {
            Throwable throwable = (Throwable)value;
            this.value = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
         } else {
            this.value = value.toString();
         }

      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }
   }
}