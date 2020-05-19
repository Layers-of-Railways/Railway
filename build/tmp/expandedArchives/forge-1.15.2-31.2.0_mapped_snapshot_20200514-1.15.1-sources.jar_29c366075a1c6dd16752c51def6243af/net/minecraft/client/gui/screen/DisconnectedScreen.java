package net.minecraft.client.gui.screen;

import java.util.List;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedScreen extends Screen {
   private final ITextComponent message;
   private List<String> multilineMessage;
   private final Screen nextScreen;
   private int textHeight;

   public DisconnectedScreen(Screen screen, String reasonLocalizationKey, ITextComponent chatComp) {
      super(new TranslationTextComponent(reasonLocalizationKey));
      this.nextScreen = screen;
      this.message = chatComp;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.multilineMessage = this.font.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
      this.textHeight = this.multilineMessage.size() * 9;
      this.addButton(new Button(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + 9, this.height - 30), 200, 20, I18n.format("gui.toMenu"), (p_213033_1_) -> {
         this.minecraft.displayGuiScreen(this.nextScreen);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
      int i = this.height / 2 - this.textHeight / 2;
      if (this.multilineMessage != null) {
         for(String s : this.multilineMessage) {
            this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
            i += 9;
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}