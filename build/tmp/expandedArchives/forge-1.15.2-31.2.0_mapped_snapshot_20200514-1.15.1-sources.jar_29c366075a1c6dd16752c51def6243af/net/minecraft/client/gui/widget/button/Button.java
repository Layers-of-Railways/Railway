package net.minecraft.client.gui.widget.button;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Button extends AbstractButton {
   protected final Button.IPressable onPress;

   public Button(int widthIn, int heightIn, int width, int height, String text, Button.IPressable onPress) {
      super(widthIn, heightIn, width, height, text);
      this.onPress = onPress;
   }

   public void onPress() {
      this.onPress.onPress(this);
   }

   @OnlyIn(Dist.CLIENT)
   public interface IPressable {
      void onPress(Button p_onPress_1_);
   }
}