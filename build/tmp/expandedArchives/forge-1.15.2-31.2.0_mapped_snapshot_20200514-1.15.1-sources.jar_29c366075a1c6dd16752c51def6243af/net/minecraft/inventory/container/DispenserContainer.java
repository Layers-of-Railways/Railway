package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class DispenserContainer extends Container {
   private final IInventory dispenserInventory;

   public DispenserContainer(int p_i50087_1_, PlayerInventory p_i50087_2_) {
      this(p_i50087_1_, p_i50087_2_, new Inventory(9));
   }

   public DispenserContainer(int p_i50088_1_, PlayerInventory p_i50088_2_, IInventory p_i50088_3_) {
      super(ContainerType.GENERIC_3X3, p_i50088_1_);
      assertInventorySize(p_i50088_3_, 9);
      this.dispenserInventory = p_i50088_3_;
      p_i50088_3_.openInventory(p_i50088_2_.player);

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlot(new Slot(p_i50088_3_, j + i * 3, 62 + j * 18, 17 + i * 18));
         }
      }

      for(int k = 0; k < 3; ++k) {
         for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(p_i50088_2_, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
         }
      }

      for(int l = 0; l < 9; ++l) {
         this.addSlot(new Slot(p_i50088_2_, l, 8 + l * 18, 142));
      }

   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(PlayerEntity playerIn) {
      return this.dispenserInventory.isUsableByPlayer(playerIn);
   }

   /**
    * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
    * inventory and the other inventory(s).
    */
   public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index < 9) {
            if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(playerIn, itemstack1);
      }

      return itemstack;
   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(PlayerEntity playerIn) {
      super.onContainerClosed(playerIn);
      this.dispenserInventory.closeInventory(playerIn);
   }
}