package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsScreenProxy extends Screen {
   private final RealmsScreen screen;
   private static final Logger LOGGER = LogManager.getLogger();

   public RealmsScreenProxy(RealmsScreen p_i50912_1_) {
      super(NarratorChatListener.EMPTY);
      this.screen = p_i50912_1_;
   }

   public RealmsScreen getScreen() {
      return this.screen;
   }

   public void init(Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
      this.screen.init(p_init_1_, p_init_2_, p_init_3_);
      super.init(p_init_1_, p_init_2_, p_init_3_);
   }

   public void init() {
      this.screen.init();
      super.init();
   }

   public void drawCenteredString(String p_drawCenteredString_1_, int p_drawCenteredString_2_, int p_drawCenteredString_3_, int p_drawCenteredString_4_) {
      super.drawCenteredString(this.font, p_drawCenteredString_1_, p_drawCenteredString_2_, p_drawCenteredString_3_, p_drawCenteredString_4_);
   }

   public void drawString(String p_drawString_1_, int p_drawString_2_, int p_drawString_3_, int p_drawString_4_, boolean p_drawString_5_) {
      if (p_drawString_5_) {
         super.drawString(this.font, p_drawString_1_, p_drawString_2_, p_drawString_3_, p_drawString_4_);
      } else {
         this.font.drawString(p_drawString_1_, (float)p_drawString_2_, (float)p_drawString_3_, p_drawString_4_);
      }

   }

   public void blit(int p_blit_1_, int p_blit_2_, int p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_) {
      this.screen.blit(p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_);
      super.blit(p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, float p_blit_2_, float p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_, int p_blit_8_, int p_blit_9_) {
      AbstractGui.blit(p_blit_0_, p_blit_1_, p_blit_6_, p_blit_7_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_8_, p_blit_9_);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, float p_blit_2_, float p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_) {
      AbstractGui.blit(p_blit_0_, p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_, p_blit_7_);
   }

   public void fillGradient(int p_fillGradient_1_, int p_fillGradient_2_, int p_fillGradient_3_, int p_fillGradient_4_, int p_fillGradient_5_, int p_fillGradient_6_) {
      super.fillGradient(p_fillGradient_1_, p_fillGradient_2_, p_fillGradient_3_, p_fillGradient_4_, p_fillGradient_5_, p_fillGradient_6_);
   }

   public void renderBackground() {
      super.renderBackground();
   }

   public boolean isPauseScreen() {
      return super.isPauseScreen();
   }

   public void renderBackground(int p_renderBackground_1_) {
      super.renderBackground(p_renderBackground_1_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.screen.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void renderTooltip(ItemStack p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_);
   }

   public void renderTooltip(String p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_);
   }

   public void renderTooltip(List<String> p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_);
   }

   public void tick() {
      this.screen.tick();
      super.tick();
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public int fontLineHeight() {
      return 9;
   }

   public int fontWidth(String p_fontWidth_1_) {
      return this.font.getStringWidth(p_fontWidth_1_);
   }

   public void fontDrawShadow(String p_fontDrawShadow_1_, int p_fontDrawShadow_2_, int p_fontDrawShadow_3_, int p_fontDrawShadow_4_) {
      this.font.drawStringWithShadow(p_fontDrawShadow_1_, (float)p_fontDrawShadow_2_, (float)p_fontDrawShadow_3_, p_fontDrawShadow_4_);
   }

   public List<String> fontSplit(String p_fontSplit_1_, int p_fontSplit_2_) {
      return this.font.listFormattedStringToWidth(p_fontSplit_1_, p_fontSplit_2_);
   }

   public void childrenClear() {
      this.children.clear();
   }

   public void addWidget(RealmsGuiEventListener p_addWidget_1_) {
      if (this.hasWidget(p_addWidget_1_) || !this.children.add(p_addWidget_1_.getProxy())) {
         LOGGER.error("Tried to add the same widget multiple times: " + p_addWidget_1_);
      }

   }

   public void narrateLabels() {
      List<String> list = this.children.stream().filter((p_229954_0_) -> {
         return p_229954_0_ instanceof RealmsLabelProxy;
      }).map((p_229955_0_) -> {
         return ((RealmsLabelProxy)p_229955_0_).getLabel().getText();
      }).collect(Collectors.toList());
      Realms.narrateNow(list);
   }

   public void removeWidget(RealmsGuiEventListener p_removeWidget_1_) {
      if (!this.hasWidget(p_removeWidget_1_) || !this.children.remove(p_removeWidget_1_.getProxy())) {
         LOGGER.error("Tried to add the same widget multiple times: " + p_removeWidget_1_);
      }

   }

   public boolean hasWidget(RealmsGuiEventListener p_hasWidget_1_) {
      return this.children.contains(p_hasWidget_1_.getProxy());
   }

   public void buttonsAdd(AbstractRealmsButton<?> p_buttonsAdd_1_) {
      this.addButton(p_buttonsAdd_1_.getProxy());
   }

   public List<AbstractRealmsButton<?>> buttons() {
      List<AbstractRealmsButton<?>> list = Lists.newArrayListWithExpectedSize(this.buttons.size());

      for(Widget widget : this.buttons) {
         list.add(((RealmsAbstractButtonProxy)widget).getButton());
      }

      return list;
   }

   public void buttonsClear() {
      Set<IGuiEventListener> set = Sets.newHashSet(this.buttons);
      this.children.removeIf(set::contains);
      this.buttons.clear();
   }

   public void removeButton(RealmsButton p_removeButton_1_) {
      this.children.remove(p_removeButton_1_.getProxy());
      this.buttons.remove(p_removeButton_1_.getProxy());
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.screen.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.screen.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.screen.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_) ? true : super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.screen.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? true : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.screen.charTyped(p_charTyped_1_, p_charTyped_2_) ? true : super.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void removed() {
      this.screen.removed();
      super.removed();
   }

   public int draw(String p_draw_1_, int p_draw_2_, int p_draw_3_, int p_draw_4_, boolean p_draw_5_) {
      return p_draw_5_ ? this.font.drawStringWithShadow(p_draw_1_, (float)p_draw_2_, (float)p_draw_3_, p_draw_4_) : this.font.drawString(p_draw_1_, (float)p_draw_2_, (float)p_draw_3_, p_draw_4_);
   }

   public FontRenderer getFont() {
      return this.font;
   }
}