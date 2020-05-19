package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsSliderButtonProxy extends AbstractSlider implements RealmsAbstractButtonProxy<RealmsSliderButton> {
   private final RealmsSliderButton button;

   public RealmsSliderButtonProxy(RealmsSliderButton p_i50875_1_, int p_i50875_2_, int p_i50875_3_, int p_i50875_4_, int p_i50875_5_, double p_i50875_6_) {
      super(p_i50875_2_, p_i50875_3_, p_i50875_4_, p_i50875_5_, p_i50875_6_);
      this.button = p_i50875_1_;
   }

   public boolean active() {
      return this.active;
   }

   public void active(boolean p_active_1_) {
      this.active = p_active_1_;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean p_setVisible_1_) {
      this.visible = p_setVisible_1_;
   }

   public void setMessage(String p_setMessage_1_) {
      super.setMessage(p_setMessage_1_);
   }

   public int getWidth() {
      return super.getWidth();
   }

   public int func_212934_y() {
      return this.y;
   }

   public void onClick(double p_onClick_1_, double p_onClick_3_) {
      this.button.onClick(p_onClick_1_, p_onClick_3_);
   }

   public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
      this.button.onRelease(p_onRelease_1_, p_onRelease_3_);
   }

   public void updateMessage() {
      this.button.updateMessage();
   }

   public void applyValue() {
      this.button.applyValue();
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double p_setValue_1_) {
      this.value = p_setValue_1_;
   }

   public void renderBg(Minecraft p_renderBg_1_, int p_renderBg_2_, int p_renderBg_3_) {
      super.renderBg(p_renderBg_1_, p_renderBg_2_, p_renderBg_3_);
   }

   public RealmsSliderButton getButton() {
      return this.button;
   }

   public int getYImage(boolean p_getYImage_1_) {
      return this.button.getYImage(p_getYImage_1_);
   }

   public int getSuperYImage(boolean p_getSuperYImage_1_) {
      return super.getYImage(p_getSuperYImage_1_);
   }

   public int getHeight() {
      return this.height;
   }
}