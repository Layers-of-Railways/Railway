package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.EnderChestTileEntity;

public class EnderChestInventory extends Inventory {
   private EnderChestTileEntity associatedChest;

   public EnderChestInventory() {
      super(27);
   }

   public void setChestTileEntity(EnderChestTileEntity chestTileEntity) {
      this.associatedChest = chestTileEntity;
   }

   public void read(ListNBT p_70486_1_) {
      for(int i = 0; i < this.getSizeInventory(); ++i) {
         this.setInventorySlotContents(i, ItemStack.EMPTY);
      }

      for(int k = 0; k < p_70486_1_.size(); ++k) {
         CompoundNBT compoundnbt = p_70486_1_.getCompound(k);
         int j = compoundnbt.getByte("Slot") & 255;
         if (j >= 0 && j < this.getSizeInventory()) {
            this.setInventorySlotContents(j, ItemStack.read(compoundnbt));
         }
      }

   }

   public ListNBT write() {
      ListNBT listnbt = new ListNBT();

      for(int i = 0; i < this.getSizeInventory(); ++i) {
         ItemStack itemstack = this.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putByte("Slot", (byte)i);
            itemstack.write(compoundnbt);
            listnbt.add(compoundnbt);
         }
      }

      return listnbt;
   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(PlayerEntity player) {
      return this.associatedChest != null && !this.associatedChest.canBeUsed(player) ? false : super.isUsableByPlayer(player);
   }

   public void openInventory(PlayerEntity player) {
      if (this.associatedChest != null) {
         this.associatedChest.openChest();
      }

      super.openInventory(player);
   }

   public void closeInventory(PlayerEntity player) {
      if (this.associatedChest != null) {
         this.associatedChest.closeChest();
      }

      super.closeInventory(player);
      this.associatedChest = null;
   }
}