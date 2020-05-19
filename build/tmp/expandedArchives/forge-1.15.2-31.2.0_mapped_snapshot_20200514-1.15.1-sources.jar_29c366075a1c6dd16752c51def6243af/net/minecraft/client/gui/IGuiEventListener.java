package net.minecraft.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGuiEventListener {
   default void mouseMoved(double xPos, double mouseY) {
   }

   default boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return false;
   }

   default boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return false;
   }

   default boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return false;
   }

   default boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return false;
   }

   default boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return false;
   }

   default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return false;
   }

   default boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return false;
   }

   default boolean changeFocus(boolean p_changeFocus_1_) {
      return false;
   }

   default boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return false;
   }
}