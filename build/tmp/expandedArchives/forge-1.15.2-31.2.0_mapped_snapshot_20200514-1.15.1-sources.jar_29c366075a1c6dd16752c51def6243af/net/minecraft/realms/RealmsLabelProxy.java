package net.minecraft.realms;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsLabelProxy implements IGuiEventListener {
   private final RealmsLabel label;

   public RealmsLabelProxy(RealmsLabel p_i49865_1_) {
      this.label = p_i49865_1_;
   }

   public RealmsLabel getLabel() {
      return this.label;
   }
}