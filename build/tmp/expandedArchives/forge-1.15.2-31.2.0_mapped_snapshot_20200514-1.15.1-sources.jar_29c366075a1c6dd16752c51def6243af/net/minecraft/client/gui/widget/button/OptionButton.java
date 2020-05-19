package net.minecraft.client.gui.widget.button;

import net.minecraft.client.settings.AbstractOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionButton extends Button {
   private final AbstractOption enumOptions;

   public OptionButton(int xIn, int yIn, int widthIn, int heightIn, AbstractOption optionIn, String textIn, Button.IPressable pressableIn) {
      super(xIn, yIn, widthIn, heightIn, textIn, pressableIn);
      this.enumOptions = optionIn;
   }
}