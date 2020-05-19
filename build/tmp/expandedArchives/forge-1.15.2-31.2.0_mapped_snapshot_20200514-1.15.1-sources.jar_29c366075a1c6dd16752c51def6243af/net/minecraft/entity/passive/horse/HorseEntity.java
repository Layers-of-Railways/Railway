package net.minecraft.entity.passive.horse;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HorseEntity extends AbstractHorseEntity {
   private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
   private static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.createKey(HorseEntity.class, DataSerializers.VARINT);
   private static final String[] HORSE_TEXTURES = new String[]{"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
   private static final String[] HORSE_TEXTURES_ABBR = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
   private static final String[] HORSE_MARKING_TEXTURES = new String[]{null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
   private static final String[] HORSE_MARKING_TEXTURES_ABBR = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   @Nullable
   private String texturePrefix;
   private final String[] horseTexturesArray = new String[2];

   public HorseEntity(EntityType<? extends HorseEntity> type, World worldIn) {
      super(type, worldIn);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(HORSE_VARIANT, 0);
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("Variant", this.getHorseVariant());
      if (!this.horseChest.getStackInSlot(1).isEmpty()) {
         compound.put("ArmorItem", this.horseChest.getStackInSlot(1).write(new CompoundNBT()));
      }

   }

   public ItemStack func_213803_dV() {
      return this.getItemStackFromSlot(EquipmentSlotType.CHEST);
   }

   private void func_213805_k(ItemStack p_213805_1_) {
      this.setItemStackToSlot(EquipmentSlotType.CHEST, p_213805_1_);
      this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setHorseVariant(compound.getInt("Variant"));
      if (compound.contains("ArmorItem", 10)) {
         ItemStack itemstack = ItemStack.read(compound.getCompound("ArmorItem"));
         if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
            this.horseChest.setInventorySlotContents(1, itemstack);
         }
      }

      this.updateHorseSlots();
   }

   public void setHorseVariant(int variant) {
      this.dataManager.set(HORSE_VARIANT, variant);
      this.resetTexturePrefix();
   }

   public int getHorseVariant() {
      return this.dataManager.get(HORSE_VARIANT);
   }

   private void resetTexturePrefix() {
      this.texturePrefix = null;
   }

   @OnlyIn(Dist.CLIENT)
   private void setHorseTexturePaths() {
      int i = this.getHorseVariant();
      int j = (i & 255) % 7;
      int k = ((i & '\uff00') >> 8) % 5;
      this.horseTexturesArray[0] = HORSE_TEXTURES[j];
      this.horseTexturesArray[1] = HORSE_MARKING_TEXTURES[k];
      this.texturePrefix = "horse/" + HORSE_TEXTURES_ABBR[j] + HORSE_MARKING_TEXTURES_ABBR[k];
   }

   @OnlyIn(Dist.CLIENT)
   public String getHorseTexture() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.texturePrefix;
   }

   @OnlyIn(Dist.CLIENT)
   public String[] getVariantTexturePaths() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.horseTexturesArray;
   }

   /**
    * Updates the items in the saddle and armor slots of the horse's inventory.
    */
   protected void updateHorseSlots() {
      super.updateHorseSlots();
      this.func_213804_l(this.horseChest.getStackInSlot(1));
      this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
   }

   private void func_213804_l(ItemStack p_213804_1_) {
      this.func_213805_k(p_213804_1_);
      if (!this.world.isRemote) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
         if (this.isArmor(p_213804_1_)) {
            int i = ((HorseArmorItem)p_213804_1_.getItem()).func_219977_e();
            if (i != 0) {
               this.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, AttributeModifier.Operation.ADDITION)).setSaved(false));
            }
         }
      }

   }

   /**
    * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
    */
   public void onInventoryChanged(IInventory invBasic) {
      ItemStack itemstack = this.func_213803_dV();
      super.onInventoryChanged(invBasic);
      ItemStack itemstack1 = this.func_213803_dV();
      if (this.ticksExisted > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
         this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
      }

   }

   protected void playGallopSound(SoundType p_190680_1_) {
      super.playGallopSound(p_190680_1_);
      if (this.rand.nextInt(10) == 0) {
         this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, p_190680_1_.getVolume() * 0.6F, p_190680_1_.getPitch());
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.world.isRemote && this.dataManager.isDirty()) {
         this.dataManager.setClean();
         this.resetTexturePrefix();
      }

      ItemStack stack = this.horseChest.getStackInSlot(1);
      if (isArmor(stack)) stack.onHorseArmorTick(world, this);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ENTITY_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      super.getHurtSound(damageSourceIn);
      return SoundEvents.ENTITY_HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.ENTITY_HORSE_ANGRY;
   }

   public boolean processInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      boolean flag = !itemstack.isEmpty();
      if (flag && itemstack.getItem() instanceof SpawnEggItem) {
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

         if (flag) {
            if (this.handleEating(player, itemstack)) {
               if (!player.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               return true;
            }

            if (itemstack.interactWithEntity(player, this, hand)) {
               return true;
            }

            if (!this.isTame()) {
               this.makeMad();
               return true;
            }

            boolean flag1 = !this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE;
            if (this.isArmor(itemstack) || flag1) {
               this.openGUI(player);
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

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
   public boolean canMateWith(AnimalEntity otherAnimal) {
      if (otherAnimal == this) {
         return false;
      } else if (!(otherAnimal instanceof DonkeyEntity) && !(otherAnimal instanceof HorseEntity)) {
         return false;
      } else {
         return this.canMate() && ((AbstractHorseEntity)otherAnimal).canMate();
      }
   }

   public AgeableEntity createChild(AgeableEntity ageable) {
      AbstractHorseEntity abstracthorseentity;
      if (ageable instanceof DonkeyEntity) {
         abstracthorseentity = EntityType.MULE.create(this.world);
      } else {
         HorseEntity horseentity = (HorseEntity)ageable;
         abstracthorseentity = EntityType.HORSE.create(this.world);
         int j = this.rand.nextInt(9);
         int i;
         if (j < 4) {
            i = this.getHorseVariant() & 255;
         } else if (j < 8) {
            i = horseentity.getHorseVariant() & 255;
         } else {
            i = this.rand.nextInt(7);
         }

         int k = this.rand.nextInt(5);
         if (k < 2) {
            i = i | this.getHorseVariant() & '\uff00';
         } else if (k < 4) {
            i = i | horseentity.getHorseVariant() & '\uff00';
         } else {
            i = i | this.rand.nextInt(5) << 8 & '\uff00';
         }

         ((HorseEntity)abstracthorseentity).setHorseVariant(i);
      }

      this.setOffspringAttributes(ageable, abstracthorseentity);
      return abstracthorseentity;
   }

   public boolean wearsArmor() {
      return true;
   }

   public boolean isArmor(ItemStack stack) {
      return stack.getItem() instanceof HorseArmorItem;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      int i;
      if (spawnDataIn instanceof HorseEntity.HorseData) {
         i = ((HorseEntity.HorseData)spawnDataIn).variant;
      } else {
         i = this.rand.nextInt(7);
         spawnDataIn = new HorseEntity.HorseData(i);
      }

      this.setHorseVariant(i | this.rand.nextInt(5) << 8);
      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public static class HorseData extends AgeableEntity.AgeableData {
      public final int variant;

      public HorseData(int variantIn) {
         this.variant = variantIn;
      }
   }
}