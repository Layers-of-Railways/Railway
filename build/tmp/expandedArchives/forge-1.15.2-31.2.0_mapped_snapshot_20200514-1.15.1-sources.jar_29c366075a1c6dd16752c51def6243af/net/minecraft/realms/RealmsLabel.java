package net.minecraft.realms;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsLabel extends RealmsGuiEventListener {
   private final RealmsLabelProxy proxy = new RealmsLabelProxy(this);
   private final String text;
   private final int x;
   private final int y;
   private final int color;

   public RealmsLabel(String p_i50591_1_, int p_i50591_2_, int p_i50591_3_, int p_i50591_4_) {
      this.text = p_i50591_1_;
      this.x = p_i50591_2_;
      this.y = p_i50591_3_;
      this.color = p_i50591_4_;
   }

   public void render(RealmsScreen p_render_1_) {
      p_render_1_.drawCenteredString(this.text, this.x, this.y, this.color);
   }

   public IGuiEventListener getProxy() {
      return this.proxy;
   }

   public String getText() {
      return this.text;
   }
}