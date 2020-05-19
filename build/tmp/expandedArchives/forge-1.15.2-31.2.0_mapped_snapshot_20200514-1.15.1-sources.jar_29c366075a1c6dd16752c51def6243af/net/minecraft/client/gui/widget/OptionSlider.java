package net.minecraft.client.gui.widget;

import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionSlider extends AbstractSlider {
   private final SliderPercentageOption option;

   public OptionSlider(GameSettings settings, int xIn, int yIn, int widthIn, int heightIn, SliderPercentageOption optionIn) {
      super(settings, xIn, yIn, widthIn, heightIn, (double)((float)optionIn.normalizeValue(optionIn.get(settings))));
      this.option = optionIn;
      this.updateMessage();
   }

   protected void applyValue() {
      this.option.set(this.options, this.option.denormalizeValue(this.value));
      this.options.saveOptions();
   }

   protected void updateMessage() {
      this.setMessage(this.option.getText(this.options));
   }
}