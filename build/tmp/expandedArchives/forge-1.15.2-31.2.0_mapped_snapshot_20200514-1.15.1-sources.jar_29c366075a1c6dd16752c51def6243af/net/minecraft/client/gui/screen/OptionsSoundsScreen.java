package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.SoundSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsSoundsScreen extends SettingsScreen {
   public OptionsSoundsScreen(Screen parentIn, GameSettings settingsIn) {
      super(parentIn, settingsIn, new TranslationTextComponent("options.sounds.title"));
   }

   protected void init() {
      int i = 0;
      this.addButton(new SoundSlider(this.minecraft, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, 310));
      i = i + 2;

      for(SoundCategory soundcategory : SoundCategory.values()) {
         if (soundcategory != SoundCategory.MASTER) {
            this.addButton(new SoundSlider(this.minecraft, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundcategory, 150));
            ++i;
         }
      }

      int j = this.width / 2 - 75;
      int k = this.height / 6 - 12;
      ++i;
      this.addButton(new OptionButton(j, k + 24 * (i >> 1), 150, 20, AbstractOption.SHOW_SUBTITLES, AbstractOption.SHOW_SUBTITLES.getText(this.gameSettings), (p_213105_1_) -> {
         AbstractOption.SHOW_SUBTITLES.nextValue(this.minecraft.gameSettings);
         p_213105_1_.setMessage(AbstractOption.SHOW_SUBTITLES.getText(this.minecraft.gameSettings));
         this.minecraft.gameSettings.saveOptions();
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.format("gui.done"), (p_213104_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 15, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}