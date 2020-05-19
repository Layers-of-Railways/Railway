package net.minecraft.entity.passive.horse;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class DonkeyEntity extends AbstractChestedHorseEntity {
   public DonkeyEntity(EntityType<? extends DonkeyEntity> p_i50239_1_, World p_i50239_2_) {
      super(p_i50239_1_, p_i50239_2_);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ENTITY_DONKEY_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_DONKEY_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      super.getHurtSound(damageSourceIn);
      return SoundEvents.ENTITY_DONKEY_HURT;
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
      EntityType<? extends AbstractHorseEntity> entitytype = ageable instanceof HorseEntity ? EntityType.MULE : EntityType.DONKEY;
      AbstractHorseEntity abstracthorseentity = entitytype.create(this.world);
      this.setOffspringAttributes(ageable, abstracthorseentity);
      return abstracthorseentity;
   }
}