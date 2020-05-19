package net.minecraft.client.renderer.entity.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelUtils {
   public static float func_228283_a_(float p_228283_0_, float p_228283_1_, float p_228283_2_) {
      float f;
      for(f = p_228283_1_ - p_228283_0_; f < -(float)Math.PI; f += ((float)Math.PI * 2F)) {
         ;
      }

      while(f >= (float)Math.PI) {
         f -= ((float)Math.PI * 2F);
      }

      return p_228283_0_ + p_228283_2_ * f;
   }
}