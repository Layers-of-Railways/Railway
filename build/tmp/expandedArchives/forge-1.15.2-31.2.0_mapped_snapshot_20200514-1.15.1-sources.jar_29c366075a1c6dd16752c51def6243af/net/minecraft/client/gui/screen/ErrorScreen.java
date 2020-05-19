package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ErrorScreen extends Screen {
   private final String message;

   public ErrorScreen(ITextComponent p_i51115_1_, String p_i51115_2_) {
      super(p_i51115_1_);
      this.message = p_i51115_2_;
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, 140, 200, 20, I18n.format("gui.cancel"), (p_213034_1_) -> {
         this.minecraft.displayGuiScreen((Screen)null);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 90, 16777215);
      this.drawCenteredString(this.font, this.message, this.width / 2, 110, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}