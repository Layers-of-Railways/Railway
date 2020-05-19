package net.minecraft.realms;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsScreen extends RealmsGuiEventListener implements RealmsConfirmResultListener {
   public static final int SKIN_HEAD_U = 8;
   public static final int SKIN_HEAD_V = 8;
   public static final int SKIN_HEAD_WIDTH = 8;
   public static final int SKIN_HEAD_HEIGHT = 8;
   public static final int SKIN_HAT_U = 40;
   public static final int SKIN_HAT_V = 8;
   public static final int SKIN_HAT_WIDTH = 8;
   public static final int SKIN_HAT_HEIGHT = 8;
   public static final int SKIN_TEX_WIDTH = 64;
   public static final int SKIN_TEX_HEIGHT = 64;
   private Minecraft minecraft;
   public int width;
   public int height;
   private final RealmsScreenProxy proxy = new RealmsScreenProxy(this);

   public RealmsScreenProxy getProxy() {
      return this.proxy;
   }

   public void init() {
   }

   public void init(Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
      this.minecraft = p_init_1_;
   }

   public void drawCenteredString(String p_drawCenteredString_1_, int p_drawCenteredString_2_, int p_drawCenteredString_3_, int p_drawCenteredString_4_) {
      this.proxy.drawCenteredString(p_drawCenteredString_1_, p_drawCenteredString_2_, p_drawCenteredString_3_, p_drawCenteredString_4_);
   }

   public int draw(String p_draw_1_, int p_draw_2_, int p_draw_3_, int p_draw_4_, boolean p_draw_5_) {
      return this.proxy.draw(p_draw_1_, p_draw_2_, p_draw_3_, p_draw_4_, p_draw_5_);
   }

   public void drawString(String p_drawString_1_, int p_drawString_2_, int p_drawString_3_, int p_drawString_4_) {
      this.drawString(p_drawString_1_, p_drawString_2_, p_drawString_3_, p_drawString_4_, true);
   }

   public void drawString(String p_drawString_1_, int p_drawString_2_, int p_drawString_3_, int p_drawString_4_, boolean p_drawString_5_) {
      this.proxy.drawString(p_drawString_1_, p_drawString_2_, p_drawString_3_, p_drawString_4_, false);
   }

   public void blit(int p_blit_1_, int p_blit_2_, int p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_) {
      this.proxy.blit(p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, float p_blit_2_, float p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_, int p_blit_8_, int p_blit_9_) {
      AbstractGui.blit(p_blit_0_, p_blit_1_, p_blit_6_, p_blit_7_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_8_, p_blit_9_);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, float p_blit_2_, float p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_) {
      AbstractGui.blit(p_blit_0_, p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_, p_blit_7_);
   }

   public void fillGradient(int p_fillGradient_1_, int p_fillGradient_2_, int p_fillGradient_3_, int p_fillGradient_4_, int p_fillGradient_5_, int p_fillGradient_6_) {
      this.proxy.fillGradient(p_fillGradient_1_, p_fillGradient_2_, p_fillGradient_3_, p_fillGradient_4_, p_fillGradient_5_, p_fillGradient_6_);
   }

   public void renderBackground() {
      this.proxy.renderBackground();
   }

   public boolean isPauseScreen() {
      return this.proxy.isPauseScreen();
   }

   public void renderBackground(int p_renderBackground_1_) {
      this.proxy.renderBackground(p_renderBackground_1_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      for(int i = 0; i < this.proxy.buttons().size(); ++i) {
         this.proxy.buttons().get(i).render(p_render_1_, p_render_2_, p_render_3_);
      }

   }

   public void renderTooltip(ItemStack p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      this.proxy.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_);
   }

   public void renderTooltip(String p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      this.proxy.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_);
   }

   public void renderTooltip(List<String> p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      this.proxy.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_);
   }

   public static void bind(String p_bind_0_) {
      Realms.bind(p_bind_0_);
   }

   public void tick() {
      this.tickButtons();
   }

   protected void tickButtons() {
      for(AbstractRealmsButton<?> abstractrealmsbutton : this.buttons()) {
         abstractrealmsbutton.tick();
      }

   }

   public int width() {
      return this.proxy.width;
   }

   public int height() {
      return this.proxy.height;
   }

   public int fontLineHeight() {
      return this.proxy.fontLineHeight();
   }

   public int fontWidth(String p_fontWidth_1_) {
      return this.proxy.fontWidth(p_fontWidth_1_);
   }

   public void fontDrawShadow(String p_fontDrawShadow_1_, int p_fontDrawShadow_2_, int p_fontDrawShadow_3_, int p_fontDrawShadow_4_) {
      this.proxy.fontDrawShadow(p_fontDrawShadow_1_, p_fontDrawShadow_2_, p_fontDrawShadow_3_, p_fontDrawShadow_4_);
   }

   public List<String> fontSplit(String p_fontSplit_1_, int p_fontSplit_2_) {
      return this.proxy.fontSplit(p_fontSplit_1_, p_fontSplit_2_);
   }

   public void childrenClear() {
      this.proxy.childrenClear();
   }

   public void addWidget(RealmsGuiEventListener p_addWidget_1_) {
      this.proxy.addWidget(p_addWidget_1_);
   }

   public void removeWidget(RealmsGuiEventListener p_removeWidget_1_) {
      this.proxy.removeWidget(p_removeWidget_1_);
   }

   public boolean hasWidget(RealmsGuiEventListener p_hasWidget_1_) {
      return this.proxy.hasWidget(p_hasWidget_1_);
   }

   public void buttonsAdd(AbstractRealmsButton<?> p_buttonsAdd_1_) {
      this.proxy.buttonsAdd(p_buttonsAdd_1_);
   }

   public List<AbstractRealmsButton<?>> buttons() {
      return this.proxy.buttons();
   }

   protected void buttonsClear() {
      this.proxy.buttonsClear();
   }

   protected void focusOn(RealmsGuiEventListener p_focusOn_1_) {
      this.proxy.func_212932_b(p_focusOn_1_.getProxy());
   }

   public RealmsEditBox newEditBox(int p_newEditBox_1_, int p_newEditBox_2_, int p_newEditBox_3_, int p_newEditBox_4_, int p_newEditBox_5_) {
      return this.newEditBox(p_newEditBox_1_, p_newEditBox_2_, p_newEditBox_3_, p_newEditBox_4_, p_newEditBox_5_, "");
   }

   public RealmsEditBox newEditBox(int p_newEditBox_1_, int p_newEditBox_2_, int p_newEditBox_3_, int p_newEditBox_4_, int p_newEditBox_5_, String p_newEditBox_6_) {
      return new RealmsEditBox(p_newEditBox_1_, p_newEditBox_2_, p_newEditBox_3_, p_newEditBox_4_, p_newEditBox_5_, p_newEditBox_6_);
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
   }

   public static String getLocalizedString(String p_getLocalizedString_0_) {
      return Realms.getLocalizedString(p_getLocalizedString_0_);
   }

   public static String getLocalizedString(String p_getLocalizedString_0_, Object... p_getLocalizedString_1_) {
      return Realms.getLocalizedString(p_getLocalizedString_0_, p_getLocalizedString_1_);
   }

   public List<String> getLocalizedStringWithLineWidth(String p_getLocalizedStringWithLineWidth_1_, int p_getLocalizedStringWithLineWidth_2_) {
      return this.minecraft.fontRenderer.listFormattedStringToWidth(I18n.format(p_getLocalizedStringWithLineWidth_1_), p_getLocalizedStringWithLineWidth_2_);
   }

   public RealmsAnvilLevelStorageSource getLevelStorageSource() {
      return new RealmsAnvilLevelStorageSource(Minecraft.getInstance().getSaveLoader());
   }

   public void removed() {
   }

   protected void removeButton(RealmsButton p_removeButton_1_) {
      this.proxy.removeButton(p_removeButton_1_);
   }

   protected void setKeyboardHandlerSendRepeatsToGui(boolean p_setKeyboardHandlerSendRepeatsToGui_1_) {
      this.minecraft.keyboardListener.enableRepeatEvents(p_setKeyboardHandlerSendRepeatsToGui_1_);
   }

   protected boolean isKeyDown(int p_isKeyDown_1_) {
      return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), p_isKeyDown_1_);
   }

   protected void narrateLabels() {
      this.getProxy().narrateLabels();
   }

   public boolean isFocused(RealmsGuiEventListener p_isFocused_1_) {
      return this.getProxy().getFocused() == p_isFocused_1_.getProxy();
   }
}