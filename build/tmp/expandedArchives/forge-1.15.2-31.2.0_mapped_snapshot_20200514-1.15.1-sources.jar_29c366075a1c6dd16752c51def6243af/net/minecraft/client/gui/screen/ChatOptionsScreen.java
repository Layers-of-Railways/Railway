package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatOptionsScreen extends SettingsScreen {
   private static final AbstractOption[] CHAT_OPTIONS = new AbstractOption[]{AbstractOption.CHAT_VISIBILITY, AbstractOption.CHAT_COLOR, AbstractOption.CHAT_LINKS, AbstractOption.CHAT_LINKS_PROMPT, AbstractOption.CHAT_OPACITY, AbstractOption.ACCESSIBILITY_TEXT_BACKGROUND_OPACITY, AbstractOption.CHAT_SCALE, AbstractOption.CHAT_WIDTH, AbstractOption.CHAT_HEIGHT_FOCUSED, AbstractOption.CHAT_HEIGHT_UNFOCUSED, AbstractOption.REDUCED_DEBUG_INFO, AbstractOption.AUTO_SUGGEST_COMMANDS, AbstractOption.NARRATOR};
   private Widget narratorButton;

   public ChatOptionsScreen(Screen parentScreenIn, GameSettings gameSettingsIn) {
      super(parentScreenIn, gameSettingsIn, new TranslationTextComponent("options.chat.title"));
   }

   protected void init() {
      int i = 0;

      for(AbstractOption abstractoption : CHAT_OPTIONS) {
         int j = this.width / 2 - 155 + i % 2 * 160;
         int k = this.height / 6 + 24 * (i >> 1);
         Widget widget = this.addButton(abstractoption.createWidget(this.minecraft.gameSettings, j, k, 150));
         if (abstractoption == AbstractOption.NARRATOR) {
            this.narratorButton = widget;
            widget.active = NarratorChatListener.INSTANCE.isActive();
         }

         ++i;
      }

      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (i + 1) / 2, 200, 20, I18n.format("gui.done"), (p_212990_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void updateNarratorButton() {
      this.narratorButton.setMessage(AbstractOption.NARRATOR.getText(this.gameSettings));
   }
}