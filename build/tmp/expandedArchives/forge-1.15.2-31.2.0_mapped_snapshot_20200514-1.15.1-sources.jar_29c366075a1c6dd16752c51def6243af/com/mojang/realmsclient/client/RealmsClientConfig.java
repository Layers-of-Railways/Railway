package com.mojang.realmsclient.client;

import java.net.Proxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsClientConfig {
   private static Proxy field_224897_a;

   public static Proxy func_224895_a() {
      return field_224897_a;
   }

   public static void func_224896_a(Proxy p_224896_0_) {
      if (field_224897_a == null) {
         field_224897_a = p_224896_0_;
      }

   }
}