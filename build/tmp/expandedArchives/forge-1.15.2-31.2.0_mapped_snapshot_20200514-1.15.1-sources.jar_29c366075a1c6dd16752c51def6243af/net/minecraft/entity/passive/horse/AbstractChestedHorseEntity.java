package net.minecraft.entity.passive.horse;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public abstract class AbstractChestedHorseEntity extends AbstractHorseEntity {
   private static final DataParameter<Boolean> DATA_ID_CHEST = EntityDataManager.createKey(AbstractChestedHorseEntity.class, DataSerializers.BOOLEAN);

   protected AbstractChestedHorseEntity(EntityType<? extends AbstractChestedHorseEntity> type, World worldIn) {
      super(type, worldIn);
      this.canGallop = false;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(DATA_ID_CHEST, false);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.175F);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(0.5D);
   }

   public boolean hasChest() {
      return this.dataManager.get(DATA_ID_CHEST);
   }

   public void setChested(boolean chested) {
      this.dataManager.set(DATA_ID_CHEST, chested);
   }

   protected int getInventorySize() {
      return this.hasChest() ? 17 : super.getInventorySize();
   }

   /**
    * Returns the Y offset from the entity's position for any entity riding this one.
    */
   public double getMountedYOffset() {
      return super.getMountedYOffset() - 0.25D;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.ENTITY_DONKEY_ANGRY;
   }

   protected void dropInventory() {
      super.dropInventory();
      if (this.hasChest()) {
         if (!this.world.isRemote) {
            this.entityDropItem(Blocks.CHEST);
         }

         this.setChested(false);
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putBoolean("ChestedHorse", this.hasChest());
      if (this.hasChest()) {
         ListNBT listnbt = new ListNBT();

         for(int i = 2; i < this.horseChest.getSizeInventory(); ++i) {
            ItemStack itemstack = this.horseChest.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               CompoundNBT compoundnbt = new CompoundNBT();
               compoundnbt.putByte("Slot", (byte)i);
               itemstack.write(compoundnbt);
               listnbt.add(compoundnbt);
            }
         }

         compound.put("Items", listnbt);
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setChested(compound.getBoolean("ChestedHorse"));
      if (this.hasChest()) {
         ListNBT listnbt = compound.getList("Items", 10);
         this.initHorseChest();

         for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;
            if (j >= 2 && j < this.horseChest.getSizeInventory()) {
               this.horseChest.setInventorySlotContents(j, ItemStack.read(compoundnbt));
            }
         }
      }

      this.updateHorseSlots();
   }

   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      if (inventorySlot == 499) {
         if (this.hasChest() && itemStackIn.isEmpty()) {
            this.setChested(false);
            this.initHorseChest();
            return true;
         }

         if (!this.hasChest() && itemStackIn.getItem() == Blocks.CHEST.asItem()) {
            this.setChested(true);
            this.initHorseChest();
            return true;
         }
      }

      return super.replaceItemInInventory(inventorySlot, itemStackIn);
   }

   public boolean processInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() instanceof SpawnEggItem) {
         return super.processInteract(player, hand);
      } else {
         if (!this.isChild()) {
            if (this.isTame() && player.isSecondaryUseActive()) {
               this.openGUI(player);
               return true;
            }

            if (this.isBeingRidden()) {
               return super.processInteract(player, hand);
            }
         }

         if (!itemstack.isEmpty()) {
            boolean flag = this.handleEating(player, itemstack);
            if (!flag) {
               if (!this.isTame() || itemstack.getItem() == Items.NAME_TAG) {
                  if (itemstack.interactWithEntity(player, this, hand)) {
                     return true;
                  } else {
                     this.makeMad();
                     return true;
                  }
               }

               if (!this.hasChest() && itemstack.getItem() == Blocks.CHEST.asItem()) {
                  this.setChested(true);
                  this.playChestEquipSound();
                  flag = true;
                  this.initHorseChest();
               }

               if (!this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE) {
                  this.openGUI(player);
                  return true;
               }
            }

            if (flag) {
               if (!player.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               return true;
            }
         }

         if (this.isChild()) {
            return super.processInteract(player, hand);
         } else {
            this.mountTo(player);
            return true;
         }
      }
   }

   protected void playChestEquipSound() {
      this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
   }

   public int getInventoryColumns() {
      return 5;
   }
}