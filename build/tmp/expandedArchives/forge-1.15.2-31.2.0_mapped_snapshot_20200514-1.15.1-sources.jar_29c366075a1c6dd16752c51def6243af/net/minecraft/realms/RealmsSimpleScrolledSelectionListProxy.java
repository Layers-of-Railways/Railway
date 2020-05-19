package net.minecraft.realms;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.SlotGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsSimpleScrolledSelectionListProxy extends SlotGui {
   private final RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList;

   public RealmsSimpleScrolledSelectionListProxy(RealmsSimpleScrolledSelectionList p_i51162_1_, int p_i51162_2_, int p_i51162_3_, int p_i51162_4_, int p_i51162_5_, int p_i51162_6_) {
      super(Minecraft.getInstance(), p_i51162_2_, p_i51162_3_, p_i51162_4_, p_i51162_5_, p_i51162_6_);
      this.realmsSimpleScrolledSelectionList = p_i51162_1_;
   }

   public int getItemCount() {
      return this.realmsSimpleScrolledSelectionList.getItemCount();
   }

   public boolean selectItem(int p_selectItem_1_, int p_selectItem_2_, double p_selectItem_3_, double p_selectItem_5_) {
      return this.realmsSimpleScrolledSelectionList.selectItem(p_selectItem_1_, p_selectItem_2_, p_selectItem_3_, p_selectItem_5_);
   }

   public boolean isSelectedItem(int p_isSelectedItem_1_) {
      return this.realmsSimpleScrolledSelectionList.isSelectedItem(p_isSelectedItem_1_);
   }

   public void renderBackground() {
      this.realmsSimpleScrolledSelectionList.renderBackground();
   }

   public void renderItem(int p_renderItem_1_, int p_renderItem_2_, int p_renderItem_3_, int p_renderItem_4_, int p_renderItem_5_, int p_renderItem_6_, float p_renderItem_7_) {
      this.realmsSimpleScrolledSelectionList.renderItem(p_renderItem_1_, p_renderItem_2_, p_renderItem_3_, p_renderItem_4_, p_renderItem_5_, p_renderItem_6_);
   }

   public int getWidth() {
      return this.width;
   }

   public int getMaxPosition() {
      return this.realmsSimpleScrolledSelectionList.getMaxPosition();
   }

   public int getScrollbarPosition() {
      return this.realmsSimpleScrolledSelectionList.getScrollbarPosition();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.visible) {
         this.renderBackground();
         int i = this.getScrollbarPosition();
         int j = i + 6;
         this.capYPosition();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
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
         int i1 = this.getMaxScroll();
         if (i1 > 0) {
            int j1 = (this.y1 - this.y0) * (this.y1 - this.y0) / this.getMaxPosition();
            j1 = MathHelper.clamp(j1, 32, this.y1 - this.y0 - 8);
            int k1 = (int)this.yo * (this.y1 - this.y0 - j1) / i1 + this.y0;
            if (k1 < this.y0) {
               k1 = this.y0;
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)j, (double)this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)j, (double)this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)i, (double)this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)(k1 + j1), 0.0D).tex(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j, (double)(k1 + j1), 0.0D).tex(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j, (double)k1, 0.0D).tex(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)i, (double)k1, 0.0D).tex(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)(k1 + j1 - 1), 0.0D).tex(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)(j - 1), (double)(k1 + j1 - 1), 0.0D).tex(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)(j - 1), (double)k1, 0.0D).tex(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)i, (double)k1, 0.0D).tex(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            tessellator.draw();
         }

         this.renderDecorations(p_render_1_, p_render_2_);
         RenderSystem.enableTexture();
         RenderSystem.shadeModel(7424);
         RenderSystem.enableAlphaTest();
         RenderSystem.disableBlend();
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return this.realmsSimpleScrolledSelectionList.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_) ? true : super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.realmsSimpleScrolledSelectionList.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.realmsSimpleScrolledSelectionList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.realmsSimpleScrolledSelectionList.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }
}