package net.minecraft.inventory.container;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.storage.MapData;

public class CartographyContainer extends Container {
   private final IWorldPosCallable field_216999_d;
   private boolean field_217000_e;
   private long field_226605_f_;
   public final IInventory field_216998_c = new Inventory(2) {
      /**
       * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
       * it hasn't changed and skip it.
       */
      public void markDirty() {
         CartographyContainer.this.onCraftMatrixChanged(this);
         super.markDirty();
      }
   };
   private final CraftResultInventory field_217001_f = new CraftResultInventory() {
      /**
       * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
       * it hasn't changed and skip it.
       */
      public void markDirty() {
         CartographyContainer.this.onCraftMatrixChanged(this);
         super.markDirty();
      }
   };

   public CartographyContainer(int p_i50093_1_, PlayerInventory p_i50093_2_) {
      this(p_i50093_1_, p_i50093_2_, IWorldPosCallable.DUMMY);
   }

   public CartographyContainer(int p_i50094_1_, PlayerInventory p_i50094_2_, final IWorldPosCallable p_i50094_3_) {
      super(ContainerType.field_226625_v_, p_i50094_1_);
      this.field_216999_d = p_i50094_3_;
      this.addSlot(new Slot(this.field_216998_c, 0, 15, 15) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return stack.getItem() == Items.FILLED_MAP;
         }
      });
      this.addSlot(new Slot(this.field_216998_c, 1, 15, 52) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            Item item = stack.getItem();
            return item == Items.PAPER || item == Items.MAP || item == Items.GLASS_PANE;
         }
      });
      this.addSlot(new Slot(this.field_217001_f, 2, 145, 39) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return false;
         }

         /**
          * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
          * stack.
          */
         public ItemStack decrStackSize(int amount) {
            ItemStack itemstack = super.decrStackSize(amount);
            ItemStack itemstack1 = p_i50094_3_.apply((p_216936_2_, p_216936_3_) -> {
               if (!CartographyContainer.this.field_217000_e && CartographyContainer.this.field_216998_c.getStackInSlot(1).getItem() == Items.GLASS_PANE) {
                  ItemStack itemstack2 = FilledMapItem.func_219992_b(p_216936_2_, CartographyContainer.this.field_216998_c.getStackInSlot(0));
                  if (itemstack2 != null) {
                     itemstack2.setCount(1);
                     return itemstack2;
                  }
               }

               return itemstack;
            }).orElse(itemstack);
            CartographyContainer.this.field_216998_c.decrStackSize(0, 1);
            CartographyContainer.this.field_216998_c.decrStackSize(1, 1);
            return itemstack1;
         }

         /**
          * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases
          * an internal count then calls onCrafting(item).
          */
         protected void onCrafting(ItemStack stack, int amount) {
            this.decrStackSize(amount);
            super.onCrafting(stack, amount);
         }

         public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
            stack.getItem().onCreated(stack, thePlayer.world, thePlayer);
            p_i50094_3_.consume((p_216935_1_, p_216935_2_) -> {
               long l = p_216935_1_.getGameTime();
               if (CartographyContainer.this.field_226605_f_ != l) {
                  p_216935_1_.playSound((PlayerEntity)null, p_216935_2_, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  CartographyContainer.this.field_226605_f_ = l;
               }

            });
            return super.onTake(thePlayer, stack);
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50094_2_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50094_2_, k, 8 + k * 18, 142));
      }

   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(PlayerEntity playerIn) {
      return isWithinUsableDistance(this.field_216999_d, playerIn, Blocks.CARTOGRAPHY_TABLE);
   }

   /**
    * Callback for when the crafting matrix is changed.
    */
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      ItemStack itemstack = this.field_216998_c.getStackInSlot(0);
      ItemStack itemstack1 = this.field_216998_c.getStackInSlot(1);
      ItemStack itemstack2 = this.field_217001_f.getStackInSlot(2);
      if (itemstack2.isEmpty() || !itemstack.isEmpty() && !itemstack1.isEmpty()) {
         if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
            this.func_216993_a(itemstack, itemstack1, itemstack2);
         }
      } else {
         this.field_217001_f.removeStackFromSlot(2);
      }

   }

   private void func_216993_a(ItemStack p_216993_1_, ItemStack p_216993_2_, ItemStack p_216993_3_) {
      this.field_216999_d.consume((p_216996_4_, p_216996_5_) -> {
         Item item = p_216993_2_.getItem();
         MapData mapdata = FilledMapItem.getData(p_216993_1_, p_216996_4_);
         if (mapdata != null) {
            ItemStack itemstack;
            if (item == Items.PAPER && !mapdata.locked && mapdata.scale < 4) {
               itemstack = p_216993_1_.copy();
               itemstack.setCount(1);
               itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
               this.detectAndSendChanges();
            } else if (item == Items.GLASS_PANE && !mapdata.locked) {
               itemstack = p_216993_1_.copy();
               itemstack.setCount(1);
               this.detectAndSendChanges();
            } else {
               if (item != Items.MAP) {
                  this.field_217001_f.removeStackFromSlot(2);
                  this.detectAndSendChanges();
                  return;
               }

               itemstack = p_216993_1_.copy();
               itemstack.setCount(2);
               this.detectAndSendChanges();
            }

            if (!ItemStack.areItemStacksEqual(itemstack, p_216993_3_)) {
               this.field_217001_f.setInventorySlotContents(2, itemstack);
               this.detectAndSendChanges();
            }

         }
      });
   }

   /**
    * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
    * null for the initial slot that was double-clicked.
    */
   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return slotIn.inventory != this.field_217001_f && super.canMergeSlot(stack, slotIn);
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
         ItemStack itemstack2 = itemstack1;
         Item item = itemstack1.getItem();
         itemstack = itemstack1.copy();
         if (index == 2) {
            if (this.field_216998_c.getStackInSlot(1).getItem() == Items.GLASS_PANE) {
               itemstack2 = this.field_216999_d.apply((p_216997_2_, p_216997_3_) -> {
                  ItemStack itemstack3 = FilledMapItem.func_219992_b(p_216997_2_, this.field_216998_c.getStackInSlot(0));
                  if (itemstack3 != null) {
                     itemstack3.setCount(1);
                     return itemstack3;
                  } else {
                     return itemstack1;
                  }
               }).orElse(itemstack1);
            }

            item.onCreated(itemstack2, playerIn.world, playerIn);
            if (!this.mergeItemStack(itemstack2, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack2, itemstack);
         } else if (index != 1 && index != 0) {
            if (item == Items.FILLED_MAP) {
               if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (item != Items.PAPER && item != Items.MAP && item != Items.GLASS_PANE) {
               if (index >= 3 && index < 30) {
                  if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack2.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         }

         slot.onSlotChanged();
         if (itemstack2.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         this.field_217000_e = true;
         slot.onTake(playerIn, itemstack2);
         this.field_217000_e = false;
         this.detectAndSendChanges();
      }

      return itemstack;
   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(PlayerEntity playerIn) {
      super.onContainerClosed(playerIn);
      this.field_217001_f.removeStackFromSlot(2);
      this.field_216999_d.consume((p_216995_2_, p_216995_3_) -> {
         this.clearContainer(playerIn, playerIn.world, this.field_216998_c);
      });
   }
}