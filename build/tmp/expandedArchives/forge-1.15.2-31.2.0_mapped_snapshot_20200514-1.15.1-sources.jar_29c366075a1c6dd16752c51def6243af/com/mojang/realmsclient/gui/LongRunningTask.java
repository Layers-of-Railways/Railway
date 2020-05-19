package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class LongRunningTask implements Runnable {
   protected RealmsLongRunningMcoTaskScreen field_224993_a;

   public void func_224987_a(RealmsLongRunningMcoTaskScreen p_224987_1_) {
      this.field_224993_a = p_224987_1_;
   }

   public void func_224986_a(String p_224986_1_) {
      this.field_224993_a.func_224231_a(p_224986_1_);
   }

   public void func_224989_b(String p_224989_1_) {
      this.field_224993_a.func_224234_b(p_224989_1_);
   }

   public boolean func_224988_a() {
      return this.field_224993_a.func_224235_b();
   }

   public void func_224990_b() {
   }

   public void func_224991_c() {
   }

   public void func_224992_d() {
   }
}