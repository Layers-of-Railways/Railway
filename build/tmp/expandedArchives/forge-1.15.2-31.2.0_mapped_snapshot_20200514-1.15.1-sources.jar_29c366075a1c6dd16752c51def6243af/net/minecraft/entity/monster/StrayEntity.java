package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class StrayEntity extends AbstractSkeletonEntity {
   public StrayEntity(EntityType<? extends StrayEntity> p_i50191_1_, World p_i50191_2_) {
      super(p_i50191_1_, p_i50191_2_);
   }

   public static boolean func_223327_b(EntityType<StrayEntity> p_223327_0_, IWorld p_223327_1_, SpawnReason reason, BlockPos p_223327_3_, Random p_223327_4_) {
      return canMonsterSpawnInLight(p_223327_0_, p_223327_1_, reason, p_223327_3_, p_223327_4_) && (reason == SpawnReason.SPAWNER || p_223327_1_.canSeeSky(p_223327_3_));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_STRAY_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_STRAY_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_STRAY_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_STRAY_STEP;
   }

   /**
    * Fires an arrow
    */
   protected AbstractArrowEntity fireArrow(ItemStack arrowStack, float distanceFactor) {
      AbstractArrowEntity abstractarrowentity = super.fireArrow(arrowStack, distanceFactor);
      if (abstractarrowentity instanceof ArrowEntity) {
         ((ArrowEntity)abstractarrowentity).addEffect(new EffectInstance(Effects.SLOWNESS, 600));
      }

      return abstractarrowentity;
   }
}