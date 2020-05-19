package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MerchantInventory implements IInventory {
   private final IMerchant merchant;
   private final NonNullList<ItemStack> slots = NonNullList.withSize(3, ItemStack.EMPTY);
   @Nullable
   private MerchantOffer field_214026_c;
   private int currentRecipeIndex;
   private int exp;

   public MerchantInventory(IMerchant merchantIn) {
      this.merchant = merchantIn;
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.slots.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.slots) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      return this.slots.get(index);
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      ItemStack itemstack = this.slots.get(index);
      if (index == 2 && !itemstack.isEmpty()) {
         return ItemStackHelper.getAndSplit(this.slots, index, itemstack.getCount());
      } else {
         ItemStack itemstack1 = ItemStackHelper.getAndSplit(this.slots, index, count);
         if (!itemstack1.isEmpty() && this.inventoryResetNeededOnSlotChange(index)) {
            this.resetRecipeAndSlots();
         }

         return itemstack1;
      }
   }

   /**
    * if par1 slot has changed, does resetRecipeAndSlots need to be called?
    */
   private boolean inventoryResetNeededOnSlotChange(int slotIn) {
      return slotIn == 0 || slotIn == 1;
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.slots, index);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.slots.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      if (this.inventoryResetNeededOnSlotChange(index)) {
         this.resetRecipeAndSlots();
      }

   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(PlayerEntity player) {
      return this.merchant.getCustomer() == player;
   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   public void markDirty() {
      this.resetRecipeAndSlots();
   }

   public void resetRecipeAndSlots() {
      this.field_214026_c = null;
      ItemStack itemstack;
      ItemStack itemstack1;
      if (this.slots.get(0).isEmpty()) {
         itemstack = this.slots.get(1);
         itemstack1 = ItemStack.EMPTY;
      } else {
         itemstack = this.slots.get(0);
         itemstack1 = this.slots.get(1);
      }

      if (itemstack.isEmpty()) {
         this.setInventorySlotContents(2, ItemStack.EMPTY);
         this.exp = 0;
      } else {
         MerchantOffers merchantoffers = this.merchant.getOffers();
         if (!merchantoffers.isEmpty()) {
            MerchantOffer merchantoffer = merchantoffers.func_222197_a(itemstack, itemstack1, this.currentRecipeIndex);
            if (merchantoffer == null || merchantoffer.hasNoUsesLeft()) {
               this.field_214026_c = merchantoffer;
               merchantoffer = merchantoffers.func_222197_a(itemstack1, itemstack, this.currentRecipeIndex);
            }

            if (merchantoffer != null && !merchantoffer.hasNoUsesLeft()) {
               this.field_214026_c = merchantoffer;
               this.setInventorySlotContents(2, merchantoffer.getCopyOfSellingStack());
               this.exp = merchantoffer.getGivenExp();
            } else {
               this.setInventorySlotContents(2, ItemStack.EMPTY);
               this.exp = 0;
            }
         }

         this.merchant.verifySellingItem(this.getStackInSlot(2));
      }
   }

   @Nullable
   public MerchantOffer func_214025_g() {
      return this.field_214026_c;
   }

   public void setCurrentRecipeIndex(int currentRecipeIndexIn) {
      this.currentRecipeIndex = currentRecipeIndexIn;
      this.resetRecipeAndSlots();
   }

   public void clear() {
      this.slots.clear();
   }

   @OnlyIn(Dist.CLIENT)
   public int getClientSideExp() {
      return this.exp;
   }
}