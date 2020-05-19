package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;

public interface INameable {
   ITextComponent getName();

   default boolean hasCustomName() {
      return this.getCustomName() != null;
   }

   default ITextComponent getDisplayName() {
      return this.getName();
   }

   @Nullable
   default ITextComponent getCustomName() {
      return null;
   }
}