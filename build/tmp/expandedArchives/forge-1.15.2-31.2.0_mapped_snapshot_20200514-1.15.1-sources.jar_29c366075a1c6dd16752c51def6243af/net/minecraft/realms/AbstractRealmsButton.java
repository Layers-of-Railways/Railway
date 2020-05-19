package net.minecraft.realms;

import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractRealmsButton<P extends Widget & RealmsAbstractButtonProxy<?>> {
   public abstract P getProxy();

   public boolean active() {
      return ((RealmsAbstractButtonProxy)this.getProxy()).active();
   }

   public void active(boolean p_active_1_) {
      ((RealmsAbstractButtonProxy)this.getProxy()).active(p_active_1_);
   }

   public boolean isVisible() {
      return ((RealmsAbstractButtonProxy)this.getProxy()).isVisible();
   }

   public void setVisible(boolean p_setVisible_1_) {
      ((RealmsAbstractButtonProxy)this.getProxy()).setVisible(p_setVisible_1_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.getProxy().render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void blit(int p_blit_1_, int p_blit_2_, int p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_) {
      this.getProxy().blit(p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_);
   }

   public void tick() {
   }
}