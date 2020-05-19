package net.minecraft.profiler;

import java.io.File;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProfileResult {
   @OnlyIn(Dist.CLIENT)
   List<DataPoint> getDataPoints(String sectionPath);

   boolean writeToFile(File p_219919_1_);

   long timeStop();

   int ticksStop();

   long timeStart();

   int ticksStart();

   default long nanoTime() {
      return this.timeStart() - this.timeStop();
   }

   default int ticksSpend() {
      return this.ticksStart() - this.ticksStop();
   }

   static String decodePath(String p_225434_0_) {
      return p_225434_0_.replace('\u001e', '.');
   }
}