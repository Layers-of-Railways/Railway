package net.minecraft.entity.passive.fish;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class CodEntity extends AbstractGroupFishEntity {
   public CodEntity(EntityType<? extends CodEntity> p_i50279_1_, World p_i50279_2_) {
      super(p_i50279_1_, p_i50279_2_);
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.COD_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_COD_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_COD_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_COD_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_COD_FLOP;
   }
}