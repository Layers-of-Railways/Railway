package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DoubleSidedInventory implements IInventory {
   private final IInventory upperChest;
   private final IInventory lowerChest;

   public DoubleSidedInventory(IInventory p_i50399_1_, IInventory p_i50399_2_) {
      if (p_i50399_1_ == null) {
         p_i50399_1_ = p_i50399_2_;
      }

      if (p_i50399_2_ == null) {
         p_i50399_2_ = p_i50399_1_;
      }

      this.upperChest = p_i50399_1_;
      this.lowerChest = p_i50399_2_;
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
   }

   public boolean isEmpty() {
      return this.upperChest.isEmpty() && this.lowerChest.isEmpty();
   }

   /**
    * Return whether the given inventory is part of this large chest.
    */
   public boolean isPartOfLargeChest(IInventory inventoryIn) {
      return this.upperChest == inventoryIn || this.lowerChest == inventoryIn;
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      return index >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlot(index - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlot(index);
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      return index >= this.upperChest.getSizeInventory() ? this.lowerChest.decrStackSize(index - this.upperChest.getSizeInventory(), count) : this.upperChest.decrStackSize(index, count);
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      return index >= this.upperChest.getSizeInventory() ? this.lowerChest.removeStackFromSlot(index - this.upperChest.getSizeInventory()) : this.upperChest.removeStackFromSlot(index);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      if (index >= this.upperChest.getSizeInventory()) {
         this.lowerChest.setInventorySlotContents(index - this.upperChest.getSizeInventory(), stack);
      } else {
         this.upperChest.setInventorySlotContents(index, stack);
      }

   }

   /**
    * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    */
   public int getInventoryStackLimit() {
      return this.upperChest.getInventoryStackLimit();
   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   public void markDirty() {
      this.upperChest.markDirty();
      this.lowerChest.markDirty();
   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(PlayerEntity player) {
      return this.upperChest.isUsableByPlayer(player) && this.lowerChest.isUsableByPlayer(player);
   }

   public void openInventory(PlayerEntity player) {
      this.upperChest.openInventory(player);
      this.lowerChest.openInventory(player);
   }

   public void closeInventory(PlayerEntity player) {
      this.upperChest.closeInventory(player);
      this.lowerChest.closeInventory(player);
   }

   /**
    * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
    * guis use Slot.isItemValid
    */
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return index >= this.upperChest.getSizeInventory() ? this.lowerChest.isItemValidForSlot(index - this.upperChest.getSizeInventory(), stack) : this.upperChest.isItemValidForSlot(index, stack);
   }

   public void clear() {
      this.upperChest.clear();
      this.lowerChest.clear();
   }
}