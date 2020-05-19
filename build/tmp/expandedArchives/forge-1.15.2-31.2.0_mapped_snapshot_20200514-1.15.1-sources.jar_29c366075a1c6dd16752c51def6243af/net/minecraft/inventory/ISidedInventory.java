package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public interface ISidedInventory extends IInventory {
   int[] getSlotsForFace(Direction side);

   /**
    * Returns true if automation can insert the given item in the given slot from the given side.
    */
   boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction);

   /**
    * Returns true if automation can extract the given item in the given slot from the given side.
    */
   boolean canExtractItem(int index, ItemStack stack, Direction direction);
}