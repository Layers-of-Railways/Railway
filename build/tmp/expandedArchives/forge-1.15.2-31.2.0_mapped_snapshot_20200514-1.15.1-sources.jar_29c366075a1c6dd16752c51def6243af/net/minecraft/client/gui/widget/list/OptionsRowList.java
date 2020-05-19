package net.minecraft.client.gui.widget.list;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.settings.AbstractOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsRowList extends AbstractOptionList<OptionsRowList.Row> {
   public OptionsRowList(Minecraft p_i51130_1_, int p_i51130_2_, int p_i51130_3_, int p_i51130_4_, int p_i51130_5_, int p_i51130_6_) {
      super(p_i51130_1_, p_i51130_2_, p_i51130_3_, p_i51130_4_, p_i51130_5_, p_i51130_6_);
      this.centerListVertically = false;
   }

   public int addOption(AbstractOption p_214333_1_) {
      return this.addEntry(OptionsRowList.Row.create(this.minecraft.gameSettings, this.width, p_214333_1_));
   }

   public void addOption(AbstractOption p_214334_1_, @Nullable AbstractOption p_214334_2_) {
      this.addEntry(OptionsRowList.Row.create(this.minecraft.gameSettings, this.width, p_214334_1_, p_214334_2_));
   }

   public void addOptions(AbstractOption[] p_214335_1_) {
      for(int i = 0; i < p_214335_1_.length; i += 2) {
         this.addOption(p_214335_1_[i], i < p_214335_1_.length - 1 ? p_214335_1_[i + 1] : null);
      }

   }

   public int getRowWidth() {
      return 400;
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Row extends AbstractOptionList.Entry<OptionsRowList.Row> {
      private final List<Widget> widgets;

      private Row(List<Widget> widgetsIn) {
         this.widgets = widgetsIn;
      }

      /**
       * Creates an options row with button for the specified option
       */
      public static OptionsRowList.Row create(GameSettings settings, int guiWidth, AbstractOption option) {
         return new OptionsRowList.Row(ImmutableList.of(option.createWidget(settings, guiWidth / 2 - 155, 0, 310)));
      }

      /**
       * Creates an options row with 1 or 2 buttons for specified options
       */
      public static OptionsRowList.Row create(GameSettings settings, int guiWidth, AbstractOption leftOption, @Nullable AbstractOption rightOption) {
         Widget widget = leftOption.createWidget(settings, guiWidth / 2 - 155, 0, 150);
         return rightOption == null ? new OptionsRowList.Row(ImmutableList.of(widget)) : new OptionsRowList.Row(ImmutableList.of(widget, rightOption.createWidget(settings, guiWidth / 2 - 155 + 160, 0, 150)));
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.widgets.forEach((p_214383_4_) -> {
            p_214383_4_.y = p_render_2_;
            p_214383_4_.render(p_render_6_, p_render_7_, p_render_9_);
         });
      }

      public List<? extends IGuiEventListener> children() {
         return this.widgets;
      }
   }
}