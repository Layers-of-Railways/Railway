package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler implements IResultableProfiler {
   private static final long WARN_TIME_THRESHOLD = Duration.ofMillis(100L).toNanos();
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<String> sectionList = Lists.newArrayList();
   private final LongList timeStack = new LongArrayList();
   private final Map<String, Profiler.Section> field_230078_e_ = Maps.newHashMap();
   private final IntSupplier currentTicks;
   private final long startTime;
   private final int startTicks;
   private String currentSectionName = "";
   private boolean tickStarted;
   @Nullable
   private Profiler.Section field_230079_k_;
   private final boolean field_226230_l_;

   public Profiler(long p_i225707_1_, IntSupplier p_i225707_3_, boolean p_i225707_4_) {
      this.startTime = p_i225707_1_;
      this.startTicks = p_i225707_3_.getAsInt();
      this.currentTicks = p_i225707_3_;
      this.field_226230_l_ = p_i225707_4_;
   }

   public void startTick() {
      if (this.tickStarted) {
         LOGGER.error("Profiler tick already started - missing endTick()?");
      } else {
         this.tickStarted = true;
         this.currentSectionName = "";
         this.sectionList.clear();
         this.startSection("root");
      }
   }

   public void endTick() {
      if (!this.tickStarted) {
         LOGGER.error("Profiler tick already ended - missing startTick()?");
      } else {
         this.endSection();
         this.tickStarted = false;
         if (!this.currentSectionName.isEmpty()) {
            LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", () -> {
               return IProfileResult.decodePath(this.currentSectionName);
            });
         }

      }
   }

   /**
    * Start section
    */
   public void startSection(String name) {
      if (!this.tickStarted) {
         LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", (Object)name);
      } else {
         if (!this.currentSectionName.isEmpty()) {
            this.currentSectionName = this.currentSectionName + '\u001e';
         }

         this.currentSectionName = this.currentSectionName + name;
         this.sectionList.add(this.currentSectionName);
         this.timeStack.add(Util.nanoTime());
         this.field_230079_k_ = null;
      }
   }

   public void startSection(Supplier<String> nameSupplier) {
      this.startSection(nameSupplier.get());
   }

   /**
    * End section
    */
   public void endSection() {
      if (!this.tickStarted) {
         LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
      } else if (this.timeStack.isEmpty()) {
         LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
      } else {
         long i = Util.nanoTime();
         long j = this.timeStack.removeLong(this.timeStack.size() - 1);
         this.sectionList.remove(this.sectionList.size() - 1);
         long k = i - j;
         Profiler.Section profiler$section = this.func_230081_e_();
         profiler$section.field_230082_a_ = profiler$section.field_230082_a_ + k;
         profiler$section.field_230083_b_ = profiler$section.field_230083_b_ + 1L;
         if (this.field_226230_l_ && k > WARN_TIME_THRESHOLD) {
            LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", () -> {
               return IProfileResult.decodePath(this.currentSectionName);
            }, () -> {
               return (double)k / 1000000.0D;
            });
         }

         this.currentSectionName = this.sectionList.isEmpty() ? "" : this.sectionList.get(this.sectionList.size() - 1);
         this.field_230079_k_ = null;
      }
   }

   public void endStartSection(String name) {
      this.endSection();
      this.startSection(name);
   }

   @OnlyIn(Dist.CLIENT)
   public void endStartSection(Supplier<String> nameSupplier) {
      this.endSection();
      this.startSection(nameSupplier);
   }

   private Profiler.Section func_230081_e_() {
      if (this.field_230079_k_ == null) {
         this.field_230079_k_ = this.field_230078_e_.computeIfAbsent(this.currentSectionName, (p_230080_0_) -> {
            return new Profiler.Section();
         });
      }

      return this.field_230079_k_;
   }

   public void func_230035_c_(String p_230035_1_) {
      this.func_230081_e_().field_230084_c_.addTo(p_230035_1_, 1L);
   }

   public void func_230036_c_(Supplier<String> p_230036_1_) {
      this.func_230081_e_().field_230084_c_.addTo(p_230036_1_.get(), 1L);
   }

   public IProfileResult getResults() {
      return new FilledProfileResult(this.field_230078_e_, this.startTime, this.startTicks, Util.nanoTime(), this.currentTicks.getAsInt());
   }

   static class Section implements IProfilerSection {
      private long field_230082_a_;
      private long field_230083_b_;
      private Object2LongOpenHashMap<String> field_230084_c_ = new Object2LongOpenHashMap<>();

      private Section() {
      }

      public long func_230037_a_() {
         return this.field_230082_a_;
      }

      public long func_230038_b_() {
         return this.field_230083_b_;
      }

      public Object2LongMap<String> func_230039_c_() {
         return Object2LongMaps.unmodifiable(this.field_230084_c_);
      }
   }
}