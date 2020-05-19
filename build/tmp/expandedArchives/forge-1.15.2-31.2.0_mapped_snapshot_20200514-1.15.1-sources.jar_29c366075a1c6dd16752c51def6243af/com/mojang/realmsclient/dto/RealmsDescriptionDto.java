package com.mojang.realmsclient.dto;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsDescriptionDto extends ValueObject {
   public String name;
   public String description;

   public RealmsDescriptionDto(String p_i51655_1_, String p_i51655_2_) {
      this.name = p_i51655_1_;
      this.description = p_i51655_2_;
   }
}