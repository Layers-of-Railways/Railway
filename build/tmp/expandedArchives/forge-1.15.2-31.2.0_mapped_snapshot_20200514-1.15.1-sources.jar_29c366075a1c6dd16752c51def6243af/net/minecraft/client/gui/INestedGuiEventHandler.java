package net.minecraft.client.gui;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface INestedGuiEventHandler extends IGuiEventListener {
   List<? extends IGuiEventListener> children();

   /**
    * Returns the first event listener that intersects with the mouse coordinates.
    */
   default Optional<IGuiEventListener> getEventListenerForPos(double mouseX, double mouseY) {
      for(IGuiEventListener iguieventlistener : this.children()) {
         if (iguieventlistener.isMouseOver(mouseX, mouseY)) {
            return Optional.of(iguieventlistener);
         }
      }

      return Optional.empty();
   }

   default boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      for(IGuiEventListener iguieventlistener : this.children()) {
         if (iguieventlistener.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            this.setFocused(iguieventlistener);
            if (p_mouseClicked_5_ == 0) {
               this.setDragging(true);
            }

            return true;
         }
      }

      return false;
   }

   default boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      this.setDragging(false);
      return this.getEventListenerForPos(p_mouseReleased_1_, p_mouseReleased_3_).filter((p_212931_5_) -> {
         return p_212931_5_.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }).isPresent();
   }

   default boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.getFocused() != null && this.isDragging() && p_mouseDragged_5_ == 0 ? this.getFocused().mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_) : false;
   }

   boolean isDragging();

   void setDragging(boolean p_setDragging_1_);

   default boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return this.getEventListenerForPos(p_mouseScrolled_1_, p_mouseScrolled_3_).filter((p_212929_6_) -> {
         return p_212929_6_.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
      }).isPresent();
   }

   default boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.getFocused() != null && this.getFocused().keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return this.getFocused() != null && this.getFocused().keyReleased(keyCode, scanCode, modifiers);
   }

   default boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.getFocused() != null && this.getFocused().charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   @Nullable
   IGuiEventListener getFocused();

   void setFocused(@Nullable IGuiEventListener p_setFocused_1_);

   default void setFocusedDefault(@Nullable IGuiEventListener eventListener) {
      this.setFocused(eventListener);
   }

   default void func_212932_b(@Nullable IGuiEventListener eventListener) {
      this.setFocused(eventListener);
   }

   default boolean changeFocus(boolean p_changeFocus_1_) {
      IGuiEventListener iguieventlistener = this.getFocused();
      boolean flag = iguieventlistener != null;
      if (flag && iguieventlistener.changeFocus(p_changeFocus_1_)) {
         return true;
      } else {
         List<? extends IGuiEventListener> list = this.children();
         int j = list.indexOf(iguieventlistener);
         int i;
         if (flag && j >= 0) {
            i = j + (p_changeFocus_1_ ? 1 : 0);
         } else if (p_changeFocus_1_) {
            i = 0;
         } else {
            i = list.size();
         }

         ListIterator<? extends IGuiEventListener> listiterator = list.listIterator(i);
         BooleanSupplier booleansupplier = p_changeFocus_1_ ? listiterator::hasNext : listiterator::hasPrevious;
         Supplier<? extends IGuiEventListener> supplier = p_changeFocus_1_ ? listiterator::next : listiterator::previous;

         while(booleansupplier.getAsBoolean()) {
            IGuiEventListener iguieventlistener1 = supplier.get();
            if (iguieventlistener1.changeFocus(p_changeFocus_1_)) {
               this.setFocused(iguieventlistener1);
               return true;
            }
         }

         this.setFocused((IGuiEventListener)null);
         return false;
      }
   }
}