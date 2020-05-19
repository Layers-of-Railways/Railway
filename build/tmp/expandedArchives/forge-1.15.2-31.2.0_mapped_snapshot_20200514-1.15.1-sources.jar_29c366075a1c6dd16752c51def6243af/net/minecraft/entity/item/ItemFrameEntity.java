package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemFrameEntity extends HangingEntity {
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
   private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ItemFrameEntity.class, DataSerializers.ITEMSTACK);
   private static final DataParameter<Integer> ROTATION = EntityDataManager.createKey(ItemFrameEntity.class, DataSerializers.VARINT);
   private float itemDropChance = 1.0F;

   public ItemFrameEntity(EntityType<? extends ItemFrameEntity> p_i50224_1_, World p_i50224_2_) {
      super(p_i50224_1_, p_i50224_2_);
   }

   public ItemFrameEntity(World worldIn, BlockPos p_i45852_2_, Direction p_i45852_3_) {
      super(EntityType.ITEM_FRAME, worldIn, p_i45852_2_);
      this.updateFacingWithBoundingBox(p_i45852_3_);
   }

   protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 0.0F;
   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
      this.getDataManager().register(ROTATION, 0);
   }

   /**
    * Updates facing and bounding box based on it
    */
   protected void updateFacingWithBoundingBox(Direction facingDirectionIn) {
      Validate.notNull(facingDirectionIn);
      this.facingDirection = facingDirectionIn;
      if (facingDirectionIn.getAxis().isHorizontal()) {
         this.rotationPitch = 0.0F;
         this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
      } else {
         this.rotationPitch = (float)(-90 * facingDirectionIn.getAxisDirection().getOffset());
         this.rotationYaw = 0.0F;
      }

      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      this.updateBoundingBox();
   }

   /**
    * Updates the entity bounding box based on current facing
    */
   protected void updateBoundingBox() {
      if (this.facingDirection != null) {
         double d0 = 0.46875D;
         double d1 = (double)this.hangingPosition.getX() + 0.5D - (double)this.facingDirection.getXOffset() * 0.46875D;
         double d2 = (double)this.hangingPosition.getY() + 0.5D - (double)this.facingDirection.getYOffset() * 0.46875D;
         double d3 = (double)this.hangingPosition.getZ() + 0.5D - (double)this.facingDirection.getZOffset() * 0.46875D;
         this.setRawPosition(d1, d2, d3);
         double d4 = (double)this.getWidthPixels();
         double d5 = (double)this.getHeightPixels();
         double d6 = (double)this.getWidthPixels();
         Direction.Axis direction$axis = this.facingDirection.getAxis();
         switch(direction$axis) {
         case X:
            d4 = 1.0D;
            break;
         case Y:
            d5 = 1.0D;
            break;
         case Z:
            d6 = 1.0D;
         }

         d4 = d4 / 32.0D;
         d5 = d5 / 32.0D;
         d6 = d6 / 32.0D;
         this.setBoundingBox(new AxisAlignedBB(d1 - d4, d2 - d5, d3 - d6, d1 + d4, d2 + d5, d3 + d6));
      }
   }

   /**
    * checks to make sure painting can be placed there
    */
   public boolean onValidSurface() {
      if (!this.world.hasNoCollisions(this)) {
         return false;
      } else {
         BlockState blockstate = this.world.getBlockState(this.hangingPosition.offset(this.facingDirection.getOpposite()));
         return blockstate.getMaterial().isSolid() || this.facingDirection.getAxis().isHorizontal() && RedstoneDiodeBlock.isDiode(blockstate) ? this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), IS_HANGING_ENTITY).isEmpty() : false;
      }
   }

   public float getCollisionBorderSize() {
      return 0.0F;
   }

   /**
    * Called by the /kill command.
    */
   public void onKillCommand() {
      this.removeItem(this.getDisplayedItem());
      super.onKillCommand();
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (!source.isExplosion() && !this.getDisplayedItem().isEmpty()) {
         if (!this.world.isRemote) {
            this.dropItemOrSelf(source.getTrueSource(), false);
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.attackEntityFrom(source, amount);
      }
   }

   public int getWidthPixels() {
      return 12;
   }

   public int getHeightPixels() {
      return 12;
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      double d0 = 16.0D;
      d0 = d0 * 64.0D * getRenderDistanceWeight();
      return distance < d0 * d0;
   }

   /**
    * Called when this entity is broken. Entity parameter may be null.
    */
   public void onBroken(@Nullable Entity brokenEntity) {
      this.playSound(SoundEvents.ENTITY_ITEM_FRAME_BREAK, 1.0F, 1.0F);
      this.dropItemOrSelf(brokenEntity, true);
   }

   public void playPlaceSound() {
      this.playSound(SoundEvents.ENTITY_ITEM_FRAME_PLACE, 1.0F, 1.0F);
   }

   private void dropItemOrSelf(@Nullable Entity entityIn, boolean p_146065_2_) {
      if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         if (entityIn == null) {
            this.removeItem(this.getDisplayedItem());
         }

      } else {
         ItemStack itemstack = this.getDisplayedItem();
         this.setDisplayedItem(ItemStack.EMPTY);
         if (entityIn instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)entityIn;
            if (playerentity.abilities.isCreativeMode) {
               this.removeItem(itemstack);
               return;
            }
         }

         if (p_146065_2_) {
            this.entityDropItem(Items.ITEM_FRAME);
         }

         if (!itemstack.isEmpty()) {
            itemstack = itemstack.copy();
            this.removeItem(itemstack);
            if (this.rand.nextFloat() < this.itemDropChance) {
               this.entityDropItem(itemstack);
            }
         }

      }
   }

   /**
    * Removes the dot representing this frame's position from the map when the item frame is broken.
    */
   private void removeItem(ItemStack stack) {
      if (stack.getItem() instanceof net.minecraft.item.FilledMapItem) {
         MapData mapdata = FilledMapItem.getMapData(stack, this.world);
         mapdata.removeItemFrame(this.hangingPosition, this.getEntityId());
         mapdata.setDirty(true);
      }

      stack.setItemFrame((ItemFrameEntity)null);
   }

   public ItemStack getDisplayedItem() {
      return this.getDataManager().get(ITEM);
   }

   public void setDisplayedItem(ItemStack stack) {
      this.setDisplayedItemWithUpdate(stack, true);
   }

   public void setDisplayedItemWithUpdate(ItemStack stack, boolean p_174864_2_) {
      if (!stack.isEmpty()) {
         stack = stack.copy();
         stack.setCount(1);
         stack.setItemFrame(this);
      }

      this.getDataManager().set(ITEM, stack);
      if (!stack.isEmpty()) {
         this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
      }

      if (p_174864_2_ && this.hangingPosition != null) {
         this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
      }

   }

   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      if (inventorySlot == 0) {
         this.setDisplayedItem(itemStackIn);
         return true;
      } else {
         return false;
      }
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (key.equals(ITEM)) {
         ItemStack itemstack = this.getDisplayedItem();
         if (!itemstack.isEmpty() && itemstack.getItemFrame() != this) {
            itemstack.setItemFrame(this);
         }
      }

   }

   /**
    * Return the rotation of the item currently on this frame.
    */
   public int getRotation() {
      return this.getDataManager().get(ROTATION);
   }

   public void setItemRotation(int rotationIn) {
      this.setRotation(rotationIn, true);
   }

   private void setRotation(int rotationIn, boolean p_174865_2_) {
      this.getDataManager().set(ROTATION, rotationIn % 8);
      if (p_174865_2_ && this.hangingPosition != null) {
         this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      if (!this.getDisplayedItem().isEmpty()) {
         compound.put("Item", this.getDisplayedItem().write(new CompoundNBT()));
         compound.putByte("ItemRotation", (byte)this.getRotation());
         compound.putFloat("ItemDropChance", this.itemDropChance);
      }

      compound.putByte("Facing", (byte)this.facingDirection.getIndex());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      CompoundNBT compoundnbt = compound.getCompound("Item");
      if (compoundnbt != null && !compoundnbt.isEmpty()) {
         ItemStack itemstack = ItemStack.read(compoundnbt);
         if (itemstack.isEmpty()) {
            PRIVATE_LOGGER.warn("Unable to load item from: {}", (Object)compoundnbt);
         }

         ItemStack itemstack1 = this.getDisplayedItem();
         if (!itemstack1.isEmpty() && !ItemStack.areItemStacksEqual(itemstack, itemstack1)) {
            this.removeItem(itemstack1);
         }

         this.setDisplayedItemWithUpdate(itemstack, false);
         this.setRotation(compound.getByte("ItemRotation"), false);
         if (compound.contains("ItemDropChance", 99)) {
            this.itemDropChance = compound.getFloat("ItemDropChance");
         }
      }

      this.updateFacingWithBoundingBox(Direction.byIndex(compound.getByte("Facing")));
   }

   public boolean processInitialInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      boolean flag = !this.getDisplayedItem().isEmpty();
      boolean flag1 = !itemstack.isEmpty();
      if (!this.world.isRemote) {
         if (!flag) {
            if (flag1) {
               this.setDisplayedItem(itemstack);
               if (!player.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }
            }
         } else {
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            this.setItemRotation(this.getRotation() + 1);
         }

         return true;
      } else {
         return flag || flag1;
      }
   }

   public int getAnalogOutput() {
      return this.getDisplayedItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this, this.getType(), this.facingDirection.getIndex(), this.getHangingPosition());
   }
}