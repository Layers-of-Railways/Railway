package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IStatFormatter {
   DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("########0.00"), (p_223254_0_) -> {
      p_223254_0_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   IStatFormatter DEFAULT = NumberFormat.getIntegerInstance(Locale.US)::format;
   IStatFormatter DIVIDE_BY_TEN = (p_223256_0_) -> {
      return DECIMAL_FORMAT.format((double)p_223256_0_ * 0.1D);
   };
   IStatFormatter DISTANCE = (p_223255_0_) -> {
      double d0 = (double)p_223255_0_ / 100.0D;
      double d1 = d0 / 1000.0D;
      if (d1 > 0.5D) {
         return DECIMAL_FORMAT.format(d1) + " km";
      } else {
         return d0 > 0.5D ? DECIMAL_FORMAT.format(d0) + " m" : p_223255_0_ + " cm";
      }
   };
   IStatFormatter TIME = (p_223253_0_) -> {
      double d0 = (double)p_223253_0_ / 20.0D;
      double d1 = d0 / 60.0D;
      double d2 = d1 / 60.0D;
      double d3 = d2 / 24.0D;
      double d4 = d3 / 365.0D;
      if (d4 > 0.5D) {
         return DECIMAL_FORMAT.format(d4) + " y";
      } else if (d3 > 0.5D) {
         return DECIMAL_FORMAT.format(d3) + " d";
      } else if (d2 > 0.5D) {
         return DECIMAL_FORMAT.format(d2) + " h";
      } else {
         return d1 > 0.5D ? DECIMAL_FORMAT.format(d1) + " m" : d0 + " s";
      }
   };

   @OnlyIn(Dist.CLIENT)
   String format(int p_format_1_);
}