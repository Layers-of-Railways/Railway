package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.INameable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LockCode;

public abstract class LockableTileEntity extends TileEntity implements IInventory, INamedContainerProvider, INameable {
   private LockCode code = LockCode.EMPTY_CODE;
   private ITextComponent customName;

   protected LockableTileEntity(TileEntityType<?> typeIn) {
      super(typeIn);
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      this.code = LockCode.read(compound);
      if (compound.contains("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
      }

   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      this.code.write(compound);
      if (this.customName != null) {
         compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
      }

      return compound;
   }

   public void setCustomName(ITextComponent name) {
      this.customName = name;
   }

   public ITextComponent getName() {
      return this.customName != null ? this.customName : this.getDefaultName();
   }

   public ITextComponent getDisplayName() {
      return this.getName();
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customName;
   }

   protected abstract ITextComponent getDefaultName();

   public boolean canOpen(PlayerEntity p_213904_1_) {
      return canUnlock(p_213904_1_, this.code, this.getDisplayName());
   }

   public static boolean canUnlock(PlayerEntity p_213905_0_, LockCode p_213905_1_, ITextComponent p_213905_2_) {
      if (!p_213905_0_.isSpectator() && !p_213905_1_.func_219964_a(p_213905_0_.getHeldItemMainhand())) {
         p_213905_0_.sendStatusMessage(new TranslationTextComponent("container.isLocked", p_213905_2_), true);
         p_213905_0_.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      return this.canOpen(p_createMenu_3_) ? this.createMenu(p_createMenu_1_, p_createMenu_2_) : null;
   }

   protected abstract Container createMenu(int id, PlayerInventory player);

   private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> createUnSidedHandler());
   protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
      return new net.minecraftforge.items.wrapper.InvWrapper(this);
   }

   @javax.annotation.Nullable
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @javax.annotation.Nullable net.minecraft.util.Direction side) {
      if (!this.removed && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY )
         return itemHandler.cast();
      return super.getCapability(cap, side);
   }

   /**
    * invalidates a tile entity
    */
   @Override
   public void remove() {
      super.remove();
      itemHandler.invalidate();
   }
}