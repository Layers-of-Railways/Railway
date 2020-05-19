package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.SlotGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsScrolledSelectionListProxy extends SlotGui {
   private final RealmsScrolledSelectionList realmsScrolledSelectionList;

   public RealmsScrolledSelectionListProxy(RealmsScrolledSelectionList p_i51131_1_, int p_i51131_2_, int p_i51131_3_, int p_i51131_4_, int p_i51131_5_, int p_i51131_6_) {
      super(Minecraft.getInstance(), p_i51131_2_, p_i51131_3_, p_i51131_4_, p_i51131_5_, p_i51131_6_);
      this.realmsScrolledSelectionList = p_i51131_1_;
   }

   public int getItemCount() {
      return this.realmsScrolledSelectionList.getItemCount();
   }

   public boolean selectItem(int p_selectItem_1_, int p_selectItem_2_, double p_selectItem_3_, double p_selectItem_5_) {
      return this.realmsScrolledSelectionList.selectItem(p_selectItem_1_, p_selectItem_2_, p_selectItem_3_, p_selectItem_5_);
   }

   public boolean isSelectedItem(int p_isSelectedItem_1_) {
      return this.realmsScrolledSelectionList.isSelectedItem(p_isSelectedItem_1_);
   }

   public void renderBackground() {
      this.realmsScrolledSelectionList.renderBackground();
   }

   public void renderItem(int p_renderItem_1_, int p_renderItem_2_, int p_renderItem_3_, int p_renderItem_4_, int p_renderItem_5_, int p_renderItem_6_, float p_renderItem_7_) {
      this.realmsScrolledSelectionList.renderItem(p_renderItem_1_, p_renderItem_2_, p_renderItem_3_, p_renderItem_4_, p_renderItem_5_, p_renderItem_6_);
   }

   public int getWidth() {
      return this.width;
   }

   public int getMaxPosition() {
      return this.realmsScrolledSelectionList.getMaxPosition();
   }

   public int getScrollbarPosition() {
      return this.realmsScrolledSelectionList.getScrollbarPosition();
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return this.realmsScrolledSelectionList.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_) ? true : super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.realmsScrolledSelectionList.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.realmsScrolledSelectionList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.realmsScrolledSelectionList.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }
}