package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MemoryErrorScreen extends Screen {
   public MemoryErrorScreen() {
      super(new StringTextComponent("Out of memory!"));
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.toTitle"), (p_213048_1_) -> {
         this.minecraft.displayGuiScreen(new MainMenuScreen());
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20, I18n.format("menu.quit"), (p_213047_1_) -> {
         this.minecraft.shutdown();
      }));
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, this.height / 4 - 60 + 20, 16777215);
      this.drawString(this.font, "Minecraft has run out of memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 10526880);
      this.drawString(this.font, "This could be caused by a bug in the game or by the", this.width / 2 - 140, this.height / 4 - 60 + 60 + 18, 10526880);
      this.drawString(this.font, "Java Virtual Machine not being allocated enough", this.width / 2 - 140, this.height / 4 - 60 + 60 + 27, 10526880);
      this.drawString(this.font, "memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 36, 10526880);
      this.drawString(this.font, "To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 54, 10526880);
      this.drawString(this.font, "We've tried to free up enough memory to let you go back to", this.width / 2 - 140, this.height / 4 - 60 + 60 + 63, 10526880);
      this.drawString(this.font, "the main menu and back to playing, but this may not have worked.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 72, 10526880);
      this.drawString(this.font, "Please restart the game if you see this message again.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 81, 10526880);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}