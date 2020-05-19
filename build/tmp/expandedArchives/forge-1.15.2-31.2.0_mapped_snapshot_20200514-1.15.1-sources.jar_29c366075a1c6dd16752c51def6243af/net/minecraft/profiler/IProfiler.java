package net.minecraft.profiler;

import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProfiler {
   void startTick();

   void endTick();

   /**
    * Start section
    */
   void startSection(String name);

   void startSection(Supplier<String> nameSupplier);

   /**
    * End section
    */
   void endSection();

   void endStartSection(String name);

   @OnlyIn(Dist.CLIENT)
   void endStartSection(Supplier<String> nameSupplier);

   void func_230035_c_(String p_230035_1_);

   void func_230036_c_(Supplier<String> p_230036_1_);
}