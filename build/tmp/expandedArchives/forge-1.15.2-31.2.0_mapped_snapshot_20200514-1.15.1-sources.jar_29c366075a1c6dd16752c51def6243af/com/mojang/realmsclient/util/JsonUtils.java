package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JsonUtils {
   public static String func_225171_a(String p_225171_0_, JsonObject p_225171_1_, String p_225171_2_) {
      JsonElement jsonelement = p_225171_1_.get(p_225171_0_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_225171_2_ : jsonelement.getAsString();
      } else {
         return p_225171_2_;
      }
   }

   public static int func_225172_a(String p_225172_0_, JsonObject p_225172_1_, int p_225172_2_) {
      JsonElement jsonelement = p_225172_1_.get(p_225172_0_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_225172_2_ : jsonelement.getAsInt();
      } else {
         return p_225172_2_;
      }
   }

   public static long func_225169_a(String p_225169_0_, JsonObject p_225169_1_, long p_225169_2_) {
      JsonElement jsonelement = p_225169_1_.get(p_225169_0_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_225169_2_ : jsonelement.getAsLong();
      } else {
         return p_225169_2_;
      }
   }

   public static boolean func_225170_a(String p_225170_0_, JsonObject p_225170_1_, boolean p_225170_2_) {
      JsonElement jsonelement = p_225170_1_.get(p_225170_0_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_225170_2_ : jsonelement.getAsBoolean();
      } else {
         return p_225170_2_;
      }
   }

   public static Date func_225173_a(String p_225173_0_, JsonObject p_225173_1_) {
      JsonElement jsonelement = p_225173_1_.get(p_225173_0_);
      return jsonelement != null ? new Date(Long.parseLong(jsonelement.getAsString())) : new Date();
   }
}