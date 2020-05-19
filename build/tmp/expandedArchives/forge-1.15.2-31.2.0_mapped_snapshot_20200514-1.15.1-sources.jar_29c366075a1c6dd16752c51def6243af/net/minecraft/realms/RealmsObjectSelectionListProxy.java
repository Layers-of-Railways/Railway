package net.minecraft.realms;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsObjectSelectionListProxy<E extends ExtendedList.AbstractListEntry<E>> extends ExtendedList<E> {
   private final RealmsObjectSelectionList realmsObjectSelectionList;

   public RealmsObjectSelectionListProxy(RealmsObjectSelectionList p_i50681_1_, int p_i50681_2_, int p_i50681_3_, int p_i50681_4_, int p_i50681_5_, int p_i50681_6_) {
      super(Minecraft.getInstance(), p_i50681_2_, p_i50681_3_, p_i50681_4_, p_i50681_5_, p_i50681_6_);
      this.realmsObjectSelectionList = p_i50681_1_;
   }

   public int getItemCount() {
      return super.getItemCount();
   }

   public void clear() {
      super.clearEntries();
   }

   public boolean isFocused() {
      return this.realmsObjectSelectionList.isFocused();
   }

   protected void setSelectedItem(int p_setSelectedItem_1_) {
      if (p_setSelectedItem_1_ == -1) {
         super.setSelected((E)null);
      } else if (super.getItemCount() != 0) {
         E e = super.getEntry(p_setSelectedItem_1_);
         super.setSelected(e);
      }

   }

   public void setSelected(@Nullable E p_setSelected_1_) {
      super.setSelected(p_setSelected_1_);
      this.realmsObjectSelectionList.selectItem(super.children().indexOf(p_setSelected_1_));
   }

   public void renderBackground() {
      this.realmsObjectSelectionList.renderBackground();
   }

   public int getWidth() {
      return this.width;
   }

   public int getMaxPosition() {
      return this.realmsObjectSelectionList.getMaxPosition();
   }

   public int getScrollbarPosition() {
      return this.realmsObjectSelectionList.getScrollbarPosition();
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return this.realmsObjectSelectionList.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_) ? true : super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
   }

   public int getRowWidth() {
      return this.realmsObjectSelectionList.getRowWidth();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.realmsObjectSelectionList.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.realmsObjectSelectionList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.realmsObjectSelectionList.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_) ? true : super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }

   protected final int addEntry(E p_addEntry_1_) {
      return super.addEntry(p_addEntry_1_);
   }

   public E remove(int p_remove_1_) {
      return (E)(super.remove(p_remove_1_));
   }

   public boolean removeEntry(E p_removeEntry_1_) {
      return super.removeEntry(p_removeEntry_1_);
   }

   public void setScrollAmount(double p_setScrollAmount_1_) {
      super.setScrollAmount(p_setScrollAmount_1_);
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

   public int itemHeight() {
      return this.itemHeight;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? true : this.realmsObjectSelectionList.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public void replaceEntries(Collection<E> p_replaceEntries_1_) {
      super.replaceEntries(p_replaceEntries_1_);
   }

   public int getRowTop(int p_getRowTop_1_) {
      return super.getRowTop(p_getRowTop_1_);
   }

   public int getRowLeft() {
      return super.getRowLeft();
   }
}