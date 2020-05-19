package net.minecraft.inventory.container;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ShulkerBoxSlot extends Slot {
   public ShulkerBoxSlot(IInventory inventoryIn, int slotIndexIn, int xPosition, int yPosition) {
      super(inventoryIn, slotIndexIn, xPosition, yPosition);
   }

   /**
    * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
    */
   public boolean isItemValid(ItemStack stack) {
      return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
   }
}