package net.minecraft.inventory.container;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Slot {
   private final int slotIndex;
   public final IInventory inventory;
   public int slotNumber;
   public final int xPos;
   public final int yPos;

   public Slot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      this.inventory = inventoryIn;
      this.slotIndex = index;
      this.xPos = xPosition;
      this.yPos = yPosition;
   }

   /**
    * if par2 has more items than par1, onCrafting(item,countIncrease) is called
    */
   public void onSlotChange(ItemStack oldStackIn, ItemStack newStackIn) {
      int i = newStackIn.getCount() - oldStackIn.getCount();
      if (i > 0) {
         this.onCrafting(newStackIn, i);
      }

   }

   /**
    * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
    * internal count then calls onCrafting(item).
    */
   protected void onCrafting(ItemStack stack, int amount) {
   }

   protected void onSwapCraft(int p_190900_1_) {
   }

   /**
    * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
    */
   protected void onCrafting(ItemStack stack) {
   }

   public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
      this.onSlotChanged();
      return stack;
   }

   /**
    * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
    */
   public boolean isItemValid(ItemStack stack) {
      return true;
   }

   /**
    * Helper fnct to get the stack in the slot.
    */
   public ItemStack getStack() {
      return this.inventory.getStackInSlot(this.slotIndex);
   }

   /**
    * Returns if this slot contains a stack.
    */
   public boolean getHasStack() {
      return !this.getStack().isEmpty();
   }

   /**
    * Helper method to put a stack in the slot.
    */
   public void putStack(ItemStack stack) {
      this.inventory.setInventorySlotContents(this.slotIndex, stack);
      this.onSlotChanged();
   }

   /**
    * Called when the stack in a Slot changes
    */
   public void onSlotChanged() {
      this.inventory.markDirty();
   }

   /**
    * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
    * of armor slots)
    */
   public int getSlotStackLimit() {
      return this.inventory.getInventoryStackLimit();
   }

   public int getItemStackLimit(ItemStack stack) {
      return this.getSlotStackLimit();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
      return backgroundPair;
   }

   /**
    * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
    */
   public ItemStack decrStackSize(int amount) {
      return this.inventory.decrStackSize(this.slotIndex, amount);
   }

   /**
    * Return whether this slot's stack can be taken from this slot.
    */
   public boolean canTakeStack(PlayerEntity playerIn) {
      return true;
   }

   /**
    * Actualy only call when we want to render the white square effect over the slots. Return always True, except for
    * the armor slot of the Donkey/Mule (we can't interact with the Undead and Skeleton horses)
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isEnabled() {
      return true;
   }

   /**
    * Retrieves the index in the inventory for this slot, this value should typically not
    * be used, but can be useful for some occasions.
    *
    * @return Index in associated inventory for this slot.
    */
   public int getSlotIndex() {
      return slotIndex;
   }

   /**
    * Checks if the other slot is in the same inventory, by comparing the inventory reference.
    * @param other
    * @return true if the other slot is in the same inventory
    */
   public boolean isSameInventory(Slot other) {
      return this.inventory == other.inventory;
   }

   private Pair<ResourceLocation, ResourceLocation> backgroundPair;
   /**
    * Sets the background atlas and sprite location.
    *
    * @param atlas The atlas name
    * @param sprite The sprite located on that atlas.
    * @return this, to allow chaining.
    */
   public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
       this.backgroundPair = Pair.of(atlas, sprite);
       return this;
   }
}