package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LecternContainer extends Container {
   private final IInventory field_217018_c;
   private final IIntArray field_217019_d;

   public LecternContainer(int p_i50075_1_) {
      this(p_i50075_1_, new Inventory(1), new IntArray(1));
   }

   public LecternContainer(int p_i50076_1_, IInventory p_i50076_2_, IIntArray p_i50076_3_) {
      super(ContainerType.LECTERN, p_i50076_1_);
      assertInventorySize(p_i50076_2_, 1);
      assertIntArraySize(p_i50076_3_, 1);
      this.field_217018_c = p_i50076_2_;
      this.field_217019_d = p_i50076_3_;
      this.addSlot(new Slot(p_i50076_2_, 0, 0, 0) {
         /**
          * Called when the stack in a Slot changes
          */
         public void onSlotChanged() {
            super.onSlotChanged();
            LecternContainer.this.onCraftMatrixChanged(this.inventory);
         }
      });
      this.trackIntArray(p_i50076_3_);
   }

   /**
    * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
    */
   public boolean enchantItem(PlayerEntity playerIn, int id) {
      if (id >= 100) {
         int k = id - 100;
         this.updateProgressBar(0, k);
         return true;
      } else {
         switch(id) {
         case 1:
            int j = this.field_217019_d.get(0);
            this.updateProgressBar(0, j - 1);
            return true;
         case 2:
            int i = this.field_217019_d.get(0);
            this.updateProgressBar(0, i + 1);
            return true;
         case 3:
            if (!playerIn.isAllowEdit()) {
               return false;
            }

            ItemStack itemstack = this.field_217018_c.removeStackFromSlot(0);
            this.field_217018_c.markDirty();
            if (!playerIn.inventory.addItemStackToInventory(itemstack)) {
               playerIn.dropItem(itemstack, false);
            }

            return true;
         default:
            return false;
         }
      }
   }

   public void updateProgressBar(int id, int data) {
      super.updateProgressBar(id, data);
      this.detectAndSendChanges();
   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(PlayerEntity playerIn) {
      return this.field_217018_c.isUsableByPlayer(playerIn);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getBook() {
      return this.field_217018_c.getStackInSlot(0);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPage() {
      return this.field_217019_d.get(0);
   }
}