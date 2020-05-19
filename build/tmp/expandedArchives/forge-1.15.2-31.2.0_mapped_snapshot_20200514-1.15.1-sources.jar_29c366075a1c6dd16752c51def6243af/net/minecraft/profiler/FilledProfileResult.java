package net.minecraft.profiler;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilledProfileResult implements IProfileResult {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final IProfilerSection field_230090_b_ = new IProfilerSection() {
      public long func_230037_a_() {
         return 0L;
      }

      public long func_230038_b_() {
         return 0L;
      }

      public Object2LongMap<String> func_230039_c_() {
         return Object2LongMaps.emptyMap();
      }
   };
   private static final Splitter field_230091_c_ = Splitter.on('\u001e');
   private static final Comparator<Entry<String, FilledProfileResult.Section>> field_230092_d_ = Entry.<String, FilledProfileResult.Section>comparingByValue(Comparator.comparingLong((p_230096_0_) -> {
      return p_230096_0_.field_230108_b_;
   })).reversed();
   private final Map<String, ? extends IProfilerSection> field_230093_e_;
   private final long timeStop;
   private final int ticksStop;
   private final long timeStart;
   private final int ticksStart;
   private final int ticksTotal;

   public FilledProfileResult(Map<String, ? extends IProfilerSection> p_i50407_1_, long p_i50407_2_, int p_i50407_4_, long p_i50407_5_, int p_i50407_7_) {
      this.field_230093_e_ = p_i50407_1_;
      this.timeStop = p_i50407_2_;
      this.ticksStop = p_i50407_4_;
      this.timeStart = p_i50407_5_;
      this.ticksStart = p_i50407_7_;
      this.ticksTotal = p_i50407_7_ - p_i50407_4_;
   }

   private IProfilerSection func_230104_c_(String p_230104_1_) {
      IProfilerSection iprofilersection = this.field_230093_e_.get(p_230104_1_);
      return iprofilersection != null ? iprofilersection : field_230090_b_;
   }

   public List<DataPoint> getDataPoints(String sectionPath) {
      IProfilerSection iprofilersection = this.func_230104_c_("root");
      long i = iprofilersection.func_230037_a_();
      IProfilerSection iprofilersection1 = this.func_230104_c_(sectionPath);
      long j = iprofilersection1.func_230037_a_();
      long k = iprofilersection1.func_230038_b_();
      List<DataPoint> list = Lists.newArrayList();
      if (!sectionPath.isEmpty()) {
         sectionPath = sectionPath + '\u001e';
      }

      long l = 0L;

      for(String s : this.field_230093_e_.keySet()) {
         if (func_230097_a_(sectionPath, s)) {
            l += this.func_230104_c_(s).func_230037_a_();
         }
      }

      float f = (float)l;
      if (l < j) {
         l = j;
      }

      if (i < l) {
         i = l;
      }

      for(String s1 : this.field_230093_e_.keySet()) {
         if (func_230097_a_(sectionPath, s1)) {
            IProfilerSection iprofilersection2 = this.func_230104_c_(s1);
            long i1 = iprofilersection2.func_230037_a_();
            double d0 = (double)i1 * 100.0D / (double)l;
            double d1 = (double)i1 * 100.0D / (double)i;
            String s2 = s1.substring(sectionPath.length());
            list.add(new DataPoint(s2, d0, d1, iprofilersection2.func_230038_b_()));
         }
      }

      if ((float)l > f) {
         list.add(new DataPoint("unspecified", (double)((float)l - f) * 100.0D / (double)l, (double)((float)l - f) * 100.0D / (double)i, k));
      }

      Collections.sort(list);
      list.add(0, new DataPoint(sectionPath, 100.0D, (double)l * 100.0D / (double)i, k));
      return list;
   }

   private static boolean func_230097_a_(String p_230097_0_, String p_230097_1_) {
      return p_230097_1_.length() > p_230097_0_.length() && p_230097_1_.startsWith(p_230097_0_) && p_230097_1_.indexOf(30, p_230097_0_.length() + 1) < 0;
   }

   private Map<String, FilledProfileResult.Section> func_230106_h_() {
      Map<String, FilledProfileResult.Section> map = Maps.newTreeMap();
      this.field_230093_e_.forEach((p_230101_1_, p_230101_2_) -> {
         Object2LongMap<String> object2longmap = p_230101_2_.func_230039_c_();
         if (!object2longmap.isEmpty()) {
            List<String> list = field_230091_c_.splitToList(p_230101_1_);
            object2longmap.forEach((p_230103_2_, p_230103_3_) -> {
               map.computeIfAbsent(p_230103_2_, (p_230105_0_) -> {
                  return new FilledProfileResult.Section();
               }).func_230112_a_(list.iterator(), p_230103_3_);
            });
         }

      });
      return map;
   }

   public long timeStop() {
      return this.timeStop;
   }

   public int ticksStop() {
      return this.ticksStop;
   }

   public long timeStart() {
      return this.timeStart;
   }

   public int ticksStart() {
      return this.ticksStart;
   }

   public boolean writeToFile(File p_219919_1_) {
      p_219919_1_.getParentFile().mkdirs();
      Writer writer = null;

      boolean flag1;
      try {
         writer = new OutputStreamWriter(new FileOutputStream(p_219919_1_), StandardCharsets.UTF_8);
         writer.write(this.inlineIntoCrashReport(this.nanoTime(), this.ticksSpend()));
         boolean lvt_3_1_ = true;
         return lvt_3_1_;
      } catch (Throwable throwable) {
         LOGGER.error("Could not save profiler results to {}", p_219919_1_, throwable);
         flag1 = false;
      } finally {
         IOUtils.closeQuietly(writer);
      }

      return flag1;
   }

   protected String inlineIntoCrashReport(long p_219929_1_, int p_219929_3_) {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("---- Minecraft Profiler Results ----\n");
      stringbuilder.append("// ");
      stringbuilder.append(getWittyString());
      stringbuilder.append("\n\n");
      stringbuilder.append("Version: ").append(SharedConstants.getVersion().getId()).append('\n');
      stringbuilder.append("Time span: ").append(p_219929_1_ / 1000000L).append(" ms\n");
      stringbuilder.append("Tick span: ").append(p_219929_3_).append(" ticks\n");
      stringbuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float)p_219929_3_ / ((float)p_219929_1_ / 1.0E9F))).append(" ticks per second. It should be ").append((int)20).append(" ticks per second\n\n");
      stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.format(0, "root", stringbuilder);
      stringbuilder.append("--- END PROFILE DUMP ---\n\n");
      Map<String, FilledProfileResult.Section> map = this.func_230106_h_();
      if (!map.isEmpty()) {
         stringbuilder.append("--- BEGIN COUNTER DUMP ---\n\n");
         this.func_230102_a_(map, stringbuilder, p_219929_3_);
         stringbuilder.append("--- END COUNTER DUMP ---\n\n");
      }

      return stringbuilder.toString();
   }

   private static StringBuilder func_230098_a_(StringBuilder p_230098_0_, int p_230098_1_) {
      p_230098_0_.append(String.format("[%02d] ", p_230098_1_));

      for(int i = 0; i < p_230098_1_; ++i) {
         p_230098_0_.append("|   ");
      }

      return p_230098_0_;
   }

   private void format(int p_219928_1_, String p_219928_2_, StringBuilder p_219928_3_) {
      List<DataPoint> list = this.getDataPoints(p_219928_2_);
      Object2LongMap<String> object2longmap = this.field_230093_e_.get(p_219928_2_).func_230039_c_();
      object2longmap.forEach((p_230100_3_, p_230100_4_) -> {
         func_230098_a_(p_219928_3_, p_219928_1_).append('#').append(p_230100_3_).append(' ').append((Object)p_230100_4_).append('/').append(p_230100_4_ / (long)this.ticksTotal).append('\n');
      });
      if (list.size() >= 3) {
         for(int i = 1; i < list.size(); ++i) {
            DataPoint datapoint = list.get(i);
            func_230098_a_(p_219928_3_, p_219928_1_).append(datapoint.name).append('(').append(datapoint.field_223511_c).append('/').append(String.format(Locale.ROOT, "%.0f", (float)datapoint.field_223511_c / (float)this.ticksTotal)).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", datapoint.relTime)).append("%/").append(String.format(Locale.ROOT, "%.2f", datapoint.rootRelTime)).append("%\n");
            if (!"unspecified".equals(datapoint.name)) {
               try {
                  this.format(p_219928_1_ + 1, p_219928_2_ + '\u001e' + datapoint.name, p_219928_3_);
               } catch (Exception exception) {
                  p_219928_3_.append("[[ EXCEPTION ").append((Object)exception).append(" ]]");
               }
            }
         }

      }
   }

   private void func_230095_a_(int p_230095_1_, String p_230095_2_, FilledProfileResult.Section p_230095_3_, int p_230095_4_, StringBuilder p_230095_5_) {
      func_230098_a_(p_230095_5_, p_230095_1_).append(p_230095_2_).append(" total:").append(p_230095_3_.field_230107_a_).append('/').append(p_230095_3_.field_230108_b_).append(" average: ").append(p_230095_3_.field_230107_a_ / (long)p_230095_4_).append('/').append(p_230095_3_.field_230108_b_ / (long)p_230095_4_).append('\n');
      p_230095_3_.field_230109_c_.entrySet().stream().sorted(field_230092_d_).forEach((p_230094_4_) -> {
         this.func_230095_a_(p_230095_1_ + 1, p_230094_4_.getKey(), p_230094_4_.getValue(), p_230095_4_, p_230095_5_);
      });
   }

   private void func_230102_a_(Map<String, FilledProfileResult.Section> p_230102_1_, StringBuilder p_230102_2_, int p_230102_3_) {
      p_230102_1_.forEach((p_230099_3_, p_230099_4_) -> {
         p_230102_2_.append("-- Counter: ").append(p_230099_3_).append(" --\n");
         this.func_230095_a_(0, "root", p_230099_4_.field_230109_c_.get("root"), p_230102_3_, p_230102_2_);
         p_230102_2_.append("\n\n");
      });
   }

   private static String getWittyString() {
      String[] astring = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

      try {
         return astring[(int)(Util.nanoTime() % (long)astring.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public int ticksSpend() {
      return this.ticksTotal;
   }

   static class Section {
      private long field_230107_a_;
      private long field_230108_b_;
      private final Map<String, FilledProfileResult.Section> field_230109_c_ = Maps.newHashMap();

      private Section() {
      }

      public void func_230112_a_(Iterator<String> p_230112_1_, long p_230112_2_) {
         this.field_230108_b_ += p_230112_2_;
         if (!p_230112_1_.hasNext()) {
            this.field_230107_a_ += p_230112_2_;
         } else {
            this.field_230109_c_.computeIfAbsent(p_230112_1_.next(), (p_230111_0_) -> {
               return new FilledProfileResult.Section();
            }).func_230112_a_(p_230112_1_, p_230112_2_);
         }

      }
   }
}