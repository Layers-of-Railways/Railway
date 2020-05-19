package net.minecraft.inventory.container;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerContainer extends RecipeBookContainer<CraftingInventory> {
   public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = new ResourceLocation("item/empty_armor_slot_helmet");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = new ResourceLocation("item/empty_armor_slot_chestplate");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = new ResourceLocation("item/empty_armor_slot_leggings");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = new ResourceLocation("item/empty_armor_slot_boots");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD = new ResourceLocation("item/empty_armor_slot_shield");
   private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
   private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
   private final CraftingInventory craftMatrix = new CraftingInventory(this, 2, 2);
   private final CraftResultInventory craftResult = new CraftResultInventory();
   public final boolean isLocalWorld;
   private final PlayerEntity player;

   public PlayerContainer(PlayerInventory playerInventory, boolean localWorld, PlayerEntity playerIn) {
      super((ContainerType<?>)null, 0);
      this.isLocalWorld = localWorld;
      this.player = playerIn;
      this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 154, 28));

      for(int i = 0; i < 2; ++i) {
         for(int j = 0; j < 2; ++j) {
            this.addSlot(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
         }
      }

      for(int k = 0; k < 4; ++k) {
         final EquipmentSlotType equipmentslottype = VALID_EQUIPMENT_SLOTS[k];
         this.addSlot(new Slot(playerInventory, 39 - k, 8, 8 + k * 18) {
            /**
             * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in
             * the case of armor slots)
             */
            public int getSlotStackLimit() {
               return 1;
            }

            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean isItemValid(ItemStack stack) {
               return stack.canEquip(equipmentslottype, player);
            }

            /**
             * Return whether this slot's stack can be taken from this slot.
             */
            public boolean canTakeStack(PlayerEntity playerIn) {
               ItemStack itemstack = this.getStack();
               return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
            }

            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
               return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
            }
         });
      }

      for(int l = 0; l < 3; ++l) {
         for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
         }
      }

      for(int i1 = 0; i1 < 9; ++i1) {
         this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
      }

      this.addSlot(new Slot(playerInventory, 40, 77, 62) {
         @OnlyIn(Dist.CLIENT)
         public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
            return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
         }
      });
   }

   public void fillStackedContents(RecipeItemHelper itemHelperIn) {
      this.craftMatrix.fillStackedContents(itemHelperIn);
   }

   public void clear() {
      this.craftResult.clear();
      this.craftMatrix.clear();
   }

   public boolean matches(IRecipe<? super CraftingInventory> recipeIn) {
      return recipeIn.matches(this.craftMatrix, this.player.world);
   }

   /**
    * Callback for when the crafting matrix is changed.
    */
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      WorkbenchContainer.func_217066_a(this.windowId, this.player.world, this.player, this.craftMatrix, this.craftResult);
   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(PlayerEntity playerIn) {
      super.onContainerClosed(playerIn);
      this.craftResult.clear();
      if (!playerIn.world.isRemote) {
         this.clearContainer(playerIn, playerIn.world, this.craftMatrix);
      }
   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(PlayerEntity playerIn) {
      return true;
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
         EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstack);
         if (index == 0) {
            if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (index >= 1 && index < 5) {
            if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 5 && index < 9) {
            if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (equipmentslottype.getSlotType() == EquipmentSlotType.Group.ARMOR && !this.inventorySlots.get(8 - equipmentslottype.getIndex()).getHasStack()) {
            int i = 8 - equipmentslottype.getIndex();
            if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (equipmentslottype == EquipmentSlotType.OFFHAND && !this.inventorySlots.get(45).getHasStack()) {
            if (!this.mergeItemStack(itemstack1, 45, 46, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 9 && index < 36) {
            if (!this.mergeItemStack(itemstack1, 36, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 36 && index < 45) {
            if (!this.mergeItemStack(itemstack1, 9, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
         if (index == 0) {
            playerIn.dropItem(itemstack2, false);
         }
      }

      return itemstack;
   }

   /**
    * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
    * null for the initial slot that was double-clicked.
    */
   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
   }

   public int getOutputSlot() {
      return 0;
   }

   public int getWidth() {
      return this.craftMatrix.getWidth();
   }

   public int getHeight() {
      return this.craftMatrix.getHeight();
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return 5;
   }
}