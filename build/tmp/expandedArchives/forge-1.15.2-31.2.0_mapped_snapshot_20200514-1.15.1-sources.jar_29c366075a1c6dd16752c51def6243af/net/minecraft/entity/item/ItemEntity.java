package net.minecraft.entity.item;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemEntity extends Entity {
   private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ItemEntity.class, DataSerializers.ITEMSTACK);
   private int age;
   private int pickupDelay;
   private int health = 5;
   private UUID thrower;
   private UUID owner;
   /**
    * The maximum age of this EntityItem.  The item is expired once this is reached.
    */
   public int lifespan = 6000;
   public final float hoverStart = (float)(Math.random() * Math.PI * 2.0D);

   public ItemEntity(EntityType<? extends ItemEntity> p_i50217_1_, World p_i50217_2_) {
      super(p_i50217_1_, p_i50217_2_);
   }

   public ItemEntity(World worldIn, double x, double y, double z) {
      this(EntityType.ITEM, worldIn);
      this.setPosition(x, y, z);
      this.rotationYaw = this.rand.nextFloat() * 360.0F;
      this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
   }

   public ItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn, x, y, z);
      this.setItem(stack);
      this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(worldIn));
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (getItem().onEntityItemUpdate(this)) return;
      if (this.getItem().isEmpty()) {
         this.remove();
      } else {
         super.tick();
         if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
         }

         this.prevPosX = this.getPosX();
         this.prevPosY = this.getPosY();
         this.prevPosZ = this.getPosZ();
         Vec3d vec3d = this.getMotion();
         if (this.areEyesInFluid(FluidTags.WATER)) {
            this.applyFloatMotion();
         } else if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
         }

         if (this.world.isRemote) {
            this.noClip = false;
         } else {
            this.noClip = !this.world.hasNoCollisions(this);
            if (this.noClip) {
               this.pushOutOfBlocks(this.getPosX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getPosZ());
            }
         }

         if (!this.onGround || horizontalMag(this.getMotion()) > (double)1.0E-5F || (this.ticksExisted + this.getEntityId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getMotion());
            float f = 0.98F;
            if (this.onGround) {
               BlockPos pos = new BlockPos(this.getPosX(), this.getPosY() - 1.0D, this.getPosZ());
               f = this.world.getBlockState(pos).getSlipperiness(this.world, pos, this) * 0.98F;
            }

            this.setMotion(this.getMotion().mul((double)f, 0.98D, (double)f));
            if (this.onGround) {
               this.setMotion(this.getMotion().mul(1.0D, -0.5D, 1.0D));
            }
         }

         boolean flag = MathHelper.floor(this.prevPosX) != MathHelper.floor(this.getPosX()) || MathHelper.floor(this.prevPosY) != MathHelper.floor(this.getPosY()) || MathHelper.floor(this.prevPosZ) != MathHelper.floor(this.getPosZ());
         int i = flag ? 2 : 40;
         if (this.ticksExisted % i == 0) {
            if (this.world.getFluidState(new BlockPos(this)).isTagged(FluidTags.LAVA)) {
               this.setMotion((double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F), (double)0.2F, (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F));
               this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
            }

            if (!this.world.isRemote && this.func_213857_z()) {
               this.searchForOtherItemsNearby();
            }
         }

         if (this.age != -32768) {
            ++this.age;
         }

         this.isAirBorne |= this.handleWaterMovement();
         if (!this.world.isRemote) {
            double d0 = this.getMotion().subtract(vec3d).lengthSquared();
            if (d0 > 0.01D) {
               this.isAirBorne = true;
            }
         }

         ItemStack item = this.getItem();
         if (!this.world.isRemote && this.age >= lifespan) {
             int hook = net.minecraftforge.event.ForgeEventFactory.onItemExpire(this, item);
             if (hook < 0) this.remove();
             else          this.lifespan += hook;
         }

         if (item.isEmpty()) {
            this.remove();
         }

      }
   }

   private void applyFloatMotion() {
      Vec3d vec3d = this.getMotion();
      this.setMotion(vec3d.x * (double)0.99F, vec3d.y + (double)(vec3d.y < (double)0.06F ? 5.0E-4F : 0.0F), vec3d.z * (double)0.99F);
   }

   /**
    * Looks for other itemstacks nearby and tries to stack them together
    */
   private void searchForOtherItemsNearby() {
      if (this.func_213857_z()) {
         for(ItemEntity itementity : this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(0.5D, 0.0D, 0.5D), (p_213859_1_) -> {
            return p_213859_1_ != this && p_213859_1_.func_213857_z();
         })) {
            if (itementity.func_213857_z()) {
               this.func_226530_a_(itementity);
               if (this.removed) {
                  break;
               }
            }
         }

      }
   }

   private boolean func_213857_z() {
      ItemStack itemstack = this.getItem();
      return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && itemstack.getCount() < itemstack.getMaxStackSize();
   }

   private void func_226530_a_(ItemEntity p_226530_1_) {
      ItemStack itemstack = this.getItem();
      ItemStack itemstack1 = p_226530_1_.getItem();
      if (Objects.equals(this.getOwnerId(), p_226530_1_.getOwnerId()) && func_226532_a_(itemstack, itemstack1)) {
         if (itemstack1.getCount() < itemstack.getCount()) {
            func_213858_a(this, itemstack, p_226530_1_, itemstack1);
         } else {
            func_213858_a(p_226530_1_, itemstack1, this, itemstack);
         }

      }
   }

   public static boolean func_226532_a_(ItemStack p_226532_0_, ItemStack p_226532_1_) {
      if (p_226532_1_.getItem() != p_226532_0_.getItem()) {
         return false;
      } else if (p_226532_1_.getCount() + p_226532_0_.getCount() > p_226532_1_.getMaxStackSize()) {
         return false;
      } else if (p_226532_1_.hasTag() ^ p_226532_0_.hasTag()) {
         return false;
      }else if (!p_226532_0_.areCapsCompatible(p_226532_1_)) {
         return false;
      } else {
         return !p_226532_1_.hasTag() || p_226532_1_.getTag().equals(p_226532_0_.getTag());
      }
   }

   public static ItemStack func_226533_a_(ItemStack p_226533_0_, ItemStack p_226533_1_, int p_226533_2_) {
      int i = Math.min(Math.min(p_226533_0_.getMaxStackSize(), p_226533_2_) - p_226533_0_.getCount(), p_226533_1_.getCount());
      ItemStack itemstack = p_226533_0_.copy();
      itemstack.grow(i);
      p_226533_1_.shrink(i);
      return itemstack;
   }

   private static void func_226531_a_(ItemEntity p_226531_0_, ItemStack p_226531_1_, ItemStack p_226531_2_) {
      ItemStack itemstack = func_226533_a_(p_226531_1_, p_226531_2_, 64);
      p_226531_0_.setItem(itemstack);
   }

   private static void func_213858_a(ItemEntity p_213858_0_, ItemStack p_213858_1_, ItemEntity p_213858_2_, ItemStack p_213858_3_) {
      func_226531_a_(p_213858_0_, p_213858_1_, p_213858_3_);
      p_213858_0_.pickupDelay = Math.max(p_213858_0_.pickupDelay, p_213858_2_.pickupDelay);
      p_213858_0_.age = Math.min(p_213858_0_.age, p_213858_2_.age);
      if (p_213858_3_.isEmpty()) {
         p_213858_2_.remove();
      }

   }

   /**
    * Will deal the specified amount of fire damage to the entity if the entity isn't immune to fire damage.
    */
   protected void dealFireDamage(int amount) {
      this.attackEntityFrom(DamageSource.IN_FIRE, (float)amount);
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.world.isRemote || this.removed) return false; //Forge: Fixes MC-53850
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && source.isExplosion()) {
         return false;
      } else {
         this.markVelocityChanged();
         this.health = (int)((float)this.health - amount);
         if (this.health <= 0) {
            this.remove();
         }

         return false;
      }
   }

   public void writeAdditional(CompoundNBT compound) {
      compound.putShort("Health", (short)this.health);
      compound.putShort("Age", (short)this.age);
      compound.putShort("PickupDelay", (short)this.pickupDelay);
      compound.putInt("Lifespan", lifespan);
      if (this.getThrowerId() != null) {
         compound.put("Thrower", NBTUtil.writeUniqueId(this.getThrowerId()));
      }

      if (this.getOwnerId() != null) {
         compound.put("Owner", NBTUtil.writeUniqueId(this.getOwnerId()));
      }

      if (!this.getItem().isEmpty()) {
         compound.put("Item", this.getItem().write(new CompoundNBT()));
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      this.health = compound.getShort("Health");
      this.age = compound.getShort("Age");
      if (compound.contains("PickupDelay")) {
         this.pickupDelay = compound.getShort("PickupDelay");
      }
      if (compound.contains("Lifespan")) lifespan = compound.getInt("Lifespan");

      if (compound.contains("Owner", 10)) {
         this.owner = NBTUtil.readUniqueId(compound.getCompound("Owner"));
      }

      if (compound.contains("Thrower", 10)) {
         this.thrower = NBTUtil.readUniqueId(compound.getCompound("Thrower"));
      }

      CompoundNBT compoundnbt = compound.getCompound("Item");
      this.setItem(ItemStack.read(compoundnbt));
      if (this.getItem().isEmpty()) {
         this.remove();
      }

   }

   /**
    * Called by a player entity when they collide with an entity
    */
   public void onCollideWithPlayer(PlayerEntity entityIn) {
      if (!this.world.isRemote) {
         if (this.pickupDelay > 0) return;
         ItemStack itemstack = this.getItem();
         Item item = itemstack.getItem();
         int i = itemstack.getCount();

         int hook = net.minecraftforge.event.ForgeEventFactory.onItemPickup(this, entityIn);
         if (hook < 0) return;

         ItemStack copy = itemstack.copy();
         if (this.pickupDelay == 0 && (this.owner == null || lifespan - this.age <= 200 || this.owner.equals(entityIn.getUniqueID())) && (hook == 1 || i <= 0 || entityIn.inventory.addItemStackToInventory(itemstack))) {
            copy.setCount(copy.getCount() - getItem().getCount());
            net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerItemPickupEvent(entityIn, this, copy);
            entityIn.onItemPickup(this, i);
            if (itemstack.isEmpty()) {
               entityIn.onItemPickup(this, i);
               this.remove();
               itemstack.setCount(i);
            }

            entityIn.addStat(Stats.ITEM_PICKED_UP.get(item), i);
         }

      }
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return (ITextComponent)(itextcomponent != null ? itextcomponent : new TranslationTextComponent(this.getItem().getTranslationKey()));
   }

   /**
    * Returns true if it's possible to attack this entity with an item.
    */
   public boolean canBeAttackedWithItem() {
      return false;
   }

   @Nullable
   public Entity changeDimension(DimensionType destination, net.minecraftforge.common.util.ITeleporter teleporter) {
      Entity entity = super.changeDimension(destination, teleporter);
      if (!this.world.isRemote && entity instanceof ItemEntity) {
         ((ItemEntity)entity).searchForOtherItemsNearby();
      }

      return entity;
   }

   /**
    * Gets the item that this entity represents.
    */
   public ItemStack getItem() {
      return this.getDataManager().get(ITEM);
   }

   /**
    * Sets the item that this entity represents.
    */
   public void setItem(ItemStack stack) {
      this.getDataManager().set(ITEM, stack);
   }

   @Nullable
   public UUID getOwnerId() {
      return this.owner;
   }

   public void setOwnerId(@Nullable UUID p_200217_1_) {
      this.owner = p_200217_1_;
   }

   @Nullable
   public UUID getThrowerId() {
      return this.thrower;
   }

   public void setThrowerId(@Nullable UUID p_200216_1_) {
      this.thrower = p_200216_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAge() {
      return this.age;
   }

   public void setDefaultPickupDelay() {
      this.pickupDelay = 10;
   }

   public void setNoPickupDelay() {
      this.pickupDelay = 0;
   }

   public void setInfinitePickupDelay() {
      this.pickupDelay = 32767;
   }

   public void setPickupDelay(int ticks) {
      this.pickupDelay = ticks;
   }

   public boolean cannotPickup() {
      return this.pickupDelay > 0;
   }

   public void setNoDespawn() {
      this.age = -6000;
   }

   public void makeFakeItem() {
      this.setInfinitePickupDelay();
      this.age = getItem().getEntityLifespan(world) - 1;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }
}