package net.minecraft.tileentity;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.CampfireBlock;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CampfireTileEntity extends TileEntity implements IClearable, ITickableTileEntity {
   private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
   private final int[] cookingTimes = new int[4];
   private final int[] cookingTotalTimes = new int[4];

   public CampfireTileEntity() {
      super(TileEntityType.CAMPFIRE);
   }

   public void tick() {
      boolean flag = this.getBlockState().get(CampfireBlock.LIT);
      boolean flag1 = this.world.isRemote;
      if (flag1) {
         if (flag) {
            this.addParticles();
         }

      } else {
         if (flag) {
            this.cookAndDrop();
         } else {
            for(int i = 0; i < this.inventory.size(); ++i) {
               if (this.cookingTimes[i] > 0) {
                  this.cookingTimes[i] = MathHelper.clamp(this.cookingTimes[i] - 2, 0, this.cookingTotalTimes[i]);
               }
            }
         }

      }
   }

   /**
    * Individually tracks the cooking of each item, then spawns the finished product in-world and clears the
    * corresponding held item.
    */
   private void cookAndDrop() {
      for(int i = 0; i < this.inventory.size(); ++i) {
         ItemStack itemstack = this.inventory.get(i);
         if (!itemstack.isEmpty()) {
            ++this.cookingTimes[i];
            if (this.cookingTimes[i] >= this.cookingTotalTimes[i]) {
               IInventory iinventory = new Inventory(itemstack);
               ItemStack itemstack1 = this.world.getRecipeManager().getRecipe(IRecipeType.CAMPFIRE_COOKING, iinventory, this.world).map((p_213979_1_) -> {
                  return p_213979_1_.getCraftingResult(iinventory);
               }).orElse(itemstack);
               BlockPos blockpos = this.getPos();
               InventoryHelper.spawnItemStack(this.world, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), itemstack1);
               this.inventory.set(i, ItemStack.EMPTY);
               this.inventoryChanged();
            }
         }
      }

   }

   private void addParticles() {
      World world = this.getWorld();
      if (world != null) {
         BlockPos blockpos = this.getPos();
         Random random = world.rand;
         if (random.nextFloat() < 0.11F) {
            for(int i = 0; i < random.nextInt(2) + 2; ++i) {
               CampfireBlock.spawnSmokeParticles(world, blockpos, this.getBlockState().get(CampfireBlock.SIGNAL_FIRE), false);
            }
         }

         int l = this.getBlockState().get(CampfireBlock.FACING).getHorizontalIndex();

         for(int j = 0; j < this.inventory.size(); ++j) {
            if (!this.inventory.get(j).isEmpty() && random.nextFloat() < 0.2F) {
               Direction direction = Direction.byHorizontalIndex(Math.floorMod(j + l, 4));
               float f = 0.3125F;
               double d0 = (double)blockpos.getX() + 0.5D - (double)((float)direction.getXOffset() * 0.3125F) + (double)((float)direction.rotateY().getXOffset() * 0.3125F);
               double d1 = (double)blockpos.getY() + 0.5D;
               double d2 = (double)blockpos.getZ() + 0.5D - (double)((float)direction.getZOffset() * 0.3125F) + (double)((float)direction.rotateY().getZOffset() * 0.3125F);

               for(int k = 0; k < 4; ++k) {
                  world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 5.0E-4D, 0.0D);
               }
            }
         }

      }
   }

   /**
    * Returns a NonNullList<ItemStack> of items currently held in the campfire.
    */
   public NonNullList<ItemStack> getInventory() {
      return this.inventory;
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      this.inventory.clear();
      ItemStackHelper.loadAllItems(compound, this.inventory);
      if (compound.contains("CookingTimes", 11)) {
         int[] aint = compound.getIntArray("CookingTimes");
         System.arraycopy(aint, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, aint.length));
      }

      if (compound.contains("CookingTotalTimes", 11)) {
         int[] aint1 = compound.getIntArray("CookingTotalTimes");
         System.arraycopy(aint1, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, aint1.length));
      }

   }

   public CompoundNBT write(CompoundNBT compound) {
      this.writeItems(compound);
      compound.putIntArray("CookingTimes", this.cookingTimes);
      compound.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
      return compound;
   }

   private CompoundNBT writeItems(CompoundNBT compound) {
      super.write(compound);
      ItemStackHelper.saveAllItems(compound, this.inventory, true);
      return compound;
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 13, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public CompoundNBT getUpdateTag() {
      return this.writeItems(new CompoundNBT());
   }

   public Optional<CampfireCookingRecipe> findMatchingRecipe(ItemStack itemStackIn) {
      return this.inventory.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.world.getRecipeManager().getRecipe(IRecipeType.CAMPFIRE_COOKING, new Inventory(itemStackIn), this.world);
   }

   public boolean addItem(ItemStack itemStackIn, int cookTime) {
      for(int i = 0; i < this.inventory.size(); ++i) {
         ItemStack itemstack = this.inventory.get(i);
         if (itemstack.isEmpty()) {
            this.cookingTotalTimes[i] = cookTime;
            this.cookingTimes[i] = 0;
            this.inventory.set(i, itemStackIn.split(1));
            this.inventoryChanged();
            return true;
         }
      }

      return false;
   }

   private void inventoryChanged() {
      this.markDirty();
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public void clear() {
      this.inventory.clear();
   }

   public void dropAllItems() {
      if (!this.getWorld().isRemote) {
         InventoryHelper.dropItems(this.getWorld(), this.getPos(), this.getInventory());
      }

      this.inventoryChanged();
   }
}