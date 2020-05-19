package com.mojang.realmsclient.gui.screens;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UploadResult {
   public final int field_225179_a;
   public final String field_225180_b;

   public UploadResult(int p_i51746_1_, String p_i51746_2_) {
      this.field_225179_a = p_i51746_1_;
      this.field_225180_b = p_i51746_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private int field_225177_a = -1;
      private String field_225178_b;

      public UploadResult.Builder func_225175_a(int p_225175_1_) {
         this.field_225177_a = p_225175_1_;
         return this;
      }

      public UploadResult.Builder func_225176_a(String p_225176_1_) {
         this.field_225178_b = p_225176_1_;
         return this;
      }

      public UploadResult func_225174_a() {
         return new UploadResult(this.field_225177_a, this.field_225178_b);
      }
   }
}