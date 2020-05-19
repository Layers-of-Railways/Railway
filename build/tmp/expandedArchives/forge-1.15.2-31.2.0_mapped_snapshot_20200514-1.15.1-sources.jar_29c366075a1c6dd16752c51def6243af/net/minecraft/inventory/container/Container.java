package net.minecraft.inventory.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Container {
   private final NonNullList<ItemStack> inventoryItemStacks = NonNullList.create();
   public final List<Slot> inventorySlots = Lists.newArrayList();
   private final List<IntReferenceHolder> trackedIntReferences = Lists.newArrayList();
   @Nullable
   private final ContainerType<?> containerType;
   public final int windowId;
   @OnlyIn(Dist.CLIENT)
   private short transactionID;
   private int dragMode = -1;
   private int dragEvent;
   private final Set<Slot> dragSlots = Sets.newHashSet();
   private final List<IContainerListener> listeners = Lists.newArrayList();
   private final Set<PlayerEntity> playerList = Sets.newHashSet();

   protected Container(@Nullable ContainerType<?> type, int id) {
      this.containerType = type;
      this.windowId = id;
   }

   protected static boolean isWithinUsableDistance(IWorldPosCallable worldPos, PlayerEntity playerIn, Block targetBlock) {
      return worldPos.applyOrElse((p_216960_2_, p_216960_3_) -> {
         return p_216960_2_.getBlockState(p_216960_3_).getBlock() != targetBlock ? false : playerIn.getDistanceSq((double)p_216960_3_.getX() + 0.5D, (double)p_216960_3_.getY() + 0.5D, (double)p_216960_3_.getZ() + 0.5D) <= 64.0D;
      }, true);
   }

   public ContainerType<?> getType() {
      if (this.containerType == null) {
         throw new UnsupportedOperationException("Unable to construct this menu by type");
      } else {
         return this.containerType;
      }
   }

   protected static void assertInventorySize(IInventory inventoryIn, int minSize) {
      int i = inventoryIn.getSizeInventory();
      if (i < minSize) {
         throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + minSize);
      }
   }

   protected static void assertIntArraySize(IIntArray intArrayIn, int minSize) {
      int i = intArrayIn.size();
      if (i < minSize) {
         throw new IllegalArgumentException("Container data count " + i + " is smaller than expected " + minSize);
      }
   }

   /**
    * Adds an item slot to this container
    */
   protected Slot addSlot(Slot slotIn) {
      slotIn.slotNumber = this.inventorySlots.size();
      this.inventorySlots.add(slotIn);
      this.inventoryItemStacks.add(ItemStack.EMPTY);
      return slotIn;
   }

   protected IntReferenceHolder trackInt(IntReferenceHolder intIn) {
      this.trackedIntReferences.add(intIn);
      return intIn;
   }

   protected void trackIntArray(IIntArray arrayIn) {
      for(int i = 0; i < arrayIn.size(); ++i) {
         this.trackInt(IntReferenceHolder.create(arrayIn, i));
      }

   }

   public void addListener(IContainerListener listener) {
      if (!this.listeners.contains(listener)) {
         this.listeners.add(listener);
         listener.sendAllContents(this, this.getInventory());
         this.detectAndSendChanges();
      }
   }

   /**
    * Remove the given Listener. Method name is for legacy.
    */
   @OnlyIn(Dist.CLIENT)
   public void removeListener(IContainerListener listener) {
      this.listeners.remove(listener);
   }

   /**
    * returns a list if itemStacks, for each slot.
    */
   public NonNullList<ItemStack> getInventory() {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(int i = 0; i < this.inventorySlots.size(); ++i) {
         nonnulllist.add(this.inventorySlots.get(i).getStack());
      }

      return nonnulllist;
   }

   /**
    * Looks for changes made in the container, sends them to every listener.
    */
   public void detectAndSendChanges() {
      for(int i = 0; i < this.inventorySlots.size(); ++i) {
         ItemStack itemstack = this.inventorySlots.get(i).getStack();
         ItemStack itemstack1 = this.inventoryItemStacks.get(i);
         if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
            boolean clientStackChanged = !itemstack1.equals(itemstack, true);
            itemstack1 = itemstack.copy();
            this.inventoryItemStacks.set(i, itemstack1);

            if (clientStackChanged)
            for(IContainerListener icontainerlistener : this.listeners) {
               icontainerlistener.sendSlotContents(this, i, itemstack1);
            }
         }
      }

      for(int j = 0; j < this.trackedIntReferences.size(); ++j) {
         IntReferenceHolder intreferenceholder = this.trackedIntReferences.get(j);
         if (intreferenceholder.isDirty()) {
            for(IContainerListener icontainerlistener1 : this.listeners) {
               icontainerlistener1.sendWindowProperty(this, j, intreferenceholder.get());
            }
         }
      }

   }

   /**
    * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
    */
   public boolean enchantItem(PlayerEntity playerIn, int id) {
      return false;
   }

   public Slot getSlot(int slotId) {
      return this.inventorySlots.get(slotId);
   }

   /**
    * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
    * inventory and the other inventory(s).
    */
   public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
      Slot slot = this.inventorySlots.get(index);
      return slot != null ? slot.getStack() : ItemStack.EMPTY;
   }

   public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
      ItemStack itemstack = ItemStack.EMPTY;
      PlayerInventory playerinventory = player.inventory;
      if (clickTypeIn == ClickType.QUICK_CRAFT) {
         int j1 = this.dragEvent;
         this.dragEvent = getDragEvent(dragType);
         if ((j1 != 1 || this.dragEvent != 2) && j1 != this.dragEvent) {
            this.resetDrag();
         } else if (playerinventory.getItemStack().isEmpty()) {
            this.resetDrag();
         } else if (this.dragEvent == 0) {
            this.dragMode = extractDragMode(dragType);
            if (isValidDragMode(this.dragMode, player)) {
               this.dragEvent = 1;
               this.dragSlots.clear();
            } else {
               this.resetDrag();
            }
         } else if (this.dragEvent == 1) {
            Slot slot7 = this.inventorySlots.get(slotId);
            ItemStack itemstack12 = playerinventory.getItemStack();
            if (slot7 != null && canAddItemToSlot(slot7, itemstack12, true) && slot7.isItemValid(itemstack12) && (this.dragMode == 2 || itemstack12.getCount() > this.dragSlots.size()) && this.canDragIntoSlot(slot7)) {
               this.dragSlots.add(slot7);
            }
         } else if (this.dragEvent == 2) {
            if (!this.dragSlots.isEmpty()) {
               ItemStack itemstack9 = playerinventory.getItemStack().copy();
               int k1 = playerinventory.getItemStack().getCount();

               for(Slot slot8 : this.dragSlots) {
                  ItemStack itemstack13 = playerinventory.getItemStack();
                  if (slot8 != null && canAddItemToSlot(slot8, itemstack13, true) && slot8.isItemValid(itemstack13) && (this.dragMode == 2 || itemstack13.getCount() >= this.dragSlots.size()) && this.canDragIntoSlot(slot8)) {
                     ItemStack itemstack14 = itemstack9.copy();
                     int j3 = slot8.getHasStack() ? slot8.getStack().getCount() : 0;
                     computeStackSize(this.dragSlots, this.dragMode, itemstack14, j3);
                     int k3 = Math.min(itemstack14.getMaxStackSize(), slot8.getItemStackLimit(itemstack14));
                     if (itemstack14.getCount() > k3) {
                        itemstack14.setCount(k3);
                     }

                     k1 -= itemstack14.getCount() - j3;
                     slot8.putStack(itemstack14);
                  }
               }

               itemstack9.setCount(k1);
               playerinventory.setItemStack(itemstack9);
            }

            this.resetDrag();
         } else {
            this.resetDrag();
         }
      } else if (this.dragEvent != 0) {
         this.resetDrag();
      } else if ((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1)) {
         if (slotId == -999) {
            if (!playerinventory.getItemStack().isEmpty()) {
               if (dragType == 0) {
                  player.dropItem(playerinventory.getItemStack(), true);
                  playerinventory.setItemStack(ItemStack.EMPTY);
               }

               if (dragType == 1) {
                  player.dropItem(playerinventory.getItemStack().split(1), true);
               }
            }
         } else if (clickTypeIn == ClickType.QUICK_MOVE) {
            if (slotId < 0) {
               return ItemStack.EMPTY;
            }

            Slot slot5 = this.inventorySlots.get(slotId);
            if (slot5 == null || !slot5.canTakeStack(player)) {
               return ItemStack.EMPTY;
            }

            for(ItemStack itemstack7 = this.transferStackInSlot(player, slotId); !itemstack7.isEmpty() && ItemStack.areItemsEqual(slot5.getStack(), itemstack7); itemstack7 = this.transferStackInSlot(player, slotId)) {
               itemstack = itemstack7.copy();
            }
         } else {
            if (slotId < 0) {
               return ItemStack.EMPTY;
            }

            Slot slot6 = this.inventorySlots.get(slotId);
            if (slot6 != null) {
               ItemStack itemstack8 = slot6.getStack();
               ItemStack itemstack11 = playerinventory.getItemStack();
               if (!itemstack8.isEmpty()) {
                  itemstack = itemstack8.copy();
               }

               if (itemstack8.isEmpty()) {
                  if (!itemstack11.isEmpty() && slot6.isItemValid(itemstack11)) {
                     int j2 = dragType == 0 ? itemstack11.getCount() : 1;
                     if (j2 > slot6.getItemStackLimit(itemstack11)) {
                        j2 = slot6.getItemStackLimit(itemstack11);
                     }

                     slot6.putStack(itemstack11.split(j2));
                  }
               } else if (slot6.canTakeStack(player)) {
                  if (itemstack11.isEmpty()) {
                     if (itemstack8.isEmpty()) {
                        slot6.putStack(ItemStack.EMPTY);
                        playerinventory.setItemStack(ItemStack.EMPTY);
                     } else {
                        int k2 = dragType == 0 ? itemstack8.getCount() : (itemstack8.getCount() + 1) / 2;
                        playerinventory.setItemStack(slot6.decrStackSize(k2));
                        if (itemstack8.isEmpty()) {
                           slot6.putStack(ItemStack.EMPTY);
                        }

                        slot6.onTake(player, playerinventory.getItemStack());
                     }
                  } else if (slot6.isItemValid(itemstack11)) {
                     if (areItemsAndTagsEqual(itemstack8, itemstack11)) {
                        int l2 = dragType == 0 ? itemstack11.getCount() : 1;
                        if (l2 > slot6.getItemStackLimit(itemstack11) - itemstack8.getCount()) {
                           l2 = slot6.getItemStackLimit(itemstack11) - itemstack8.getCount();
                        }

                        if (l2 > itemstack11.getMaxStackSize() - itemstack8.getCount()) {
                           l2 = itemstack11.getMaxStackSize() - itemstack8.getCount();
                        }

                        itemstack11.shrink(l2);
                        itemstack8.grow(l2);
                     } else if (itemstack11.getCount() <= slot6.getItemStackLimit(itemstack11)) {
                        slot6.putStack(itemstack11);
                        playerinventory.setItemStack(itemstack8);
                     }
                  } else if (itemstack11.getMaxStackSize() > 1 && areItemsAndTagsEqual(itemstack8, itemstack11) && !itemstack8.isEmpty()) {
                     int i3 = itemstack8.getCount();
                     if (i3 + itemstack11.getCount() <= itemstack11.getMaxStackSize()) {
                        itemstack11.grow(i3);
                        itemstack8 = slot6.decrStackSize(i3);
                        if (itemstack8.isEmpty()) {
                           slot6.putStack(ItemStack.EMPTY);
                        }

                        slot6.onTake(player, playerinventory.getItemStack());
                     }
                  }
               }

               slot6.onSlotChanged();
            }
         }
      } else if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9) {
         Slot slot4 = this.inventorySlots.get(slotId);
         ItemStack itemstack6 = playerinventory.getStackInSlot(dragType);
         ItemStack itemstack10 = slot4.getStack();
         if (!itemstack6.isEmpty() || !itemstack10.isEmpty()) {
            if (itemstack6.isEmpty()) {
               if (slot4.canTakeStack(player)) {
                  playerinventory.setInventorySlotContents(dragType, itemstack10);
                  slot4.onSwapCraft(itemstack10.getCount());
                  slot4.putStack(ItemStack.EMPTY);
                  slot4.onTake(player, itemstack10);
               }
            } else if (itemstack10.isEmpty()) {
               if (slot4.isItemValid(itemstack6)) {
                  int l1 = slot4.getItemStackLimit(itemstack6);
                  if (itemstack6.getCount() > l1) {
                     slot4.putStack(itemstack6.split(l1));
                  } else {
                     slot4.putStack(itemstack6);
                     playerinventory.setInventorySlotContents(dragType, ItemStack.EMPTY);
                  }
               }
            } else if (slot4.canTakeStack(player) && slot4.isItemValid(itemstack6)) {
               int i2 = slot4.getItemStackLimit(itemstack6);
               if (itemstack6.getCount() > i2) {
                  slot4.putStack(itemstack6.split(i2));
                  slot4.onTake(player, itemstack10);
                  if (!playerinventory.addItemStackToInventory(itemstack10)) {
                     player.dropItem(itemstack10, true);
                  }
               } else {
                  slot4.putStack(itemstack6);
                  playerinventory.setInventorySlotContents(dragType, itemstack10);
                  slot4.onTake(player, itemstack10);
               }
            }
         }
      } else if (clickTypeIn == ClickType.CLONE && player.abilities.isCreativeMode && playerinventory.getItemStack().isEmpty() && slotId >= 0) {
         Slot slot3 = this.inventorySlots.get(slotId);
         if (slot3 != null && slot3.getHasStack()) {
            ItemStack itemstack5 = slot3.getStack().copy();
            itemstack5.setCount(itemstack5.getMaxStackSize());
            playerinventory.setItemStack(itemstack5);
         }
      } else if (clickTypeIn == ClickType.THROW && playerinventory.getItemStack().isEmpty() && slotId >= 0) {
         Slot slot2 = this.inventorySlots.get(slotId);
         if (slot2 != null && slot2.getHasStack() && slot2.canTakeStack(player)) {
            ItemStack itemstack4 = slot2.decrStackSize(dragType == 0 ? 1 : slot2.getStack().getCount());
            slot2.onTake(player, itemstack4);
            player.dropItem(itemstack4, true);
         }
      } else if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0) {
         Slot slot = this.inventorySlots.get(slotId);
         ItemStack itemstack1 = playerinventory.getItemStack();
         if (!itemstack1.isEmpty() && (slot == null || !slot.getHasStack() || !slot.canTakeStack(player))) {
            int i = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
            int j = dragType == 0 ? 1 : -1;

            for(int k = 0; k < 2; ++k) {
               for(int l = i; l >= 0 && l < this.inventorySlots.size() && itemstack1.getCount() < itemstack1.getMaxStackSize(); l += j) {
                  Slot slot1 = this.inventorySlots.get(l);
                  if (slot1.getHasStack() && canAddItemToSlot(slot1, itemstack1, true) && slot1.canTakeStack(player) && this.canMergeSlot(itemstack1, slot1)) {
                     ItemStack itemstack2 = slot1.getStack();
                     if (k != 0 || itemstack2.getCount() != itemstack2.getMaxStackSize()) {
                        int i1 = Math.min(itemstack1.getMaxStackSize() - itemstack1.getCount(), itemstack2.getCount());
                        ItemStack itemstack3 = slot1.decrStackSize(i1);
                        itemstack1.grow(i1);
                        if (itemstack3.isEmpty()) {
                           slot1.putStack(ItemStack.EMPTY);
                        }

                        slot1.onTake(player, itemstack3);
                     }
                  }
               }
            }
         }

         this.detectAndSendChanges();
      }

      return itemstack;
   }

   public static boolean areItemsAndTagsEqual(ItemStack stack1, ItemStack stack2) {
      return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
   }

   /**
    * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
    * null for the initial slot that was double-clicked.
    */
   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return true;
   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(PlayerEntity playerIn) {
      PlayerInventory playerinventory = playerIn.inventory;
      if (!playerinventory.getItemStack().isEmpty()) {
         playerIn.dropItem(playerinventory.getItemStack(), false);
         playerinventory.setItemStack(ItemStack.EMPTY);
      }

   }

   protected void clearContainer(PlayerEntity playerIn, World worldIn, IInventory inventoryIn) {
      if (!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerIn).hasDisconnected()) {
         for(int j = 0; j < inventoryIn.getSizeInventory(); ++j) {
            playerIn.dropItem(inventoryIn.removeStackFromSlot(j), false);
         }

      } else {
         for(int i = 0; i < inventoryIn.getSizeInventory(); ++i) {
            playerIn.inventory.placeItemBackInInventory(worldIn, inventoryIn.removeStackFromSlot(i));
         }

      }
   }

   /**
    * Callback for when the crafting matrix is changed.
    */
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.detectAndSendChanges();
   }

   /**
    * Puts an ItemStack in a slot.
    */
   public void putStackInSlot(int slotID, ItemStack stack) {
      this.getSlot(slotID).putStack(stack);
   }

   @OnlyIn(Dist.CLIENT)
   public void setAll(List<ItemStack> p_190896_1_) {
      for(int i = 0; i < p_190896_1_.size(); ++i) {
         this.getSlot(i).putStack(p_190896_1_.get(i));
      }

   }

   public void updateProgressBar(int id, int data) {
      this.trackedIntReferences.get(id).set(data);
   }

   /**
    * Gets a unique transaction ID. Parameter is unused.
    */
   @OnlyIn(Dist.CLIENT)
   public short getNextTransactionID(PlayerInventory invPlayer) {
      ++this.transactionID;
      return this.transactionID;
   }

   /**
    * gets whether or not the player can craft in this inventory or not
    */
   public boolean getCanCraft(PlayerEntity player) {
      return !this.playerList.contains(player);
   }

   /**
    * sets whether the player can craft in this inventory or not
    */
   public void setCanCraft(PlayerEntity player, boolean canCraft) {
      if (canCraft) {
         this.playerList.remove(player);
      } else {
         this.playerList.add(player);
      }

   }

   /**
    * Determines whether supplied player can use this container
    */
   public abstract boolean canInteractWith(PlayerEntity playerIn);

   /**
    * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
    * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
    * implementation do not check if the item is valid for the slot
    */
   protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
      boolean flag = false;
      int i = startIndex;
      if (reverseDirection) {
         i = endIndex - 1;
      }

      if (stack.isStackable()) {
         while(!stack.isEmpty()) {
            if (reverseDirection) {
               if (i < startIndex) {
                  break;
               }
            } else if (i >= endIndex) {
               break;
            }

            Slot slot = this.inventorySlots.get(i);
            ItemStack itemstack = slot.getStack();
            if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
               int j = itemstack.getCount() + stack.getCount();
               int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
               if (j <= maxSize) {
                  stack.setCount(0);
                  itemstack.setCount(j);
                  slot.onSlotChanged();
                  flag = true;
               } else if (itemstack.getCount() < maxSize) {
                  stack.shrink(maxSize - itemstack.getCount());
                  itemstack.setCount(maxSize);
                  slot.onSlotChanged();
                  flag = true;
               }
            }

            if (reverseDirection) {
               --i;
            } else {
               ++i;
            }
         }
      }

      if (!stack.isEmpty()) {
         if (reverseDirection) {
            i = endIndex - 1;
         } else {
            i = startIndex;
         }

         while(true) {
            if (reverseDirection) {
               if (i < startIndex) {
                  break;
               }
            } else if (i >= endIndex) {
               break;
            }

            Slot slot1 = this.inventorySlots.get(i);
            ItemStack itemstack1 = slot1.getStack();
            if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
               if (stack.getCount() > slot1.getSlotStackLimit()) {
                  slot1.putStack(stack.split(slot1.getSlotStackLimit()));
               } else {
                  slot1.putStack(stack.split(stack.getCount()));
               }

               slot1.onSlotChanged();
               flag = true;
               break;
            }

            if (reverseDirection) {
               --i;
            } else {
               ++i;
            }
         }
      }

      return flag;
   }

   /**
    * Extracts the drag mode. Args : eventButton. Return (0 : evenly split, 1 : one item by slot, 2 : not used ?)
    */
   public static int extractDragMode(int eventButton) {
      return eventButton >> 2 & 3;
   }

   /**
    * Args : clickedButton, Returns (0 : start drag, 1 : add slot, 2 : end drag)
    */
   public static int getDragEvent(int clickedButton) {
      return clickedButton & 3;
   }

   @OnlyIn(Dist.CLIENT)
   public static int getQuickcraftMask(int p_94534_0_, int p_94534_1_) {
      return p_94534_0_ & 3 | (p_94534_1_ & 3) << 2;
   }

   public static boolean isValidDragMode(int dragModeIn, PlayerEntity player) {
      if (dragModeIn == 0) {
         return true;
      } else if (dragModeIn == 1) {
         return true;
      } else {
         return dragModeIn == 2 && player.abilities.isCreativeMode;
      }
   }

   /**
    * Reset the drag fields
    */
   protected void resetDrag() {
      this.dragEvent = 0;
      this.dragSlots.clear();
   }

   /**
    * Checks if it's possible to add the given itemstack to the given slot.
    */
   public static boolean canAddItemToSlot(@Nullable Slot slotIn, ItemStack stack, boolean stackSizeMatters) {
      boolean flag = slotIn == null || !slotIn.getHasStack();
      if (!flag && stack.isItemEqual(slotIn.getStack()) && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack)) {
         return slotIn.getStack().getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= stack.getMaxStackSize();
      } else {
         return flag;
      }
   }

   /**
    * Compute the new stack size, Returns the stack with the new size. Args : dragSlots, dragMode, dragStack,
    * slotStackSize
    */
   public static void computeStackSize(Set<Slot> dragSlotsIn, int dragModeIn, ItemStack stack, int slotStackSize) {
      switch(dragModeIn) {
      case 0:
         stack.setCount(MathHelper.floor((float)stack.getCount() / (float)dragSlotsIn.size()));
         break;
      case 1:
         stack.setCount(1);
         break;
      case 2:
         stack.setCount(stack.getMaxStackSize());
      }

      stack.grow(slotStackSize);
   }

   /**
    * Returns true if the player can "drag-spilt" items into this slot,. returns true by default. Called to check if the
    * slot can be added to a list of Slots to split the held ItemStack across.
    */
   public boolean canDragIntoSlot(Slot slotIn) {
      return true;
   }

   /**
    * Like the version that takes an inventory. If the given TileEntity is not an Inventory, 0 is returned instead.
    */
   public static int calcRedstone(@Nullable TileEntity te) {
      return te instanceof IInventory ? calcRedstoneFromInventory((IInventory)te) : 0;
   }

   public static int calcRedstoneFromInventory(@Nullable IInventory inv) {
      if (inv == null) {
         return 0;
      } else {
         int i = 0;
         float f = 0.0F;

         for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               f += (float)itemstack.getCount() / (float)Math.min(inv.getInventoryStackLimit(), itemstack.getMaxStackSize());
               ++i;
            }
         }

         f = f / (float)inv.getSizeInventory();
         return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
      }
   }
}