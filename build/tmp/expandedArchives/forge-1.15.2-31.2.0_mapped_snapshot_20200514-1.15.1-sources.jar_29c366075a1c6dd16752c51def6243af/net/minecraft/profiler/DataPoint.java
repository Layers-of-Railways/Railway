package net.minecraft.profiler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class DataPoint implements Comparable<DataPoint> {
   public final double relTime;
   public final double rootRelTime;
   public final long field_223511_c;
   public final String name;

   public DataPoint(String p_i51527_1_, double p_i51527_2_, double p_i51527_4_, long p_i51527_6_) {
      this.name = p_i51527_1_;
      this.relTime = p_i51527_2_;
      this.rootRelTime = p_i51527_4_;
      this.field_223511_c = p_i51527_6_;
   }

   public int compareTo(DataPoint p_compareTo_1_) {
      if (p_compareTo_1_.relTime < this.relTime) {
         return -1;
      } else {
         return p_compareTo_1_.relTime > this.relTime ? 1 : p_compareTo_1_.name.compareTo(this.name);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public int getTextColor() {
      return (this.name.hashCode() & 11184810) + 4473924;
   }
}