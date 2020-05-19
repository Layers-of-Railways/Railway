package net.minecraft.client.util;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MouseSmoother {
   private double targetValue;
   private double remainingValue;
   private double lastAmount;

   public double smooth(double p_199102_1_, double p_199102_3_) {
      this.targetValue += p_199102_1_;
      double d0 = this.targetValue - this.remainingValue;
      double d1 = MathHelper.lerp(0.5D, this.lastAmount, d0);
      double d2 = Math.signum(d0);
      if (d2 * d0 > d2 * this.lastAmount) {
         d0 = d1;
      }

      this.lastAmount = d1;
      this.remainingValue += d0 * p_199102_3_;
      return d0 * p_199102_3_;
   }

   public void reset() {
      this.targetValue = 0.0D;
      this.remainingValue = 0.0D;
      this.lastAmount = 0.0D;
   }
}