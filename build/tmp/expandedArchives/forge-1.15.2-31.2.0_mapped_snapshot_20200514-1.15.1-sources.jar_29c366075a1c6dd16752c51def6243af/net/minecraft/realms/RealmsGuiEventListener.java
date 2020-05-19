package net.minecraft.realms;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsGuiEventListener {
   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return false;
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return false;
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return false;
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return false;
   }

   public boolean keyReleased(int p_keyReleased_1_, int p_keyReleased_2_, int p_keyReleased_3_) {
      return false;
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return false;
   }

   public abstract IGuiEventListener getProxy();
}