package net.minecraft.client.gui.widget.list;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractOptionList<E extends AbstractOptionList.Entry<E>> extends AbstractList<E> {
   public AbstractOptionList(Minecraft p_i51139_1_, int p_i51139_2_, int p_i51139_3_, int p_i51139_4_, int p_i51139_5_, int p_i51139_6_) {
      super(p_i51139_1_, p_i51139_2_, p_i51139_3_, p_i51139_4_, p_i51139_5_, p_i51139_6_);
   }

   public boolean changeFocus(boolean p_changeFocus_1_) {
      boolean flag = super.changeFocus(p_changeFocus_1_);
      if (flag) {
         this.ensureVisible((E)this.getFocused());
      }

      return flag;
   }

   protected boolean isSelectedItem(int p_isSelectedItem_1_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry<E extends AbstractOptionList.Entry<E>> extends AbstractList.AbstractListEntry<E> implements INestedGuiEventHandler {
      @Nullable
      private IGuiEventListener field_214380_a;
      private boolean field_214381_b;

      public boolean isDragging() {
         return this.field_214381_b;
      }

      public void setDragging(boolean p_setDragging_1_) {
         this.field_214381_b = p_setDragging_1_;
      }

      public void setFocused(@Nullable IGuiEventListener p_setFocused_1_) {
         this.field_214380_a = p_setFocused_1_;
      }

      @Nullable
      public IGuiEventListener getFocused() {
         return this.field_214380_a;
      }
   }
}