package net.minecraft.realms;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsSliderButton extends AbstractRealmsButton<RealmsSliderButtonProxy> {
   protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private final int id;
   private final RealmsSliderButtonProxy proxy;
   private final double minValue;
   private final double maxValue;

   public RealmsSliderButton(int p_i50553_1_, int p_i50553_2_, int p_i50553_3_, int p_i50553_4_, int p_i50553_5_, double p_i50553_6_, double p_i50553_8_) {
      this.id = p_i50553_1_;
      this.minValue = p_i50553_6_;
      this.maxValue = p_i50553_8_;
      this.proxy = new RealmsSliderButtonProxy(this, p_i50553_2_, p_i50553_3_, p_i50553_4_, 20, this.toPct((double)p_i50553_5_));
      this.getProxy().setMessage(this.getMessage());
   }

   public String getMessage() {
      return "";
   }

   public double toPct(double p_toPct_1_) {
      return MathHelper.clamp((this.clamp(p_toPct_1_) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
   }

   public double toValue(double p_toValue_1_) {
      return this.clamp(MathHelper.lerp(MathHelper.clamp(p_toValue_1_, 0.0D, 1.0D), this.minValue, this.maxValue));
   }

   public double clamp(double p_clamp_1_) {
      return MathHelper.clamp(p_clamp_1_, this.minValue, this.maxValue);
   }

   public int getYImage(boolean p_getYImage_1_) {
      return 0;
   }

   public void onClick(double p_onClick_1_, double p_onClick_3_) {
   }

   public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
   }

   public RealmsSliderButtonProxy getProxy() {
      return this.proxy;
   }

   public double getValue() {
      return this.proxy.getValue();
   }

   public void setValue(double p_setValue_1_) {
      this.proxy.setValue(p_setValue_1_);
   }

   public int id() {
      return this.id;
   }

   public void setMessage(String p_setMessage_1_) {
      this.proxy.setMessage(p_setMessage_1_);
   }

   public int getWidth() {
      return this.proxy.getWidth();
   }

   public int getHeight() {
      return this.proxy.getHeight();
   }

   public int func_214459_y() {
      return this.proxy.func_212934_y();
   }

   public abstract void applyValue();

   public void updateMessage() {
      this.proxy.setMessage(this.getMessage());
   }
}