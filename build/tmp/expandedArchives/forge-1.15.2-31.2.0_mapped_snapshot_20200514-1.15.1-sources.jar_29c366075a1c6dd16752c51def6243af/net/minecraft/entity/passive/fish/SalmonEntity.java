package net.minecraft.entity.passive.fish;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SalmonEntity extends AbstractGroupFishEntity {
   public SalmonEntity(EntityType<? extends SalmonEntity> p_i50246_1_, World p_i50246_2_) {
      super(p_i50246_1_, p_i50246_2_);
   }

   public int getMaxGroupSize() {
      return 5;
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.SALMON_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SALMON_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SALMON_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SALMON_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_SALMON_FLOP;
   }
}