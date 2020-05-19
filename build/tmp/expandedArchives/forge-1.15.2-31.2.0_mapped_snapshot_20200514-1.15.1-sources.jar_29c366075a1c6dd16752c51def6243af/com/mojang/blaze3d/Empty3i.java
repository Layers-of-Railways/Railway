package com.mojang.blaze3d;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.IRenderCall;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Empty3i {
   private final List<ConcurrentLinkedQueue<IRenderCall>> field_227581_a_ = ImmutableList.of(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
   private volatile int field_227582_b_;
   private volatile int field_227583_c_;
   private volatile int field_227584_d_;

   public Empty3i() {
      this.field_227582_b_ = this.field_227583_c_ = this.field_227584_d_ + 1;
   }
}