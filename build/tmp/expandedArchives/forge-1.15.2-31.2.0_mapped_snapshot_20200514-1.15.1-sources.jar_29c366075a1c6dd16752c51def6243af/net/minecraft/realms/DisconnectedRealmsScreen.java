package net.minecraft.realms;

import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedRealmsScreen extends RealmsScreen {
   private final String title;
   private final ITextComponent reason;
   private List<String> lines;
   private final RealmsScreen parent;
   private int textHeight;

   public DisconnectedRealmsScreen(RealmsScreen parentIn, String unlocalizedTitle, ITextComponent reasonIn) {
      this.parent = parentIn;
      this.title = getLocalizedString(unlocalizedTitle);
      this.reason = reasonIn;
   }

   public void init() {
      Realms.setConnectedToRealms(false);
      Realms.clearResourcePack();
      Realms.narrateNow(this.title + ": " + this.reason.getString());
      this.lines = this.fontSplit(this.reason.getFormattedText(), this.width() - 50);
      this.textHeight = this.lines.size() * this.fontLineHeight();
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() / 2 + this.textHeight / 2 + this.fontLineHeight(), getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(DisconnectedRealmsScreen.this.parent);
         }
      });
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.parent);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - this.textHeight / 2 - this.fontLineHeight() * 2, 11184810);
      int i = this.height() / 2 - this.textHeight / 2;
      if (this.lines != null) {
         for(String s : this.lines) {
            this.drawCenteredString(s, this.width() / 2, i, 16777215);
            i += this.fontLineHeight();
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}