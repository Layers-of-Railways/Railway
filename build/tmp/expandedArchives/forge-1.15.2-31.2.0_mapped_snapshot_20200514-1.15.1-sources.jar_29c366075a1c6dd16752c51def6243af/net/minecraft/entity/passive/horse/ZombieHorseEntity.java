package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ZombieHorseEntity extends AbstractHorseEntity {
   public ZombieHorseEntity(EntityType<? extends ZombieHorseEntity> p_i50233_1_, World p_i50233_2_) {
      super(p_i50233_1_, p_i50233_2_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      super.getHurtSound(damageSourceIn);
      return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity ageable) {
      return EntityType.ZOMBIE_HORSE.create(this.world);
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
            if (!this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE) {
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

   protected void initExtraAI() {
   }
}