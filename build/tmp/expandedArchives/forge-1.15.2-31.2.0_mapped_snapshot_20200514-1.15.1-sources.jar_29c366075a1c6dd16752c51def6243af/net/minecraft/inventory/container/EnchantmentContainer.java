package net.minecraft.inventory.container;

import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnchantmentContainer extends Container {
   private final IInventory tableInventory = new Inventory(2) {
      /**
       * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
       * it hasn't changed and skip it.
       */
      public void markDirty() {
         super.markDirty();
         EnchantmentContainer.this.onCraftMatrixChanged(this);
      }
   };
   private final IWorldPosCallable field_217006_g;
   private final Random rand = new Random();
   private final IntReferenceHolder xpSeed = IntReferenceHolder.single();
   public final int[] enchantLevels = new int[3];
   public final int[] enchantClue = new int[]{-1, -1, -1};
   public final int[] worldClue = new int[]{-1, -1, -1};

   public EnchantmentContainer(int p_i50085_1_, PlayerInventory p_i50085_2_) {
      this(p_i50085_1_, p_i50085_2_, IWorldPosCallable.DUMMY);
   }

   public EnchantmentContainer(int p_i50086_1_, PlayerInventory p_i50086_2_, IWorldPosCallable p_i50086_3_) {
      super(ContainerType.ENCHANTMENT, p_i50086_1_);
      this.field_217006_g = p_i50086_3_;
      this.addSlot(new Slot(this.tableInventory, 0, 15, 47) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return true;
         }

         /**
          * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
          * case of armor slots)
          */
         public int getSlotStackLimit() {
            return 1;
         }
      });
      this.addSlot(new Slot(this.tableInventory, 1, 35, 47) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return net.minecraftforge.common.Tags.Items.GEMS_LAPIS.contains(stack.getItem());
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50086_2_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50086_2_, k, 8 + k * 18, 142));
      }

      this.trackInt(IntReferenceHolder.create(this.enchantLevels, 0));
      this.trackInt(IntReferenceHolder.create(this.enchantLevels, 1));
      this.trackInt(IntReferenceHolder.create(this.enchantLevels, 2));
      this.trackInt(this.xpSeed).set(p_i50086_2_.player.getXPSeed());
      this.trackInt(IntReferenceHolder.create(this.enchantClue, 0));
      this.trackInt(IntReferenceHolder.create(this.enchantClue, 1));
      this.trackInt(IntReferenceHolder.create(this.enchantClue, 2));
      this.trackInt(IntReferenceHolder.create(this.worldClue, 0));
      this.trackInt(IntReferenceHolder.create(this.worldClue, 1));
      this.trackInt(IntReferenceHolder.create(this.worldClue, 2));
   }

   private float getPower(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos) {
      return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
   }

   /**
    * Callback for when the crafting matrix is changed.
    */
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      if (inventoryIn == this.tableInventory) {
         ItemStack itemstack = inventoryIn.getStackInSlot(0);
         if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
            this.field_217006_g.consume((p_217002_2_, p_217002_3_) -> {
               float power = 0;

               for(int k = -1; k <= 1; ++k) {
                  for(int l = -1; l <= 1; ++l) {
                     if ((k != 0 || l != 0) && p_217002_2_.isAirBlock(p_217002_3_.add(l, 0, k)) && p_217002_2_.isAirBlock(p_217002_3_.add(l, 1, k))) {
                        power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 0, k * 2));
                        power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 1, k * 2));

                        if (l != 0 && k != 0) {
                           power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 0, k));
                           power += getPower(p_217002_2_, p_217002_3_.add(l * 2, 1, k));
                           power += getPower(p_217002_2_, p_217002_3_.add(l, 0, k * 2));
                           power += getPower(p_217002_2_, p_217002_3_.add(l, 1, k * 2));
                        }
                     }
                  }
               }

               this.rand.setSeed((long)this.xpSeed.get());

               for(int i1 = 0; i1 < 3; ++i1) {
                  this.enchantLevels[i1] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i1, (int)power, itemstack);
                  this.enchantClue[i1] = -1;
                  this.worldClue[i1] = -1;
                  if (this.enchantLevels[i1] < i1 + 1) {
                     this.enchantLevels[i1] = 0;
                  }
                  this.enchantLevels[i1] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(p_217002_2_, p_217002_3_, i1, (int)power, itemstack, enchantLevels[i1]);
               }

               for(int j1 = 0; j1 < 3; ++j1) {
                  if (this.enchantLevels[j1] > 0) {
                     List<EnchantmentData> list = this.getEnchantmentList(itemstack, j1, this.enchantLevels[j1]);
                     if (list != null && !list.isEmpty()) {
                        EnchantmentData enchantmentdata = list.get(this.rand.nextInt(list.size()));
                        this.enchantClue[j1] = Registry.ENCHANTMENT.getId(enchantmentdata.enchantment);
                        this.worldClue[j1] = enchantmentdata.enchantmentLevel;
                     }
                  }
               }

               this.detectAndSendChanges();
            });
         } else {
            for(int i = 0; i < 3; ++i) {
               this.enchantLevels[i] = 0;
               this.enchantClue[i] = -1;
               this.worldClue[i] = -1;
            }
         }
      }

   }

   /**
    * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
    */
   public boolean enchantItem(PlayerEntity playerIn, int id) {
      ItemStack itemstack = this.tableInventory.getStackInSlot(0);
      ItemStack itemstack1 = this.tableInventory.getStackInSlot(1);
      int i = id + 1;
      if ((itemstack1.isEmpty() || itemstack1.getCount() < i) && !playerIn.abilities.isCreativeMode) {
         return false;
      } else if (this.enchantLevels[id] <= 0 || itemstack.isEmpty() || (playerIn.experienceLevel < i || playerIn.experienceLevel < this.enchantLevels[id]) && !playerIn.abilities.isCreativeMode) {
         return false;
      } else {
         this.field_217006_g.consume((p_217003_6_, p_217003_7_) -> {
            ItemStack itemstack2 = itemstack;
            List<EnchantmentData> list = this.getEnchantmentList(itemstack, id, this.enchantLevels[id]);
            if (!list.isEmpty()) {
               playerIn.onEnchant(itemstack, i);
               boolean flag = itemstack.getItem() == Items.BOOK;
               if (flag) {
                  itemstack2 = new ItemStack(Items.ENCHANTED_BOOK);
                  this.tableInventory.setInventorySlotContents(0, itemstack2);
               }

               for(int j = 0; j < list.size(); ++j) {
                  EnchantmentData enchantmentdata = list.get(j);
                  if (flag) {
                     EnchantedBookItem.addEnchantment(itemstack2, enchantmentdata);
                  } else {
                     itemstack2.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
                  }
               }

               if (!playerIn.abilities.isCreativeMode) {
                  itemstack1.shrink(i);
                  if (itemstack1.isEmpty()) {
                     this.tableInventory.setInventorySlotContents(1, ItemStack.EMPTY);
                  }
               }

               playerIn.addStat(Stats.ENCHANT_ITEM);
               if (playerIn instanceof ServerPlayerEntity) {
                  CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity)playerIn, itemstack2, i);
               }

               this.tableInventory.markDirty();
               this.xpSeed.set(playerIn.getXPSeed());
               this.onCraftMatrixChanged(this.tableInventory);
               p_217003_6_.playSound((PlayerEntity)null, p_217003_7_, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, p_217003_6_.rand.nextFloat() * 0.1F + 0.9F);
            }

         });
         return true;
      }
   }

   private List<EnchantmentData> getEnchantmentList(ItemStack stack, int enchantSlot, int level) {
      this.rand.setSeed((long)(this.xpSeed.get() + enchantSlot));
      List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(this.rand, stack, level, false);
      if (stack.getItem() == Items.BOOK && list.size() > 1) {
         list.remove(this.rand.nextInt(list.size()));
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLapisAmount() {
      ItemStack itemstack = this.tableInventory.getStackInSlot(1);
      return itemstack.isEmpty() ? 0 : itemstack.getCount();
   }

   @OnlyIn(Dist.CLIENT)
   public int func_217005_f() {
      return this.xpSeed.get();
   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(PlayerEntity playerIn) {
      super.onContainerClosed(playerIn);
      this.field_217006_g.consume((p_217004_2_, p_217004_3_) -> {
         this.clearContainer(playerIn, playerIn.world, this.tableInventory);
      });
   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(PlayerEntity playerIn) {
      return isWithinUsableDistance(this.field_217006_g, playerIn, Blocks.ENCHANTING_TABLE);
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
         if (index == 0) {
            if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (index == 1) {
            if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (itemstack1.getItem() == Items.LAPIS_LAZULI) {
            if (!this.mergeItemStack(itemstack1, 1, 2, true)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(itemstack1)) {
               return ItemStack.EMPTY;
            }

            if (itemstack1.hasTag()) { // Forge: Fix MC-17431
               ((Slot)this.inventorySlots.get(0)).putStack(itemstack1.split(1));
            } else if (!itemstack1.isEmpty()) {
               this.inventorySlots.get(0).putStack(new ItemStack(itemstack1.getItem()));
               itemstack1.shrink(1);
            }
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
}