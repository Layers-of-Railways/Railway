package net.minecraft.client.gui.screen;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DirtMessageScreen extends Screen {
   public DirtMessageScreen(ITextComponent p_i51114_1_) {
      super(p_i51114_1_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderDirtBackground(0);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 70, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}