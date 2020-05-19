package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.tags.Tag;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerInventory implements IInventory, INameable {
   public final NonNullList<ItemStack> mainInventory = NonNullList.withSize(36, ItemStack.EMPTY);
   public final NonNullList<ItemStack> armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);
   public final NonNullList<ItemStack> offHandInventory = NonNullList.withSize(1, ItemStack.EMPTY);
   private final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(this.mainInventory, this.armorInventory, this.offHandInventory);
   public int currentItem;
   public final PlayerEntity player;
   private ItemStack itemStack = ItemStack.EMPTY;
   private int timesChanged;

   public PlayerInventory(PlayerEntity playerIn) {
      this.player = playerIn;
   }

   /**
    * Returns the item stack currently held by the player.
    */
   public ItemStack getCurrentItem() {
      return isHotbar(this.currentItem) ? this.mainInventory.get(this.currentItem) : ItemStack.EMPTY;
   }

   /**
    * Get the size of the player hotbar inventory
    */
   public static int getHotbarSize() {
      return 9;
   }

   private boolean canMergeStacks(ItemStack stack1, ItemStack stack2) {
      return !stack1.isEmpty() && this.stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < this.getInventoryStackLimit();
   }

   /**
    * Checks item, NBT, and meta if the item is not damageable
    */
   private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
      return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
   }

   /**
    * Returns the first item stack that is empty.
    */
   public int getFirstEmptyStack() {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         if (this.mainInventory.get(i).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPickedItemStack(ItemStack stack) {
      int i = this.getSlotFor(stack);
      if (isHotbar(i)) {
         this.currentItem = i;
      } else {
         if (i == -1) {
            this.currentItem = this.getBestHotbarSlot();
            if (!this.mainInventory.get(this.currentItem).isEmpty()) {
               int j = this.getFirstEmptyStack();
               if (j != -1) {
                  this.mainInventory.set(j, this.mainInventory.get(this.currentItem));
               }
            }

            this.mainInventory.set(this.currentItem, stack);
         } else {
            this.pickItem(i);
         }

      }
   }

   public void pickItem(int index) {
      this.currentItem = this.getBestHotbarSlot();
      ItemStack itemstack = this.mainInventory.get(this.currentItem);
      this.mainInventory.set(this.currentItem, this.mainInventory.get(index));
      this.mainInventory.set(index, itemstack);
   }

   public static boolean isHotbar(int index) {
      return index >= 0 && index < 9;
   }

   /**
    * Finds the stack or an equivalent one in the main inventory
    */
   @OnlyIn(Dist.CLIENT)
   public int getSlotFor(ItemStack stack) {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         if (!this.mainInventory.get(i).isEmpty() && this.stackEqualExact(stack, this.mainInventory.get(i))) {
            return i;
         }
      }

      return -1;
   }

   public int findSlotMatchingUnusedItem(ItemStack p_194014_1_) {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         ItemStack itemstack = this.mainInventory.get(i);
         if (!this.mainInventory.get(i).isEmpty() && this.stackEqualExact(p_194014_1_, this.mainInventory.get(i)) && !this.mainInventory.get(i).isDamaged() && !itemstack.isEnchanted() && !itemstack.hasDisplayName()) {
            return i;
         }
      }

      return -1;
   }

   public int getBestHotbarSlot() {
      for(int i = 0; i < 9; ++i) {
         int j = (this.currentItem + i) % 9;
         if (this.mainInventory.get(j).isEmpty()) {
            return j;
         }
      }

      for(int k = 0; k < 9; ++k) {
         int l = (this.currentItem + k) % 9;
         if (!this.mainInventory.get(l).isEnchanted()) {
            return l;
         }
      }

      return this.currentItem;
   }

   @OnlyIn(Dist.CLIENT)
   public void changeCurrentItem(double direction) {
      if (direction > 0.0D) {
         direction = 1.0D;
      }

      if (direction < 0.0D) {
         direction = -1.0D;
      }

      for(this.currentItem = (int)((double)this.currentItem - direction); this.currentItem < 0; this.currentItem += 9) {
         ;
      }

      while(this.currentItem >= 9) {
         this.currentItem -= 9;
      }

   }

   public int clearMatchingItems(Predicate<ItemStack> p_195408_1_, int count) {
      int i = 0;

      for(int j = 0; j < this.getSizeInventory(); ++j) {
         ItemStack itemstack = this.getStackInSlot(j);
         if (!itemstack.isEmpty() && p_195408_1_.test(itemstack)) {
            int k = count <= 0 ? itemstack.getCount() : Math.min(count - i, itemstack.getCount());
            i += k;
            if (count != 0) {
               itemstack.shrink(k);
               if (itemstack.isEmpty()) {
                  this.setInventorySlotContents(j, ItemStack.EMPTY);
               }

               if (count > 0 && i >= count) {
                  return i;
               }
            }
         }
      }

      if (!this.itemStack.isEmpty() && p_195408_1_.test(this.itemStack)) {
         int l = count <= 0 ? this.itemStack.getCount() : Math.min(count - i, this.itemStack.getCount());
         i += l;
         if (count != 0) {
            this.itemStack.shrink(l);
            if (this.itemStack.isEmpty()) {
               this.itemStack = ItemStack.EMPTY;
            }

            if (count > 0 && i >= count) {
               return i;
            }
         }
      }

      return i;
   }

   /**
    * This function stores as many items of an ItemStack as possible in a matching slot and returns the quantity of left
    * over items.
    */
   private int storePartialItemStack(ItemStack itemStackIn) {
      int i = this.storeItemStack(itemStackIn);
      if (i == -1) {
         i = this.getFirstEmptyStack();
      }

      return i == -1 ? itemStackIn.getCount() : this.addResource(i, itemStackIn);
   }

   private int addResource(int p_191973_1_, ItemStack p_191973_2_) {
      Item item = p_191973_2_.getItem();
      int i = p_191973_2_.getCount();
      ItemStack itemstack = this.getStackInSlot(p_191973_1_);
      if (itemstack.isEmpty()) {
         itemstack = p_191973_2_.copy(); // Forge: Replace Item clone above to preserve item capabilities when picking the item up.
         itemstack.setCount(0);
         if (p_191973_2_.hasTag()) {
            itemstack.setTag(p_191973_2_.getTag().copy());
         }

         this.setInventorySlotContents(p_191973_1_, itemstack);
      }

      int j = i;
      if (i > itemstack.getMaxStackSize() - itemstack.getCount()) {
         j = itemstack.getMaxStackSize() - itemstack.getCount();
      }

      if (j > this.getInventoryStackLimit() - itemstack.getCount()) {
         j = this.getInventoryStackLimit() - itemstack.getCount();
      }

      if (j == 0) {
         return i;
      } else {
         i = i - j;
         itemstack.grow(j);
         itemstack.setAnimationsToGo(5);
         return i;
      }
   }

   /**
    * Stores a stack in the player's inventory. It first tries to place it in the selected slot in the player's hotbar,
    * then the offhand slot, then any available/empty slot in the player's inventory.
    */
   public int storeItemStack(ItemStack itemStackIn) {
      if (this.canMergeStacks(this.getStackInSlot(this.currentItem), itemStackIn)) {
         return this.currentItem;
      } else if (this.canMergeStacks(this.getStackInSlot(40), itemStackIn)) {
         return 40;
      } else {
         for(int i = 0; i < this.mainInventory.size(); ++i) {
            if (this.canMergeStacks(this.mainInventory.get(i), itemStackIn)) {
               return i;
            }
         }

         return -1;
      }
   }

   /**
    * Decrement the number of animations remaining. Only called on client side. This is used to handle the animation of
    * receiving a block.
    */
   public void tick() {
      for(NonNullList<ItemStack> nonnulllist : this.allInventories) {
         for(int i = 0; i < nonnulllist.size(); ++i) {
            if (!nonnulllist.get(i).isEmpty()) {
               nonnulllist.get(i).inventoryTick(this.player.world, this.player, i, this.currentItem == i);
            }
         }
      }
      armorInventory.forEach(e -> e.onArmorTick(player.world, player));
   }

   /**
    * Adds the stack to the first empty slot in the player's inventory. Returns {@code false} if it's not possible to
    * place the entire stack in the inventory.
    */
   public boolean addItemStackToInventory(ItemStack itemStackIn) {
      return this.add(-1, itemStackIn);
   }

   /**
    * Adds the stack to the specified slot in the player's inventory. Returns {@code false} if it's not possible to
    * place the entire stack in the inventory.
    */
   public boolean add(int slotIn, ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         try {
            if (stack.isDamaged()) {
               if (slotIn == -1) {
                  slotIn = this.getFirstEmptyStack();
               }

               if (slotIn >= 0) {
                  this.mainInventory.set(slotIn, stack.copy());
                  this.mainInventory.get(slotIn).setAnimationsToGo(5);
                  stack.setCount(0);
                  return true;
               } else if (this.player.abilities.isCreativeMode) {
                  stack.setCount(0);
                  return true;
               } else {
                  return false;
               }
            } else {
               int i;
               while(true) {
                  i = stack.getCount();
                  if (slotIn == -1) {
                     stack.setCount(this.storePartialItemStack(stack));
                  } else {
                     stack.setCount(this.addResource(slotIn, stack));
                  }

                  if (stack.isEmpty() || stack.getCount() >= i) {
                     break;
                  }
               }

               if (stack.getCount() == i && this.player.abilities.isCreativeMode) {
                  stack.setCount(0);
                  return true;
               } else {
                  return stack.getCount() < i;
               }
            }
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
            crashreportcategory.addDetail("Registry Name", () -> String.valueOf(stack.getItem().getRegistryName()));
            crashreportcategory.addDetail("Item Class", () -> stack.getItem().getClass().getName());
            crashreportcategory.addDetail("Item ID", Item.getIdFromItem(stack.getItem()));
            crashreportcategory.addDetail("Item data", stack.getDamage());
            crashreportcategory.addDetail("Item name", () -> {
               return stack.getDisplayName().getString();
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public void placeItemBackInInventory(World worldIn, ItemStack stack) {
      if (!worldIn.isRemote) {
         while(!stack.isEmpty()) {
            int i = this.storeItemStack(stack);
            if (i == -1) {
               i = this.getFirstEmptyStack();
            }

            if (i == -1) {
               this.player.dropItem(stack, false);
               break;
            }

            int j = stack.getMaxStackSize() - this.getStackInSlot(i).getCount();
            if (this.add(i, stack.split(j))) {
               ((ServerPlayerEntity)this.player).connection.sendPacket(new SSetSlotPacket(-2, i, this.getStackInSlot(i)));
            }
         }

      }
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      List<ItemStack> list = null;

      for(NonNullList<ItemStack> nonnulllist : this.allInventories) {
         if (index < nonnulllist.size()) {
            list = nonnulllist;
            break;
         }

         index -= nonnulllist.size();
      }

      return list != null && !list.get(index).isEmpty() ? ItemStackHelper.getAndSplit(list, index, count) : ItemStack.EMPTY;
   }

   public void deleteStack(ItemStack stack) {
      for(NonNullList<ItemStack> nonnulllist : this.allInventories) {
         for(int i = 0; i < nonnulllist.size(); ++i) {
            if (nonnulllist.get(i) == stack) {
               nonnulllist.set(i, ItemStack.EMPTY);
               break;
            }
         }
      }

   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      NonNullList<ItemStack> nonnulllist = null;

      for(NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
         if (index < nonnulllist1.size()) {
            nonnulllist = nonnulllist1;
            break;
         }

         index -= nonnulllist1.size();
      }

      if (nonnulllist != null && !nonnulllist.get(index).isEmpty()) {
         ItemStack itemstack = nonnulllist.get(index);
         nonnulllist.set(index, ItemStack.EMPTY);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      NonNullList<ItemStack> nonnulllist = null;

      for(NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
         if (index < nonnulllist1.size()) {
            nonnulllist = nonnulllist1;
            break;
         }

         index -= nonnulllist1.size();
      }

      if (nonnulllist != null) {
         nonnulllist.set(index, stack);
      }

   }

   public float getDestroySpeed(BlockState state) {
      return this.mainInventory.get(this.currentItem).getDestroySpeed(state);
   }

   /**
    * Writes the inventory out as a list of compound tags. This is where the slot indices are used (+100 for armor, +80
    * for crafting).
    */
   public ListNBT write(ListNBT nbtTagListIn) {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         if (!this.mainInventory.get(i).isEmpty()) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putByte("Slot", (byte)i);
            this.mainInventory.get(i).write(compoundnbt);
            nbtTagListIn.add(compoundnbt);
         }
      }

      for(int j = 0; j < this.armorInventory.size(); ++j) {
         if (!this.armorInventory.get(j).isEmpty()) {
            CompoundNBT compoundnbt1 = new CompoundNBT();
            compoundnbt1.putByte("Slot", (byte)(j + 100));
            this.armorInventory.get(j).write(compoundnbt1);
            nbtTagListIn.add(compoundnbt1);
         }
      }

      for(int k = 0; k < this.offHandInventory.size(); ++k) {
         if (!this.offHandInventory.get(k).isEmpty()) {
            CompoundNBT compoundnbt2 = new CompoundNBT();
            compoundnbt2.putByte("Slot", (byte)(k + 150));
            this.offHandInventory.get(k).write(compoundnbt2);
            nbtTagListIn.add(compoundnbt2);
         }
      }

      return nbtTagListIn;
   }

   /**
    * Reads from the given tag list and fills the slots in the inventory with the correct items.
    */
   public void read(ListNBT nbtTagListIn) {
      this.mainInventory.clear();
      this.armorInventory.clear();
      this.offHandInventory.clear();

      for(int i = 0; i < nbtTagListIn.size(); ++i) {
         CompoundNBT compoundnbt = nbtTagListIn.getCompound(i);
         int j = compoundnbt.getByte("Slot") & 255;
         ItemStack itemstack = ItemStack.read(compoundnbt);
         if (!itemstack.isEmpty()) {
            if (j >= 0 && j < this.mainInventory.size()) {
               this.mainInventory.set(j, itemstack);
            } else if (j >= 100 && j < this.armorInventory.size() + 100) {
               this.armorInventory.set(j - 100, itemstack);
            } else if (j >= 150 && j < this.offHandInventory.size() + 150) {
               this.offHandInventory.set(j - 150, itemstack);
            }
         }
      }

   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.mainInventory.size() + this.armorInventory.size() + this.offHandInventory.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.mainInventory) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      for(ItemStack itemstack1 : this.armorInventory) {
         if (!itemstack1.isEmpty()) {
            return false;
         }
      }

      for(ItemStack itemstack2 : this.offHandInventory) {
         if (!itemstack2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      List<ItemStack> list = null;

      for(NonNullList<ItemStack> nonnulllist : this.allInventories) {
         if (index < nonnulllist.size()) {
            list = nonnulllist;
            break;
         }

         index -= nonnulllist.size();
      }

      return list == null ? ItemStack.EMPTY : list.get(index);
   }

   public ITextComponent getName() {
      return new TranslationTextComponent("container.inventory");
   }

   public boolean canHarvestBlock(BlockState state) {
      return this.getStackInSlot(this.currentItem).canHarvestBlock(state);
   }

   /**
    * returns a player armor item (as itemstack) contained in specified armor slot.
    */
   @OnlyIn(Dist.CLIENT)
   public ItemStack armorItemInSlot(int slotIn) {
      return this.armorInventory.get(slotIn);
   }

   /**
    * Damages armor in each slot by the specified amount.
    */
   public void damageArmor(float damage) {
      if (!(damage <= 0.0F)) {
         damage = damage / 4.0F;
         if (damage < 1.0F) {
            damage = 1.0F;
         }

         for(int i = 0; i < this.armorInventory.size(); ++i) {
            ItemStack itemstack = this.armorInventory.get(i);
            if (itemstack.getItem() instanceof ArmorItem) {
               int j = i;
               itemstack.damageItem((int)damage, this.player, (p_214023_1_) -> {
                  p_214023_1_.sendBreakAnimation(EquipmentSlotType.fromSlotTypeAndIndex(EquipmentSlotType.Group.ARMOR, j));
               });
            }
         }

      }
   }

   /**
    * Drop all armor and main inventory items.
    */
   public void dropAllItems() {
      for(List<ItemStack> list : this.allInventories) {
         for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            if (!itemstack.isEmpty()) {
               this.player.dropItem(itemstack, true, false);
               list.set(i, ItemStack.EMPTY);
            }
         }
      }

   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   public void markDirty() {
      ++this.timesChanged;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTimesChanged() {
      return this.timesChanged;
   }

   /**
    * Set the stack helds by mouse, used in GUI/Container
    */
   public void setItemStack(ItemStack itemStackIn) {
      this.itemStack = itemStackIn;
   }

   /**
    * Stack helds by mouse, used in GUI and Containers
    */
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(PlayerEntity player) {
      if (this.player.removed) {
         return false;
      } else {
         return !(player.getDistanceSq(this.player) > 64.0D);
      }
   }

   /**
    * Returns true if the specified ItemStack exists in the inventory.
    */
   public boolean hasItemStack(ItemStack itemStackIn) {
      label23:
      for(List<ItemStack> list : this.allInventories) {
         Iterator iterator = list.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               continue label23;
            }

            ItemStack itemstack = (ItemStack)iterator.next();
            if (!itemstack.isEmpty() && itemstack.isItemEqual(itemStackIn)) {
               break;
            }
         }

         return true;
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasTag(Tag<Item> itemTag) {
      label23:
      for(List<ItemStack> list : this.allInventories) {
         Iterator iterator = list.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               continue label23;
            }

            ItemStack itemstack = (ItemStack)iterator.next();
            if (!itemstack.isEmpty() && itemTag.contains(itemstack.getItem())) {
               break;
            }
         }

         return true;
      }

      return false;
   }

   /**
    * Copy the ItemStack contents from another InventoryPlayer instance
    */
   public void copyInventory(PlayerInventory playerInventory) {
      for(int i = 0; i < this.getSizeInventory(); ++i) {
         this.setInventorySlotContents(i, playerInventory.getStackInSlot(i));
      }

      this.currentItem = playerInventory.currentItem;
   }

   public void clear() {
      for(List<ItemStack> list : this.allInventories) {
         list.clear();
      }

   }

   public void accountStacks(RecipeItemHelper p_201571_1_) {
      for(ItemStack itemstack : this.mainInventory) {
         p_201571_1_.accountPlainStack(itemstack);
      }

   }
}