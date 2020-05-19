package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmOpenLinkScreen extends ConfirmScreen {
   /** Text to warn players from opening unsafe links. */
   private final String openLinkWarning;
   /** Label for the Copy to Clipboard button. */
   private final String copyLinkButtonText;
   private final String linkText;
   private final boolean showSecurityWarning;

   public ConfirmOpenLinkScreen(BooleanConsumer p_i51121_1_, String p_i51121_2_, boolean p_i51121_3_) {
      super(p_i51121_1_, new TranslationTextComponent(p_i51121_3_ ? "chat.link.confirmTrusted" : "chat.link.confirm"), new StringTextComponent(p_i51121_2_));
      this.confirmButtonText = I18n.format(p_i51121_3_ ? "chat.link.open" : "gui.yes");
      this.cancelButtonText = I18n.format(p_i51121_3_ ? "gui.cancel" : "gui.no");
      this.copyLinkButtonText = I18n.format("chat.copy");
      this.openLinkWarning = I18n.format("chat.link.warning");
      this.showSecurityWarning = !p_i51121_3_;
      this.linkText = p_i51121_2_;
   }

   protected void init() {
      super.init();
      this.buttons.clear();
      this.children.clear();
      this.addButton(new Button(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.confirmButtonText, (p_213006_1_) -> {
         this.callbackFunction.accept(true);
      }));
      this.addButton(new Button(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyLinkButtonText, (p_213005_1_) -> {
         this.copyLinkToClipboard();
         this.callbackFunction.accept(false);
      }));
      this.addButton(new Button(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.cancelButtonText, (p_213004_1_) -> {
         this.callbackFunction.accept(false);
      }));
   }

   /**
    * Copies the link to the system clipboard.
    */
   public void copyLinkToClipboard() {
      this.minecraft.keyboardListener.setClipboardString(this.linkText);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.showSecurityWarning) {
         this.drawCenteredString(this.font, this.openLinkWarning, this.width / 2, 110, 16764108);
      }

   }
}