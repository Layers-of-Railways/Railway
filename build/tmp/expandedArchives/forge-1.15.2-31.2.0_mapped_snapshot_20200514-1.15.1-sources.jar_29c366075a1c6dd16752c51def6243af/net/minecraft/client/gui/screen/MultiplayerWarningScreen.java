package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiplayerWarningScreen extends Screen {
   private final Screen field_230156_a_;
   private final ITextComponent field_230157_b_ = (new TranslationTextComponent("multiplayerWarning.header")).applyTextStyle(TextFormatting.BOLD);
   private final ITextComponent field_230158_c_ = new TranslationTextComponent("multiplayerWarning.message");
   private final ITextComponent field_230159_d_ = new TranslationTextComponent("multiplayerWarning.check");
   private final ITextComponent field_230160_e_ = new TranslationTextComponent("gui.proceed");
   private final ITextComponent field_230161_f_ = new TranslationTextComponent("gui.back");
   private CheckboxButton field_230162_g_;
   private final List<String> field_230163_h_ = Lists.newArrayList();

   public MultiplayerWarningScreen(Screen p_i230052_1_) {
      super(NarratorChatListener.EMPTY);
      this.field_230156_a_ = p_i230052_1_;
   }

   protected void init() {
      super.init();
      this.field_230163_h_.clear();
      this.field_230163_h_.addAll(this.font.listFormattedStringToWidth(this.field_230158_c_.getFormattedText(), this.width - 50));
      int i = (this.field_230163_h_.size() + 1) * 9;
      this.addButton(new Button(this.width / 2 - 155, 100 + i, 150, 20, this.field_230160_e_.getFormattedText(), (p_230165_1_) -> {
         if (this.field_230162_g_.isChecked()) {
            this.minecraft.gameSettings.field_230152_Z_ = true;
            this.minecraft.gameSettings.saveOptions();
         }

         this.minecraft.displayGuiScreen(new MultiplayerScreen(this.field_230156_a_));
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, 100 + i, 150, 20, this.field_230161_f_.getFormattedText(), (p_230164_1_) -> {
         this.minecraft.displayGuiScreen(this.field_230156_a_);
      }));
      this.field_230162_g_ = new CheckboxButton(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.field_230159_d_.getFormattedText(), false);
      this.addButton(this.field_230162_g_);
   }

   public String getNarrationMessage() {
      return this.field_230157_b_.getString() + "\n" + this.field_230158_c_.getString();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderDirtBackground(0);
      this.drawCenteredString(this.font, this.field_230157_b_.getFormattedText(), this.width / 2, 30, 16777215);
      int i = 70;

      for(String s : this.field_230163_h_) {
         this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
         i += 9;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}