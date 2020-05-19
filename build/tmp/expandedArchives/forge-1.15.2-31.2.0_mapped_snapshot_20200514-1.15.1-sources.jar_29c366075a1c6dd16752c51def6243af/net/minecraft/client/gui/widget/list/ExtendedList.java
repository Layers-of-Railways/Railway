package net.minecraft.client.gui.widget.list;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ExtendedList<E extends AbstractList.AbstractListEntry<E>> extends AbstractList<E> {
   private boolean inFocus;

   public ExtendedList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
   }

   public boolean changeFocus(boolean p_changeFocus_1_) {
      if (!this.inFocus && this.getItemCount() == 0) {
         return false;
      } else {
         this.inFocus = !this.inFocus;
         if (this.inFocus && this.getSelected() == null && this.getItemCount() > 0) {
            this.moveSelection(1);
         } else if (this.inFocus && this.getSelected() != null) {
            this.moveSelection(0);
         }

         return this.inFocus;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class AbstractListEntry<E extends ExtendedList.AbstractListEntry<E>> extends AbstractList.AbstractListEntry<E> {
      public boolean changeFocus(boolean p_changeFocus_1_) {
         return false;
      }
   }
}