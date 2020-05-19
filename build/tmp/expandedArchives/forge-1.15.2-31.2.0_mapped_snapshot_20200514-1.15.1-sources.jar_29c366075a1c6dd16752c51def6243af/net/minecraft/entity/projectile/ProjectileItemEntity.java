package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public abstract class ProjectileItemEntity extends ThrowableEntity implements IRendersAsItem {
   private static final DataParameter<ItemStack> ITEMSTACK_DATA = EntityDataManager.createKey(ProjectileItemEntity.class, DataSerializers.ITEMSTACK);

   public ProjectileItemEntity(EntityType<? extends ProjectileItemEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public ProjectileItemEntity(EntityType<? extends ProjectileItemEntity> type, double x, double y, double z, World worldIn) {
      super(type, x, y, z, worldIn);
   }

   public ProjectileItemEntity(EntityType<? extends ProjectileItemEntity> type, LivingEntity livingEntityIn, World worldIn) {
      super(type, livingEntityIn, worldIn);
   }

   public void setItem(ItemStack stack) {
      if (stack.getItem() != this.getDefaultItem() || stack.hasTag()) {
         this.getDataManager().set(ITEMSTACK_DATA, Util.make(stack.copy(), (p_213883_0_) -> {
            p_213883_0_.setCount(1);
         }));
      }

   }

   protected abstract Item getDefaultItem();

   protected ItemStack func_213882_k() {
      return this.getDataManager().get(ITEMSTACK_DATA);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      ItemStack itemstack = this.func_213882_k();
      return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
   }

   protected void registerData() {
      this.getDataManager().register(ITEMSTACK_DATA, ItemStack.EMPTY);
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      ItemStack itemstack = this.func_213882_k();
      if (!itemstack.isEmpty()) {
         compound.put("Item", itemstack.write(new CompoundNBT()));
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      ItemStack itemstack = ItemStack.read(compound.getCompound("Item"));
      this.setItem(itemstack);
   }
}