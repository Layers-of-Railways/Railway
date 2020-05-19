package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomizeSkinScreen extends SettingsScreen {
   public CustomizeSkinScreen(Screen parentScreenIn, GameSettings gameSettingsIn) {
      super(parentScreenIn, gameSettingsIn, new TranslationTextComponent("options.skinCustomisation.title"));
   }

   protected void init() {
      int i = 0;

      for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
         this.addButton(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, this.getMessage(playermodelpart), (p_213080_2_) -> {
            this.gameSettings.switchModelPartEnabled(playermodelpart);
            p_213080_2_.setMessage(this.getMessage(playermodelpart));
         }));
         ++i;
      }

      this.addButton(new OptionButton(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, AbstractOption.MAIN_HAND, AbstractOption.MAIN_HAND.getText(this.gameSettings), (p_213081_1_) -> {
         AbstractOption.MAIN_HAND.setValueIndex(this.gameSettings, 1);
         this.gameSettings.saveOptions();
         p_213081_1_.setMessage(AbstractOption.MAIN_HAND.getText(this.gameSettings));
         this.gameSettings.sendSettingsToServer();
      }));
      ++i;
      if (i % 2 == 1) {
         ++i;
      }

      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, I18n.format("gui.done"), (p_213079_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private String getMessage(PlayerModelPart playerModelParts) {
      String s;
      if (this.gameSettings.getModelParts().contains(playerModelParts)) {
         s = I18n.format("options.on");
      } else {
         s = I18n.format("options.off");
      }

      return playerModelParts.getName().getFormattedText() + ": " + s;
   }
}