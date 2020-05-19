package net.minecraft.client.gui.widget.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractButton extends Widget {
   public AbstractButton(int xIn, int yIn, int widthIn, int heightIn, String msg) {
      super(xIn, yIn, widthIn, heightIn, msg);
   }

   public abstract void onPress();

   public void onClick(double p_onClick_1_, double p_onClick_3_) {
      this.onPress();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.active && this.visible) {
         if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 32 && p_keyPressed_1_ != 335) {
            return false;
         } else {
            this.playDownSound(Minecraft.getInstance().getSoundHandler());
            this.onPress();
            return true;
         }
      } else {
         return false;
      }
   }
}