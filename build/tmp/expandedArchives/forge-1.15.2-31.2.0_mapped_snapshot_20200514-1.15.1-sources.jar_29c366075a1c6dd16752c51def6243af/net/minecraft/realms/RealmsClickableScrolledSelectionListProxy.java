package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.SlotGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsClickableScrolledSelectionListProxy extends SlotGui {
   private final RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList;

   public RealmsClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList p_i50680_1_, int p_i50680_2_, int p_i50680_3_, int p_i50680_4_, int p_i50680_5_, int p_i50680_6_) {
      super(Minecraft.getInstance(), p_i50680_2_, p_i50680_3_, p_i50680_4_, p_i50680_5_, p_i50680_6_);
      this.realmsClickableScrolledSelectionList = p_i50680_1_;
   }

   public int getItemCount() {
      return this.realmsClickableScrolledSelectionList.getItemCount();
   }

   public boolean selectItem(int p_selectItem_1_, int p_selectItem_2_, double p_selectItem_3_, double p_selectItem_5_) {
      return this.realmsClickableScrolledSelectionList.selectItem(p_selectItem_1_, p_selectItem_2_, p_selectItem_3_, p_selectItem_5_);
   }

   public boolean isSelectedItem(int p_isSelectedItem_1_) {
      return this.realmsClickableScrolledSelectionList.isSelectedItem(p_isSelectedItem_1_);
   }

   public void renderBackground() {
      this.realmsClickableScrolledSelectionList.renderBackground();
   }

   public void renderItem(int p_renderItem_1_, int p_renderItem_2_, int p_renderItem_3_, int p_renderItem_4_, int p_renderItem_5_, int p_renderItem_6_, float p_renderItem_7_) {
      this.realmsClickableScrolledSelectionList.renderItem(p_renderItem_1_, p_renderItem_2_, p_renderItem_3_, p_renderItem_4_, p_renderItem_5_, p_renderItem_6_);
   }

   public int getWidth() {
      return this.width;
   }

   public int getMaxPosition() {
      return this.realmsClickableScrolledSelectionList.getMaxPosition();
   }

   public int getScrollbarPosition() {
      return this.realmsClickableScrolledSelectionList.getScrollbarPosition();
   }

   public void itemClicked(int p_itemClicked_1_, int p_itemClicked_2_, int p_itemClicked_3_, int p_itemClicked_4_, int p_itemClicked_5_) {
      this.realmsClickableScrolledSelectionList.itemClicked(p_itemClicked_1_, p_itemClicked_2_, (double)p_itemClicked_3_, (double)p_itemClicked_4_, p_itemClicked_5_);
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return this.realmsClickableScrolledSelectionList.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_) ? true : super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.realmsClickableScrolledSelectionList.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.realmsClickableScrolledSelectionList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.realmsClickableScrolledSelectionList.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_) ? true : super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }

   public void renderSelected(int p_renderSelected_1_, int p_renderSelected_2_, int p_renderSelected_3_, Tezzelator p_renderSelected_4_) {
      this.realmsClickableScrolledSelectionList.renderSelected(p_renderSelected_1_, p_renderSelected_2_, p_renderSelected_3_, p_renderSelected_4_);
   }

   public void renderList(int p_renderList_1_, int p_renderList_2_, int p_renderList_3_, int p_renderList_4_, float p_renderList_5_) {
      int i = this.getItemCount();

      for(int j = 0; j < i; ++j) {
         int k = p_renderList_2_ + j * this.itemHeight + this.headerHeight;
         int l = this.itemHeight - 4;
         if (k > this.y1 || k + l < this.y0) {
            this.updateItemPosition(j, p_renderList_1_, k, p_renderList_5_);
         }

         if (this.renderSelection && this.isSelectedItem(j)) {
            this.renderSelected(this.width, k, l, Tezzelator.instance);
         }

         this.renderItem(j, p_renderList_1_, k, l, p_renderList_3_, p_renderList_4_, p_renderList_5_);
      }

   }

   public int y0() {
      return this.y0;
   }

   public int y1() {
      return this.y1;
   }

   public int headerHeight() {
      return this.headerHeight;
   }

   public double yo() {
      return this.yo;
   }

   public int itemHeight() {
      return this.itemHeight;
   }
}