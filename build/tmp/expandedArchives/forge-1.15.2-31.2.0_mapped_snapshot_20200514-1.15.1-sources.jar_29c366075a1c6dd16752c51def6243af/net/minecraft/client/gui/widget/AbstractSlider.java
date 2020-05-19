package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSlider extends Widget {
   protected final GameSettings options;
   protected double value;

   protected AbstractSlider(int xIn, int yIn, int widthIn, int heightIn, double valueIn) {
      this(Minecraft.getInstance().gameSettings, xIn, yIn, widthIn, heightIn, valueIn);
   }

   protected AbstractSlider(GameSettings options, int xIn, int yIn, int widthIn, int heightIn, double valueIn) {
      super(xIn, yIn, widthIn, heightIn, "");
      this.options = options;
      this.value = valueIn;
   }

   protected int getYImage(boolean p_getYImage_1_) {
      return 0;
   }

   protected String getNarrationMessage() {
      return I18n.format("gui.narrate.slider", this.getMessage());
   }

   protected void renderBg(Minecraft p_renderBg_1_, int p_renderBg_2_, int p_renderBg_3_) {
      p_renderBg_1_.getTextureManager().bindTexture(WIDGETS_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int i = (this.isHovered() ? 2 : 1) * 20;
      this.blit(this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + i, 4, 20);
      this.blit(this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
   }

   public void onClick(double p_onClick_1_, double p_onClick_3_) {
      this.setValueFromMouse(p_onClick_1_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      boolean flag = p_keyPressed_1_ == 263;
      if (flag || p_keyPressed_1_ == 262) {
         float f = flag ? -1.0F : 1.0F;
         this.setValue(this.value + (double)(f / (float)(this.width - 8)));
      }

      return false;
   }

   private void setValueFromMouse(double p_setValueFromMouse_1_) {
      this.setValue((p_setValueFromMouse_1_ - (double)(this.x + 4)) / (double)(this.width - 8));
   }

   private void setValue(double p_setValue_1_) {
      double d0 = this.value;
      this.value = MathHelper.clamp(p_setValue_1_, 0.0D, 1.0D);
      if (d0 != this.value) {
         this.applyValue();
      }

      this.updateMessage();
   }

   protected void onDrag(double p_onDrag_1_, double p_onDrag_3_, double p_onDrag_5_, double p_onDrag_7_) {
      this.setValueFromMouse(p_onDrag_1_);
      super.onDrag(p_onDrag_1_, p_onDrag_3_, p_onDrag_5_, p_onDrag_7_);
   }

   public void playDownSound(SoundHandler p_playDownSound_1_) {
   }

   public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
      super.playDownSound(Minecraft.getInstance().getSoundHandler());
   }

   protected abstract void updateMessage();

   protected abstract void applyValue();
}