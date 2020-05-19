package net.minecraft.client.gui.widget.list;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractList<E extends AbstractList.AbstractListEntry<E>> extends FocusableGui implements IRenderable {
   protected static final int DRAG_OUTSIDE = -2;
   protected final Minecraft minecraft;
   protected final int itemHeight;
   private final List<E> children = new AbstractList.SimpleArrayList();
   protected int width;
   protected int height;
   protected int y0;
   protected int y1;
   protected int x1;
   protected int x0;
   protected boolean centerListVertically = true;
   protected int yDrag = -2;
   private double scrollAmount;
   protected boolean renderSelection = true;
   protected boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;
   private E selected;

   public AbstractList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int itemHeightIn) {
      this.minecraft = mcIn;
      this.width = widthIn;
      this.height = heightIn;
      this.y0 = topIn;
      this.y1 = bottomIn;
      this.itemHeight = itemHeightIn;
      this.x0 = 0;
      this.x1 = widthIn;
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

   public int getRowWidth() {
      return 220;
   }

   @Nullable
   public E getSelected() {
      return this.selected;
   }

   public void setSelected(@Nullable E p_setSelected_1_) {
      this.selected = p_setSelected_1_;
   }

   @Nullable
   public E getFocused() {
      return (E)(super.getFocused());
   }

   public final List<E> children() {
      return this.children;
   }

   protected final void clearEntries() {
      this.children.clear();
   }

   protected void replaceEntries(Collection<E> p_replaceEntries_1_) {
      this.children.clear();
      this.children.addAll(p_replaceEntries_1_);
   }

   protected E getEntry(int p_getEntry_1_) {
      return (E)(this.children().get(p_getEntry_1_));
   }

   protected int addEntry(E p_addEntry_1_) {
      this.children.add(p_addEntry_1_);
      return this.children.size() - 1;
   }

   protected int getItemCount() {
      return this.children().size();
   }

   protected boolean isSelectedItem(int p_isSelectedItem_1_) {
      return Objects.equals(this.getSelected(), this.children().get(p_isSelectedItem_1_));
   }

   @Nullable
   protected final E getEntryAtPosition(double p_getEntryAtPosition_1_, double p_getEntryAtPosition_3_) {
      int i = this.getRowWidth() / 2;
      int j = this.x0 + this.width / 2;
      int k = j - i;
      int l = j + i;
      int i1 = MathHelper.floor(p_getEntryAtPosition_3_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
      int j1 = i1 / this.itemHeight;
      return (E)(p_getEntryAtPosition_1_ < (double)this.getScrollbarPosition() && p_getEntryAtPosition_1_ >= (double)k && p_getEntryAtPosition_1_ <= (double)l && j1 >= 0 && i1 >= 0 && j1 < this.getItemCount() ? this.children().get(j1) : null);
   }

   public void updateSize(int p_updateSize_1_, int p_updateSize_2_, int p_updateSize_3_, int p_updateSize_4_) {
      this.width = p_updateSize_1_;
      this.height = p_updateSize_2_;
      this.y0 = p_updateSize_3_;
      this.y1 = p_updateSize_4_;
      this.x0 = 0;
      this.x1 = p_updateSize_1_;
   }

   public void setLeftPos(int p_setLeftPos_1_) {
      this.x0 = p_setLeftPos_1_;
      this.x1 = p_setLeftPos_1_ + this.width;
   }

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected void clickedHeader(int p_clickedHeader_1_, int p_clickedHeader_2_) {
   }

   protected void renderHeader(int p_renderHeader_1_, int p_renderHeader_2_, Tessellator p_renderHeader_3_) {
   }

   protected void renderBackground() {
   }

   protected void renderDecorations(int p_renderDecorations_1_, int p_renderDecorations_2_) {
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      int i = this.getScrollbarPosition();
      int j = i + 6;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos((double)this.x0, (double)this.y1, 0.0D).tex((float)this.x0 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
      bufferbuilder.pos((double)this.x1, (double)this.y1, 0.0D).tex((float)this.x1 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
      bufferbuilder.pos((double)this.x1, (double)this.y0, 0.0D).tex((float)this.x1 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
      bufferbuilder.pos((double)this.x0, (double)this.y0, 0.0D).tex((float)this.x0 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
      tessellator.draw();
      int k = this.getRowLeft();
      int l = this.y0 + 4 - (int)this.getScrollAmount();
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
         int l1 = (int)this.getScrollAmount() * (this.y1 - this.y0 - k1) / j1 + this.y0;
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

   protected void centerScrollOn(E p_centerScrollOn_1_) {
      this.setScrollAmount((double)(this.children().indexOf(p_centerScrollOn_1_) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2));
   }

   protected void ensureVisible(E p_ensureVisible_1_) {
      int i = this.getRowTop(this.children().indexOf(p_ensureVisible_1_));
      int j = i - this.y0 - 4 - this.itemHeight;
      if (j < 0) {
         this.scroll(j);
      }

      int k = this.y1 - i - this.itemHeight - this.itemHeight;
      if (k < 0) {
         this.scroll(-k);
      }

   }

   private void scroll(int p_scroll_1_) {
      this.setScrollAmount(this.getScrollAmount() + (double)p_scroll_1_);
      this.yDrag = -2;
   }

   public double getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double p_setScrollAmount_1_) {
      this.scrollAmount = MathHelper.clamp(p_setScrollAmount_1_, 0.0D, (double)this.getMaxScroll());
   }

   private int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
   }

   public int getScrollBottom() {
      return (int)this.getScrollAmount() - this.height - this.headerHeight;
   }

   protected void updateScrollingState(double p_updateScrollingState_1_, double p_updateScrollingState_3_, int p_updateScrollingState_5_) {
      this.scrolling = p_updateScrollingState_5_ == 0 && p_updateScrollingState_1_ >= (double)this.getScrollbarPosition() && p_updateScrollingState_1_ < (double)(this.getScrollbarPosition() + 6);
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.updateScrollingState(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      if (!this.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_)) {
         return false;
      } else {
         E e = this.getEntryAtPosition(p_mouseClicked_1_, p_mouseClicked_3_);
         if (e != null) {
            if (e.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
               this.setFocused(e);
               this.setDragging(true);
               return true;
            }
         } else if (p_mouseClicked_5_ == 0) {
            this.clickedHeader((int)(p_mouseClicked_1_ - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(p_mouseClicked_3_ - (double)this.y0) + (int)this.getScrollAmount() - 4);
            return true;
         }

         return this.scrolling;
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
      } else if (p_mouseDragged_5_ == 0 && this.scrolling) {
         if (p_mouseDragged_3_ < (double)this.y0) {
            this.setScrollAmount(0.0D);
         } else if (p_mouseDragged_3_ > (double)this.y1) {
            this.setScrollAmount((double)this.getMaxScroll());
         } else {
            double d0 = (double)Math.max(1, this.getMaxScroll());
            int i = this.y1 - this.y0;
            int j = MathHelper.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
            double d1 = Math.max(1.0D, d0 / (double)(i - j));
            this.setScrollAmount(this.getScrollAmount() + p_mouseDragged_8_ * d1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      this.setScrollAmount(this.getScrollAmount() - p_mouseScrolled_5_ * (double)this.itemHeight / 2.0D);
      return true;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
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
      if (!this.children().isEmpty()) {
         int i = this.children().indexOf(this.getSelected());
         int j = MathHelper.clamp(i + p_moveSelection_1_, 0, this.getItemCount() - 1);
         E e = this.children().get(j);
         this.setSelected(e);
         this.ensureVisible(e);
      }

   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return p_isMouseOver_3_ >= (double)this.y0 && p_isMouseOver_3_ <= (double)this.y1 && p_isMouseOver_1_ >= (double)this.x0 && p_isMouseOver_1_ <= (double)this.x1;
   }

   protected void renderList(int p_renderList_1_, int p_renderList_2_, int p_renderList_3_, int p_renderList_4_, float p_renderList_5_) {
      int i = this.getItemCount();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();

      for(int j = 0; j < i; ++j) {
         int k = this.getRowTop(j);
         int l = this.getRowBottom(j);
         if (l >= this.y0 && k <= this.y1) {
            int i1 = p_renderList_2_ + j * this.itemHeight + this.headerHeight;
            int j1 = this.itemHeight - 4;
            E e = this.getEntry(j);
            int k1 = this.getRowWidth();
            if (this.renderSelection && this.isSelectedItem(j)) {
               int l1 = this.x0 + this.width / 2 - k1 / 2;
               int i2 = this.x0 + this.width / 2 + k1 / 2;
               RenderSystem.disableTexture();
               float f = this.isFocused() ? 1.0F : 0.5F;
               RenderSystem.color4f(f, f, f, 1.0F);
               bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
               bufferbuilder.pos((double)l1, (double)(i1 + j1 + 2), 0.0D).endVertex();
               bufferbuilder.pos((double)i2, (double)(i1 + j1 + 2), 0.0D).endVertex();
               bufferbuilder.pos((double)i2, (double)(i1 - 2), 0.0D).endVertex();
               bufferbuilder.pos((double)l1, (double)(i1 - 2), 0.0D).endVertex();
               tessellator.draw();
               RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
               bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
               bufferbuilder.pos((double)(l1 + 1), (double)(i1 + j1 + 1), 0.0D).endVertex();
               bufferbuilder.pos((double)(i2 - 1), (double)(i1 + j1 + 1), 0.0D).endVertex();
               bufferbuilder.pos((double)(i2 - 1), (double)(i1 - 1), 0.0D).endVertex();
               bufferbuilder.pos((double)(l1 + 1), (double)(i1 - 1), 0.0D).endVertex();
               tessellator.draw();
               RenderSystem.enableTexture();
            }

            int j2 = this.getRowLeft();
            e.render(j, k, j2, k1, j1, p_renderList_3_, p_renderList_4_, this.isMouseOver((double)p_renderList_3_, (double)p_renderList_4_) && Objects.equals(this.getEntryAtPosition((double)p_renderList_3_, (double)p_renderList_4_), e), p_renderList_5_);
         }
      }

   }

   protected int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   protected int getRowTop(int p_getRowTop_1_) {
      return this.y0 + 4 - (int)this.getScrollAmount() + p_getRowTop_1_ * this.itemHeight + this.headerHeight;
   }

   private int getRowBottom(int p_getRowBottom_1_) {
      return this.getRowTop(p_getRowBottom_1_) + this.itemHeight;
   }

   protected boolean isFocused() {
      return false;
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

   protected E remove(int p_remove_1_) {
      E e = this.children.get(p_remove_1_);
      return (E)(this.removeEntry((E)(this.children.get(p_remove_1_))) ? e : null);
   }

   protected boolean removeEntry(E p_removeEntry_1_) {
      boolean flag = this.children.remove(p_removeEntry_1_);
      if (flag && p_removeEntry_1_ == this.getSelected()) {
         this.setSelected((E)null);
      }

      return flag;
   }

   public int getWidth() { return this.width; }
   public int getHeight() { return this.height; }
   public int getTop() { return this.y0; }
   public int getBottom() { return this.y1; }
   public int getLeft() { return this.x0; }
   public int getRight() { return this.x1; }

   @OnlyIn(Dist.CLIENT)
   public abstract static class AbstractListEntry<E extends AbstractList.AbstractListEntry<E>> implements IGuiEventListener {
      @Deprecated
      protected AbstractList<E> list;

      public abstract void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_);

      public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
         return Objects.equals(this.list.getEntryAtPosition(p_isMouseOver_1_, p_isMouseOver_3_), this);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class SimpleArrayList extends java.util.AbstractList<E> {
      private final List<E> field_216871_b = Lists.newArrayList();

      private SimpleArrayList() {
      }

      public E get(int p_get_1_) {
         return (E)(this.field_216871_b.get(p_get_1_));
      }

      public int size() {
         return this.field_216871_b.size();
      }

      public E set(int p_set_1_, E p_set_2_) {
         E e = this.field_216871_b.set(p_set_1_, p_set_2_);
         p_set_2_.list = AbstractList.this;
         return e;
      }

      public void add(int p_add_1_, E p_add_2_) {
         this.field_216871_b.add(p_add_1_, p_add_2_);
         p_add_2_.list = AbstractList.this;
      }

      public E remove(int p_remove_1_) {
         return (E)(this.field_216871_b.remove(p_remove_1_));
      }
   }
}