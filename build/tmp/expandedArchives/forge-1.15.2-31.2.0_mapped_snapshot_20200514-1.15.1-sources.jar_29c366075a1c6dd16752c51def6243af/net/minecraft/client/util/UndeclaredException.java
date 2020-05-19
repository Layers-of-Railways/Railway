package net.minecraft.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UndeclaredException extends RuntimeException {
   public UndeclaredException(String p_i225941_1_) {
      super(p_i225941_1_);
   }
}