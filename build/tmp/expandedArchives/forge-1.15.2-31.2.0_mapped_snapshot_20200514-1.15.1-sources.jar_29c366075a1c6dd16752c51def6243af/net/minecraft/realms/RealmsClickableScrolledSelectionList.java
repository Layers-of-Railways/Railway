package net.minecraft.realms;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsClickableScrolledSelectionList extends RealmsGuiEventListener {
   private final RealmsClickableScrolledSelectionListProxy proxy;

   public RealmsClickableScrolledSelectionList(int p_i46052_1_, int p_i46052_2_, int p_i46052_3_, int p_i46052_4_, int p_i46052_5_) {
      this.proxy = new RealmsClickableScrolledSelectionListProxy(this, p_i46052_1_, p_i46052_2_, p_i46052_3_, p_i46052_4_, p_i46052_5_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.proxy.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public int width() {
      return this.proxy.getWidth();
   }

   protected void renderItem(int p_renderItem_1_, int p_renderItem_2_, int p_renderItem_3_, int p_renderItem_4_, Tezzelator p_renderItem_5_, int p_renderItem_6_, int p_renderItem_7_) {
   }

   public void renderItem(int p_renderItem_1_, int p_renderItem_2_, int p_renderItem_3_, int p_renderItem_4_, int p_renderItem_5_, int p_renderItem_6_) {
      this.renderItem(p_renderItem_1_, p_renderItem_2_, p_renderItem_3_, p_renderItem_4_, Tezzelator.instance, p_renderItem_5_, p_renderItem_6_);
   }

   public int getItemCount() {
      return 0;
   }

   public boolean selectItem(int p_selectItem_1_, int p_selectItem_2_, double p_selectItem_3_, double p_selectItem_5_) {
      return true;
   }

   public boolean isSelectedItem(int p_isSelectedItem_1_) {
      return false;
   }

   public void renderBackground() {
   }

   public int getMaxPosition() {
      return 0;
   }

   public int getScrollbarPosition() {
      return this.proxy.getWidth() / 2 + 124;
   }

   public IGuiEventListener getProxy() {
      return this.proxy;
   }

   public void scroll(int p_scroll_1_) {
      this.proxy.scroll(p_scroll_1_);
   }

   public int getScroll() {
      return this.proxy.getScroll();
   }

   protected void renderList(int p_renderList_1_, int p_renderList_2_, int p_renderList_3_, int p_renderList_4_) {
   }

   public void itemClicked(int p_itemClicked_1_, int p_itemClicked_2_, double p_itemClicked_3_, double p_itemClicked_5_, int p_itemClicked_7_) {
   }

   public void renderSelected(int p_renderSelected_1_, int p_renderSelected_2_, int p_renderSelected_3_, Tezzelator p_renderSelected_4_) {
   }

   public void setLeftPos(int p_setLeftPos_1_) {
      this.proxy.setLeftPos(p_setLeftPos_1_);
   }

   public int y0() {
      return this.proxy.y0();
   }

   public int y1() {
      return this.proxy.y1();
   }

   public int headerHeight() {
      return this.proxy.headerHeight();
   }

   public double yo() {
      return this.proxy.yo();
   }

   public int itemHeight() {
      return this.proxy.itemHeight();
   }

   public boolean isVisible() {
      return this.proxy.isVisible();
   }
}