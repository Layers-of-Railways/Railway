package net.minecraft.profiler;

import java.time.Duration;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugProfiler implements IProfiler {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final long MAX_TICK_TIME_NS = Duration.ofMillis(300L).toNanos();
   private final IntSupplier tickCounter;
   private final DebugProfiler.DebugResultEntryImpl fixedProfiler = new DebugProfiler.DebugResultEntryImpl();
   private final DebugProfiler.DebugResultEntryImpl tickProfiler = new DebugProfiler.DebugResultEntryImpl();

   public DebugProfiler(IntSupplier tickCounterIn) {
      this.tickCounter = tickCounterIn;
   }

   public DebugProfiler.IDebugResultEntry getFixedProfiler() {
      return this.fixedProfiler;
   }

   public void startTick() {
      this.fixedProfiler.profiler.startTick();
      this.tickProfiler.profiler.startTick();
   }

   public void endTick() {
      this.fixedProfiler.profiler.endTick();
      this.tickProfiler.profiler.endTick();
   }

   /**
    * Start section
    */
   public void startSection(String name) {
      this.fixedProfiler.profiler.startSection(name);
      this.tickProfiler.profiler.startSection(name);
   }

   public void startSection(Supplier<String> nameSupplier) {
      this.fixedProfiler.profiler.startSection(nameSupplier);
      this.tickProfiler.profiler.startSection(nameSupplier);
   }

   /**
    * End section
    */
   public void endSection() {
      this.fixedProfiler.profiler.endSection();
      this.tickProfiler.profiler.endSection();
   }

   public void endStartSection(String name) {
      this.fixedProfiler.profiler.endStartSection(name);
      this.tickProfiler.profiler.endStartSection(name);
   }

   @OnlyIn(Dist.CLIENT)
   public void endStartSection(Supplier<String> nameSupplier) {
      this.fixedProfiler.profiler.endStartSection(nameSupplier);
      this.tickProfiler.profiler.endStartSection(nameSupplier);
   }

   public void func_230035_c_(String p_230035_1_) {
      this.fixedProfiler.profiler.func_230035_c_(p_230035_1_);
      this.tickProfiler.profiler.func_230035_c_(p_230035_1_);
   }

   public void func_230036_c_(Supplier<String> p_230036_1_) {
      this.fixedProfiler.profiler.func_230036_c_(p_230036_1_);
      this.tickProfiler.profiler.func_230036_c_(p_230036_1_);
   }

   class DebugResultEntryImpl implements DebugProfiler.IDebugResultEntry {
      protected IResultableProfiler profiler = EmptyProfiler.INSTANCE;

      private DebugResultEntryImpl() {
      }

      public boolean isEnabled() {
         return this.profiler != EmptyProfiler.INSTANCE;
      }

      public IProfileResult disable() {
         IProfileResult iprofileresult = this.profiler.getResults();
         this.profiler = EmptyProfiler.INSTANCE;
         return iprofileresult;
      }

      @OnlyIn(Dist.CLIENT)
      public IProfileResult getResults() {
         return this.profiler.getResults();
      }

      public void enable() {
         if (this.profiler == EmptyProfiler.INSTANCE) {
            this.profiler = new Profiler(Util.nanoTime(), DebugProfiler.this.tickCounter, true);
         }

      }
   }

   public interface IDebugResultEntry {
      boolean isEnabled();

      IProfileResult disable();

      @OnlyIn(Dist.CLIENT)
      IProfileResult getResults();

      void enable();
   }
}