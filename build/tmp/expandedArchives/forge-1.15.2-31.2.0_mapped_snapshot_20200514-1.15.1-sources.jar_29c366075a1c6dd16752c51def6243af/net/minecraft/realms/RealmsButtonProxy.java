package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsButtonProxy extends Button implements RealmsAbstractButtonProxy<RealmsButton> {
   private final RealmsButton button;

   public RealmsButtonProxy(RealmsButton p_i50623_1_, int p_i50623_2_, int p_i50623_3_, String p_i50623_4_, int p_i50623_5_, int p_i50623_6_, Button.IPressable p_i50623_7_) {
      super(p_i50623_2_, p_i50623_3_, p_i50623_5_, p_i50623_6_, p_i50623_4_, p_i50623_7_);
      this.button = p_i50623_1_;
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

   public int y() {
      return this.y;
   }

   public void onClick(double p_onClick_1_, double p_onClick_3_) {
      this.button.onPress();
   }

   public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
      this.button.onRelease(p_onRelease_1_, p_onRelease_3_);
   }

   public void renderBg(Minecraft p_renderBg_1_, int p_renderBg_2_, int p_renderBg_3_) {
      this.button.renderBg(p_renderBg_2_, p_renderBg_3_);
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      this.button.renderButton(p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
   }

   public void superRenderButton(int p_superRenderButton_1_, int p_superRenderButton_2_, float p_superRenderButton_3_) {
      super.renderButton(p_superRenderButton_1_, p_superRenderButton_2_, p_superRenderButton_3_);
   }

   public RealmsButton getButton() {
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

   public boolean isHovered() {
      return super.isHovered();
   }
}