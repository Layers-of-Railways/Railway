package net.minecraft.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;

public abstract class LockableLootTileEntity extends LockableTileEntity {
   @Nullable
   protected ResourceLocation lootTable;
   protected long lootTableSeed;

   protected LockableLootTileEntity(TileEntityType<?> typeIn) {
      super(typeIn);
   }

   public static void setLootTable(IBlockReader reader, Random rand, BlockPos p_195479_2_, ResourceLocation lootTableIn) {
      TileEntity tileentity = reader.getTileEntity(p_195479_2_);
      if (tileentity instanceof LockableLootTileEntity) {
         ((LockableLootTileEntity)tileentity).setLootTable(lootTableIn, rand.nextLong());
      }

   }

   protected boolean checkLootAndRead(CompoundNBT compound) {
      if (compound.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(compound.getString("LootTable"));
         this.lootTableSeed = compound.getLong("LootTableSeed");
         return true;
      } else {
         return false;
      }
   }

   protected boolean checkLootAndWrite(CompoundNBT compound) {
      if (this.lootTable == null) {
         return false;
      } else {
         compound.putString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            compound.putLong("LootTableSeed", this.lootTableSeed);
         }

         return true;
      }
   }

   public void fillWithLoot(@Nullable PlayerEntity player) {
      if (this.lootTable != null && this.world.getServer() != null) {
         LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
         this.lootTable = null;
         LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withParameter(LootParameters.POSITION, new BlockPos(this.pos)).withSeed(this.lootTableSeed);
         if (player != null) {
            lootcontext$builder.withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
         }

         loottable.fillInventory(this, lootcontext$builder.build(LootParameterSets.CHEST));
      }

   }

   public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
      this.lootTable = lootTableIn;
      this.lootTableSeed = seedIn;
   }

   public boolean isEmpty() {
      this.fillWithLoot((PlayerEntity)null);
      return this.getItems().stream().allMatch(ItemStack::isEmpty);
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      this.fillWithLoot((PlayerEntity)null);
      return this.getItems().get(index);
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      this.fillWithLoot((PlayerEntity)null);
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.getItems(), index, count);
      if (!itemstack.isEmpty()) {
         this.markDirty();
      }

      return itemstack;
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      this.fillWithLoot((PlayerEntity)null);
      return ItemStackHelper.getAndRemove(this.getItems(), index);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.fillWithLoot((PlayerEntity)null);
      this.getItems().set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
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

   public void clear() {
      this.getItems().clear();
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> itemsIn);

   public boolean canOpen(PlayerEntity p_213904_1_) {
      return super.canOpen(p_213904_1_) && (this.lootTable == null || !p_213904_1_.isSpectator());
   }

   @Nullable
   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      if (this.canOpen(p_createMenu_3_)) {
         this.fillWithLoot(p_createMenu_2_.player);
         return this.createMenu(p_createMenu_1_, p_createMenu_2_);
      } else {
         return null;
      }
   }
}