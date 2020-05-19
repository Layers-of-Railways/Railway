package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SkeletonEntity extends AbstractSkeletonEntity {
   public SkeletonEntity(EntityType<? extends SkeletonEntity> p_i50194_1_, World p_i50194_2_) {
      super(p_i50194_1_, p_i50194_2_);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SKELETON_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_SKELETON_STEP;
   }

   protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
      super.dropSpecialItems(source, looting, recentlyHitIn);
      Entity entity = source.getTrueSource();
      if (entity instanceof CreeperEntity) {
         CreeperEntity creeperentity = (CreeperEntity)entity;
         if (creeperentity.ableToCauseSkullDrop()) {
            creeperentity.incrementDroppedSkulls();
            this.entityDropItem(Items.SKELETON_SKULL);
         }
      }

   }
}