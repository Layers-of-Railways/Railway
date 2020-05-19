package net.minecraft.tileentity;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BrewingStandTileEntity extends LockableTileEntity implements ISidedInventory, ITickableTileEntity {
   /** an array of the input slot indices */
   private static final int[] SLOTS_FOR_UP = new int[]{3};
   private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
   /** an array of the output slot indices */
   private static final int[] OUTPUT_SLOTS = new int[]{0, 1, 2, 4};
   /** The ItemStacks currently placed in the slots of the brewing stand */
   private NonNullList<ItemStack> brewingItemStacks = NonNullList.withSize(5, ItemStack.EMPTY);
   private int brewTime;
   /** an integer with each bit specifying whether that slot of the stand contains a potion */
   private boolean[] filledSlots;
   /** used to check if the current ingredient has been removed from the brewing stand during brewing */
   private Item ingredientID;
   private int fuel;
   protected final IIntArray field_213954_a = new IIntArray() {
      public int get(int index) {
         switch(index) {
         case 0:
            return BrewingStandTileEntity.this.brewTime;
         case 1:
            return BrewingStandTileEntity.this.fuel;
         default:
            return 0;
         }
      }

      public void set(int index, int value) {
         switch(index) {
         case 0:
            BrewingStandTileEntity.this.brewTime = value;
            break;
         case 1:
            BrewingStandTileEntity.this.fuel = value;
         }

      }

      public int size() {
         return 2;
      }
   };

   public BrewingStandTileEntity() {
      super(TileEntityType.BREWING_STAND);
   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.brewing");
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.brewingItemStacks.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.brewingItemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void tick() {
      ItemStack itemstack = this.brewingItemStacks.get(4);
      if (this.fuel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
         this.fuel = 20;
         itemstack.shrink(1);
         this.markDirty();
      }

      boolean flag = this.canBrew();
      boolean flag1 = this.brewTime > 0;
      ItemStack itemstack1 = this.brewingItemStacks.get(3);
      if (flag1) {
         --this.brewTime;
         boolean flag2 = this.brewTime == 0;
         if (flag2 && flag) {
            this.brewPotions();
            this.markDirty();
         } else if (!flag) {
            this.brewTime = 0;
            this.markDirty();
         } else if (this.ingredientID != itemstack1.getItem()) {
            this.brewTime = 0;
            this.markDirty();
         }
      } else if (flag && this.fuel > 0) {
         --this.fuel;
         this.brewTime = 400;
         this.ingredientID = itemstack1.getItem();
         this.markDirty();
      }

      if (!this.world.isRemote) {
         boolean[] aboolean = this.createFilledSlotsArray();
         if (!Arrays.equals(aboolean, this.filledSlots)) {
            this.filledSlots = aboolean;
            BlockState blockstate = this.world.getBlockState(this.getPos());
            if (!(blockstate.getBlock() instanceof BrewingStandBlock)) {
               return;
            }

            for(int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
               blockstate = blockstate.with(BrewingStandBlock.HAS_BOTTLE[i], Boolean.valueOf(aboolean[i]));
            }

            this.world.setBlockState(this.pos, blockstate, 2);
         }
      }

   }

   /**
    * Creates an array of boolean values, each value represents a potion input slot, value is true if the slot is not
    * null.
    */
   public boolean[] createFilledSlotsArray() {
      boolean[] aboolean = new boolean[3];

      for(int i = 0; i < 3; ++i) {
         if (!this.brewingItemStacks.get(i).isEmpty()) {
            aboolean[i] = true;
         }
      }

      return aboolean;
   }

   private boolean canBrew() {
      ItemStack itemstack = this.brewingItemStacks.get(3);
      if (!itemstack.isEmpty()) return net.minecraftforge.common.brewing.BrewingRecipeRegistry.canBrew(brewingItemStacks, itemstack, OUTPUT_SLOTS); // divert to VanillaBrewingRegistry
      if (itemstack.isEmpty()) {
         return false;
      } else if (!PotionBrewing.isReagent(itemstack)) {
         return false;
      } else {
         for(int i = 0; i < 3; ++i) {
            ItemStack itemstack1 = this.brewingItemStacks.get(i);
            if (!itemstack1.isEmpty() && PotionBrewing.hasConversions(itemstack1, itemstack)) {
               return true;
            }
         }

         return false;
      }
   }

   private void brewPotions() {
      if (net.minecraftforge.event.ForgeEventFactory.onPotionAttemptBrew(brewingItemStacks)) return;
      ItemStack itemstack = this.brewingItemStacks.get(3);

      net.minecraftforge.common.brewing.BrewingRecipeRegistry.brewPotions(brewingItemStacks, itemstack, OUTPUT_SLOTS);

      itemstack.shrink(1);
      net.minecraftforge.event.ForgeEventFactory.onPotionBrewed(brewingItemStacks);
      BlockPos blockpos = this.getPos();
      if (itemstack.hasContainerItem()) {
         ItemStack itemstack1 = itemstack.getContainerItem();
         if (itemstack.isEmpty()) {
            itemstack = itemstack1;
         } else if (!this.world.isRemote) {
            InventoryHelper.spawnItemStack(this.world, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), itemstack1);
         }
      }

      this.brewingItemStacks.set(3, itemstack);
      this.world.playEvent(1035, blockpos, 0);
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      this.brewingItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound, this.brewingItemStacks);
      this.brewTime = compound.getShort("BrewTime");
      this.fuel = compound.getByte("Fuel");
   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      compound.putShort("BrewTime", (short)this.brewTime);
      ItemStackHelper.saveAllItems(compound, this.brewingItemStacks);
      compound.putByte("Fuel", (byte)this.fuel);
      return compound;
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.brewingItemStacks.size() ? this.brewingItemStacks.get(index) : ItemStack.EMPTY;
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      return ItemStackHelper.getAndSplit(this.brewingItemStacks, index, count);
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.brewingItemStacks, index);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      if (index >= 0 && index < this.brewingItemStacks.size()) {
         this.brewingItemStacks.set(index, stack);
      }

   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(PlayerEntity player) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return !(player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
      }
   }

   /**
    * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
    * guis use Slot.isItemValid
    */
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      if (index == 3) {
         return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidIngredient(stack);
      } else {
         Item item = stack.getItem();
         if (index == 4) {
            return item == Items.BLAZE_POWDER;
         } else {
            return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput(stack) && this.getStackInSlot(index).isEmpty();
         }
      }
   }

   public int[] getSlotsForFace(Direction side) {
      if (side == Direction.UP) {
         return SLOTS_FOR_UP;
      } else {
         return side == Direction.DOWN ? SLOTS_FOR_DOWN : OUTPUT_SLOTS;
      }
   }

   /**
    * Returns true if automation can insert the given item in the given slot from the given side.
    */
   public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return this.isItemValidForSlot(index, itemStackIn);
   }

   /**
    * Returns true if automation can extract the given item in the given slot from the given side.
    */
   public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
      if (index == 3) {
         return stack.getItem() == Items.GLASS_BOTTLE;
      } else {
         return true;
      }
   }

   public void clear() {
      this.brewingItemStacks.clear();
   }

   protected Container createMenu(int id, PlayerInventory player) {
      return new BrewingStandContainer(id, player, this, this.field_213954_a);
   }

   net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
           net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
      if (!this.removed && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (facing == Direction.UP)
            return handlers[0].cast();
         else if (facing == Direction.DOWN)
            return handlers[1].cast();
         else
            return handlers[2].cast();
      }
      return super.getCapability(capability, facing);
   }

   /**
    * invalidates a tile entity
    */
   @Override
   public void remove() {
      super.remove();
      for (int x = 0; x < handlers.length; x++)
        handlers[x].invalidate();
   }
}