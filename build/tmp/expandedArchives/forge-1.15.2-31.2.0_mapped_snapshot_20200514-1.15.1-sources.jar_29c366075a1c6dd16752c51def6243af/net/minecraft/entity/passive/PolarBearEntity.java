package net.minecraft.entity.passive;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PolarBearEntity extends AnimalEntity {
   private static final DataParameter<Boolean> IS_STANDING = EntityDataManager.createKey(PolarBearEntity.class, DataSerializers.BOOLEAN);
   private float clientSideStandAnimation0;
   private float clientSideStandAnimation;
   private int warningSoundTicks;

   public PolarBearEntity(EntityType<? extends PolarBearEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public AgeableEntity createChild(AgeableEntity ageable) {
      return EntityType.POLAR_BEAR.create(this.world);
   }

   /**
    * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
    * the animal type)
    */
   public boolean isBreedingItem(ItemStack stack) {
      return false;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new PolarBearEntity.MeleeAttackGoal());
      this.goalSelector.addGoal(1, new PolarBearEntity.PanicGoal());
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new PolarBearEntity.HurtByTargetGoal());
      this.targetSelector.addGoal(2, new PolarBearEntity.AttackPlayerGoal());
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, FoxEntity.class, 10, true, true, (Predicate<LivingEntity>)null));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
   }

   public static boolean func_223320_c(EntityType<PolarBearEntity> p_223320_0_, IWorld p_223320_1_, SpawnReason reason, BlockPos p_223320_3_, Random p_223320_4_) {
      Biome biome = p_223320_1_.getBiome(p_223320_3_);
      if (biome != Biomes.FROZEN_OCEAN && biome != Biomes.DEEP_FROZEN_OCEAN) {
         return canAnimalSpawn(p_223320_0_, p_223320_1_, reason, p_223320_3_, p_223320_4_);
      } else {
         return p_223320_1_.getLightSubtracted(p_223320_3_, 0) > 8 && p_223320_1_.getBlockState(p_223320_3_.down()).getBlock() == Blocks.ICE;
      }
   }

   protected SoundEvent getAmbientSound() {
      return this.isChild() ? SoundEvents.ENTITY_POLAR_BEAR_AMBIENT_BABY : SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_POLAR_BEAR_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
   }

   protected void playWarningSound() {
      if (this.warningSoundTicks <= 0) {
         this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, this.getSoundPitch());
         this.warningSoundTicks = 40;
      }

   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(IS_STANDING, false);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         if (this.clientSideStandAnimation != this.clientSideStandAnimation0) {
            this.recalculateSize();
         }

         this.clientSideStandAnimation0 = this.clientSideStandAnimation;
         if (this.isStanding()) {
            this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
         } else {
            this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
         }
      }

      if (this.warningSoundTicks > 0) {
         --this.warningSoundTicks;
      }

   }

   public EntitySize getSize(Pose poseIn) {
      if (this.clientSideStandAnimation > 0.0F) {
         float f = this.clientSideStandAnimation / 6.0F;
         float f1 = 1.0F + f;
         return super.getSize(poseIn).scale(1.0F, f1);
      } else {
         return super.getSize(poseIn);
      }
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
      if (flag) {
         this.applyEnchantments(this, entityIn);
      }

      return flag;
   }

   public boolean isStanding() {
      return this.dataManager.get(IS_STANDING);
   }

   public void setStanding(boolean standing) {
      this.dataManager.set(IS_STANDING, standing);
   }

   @OnlyIn(Dist.CLIENT)
   public float getStandingAnimationScale(float p_189795_1_) {
      return MathHelper.lerp(p_189795_1_, this.clientSideStandAnimation0, this.clientSideStandAnimation) / 6.0F;
   }

   protected float getWaterSlowDown() {
      return 0.98F;
   }

   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      if (spawnDataIn == null) {
         spawnDataIn = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)spawnDataIn).func_226258_a_(1.0F);
      }

      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
      public AttackPlayerGoal() {
         super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, (Predicate<LivingEntity>)null);
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (PolarBearEntity.this.isChild()) {
            return false;
         } else {
            if (super.shouldExecute()) {
               for(PolarBearEntity polarbearentity : PolarBearEntity.this.world.getEntitiesWithinAABB(PolarBearEntity.class, PolarBearEntity.this.getBoundingBox().grow(8.0D, 4.0D, 8.0D))) {
                  if (polarbearentity.isChild()) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      protected double getTargetDistance() {
         return super.getTargetDistance() * 0.5D;
      }
   }

   class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
      public HurtByTargetGoal() {
         super(PolarBearEntity.this);
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         super.startExecuting();
         if (PolarBearEntity.this.isChild()) {
            this.alertOthers();
            this.resetTask();
         }

      }

      protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
         if (mobIn instanceof PolarBearEntity && !mobIn.isChild()) {
            super.setAttackTarget(mobIn, targetIn);
         }

      }
   }

   class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {
      public MeleeAttackGoal() {
         super(PolarBearEntity.this, 1.25D, true);
      }

      protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
         double d0 = this.getAttackReachSqr(enemy);
         if (distToEnemySqr <= d0 && this.attackTick <= 0) {
            this.attackTick = 20;
            this.attacker.attackEntityAsMob(enemy);
            PolarBearEntity.this.setStanding(false);
         } else if (distToEnemySqr <= d0 * 2.0D) {
            if (this.attackTick <= 0) {
               PolarBearEntity.this.setStanding(false);
               this.attackTick = 20;
            }

            if (this.attackTick <= 10) {
               PolarBearEntity.this.setStanding(true);
               PolarBearEntity.this.playWarningSound();
            }
         } else {
            this.attackTick = 20;
            PolarBearEntity.this.setStanding(false);
         }

      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         PolarBearEntity.this.setStanding(false);
         super.resetTask();
      }

      protected double getAttackReachSqr(LivingEntity attackTarget) {
         return (double)(4.0F + attackTarget.getWidth());
      }
   }

   class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      public PanicGoal() {
         super(PolarBearEntity.this, 2.0D);
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return !PolarBearEntity.this.isChild() && !PolarBearEntity.this.isBurning() ? false : super.shouldExecute();
      }
   }
}