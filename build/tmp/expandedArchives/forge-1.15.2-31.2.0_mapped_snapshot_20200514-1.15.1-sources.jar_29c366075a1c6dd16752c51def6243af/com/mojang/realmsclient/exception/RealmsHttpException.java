package com.mojang.realmsclient.exception;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsHttpException extends RuntimeException {
   public RealmsHttpException(String p_i51786_1_, Exception p_i51786_2_) {
      super(p_i51786_1_, p_i51786_2_);
   }
}