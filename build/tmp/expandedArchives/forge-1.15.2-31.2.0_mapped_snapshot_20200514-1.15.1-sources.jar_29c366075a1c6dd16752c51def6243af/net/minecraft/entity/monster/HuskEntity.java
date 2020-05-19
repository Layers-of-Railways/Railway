package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HuskEntity extends ZombieEntity {
   public HuskEntity(EntityType<? extends HuskEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public static boolean func_223334_b(EntityType<HuskEntity> p_223334_0_, IWorld p_223334_1_, SpawnReason reason, BlockPos p_223334_3_, Random p_223334_4_) {
      return canMonsterSpawnInLight(p_223334_0_, p_223334_1_, reason, p_223334_3_, p_223334_4_) && (reason == SpawnReason.SPAWNER || p_223334_1_.canSeeSky(p_223334_3_));
   }

   protected boolean shouldBurnInDay() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_HUSK_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_HUSK_STEP;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && this.getHeldItemMainhand().isEmpty() && entityIn instanceof LivingEntity) {
         float f = this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
         ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.HUNGER, 140 * (int)f));
      }

      return flag;
   }

   protected boolean shouldDrown() {
      return true;
   }

   protected void onDrowned() {
      this.func_213698_b(EntityType.ZOMBIE);
      this.world.playEvent((PlayerEntity)null, 1041, new BlockPos(this), 0);
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }
}