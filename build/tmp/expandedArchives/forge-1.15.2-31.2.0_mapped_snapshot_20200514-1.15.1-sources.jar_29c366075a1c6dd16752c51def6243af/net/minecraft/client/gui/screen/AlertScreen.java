package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AlertScreen extends Screen {
   private final Runnable field_201552_h;
   protected final ITextComponent field_201550_f;
   private final List<String> field_201553_i = Lists.newArrayList();
   protected final String field_201551_g;
   private int field_201549_s;

   public AlertScreen(Runnable p_i48623_1_, ITextComponent p_i48623_2_, ITextComponent p_i48623_3_) {
      this(p_i48623_1_, p_i48623_2_, p_i48623_3_, "gui.back");
   }

   public AlertScreen(Runnable p_i49786_1_, ITextComponent p_i49786_2_, ITextComponent p_i49786_3_, String p_i49786_4_) {
      super(p_i49786_2_);
      this.field_201552_h = p_i49786_1_;
      this.field_201550_f = p_i49786_3_;
      this.field_201551_g = I18n.format(p_i49786_4_);
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.field_201551_g, (p_212983_1_) -> {
         this.field_201552_h.run();
      }));
      this.field_201553_i.clear();
      this.field_201553_i.addAll(this.font.listFormattedStringToWidth(this.field_201550_f.getFormattedText(), this.width - 50));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 70, 16777215);
      int i = 90;

      for(String s : this.field_201553_i) {
         this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
         i += 9;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void tick() {
      super.tick();
      if (--this.field_201549_s == 0) {
         for(Widget widget : this.buttons) {
            widget.active = true;
         }
      }

   }
}