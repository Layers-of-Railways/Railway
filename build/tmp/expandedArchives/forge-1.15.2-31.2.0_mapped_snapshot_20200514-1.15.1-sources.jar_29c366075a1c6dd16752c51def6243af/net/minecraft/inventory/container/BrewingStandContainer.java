package net.minecraft.inventory.container;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BrewingStandContainer extends Container {
   private final IInventory tileBrewingStand;
   private final IIntArray field_216983_d;
   private final Slot slot;

   public BrewingStandContainer(int p_i50095_1_, PlayerInventory p_i50095_2_) {
      this(p_i50095_1_, p_i50095_2_, new Inventory(5), new IntArray(2));
   }

   public BrewingStandContainer(int p_i50096_1_, PlayerInventory p_i50096_2_, IInventory p_i50096_3_, IIntArray p_i50096_4_) {
      super(ContainerType.BREWING_STAND, p_i50096_1_);
      assertInventorySize(p_i50096_3_, 5);
      assertIntArraySize(p_i50096_4_, 2);
      this.tileBrewingStand = p_i50096_3_;
      this.field_216983_d = p_i50096_4_;
      this.addSlot(new BrewingStandContainer.PotionSlot(p_i50096_3_, 0, 56, 51));
      this.addSlot(new BrewingStandContainer.PotionSlot(p_i50096_3_, 1, 79, 58));
      this.addSlot(new BrewingStandContainer.PotionSlot(p_i50096_3_, 2, 102, 51));
      this.slot = this.addSlot(new BrewingStandContainer.IngredientSlot(p_i50096_3_, 3, 79, 17));
      this.addSlot(new BrewingStandContainer.FuelSlot(p_i50096_3_, 4, 17, 17));
      this.trackIntArray(p_i50096_4_);

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50096_2_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50096_2_, k, 8 + k * 18, 142));
      }

   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(PlayerEntity playerIn) {
      return this.tileBrewingStand.isUsableByPlayer(playerIn);
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
         if ((index < 0 || index > 2) && index != 3 && index != 4) {
            if (BrewingStandContainer.FuelSlot.isValidBrewingFuel(itemstack)) {
               if (this.mergeItemStack(itemstack1, 4, 5, false) || this.slot.isItemValid(itemstack1) && !this.mergeItemStack(itemstack1, 3, 4, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.slot.isItemValid(itemstack1)) {
               if (!this.mergeItemStack(itemstack1, 3, 4, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (BrewingStandContainer.PotionSlot.canHoldPotion(itemstack) && itemstack.getCount() == 1) {
               if (!this.mergeItemStack(itemstack1, 0, 3, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (index >= 5 && index < 32) {
               if (!this.mergeItemStack(itemstack1, 32, 41, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (index >= 32 && index < 41) {
               if (!this.mergeItemStack(itemstack1, 5, 32, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.mergeItemStack(itemstack1, 5, 41, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.mergeItemStack(itemstack1, 5, 41, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
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

   @OnlyIn(Dist.CLIENT)
   public int func_216982_e() {
      return this.field_216983_d.get(1);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_216981_f() {
      return this.field_216983_d.get(0);
   }

   static class FuelSlot extends Slot {
      public FuelSlot(IInventory iInventoryIn, int index, int xPosition, int yPosition) {
         super(iInventoryIn, index, xPosition, yPosition);
      }

      /**
       * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
       */
      public boolean isItemValid(ItemStack stack) {
         return isValidBrewingFuel(stack);
      }

      /**
       * Returns true if the given ItemStack is usable as a fuel in the brewing stand.
       */
      public static boolean isValidBrewingFuel(ItemStack itemStackIn) {
         return itemStackIn.getItem() == Items.BLAZE_POWDER;
      }

      /**
       * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
       * case of armor slots)
       */
      public int getSlotStackLimit() {
         return 64;
      }
   }

   static class IngredientSlot extends Slot {
      public IngredientSlot(IInventory iInventoryIn, int index, int xPosition, int yPosition) {
         super(iInventoryIn, index, xPosition, yPosition);
      }

      /**
       * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
       */
      public boolean isItemValid(ItemStack stack) {
         return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidIngredient(stack);
      }

      /**
       * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
       * case of armor slots)
       */
      public int getSlotStackLimit() {
         return 64;
      }
   }

   static class PotionSlot extends Slot {
      public PotionSlot(IInventory p_i47598_1_, int p_i47598_2_, int p_i47598_3_, int p_i47598_4_) {
         super(p_i47598_1_, p_i47598_2_, p_i47598_3_, p_i47598_4_);
      }

      /**
       * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
       */
      public boolean isItemValid(ItemStack stack) {
         return canHoldPotion(stack);
      }

      /**
       * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
       * case of armor slots)
       */
      public int getSlotStackLimit() {
         return 1;
      }

      public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
         Potion potion = PotionUtils.getPotionFromItem(stack);
         if (thePlayer instanceof ServerPlayerEntity) {
            net.minecraftforge.event.ForgeEventFactory.onPlayerBrewedPotion(thePlayer, stack);
            CriteriaTriggers.BREWED_POTION.trigger((ServerPlayerEntity)thePlayer, potion);
         }

         super.onTake(thePlayer, stack);
         return stack;
      }

      /**
       * Returns true if this itemstack can be filled with a potion
       */
      public static boolean canHoldPotion(ItemStack stack) {
         return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput(stack);
      }
   }
}