package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SlotGui extends FocusableGui implements IRenderable {
   protected static final int NO_DRAG = -1;
   protected static final int DRAG_OUTSIDE = -2;
   protected final Minecraft minecraft;
   protected int width;
   protected int height;
   protected int y0;
   protected int y1;
   protected int x1;
   protected int x0;
   protected final int itemHeight;
   protected boolean centerListVertically = true;
   protected int yDrag = -2;
   protected double yo;
   protected boolean visible = true;
   protected boolean renderSelection = true;
   protected boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;

   public SlotGui(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
      this.minecraft = mcIn;
      this.width = width;
      this.height = height;
      this.y0 = topIn;
      this.y1 = bottomIn;
      this.itemHeight = slotHeightIn;
      this.x0 = 0;
      this.x1 = width;
   }

   public void updateSize(int p_updateSize_1_, int p_updateSize_2_, int p_updateSize_3_, int p_updateSize_4_) {
      this.width = p_updateSize_1_;
      this.height = p_updateSize_2_;
      this.y0 = p_updateSize_3_;
      this.y1 = p_updateSize_4_;
      this.x0 = 0;
      this.x1 = p_updateSize_1_;
   }

   public void setRenderSelection(boolean p_setRenderSelection_1_) {
      this.renderSelection = p_setRenderSelection_1_;
   }

   protected void setRenderHeader(boolean p_setRenderHeader_1_, int p_setRenderHeader_2_) {
      this.renderHeader = p_setRenderHeader_1_;
      this.headerHeight = p_setRenderHeader_2_;
      if (!p_setRenderHeader_1_) {
         this.headerHeight = 0;
      }

   }

   public void setVisible(boolean p_setVisible_1_) {
      this.visible = p_setVisible_1_;
   }

   public boolean isVisible() {
      return this.visible;
   }

   protected abstract int getItemCount();

   public List<? extends IGuiEventListener> children() {
      return Collections.emptyList();
   }

   protected boolean selectItem(int p_selectItem_1_, int p_selectItem_2_, double p_selectItem_3_, double p_selectItem_5_) {
      return true;
   }

   protected abstract boolean isSelectedItem(int p_isSelectedItem_1_);

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected abstract void renderBackground();

   protected void updateItemPosition(int p_updateItemPosition_1_, int p_updateItemPosition_2_, int p_updateItemPosition_3_, float p_updateItemPosition_4_) {
   }

   protected abstract void renderItem(int p_renderItem_1_, int p_renderItem_2_, int p_renderItem_3_, int p_renderItem_4_, int p_renderItem_5_, int p_renderItem_6_, float p_renderItem_7_);

   protected void renderHeader(int p_renderHeader_1_, int p_renderHeader_2_, Tessellator p_renderHeader_3_) {
   }

   protected void clickedHeader(int p_clickedHeader_1_, int p_clickedHeader_2_) {
   }

   protected void renderDecorations(int p_renderDecorations_1_, int p_renderDecorations_2_) {
   }

   public int getItemAtPosition(double p_getItemAtPosition_1_, double p_getItemAtPosition_3_) {
      int i = this.x0 + this.width / 2 - this.getRowWidth() / 2;
      int j = this.x0 + this.width / 2 + this.getRowWidth() / 2;
      int k = MathHelper.floor(p_getItemAtPosition_3_ - (double)this.y0) - this.headerHeight + (int)this.yo - 4;
      int l = k / this.itemHeight;
      return p_getItemAtPosition_1_ < (double)this.getScrollbarPosition() && p_getItemAtPosition_1_ >= (double)i && p_getItemAtPosition_1_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount() ? l : -1;
   }

   protected void capYPosition() {
      this.yo = MathHelper.clamp(this.yo, 0.0D, (double)this.getMaxScroll());
   }

   public int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
   }

   public void centerScrollOn(int p_centerScrollOn_1_) {
      this.yo = (double)(p_centerScrollOn_1_ * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2);
      this.capYPosition();
   }

   public int getScroll() {
      return (int)this.yo;
   }

   public boolean isMouseInList(double p_isMouseInList_1_, double p_isMouseInList_3_) {
      return p_isMouseInList_3_ >= (double)this.y0 && p_isMouseInList_3_ <= (double)this.y1 && p_isMouseInList_1_ >= (double)this.x0 && p_isMouseInList_1_ <= (double)this.x1;
   }

   public int getScrollBottom() {
      return (int)this.yo - this.height - this.headerHeight;
   }

   public void scroll(int p_scroll_1_) {
      this.yo += (double)p_scroll_1_;
      this.capYPosition();
      this.yDrag = -2;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.visible) {
         this.renderBackground();
         int i = this.getScrollbarPosition();
         int j = i + 6;
         this.capYPosition();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         // Forge: background rendering moved into separate method.
         this.drawContainerBackground(tessellator);
         int k = this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
         int l = this.y0 + 4 - (int)this.yo;
         if (this.renderHeader) {
            this.renderHeader(k, l, tessellator);
         }

         this.renderList(k, l, p_render_1_, p_render_2_, p_render_3_);
         RenderSystem.disableDepthTest();
         this.renderHoleBackground(0, this.y0, 255, 255);
         this.renderHoleBackground(this.y1, this.height, 255, 255);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         RenderSystem.disableAlphaTest();
         RenderSystem.shadeModel(7425);
         RenderSystem.disableTexture();
         int i1 = 4;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.pos((double)this.x0, (double)(this.y0 + 4), 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)this.x1, (double)(this.y0 + 4), 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)this.x1, (double)this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)this.x0, (double)this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
         tessellator.draw();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.pos((double)this.x0, (double)this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)this.x1, (double)this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)this.x1, (double)(this.y1 - 4), 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)this.x0, (double)(this.y1 - 4), 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
         tessellator.draw();
         int j1 = this.getMaxScroll();
         if (j1 > 0) {
            int k1 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
            k1 = MathHelper.clamp(k1, 32, this.y1 - this.y0 - 8);
            int l1 = (int)this.yo * (this.y1 - this.y0 - k1) / j1 + this.y0;
            if (l1 < this.y0) {
               l1 = this.y0;
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)j, (double)this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)j, (double)this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)i, (double)this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)(l1 + k1), 0.0D).tex(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j, (double)(l1 + k1), 0.0D).tex(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j, (double)l1, 0.0D).tex(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)i, (double)l1, 0.0D).tex(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)(l1 + k1 - 1), 0.0D).tex(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)(j - 1), (double)(l1 + k1 - 1), 0.0D).tex(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)(j - 1), (double)l1, 0.0D).tex(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)i, (double)l1, 0.0D).tex(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            tessellator.draw();
         }

         this.renderDecorations(p_render_1_, p_render_2_);
         RenderSystem.enableTexture();
         RenderSystem.shadeModel(7424);
         RenderSystem.enableAlphaTest();
         RenderSystem.disableBlend();
      }
   }

   protected void updateScrollingState(double p_updateScrollingState_1_, double p_updateScrollingState_3_, int p_updateScrollingState_5_) {
      this.scrolling = p_updateScrollingState_5_ == 0 && p_updateScrollingState_1_ >= (double)this.getScrollbarPosition() && p_updateScrollingState_1_ < (double)(this.getScrollbarPosition() + 6);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.updateScrollingState(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      if (this.isVisible() && this.isMouseInList(p_mouseClicked_1_, p_mouseClicked_3_)) {
         int i = this.getItemAtPosition(p_mouseClicked_1_, p_mouseClicked_3_);
         if (i == -1 && p_mouseClicked_5_ == 0) {
            this.clickedHeader((int)(p_mouseClicked_1_ - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(p_mouseClicked_3_ - (double)this.y0) + (int)this.yo - 4);
            return true;
         } else if (i != -1 && this.selectItem(i, p_mouseClicked_5_, p_mouseClicked_1_, p_mouseClicked_3_)) {
            if (this.children().size() > i) {
               this.setFocused(this.children().get(i));
            }

            this.setDragging(true);
            return true;
         } else {
            return this.scrolling;
         }
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }

      return false;
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_)) {
         return true;
      } else if (this.isVisible() && p_mouseDragged_5_ == 0 && this.scrolling) {
         if (p_mouseDragged_3_ < (double)this.y0) {
            this.yo = 0.0D;
         } else if (p_mouseDragged_3_ > (double)this.y1) {
            this.yo = (double)this.getMaxScroll();
         } else {
            double d0 = (double)this.getMaxScroll();
            if (d0 < 1.0D) {
               d0 = 1.0D;
            }

            int i = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
            i = MathHelper.clamp(i, 32, this.y1 - this.y0 - 8);
            double d1 = d0 / (double)(this.y1 - this.y0 - i);
            if (d1 < 1.0D) {
               d1 = 1.0D;
            }

            this.yo += p_mouseDragged_8_ * d1;
            this.capYPosition();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (!this.isVisible()) {
         return false;
      } else {
         this.yo -= p_mouseScrolled_5_ * (double)this.itemHeight / 2.0D;
         return true;
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (!this.isVisible()) {
         return false;
      } else if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ == 264) {
         this.moveSelection(1);
         return true;
      } else if (p_keyPressed_1_ == 265) {
         this.moveSelection(-1);
         return true;
      } else {
         return false;
      }
   }

   protected void moveSelection(int p_moveSelection_1_) {
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return !this.isVisible() ? false : super.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return this.isMouseInList(p_isMouseOver_1_, p_isMouseOver_3_);
   }

   public int getRowWidth() {
      return 220;
   }

   protected void renderList(int p_renderList_1_, int p_renderList_2_, int p_renderList_3_, int p_renderList_4_, float p_renderList_5_) {
      int i = this.getItemCount();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();

      for(int j = 0; j < i; ++j) {
         int k = p_renderList_2_ + j * this.itemHeight + this.headerHeight;
         int l = this.itemHeight - 4;
         if (k > this.y1 || k + l < this.y0) {
            this.updateItemPosition(j, p_renderList_1_, k, p_renderList_5_);
         }

         if (this.renderSelection && this.isSelectedItem(j)) {
            int i1 = this.x0 + this.width / 2 - this.getRowWidth() / 2;
            int j1 = this.x0 + this.width / 2 + this.getRowWidth() / 2;
            RenderSystem.disableTexture();
            float f = this.isFocused() ? 1.0F : 0.5F;
            RenderSystem.color4f(f, f, f, 1.0F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
            bufferbuilder.pos((double)i1, (double)(k + l + 2), 0.0D).endVertex();
            bufferbuilder.pos((double)j1, (double)(k + l + 2), 0.0D).endVertex();
            bufferbuilder.pos((double)j1, (double)(k - 2), 0.0D).endVertex();
            bufferbuilder.pos((double)i1, (double)(k - 2), 0.0D).endVertex();
            tessellator.draw();
            RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
            bufferbuilder.pos((double)(i1 + 1), (double)(k + l + 1), 0.0D).endVertex();
            bufferbuilder.pos((double)(j1 - 1), (double)(k + l + 1), 0.0D).endVertex();
            bufferbuilder.pos((double)(j1 - 1), (double)(k - 1), 0.0D).endVertex();
            bufferbuilder.pos((double)(i1 + 1), (double)(k - 1), 0.0D).endVertex();
            tessellator.draw();
            RenderSystem.enableTexture();
         }

         this.renderItem(j, p_renderList_1_, k, l, p_renderList_3_, p_renderList_4_, p_renderList_5_);
      }

   }

   protected boolean isFocused() {
      return false;
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   protected void renderHoleBackground(int p_renderHoleBackground_1_, int p_renderHoleBackground_2_, int p_renderHoleBackground_3_, int p_renderHoleBackground_4_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos((double)this.x0, (double)p_renderHoleBackground_2_, 0.0D).tex(0.0F, (float)p_renderHoleBackground_2_ / 32.0F).color(64, 64, 64, p_renderHoleBackground_4_).endVertex();
      bufferbuilder.pos((double)(this.x0 + this.width), (double)p_renderHoleBackground_2_, 0.0D).tex((float)this.width / 32.0F, (float)p_renderHoleBackground_2_ / 32.0F).color(64, 64, 64, p_renderHoleBackground_4_).endVertex();
      bufferbuilder.pos((double)(this.x0 + this.width), (double)p_renderHoleBackground_1_, 0.0D).tex((float)this.width / 32.0F, (float)p_renderHoleBackground_1_ / 32.0F).color(64, 64, 64, p_renderHoleBackground_3_).endVertex();
      bufferbuilder.pos((double)this.x0, (double)p_renderHoleBackground_1_, 0.0D).tex(0.0F, (float)p_renderHoleBackground_1_ / 32.0F).color(64, 64, 64, p_renderHoleBackground_3_).endVertex();
      tessellator.draw();
   }

   public void setLeftPos(int p_setLeftPos_1_) {
      this.x0 = p_setLeftPos_1_;
      this.x1 = p_setLeftPos_1_ + this.width;
   }

   public int getItemHeight() {
      return this.itemHeight;
   }

   protected void drawContainerBackground(Tessellator tessellator) {
      BufferBuilder buffer = tessellator.getBuffer();
      this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float scale = 32.0F;
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.pos((double)this.x0, (double)this.y1, 0.0D).tex(this.x0 / scale, (this.y1 + (int)this.yo) / scale).color(32, 32, 32, 255).endVertex();
      buffer.pos((double)this.x1, (double)this.y1, 0.0D).tex(this.x1 / scale, (this.y1 + (int)this.yo) / scale).color(32, 32, 32, 255).endVertex();
      buffer.pos((double)this.x1, (double)this.y0, 0.0D).tex(this.x1 / scale, (this.y0 + (int)this.yo) / scale).color(32, 32, 32, 255).endVertex();
      buffer.pos((double)this.x0, (double)this.y0, 0.0D).tex(this.x0 / scale, (this.y0 + (int)this.yo) / scale).color(32, 32, 32, 255).endVertex();
      tessellator.draw();
   }
}