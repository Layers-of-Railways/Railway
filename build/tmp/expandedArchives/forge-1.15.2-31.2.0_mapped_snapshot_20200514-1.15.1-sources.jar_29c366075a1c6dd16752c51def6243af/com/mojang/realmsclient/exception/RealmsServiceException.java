package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsServiceException extends Exception {
   public final int field_224981_a;
   public final String field_224982_b;
   public final int field_224983_c;
   public final String field_224984_d;

   public RealmsServiceException(int p_i51784_1_, String p_i51784_2_, RealmsError p_i51784_3_) {
      super(p_i51784_2_);
      this.field_224981_a = p_i51784_1_;
      this.field_224982_b = p_i51784_2_;
      this.field_224983_c = p_i51784_3_.func_224974_b();
      this.field_224984_d = p_i51784_3_.func_224973_a();
   }

   public RealmsServiceException(int p_i51785_1_, String p_i51785_2_, int p_i51785_3_, String p_i51785_4_) {
      super(p_i51785_2_);
      this.field_224981_a = p_i51785_1_;
      this.field_224982_b = p_i51785_2_;
      this.field_224983_c = p_i51785_3_;
      this.field_224984_d = p_i51785_4_;
   }

   public String toString() {
      if (this.field_224983_c == -1) {
         return "Realms (" + this.field_224981_a + ") " + this.field_224982_b;
      } else {
         String s = "mco.errorMessage." + this.field_224983_c;
         String s1 = RealmsScreen.getLocalizedString(s);
         return (s1.equals(s) ? this.field_224984_d : s1) + " - " + this.field_224983_c;
      }
   }
}