package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.TriggerSkeletonTrapGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SkeletonHorseEntity extends AbstractHorseEntity {
   private final TriggerSkeletonTrapGoal skeletonTrapAI = new TriggerSkeletonTrapGoal(this);
   private boolean skeletonTrap;
   private int skeletonTrapTime;

   public SkeletonHorseEntity(EntityType<? extends SkeletonHorseEntity> p_i50235_1_, World p_i50235_2_) {
      super(p_i50235_1_, p_i50235_2_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
   }

   protected void initExtraAI() {
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return this.areEyesInFluid(FluidTags.WATER) ? SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT_WATER : SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      super.getHurtSound(damageSourceIn);
      return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
   }

   protected SoundEvent getSwimSound() {
      if (this.onGround) {
         if (!this.isBeingRidden()) {
            return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
         }

         ++this.gallopTime;
         if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
            return SoundEvents.ENTITY_SKELETON_HORSE_GALLOP_WATER;
         }

         if (this.gallopTime <= 5) {
            return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
         }
      }

      return SoundEvents.ENTITY_SKELETON_HORSE_SWIM;
   }

   protected void playSwimSound(float volume) {
      if (this.onGround) {
         super.playSwimSound(0.3F);
      } else {
         super.playSwimSound(Math.min(0.1F, volume * 25.0F));
      }

   }

   protected void playJumpSound() {
      if (this.isInWater()) {
         this.playSound(SoundEvents.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
      } else {
         super.playJumpSound();
      }

   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   /**
    * Returns the Y offset from the entity's position for any entity riding this one.
    */
   public double getMountedYOffset() {
      return super.getMountedYOffset() - 0.1875D;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      if (this.isTrap() && this.skeletonTrapTime++ >= 18000) {
         this.remove();
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putBoolean("SkeletonTrap", this.isTrap());
      compound.putInt("SkeletonTrapTime", this.skeletonTrapTime);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setTrap(compound.getBoolean("SkeletonTrap"));
      this.skeletonTrapTime = compound.getInt("SkeletonTrapTime");
   }

   public boolean canBeRiddenInWater() {
      return true;
   }

   protected float getWaterSlowDown() {
      return 0.96F;
   }

   public boolean isTrap() {
      return this.skeletonTrap;
   }

   public void setTrap(boolean trap) {
      if (trap != this.skeletonTrap) {
         this.skeletonTrap = trap;
         if (trap) {
            this.goalSelector.addGoal(1, this.skeletonTrapAI);
         } else {
            this.goalSelector.removeGoal(this.skeletonTrapAI);
         }

      }
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity ageable) {
      return EntityType.SKELETON_HORSE.create(this.world);
   }

   public boolean processInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() instanceof SpawnEggItem) {
         return super.processInteract(player, hand);
      } else if (!this.isTame()) {
         return false;
      } else if (this.isChild()) {
         return super.processInteract(player, hand);
      } else if (player.isSecondaryUseActive()) {
         this.openGUI(player);
         return true;
      } else if (this.isBeingRidden()) {
         return super.processInteract(player, hand);
      } else {
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem() == Items.SADDLE && !this.isHorseSaddled()) {
               this.openGUI(player);
               return true;
            }

            if (itemstack.interactWithEntity(player, this, hand)) {
               return true;
            }
         }

         this.mountTo(player);
         return true;
      }
   }
}