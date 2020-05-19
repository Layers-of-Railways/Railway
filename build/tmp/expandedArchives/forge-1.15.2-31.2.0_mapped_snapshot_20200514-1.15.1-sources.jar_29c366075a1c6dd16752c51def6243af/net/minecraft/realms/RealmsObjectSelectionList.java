package net.minecraft.realms;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsObjectSelectionList<E extends RealmListEntry> extends RealmsGuiEventListener {
   private final RealmsObjectSelectionListProxy proxy;

   public RealmsObjectSelectionList(int p_i50516_1_, int p_i50516_2_, int p_i50516_3_, int p_i50516_4_, int p_i50516_5_) {
      this.proxy = new RealmsObjectSelectionListProxy(this, p_i50516_1_, p_i50516_2_, p_i50516_3_, p_i50516_4_, p_i50516_5_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.proxy.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void addEntry(E p_addEntry_1_) {
      this.proxy.addEntry(p_addEntry_1_);
   }

   public void remove(int p_remove_1_) {
      this.proxy.remove(p_remove_1_);
   }

   public void clear() {
      this.proxy.clear();
   }

   public boolean removeEntry(E p_removeEntry_1_) {
      return this.proxy.removeEntry(p_removeEntry_1_);
   }

   public int width() {
      return this.proxy.getWidth();
   }

   protected void renderItem(int p_renderItem_1_, int p_renderItem_2_, int p_renderItem_3_, int p_renderItem_4_, Tezzelator p_renderItem_5_, int p_renderItem_6_, int p_renderItem_7_) {
   }

   public void setLeftPos(int p_setLeftPos_1_) {
      this.proxy.setLeftPos(p_setLeftPos_1_);
   }

   public void renderItem(int p_renderItem_1_, int p_renderItem_2_, int p_renderItem_3_, int p_renderItem_4_, int p_renderItem_5_, int p_renderItem_6_) {
      this.renderItem(p_renderItem_1_, p_renderItem_2_, p_renderItem_3_, p_renderItem_4_, Tezzelator.instance, p_renderItem_5_, p_renderItem_6_);
   }

   public void setSelected(int p_setSelected_1_) {
      this.proxy.setSelectedItem(p_setSelected_1_);
   }

   public void itemClicked(int p_itemClicked_1_, int p_itemClicked_2_, double p_itemClicked_3_, double p_itemClicked_5_, int p_itemClicked_7_) {
   }

   public int getItemCount() {
      return this.proxy.getItemCount();
   }

   public void renderBackground() {
   }

   public int getMaxPosition() {
      return 0;
   }

   public int getScrollbarPosition() {
      return this.proxy.getRowLeft() + this.proxy.getRowWidth();
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

   public int itemHeight() {
      return this.proxy.itemHeight();
   }

   public void scroll(int p_scroll_1_) {
      this.proxy.setScrollAmount((double)p_scroll_1_);
   }

   public int getScroll() {
      return (int)this.proxy.getScrollAmount();
   }

   public IGuiEventListener getProxy() {
      return this.proxy;
   }

   public int getRowWidth() {
      return (int)((double)this.width() * 0.6D);
   }

   public abstract boolean isFocused();

   public void selectItem(int p_selectItem_1_) {
      this.setSelected(p_selectItem_1_);
   }

   @Nullable
   public E getSelected() {
      return (E)(this.proxy.getSelected());
   }

   public List<E> children() {
      return this.proxy.children();
   }

   public void replaceEntries(Collection<E> p_replaceEntries_1_) {
      this.proxy.replaceEntries(p_replaceEntries_1_);
   }

   public int getRowTop(int p_getRowTop_1_) {
      return this.proxy.getRowTop(p_getRowTop_1_);
   }

   public int getRowLeft() {
      return this.proxy.getRowLeft();
   }
}