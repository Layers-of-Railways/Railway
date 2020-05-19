package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IInventory extends IClearable {
   /**
    * Returns the number of slots in the inventory.
    */
   int getSizeInventory();

   boolean isEmpty();

   /**
    * Returns the stack in the given slot.
    */
   ItemStack getStackInSlot(int index);

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   ItemStack decrStackSize(int index, int count);

   /**
    * Removes a stack from the given slot and returns it.
    */
   ItemStack removeStackFromSlot(int index);

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   void setInventorySlotContents(int index, ItemStack stack);

   /**
    * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    */
   default int getInventoryStackLimit() {
      return 64;
   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   void markDirty();

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   boolean isUsableByPlayer(PlayerEntity player);

   default void openInventory(PlayerEntity player) {
   }

   default void closeInventory(PlayerEntity player) {
   }

   /**
    * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
    * guis use Slot.isItemValid
    */
   default boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   /**
    * Returns the total amount of the specified item in this inventory. This method does not check for nbt.
    */
   default int count(Item itemIn) {
      int i = 0;

      for(int j = 0; j < this.getSizeInventory(); ++j) {
         ItemStack itemstack = this.getStackInSlot(j);
         if (itemstack.getItem().equals(itemIn)) {
            i += itemstack.getCount();
         }
      }

      return i;
   }

   /**
    * Returns true if any item from the passed set exists in this inventory.
    */
   default boolean hasAny(Set<Item> set) {
      for(int i = 0; i < this.getSizeInventory(); ++i) {
         ItemStack itemstack = this.getStackInSlot(i);
         if (set.contains(itemstack.getItem()) && itemstack.getCount() > 0) {
            return true;
         }
      }

      return false;
   }
}