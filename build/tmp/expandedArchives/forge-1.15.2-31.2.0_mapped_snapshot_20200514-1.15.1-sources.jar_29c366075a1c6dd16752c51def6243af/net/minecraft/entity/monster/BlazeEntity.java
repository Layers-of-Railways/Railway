package net.minecraft.entity.monster;

import java.util.EnumSet;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlazeEntity extends MonsterEntity {
   private float heightOffset = 0.5F;
   private int heightOffsetUpdateTime;
   private static final DataParameter<Byte> ON_FIRE = EntityDataManager.createKey(BlazeEntity.class, DataSerializers.BYTE);

   public BlazeEntity(EntityType<? extends BlazeEntity> type, World p_i50215_2_) {
      super(type, p_i50215_2_);
      this.setPathPriority(PathNodeType.WATER, -1.0F);
      this.setPathPriority(PathNodeType.LAVA, 8.0F);
      this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
      this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
      this.experienceValue = 10;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(4, new BlazeEntity.FireballAttackGoal(this));
      this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 0.0F));
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.23F);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(ON_FIRE, (byte)0);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_BLAZE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_BLAZE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_BLAZE_DEATH;
   }

   /**
    * Gets how bright this entity is.
    */
   public float getBrightness() {
      return 1.0F;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      if (!this.onGround && this.getMotion().y < 0.0D) {
         this.setMotion(this.getMotion().mul(1.0D, 0.6D, 1.0D));
      }

      if (this.world.isRemote) {
         if (this.rand.nextInt(24) == 0 && !this.isSilent()) {
            this.world.playSound(this.getPosX() + 0.5D, this.getPosY() + 0.5D, this.getPosZ() + 0.5D, SoundEvents.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
         }

         for(int i = 0; i < 2; ++i) {
            this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), 0.0D, 0.0D, 0.0D);
         }
      }

      super.livingTick();
   }

   protected void updateAITasks() {
      if (this.isInWaterRainOrBubbleColumn()) {
         this.attackEntityFrom(DamageSource.DROWN, 1.0F);
      }

      --this.heightOffsetUpdateTime;
      if (this.heightOffsetUpdateTime <= 0) {
         this.heightOffsetUpdateTime = 100;
         this.heightOffset = 0.5F + (float)this.rand.nextGaussian() * 3.0F;
      }

      LivingEntity livingentity = this.getAttackTarget();
      if (livingentity != null && livingentity.getPosYEye() > this.getPosYEye() + (double)this.heightOffset && this.canAttack(livingentity)) {
         Vec3d vec3d = this.getMotion();
         this.setMotion(this.getMotion().add(0.0D, ((double)0.3F - vec3d.y) * (double)0.3F, 0.0D));
         this.isAirBorne = true;
      }

      super.updateAITasks();
   }

   public boolean onLivingFall(float distance, float damageMultiplier) {
      return false;
   }

   /**
    * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
    */
   public boolean isBurning() {
      return this.isCharged();
   }

   private boolean isCharged() {
      return (this.dataManager.get(ON_FIRE) & 1) != 0;
   }

   private void setOnFire(boolean onFire) {
      byte b0 = this.dataManager.get(ON_FIRE);
      if (onFire) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.dataManager.set(ON_FIRE, b0);
   }

   static class FireballAttackGoal extends Goal {
      private final BlazeEntity blaze;
      private int attackStep;
      private int attackTime;
      private int field_223527_d;

      public FireballAttackGoal(BlazeEntity blazeIn) {
         this.blaze = blazeIn;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         LivingEntity livingentity = this.blaze.getAttackTarget();
         return livingentity != null && livingentity.isAlive() && this.blaze.canAttack(livingentity);
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.attackStep = 0;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.blaze.setOnFire(false);
         this.field_223527_d = 0;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         --this.attackTime;
         LivingEntity livingentity = this.blaze.getAttackTarget();
         if (livingentity != null) {
            boolean flag = this.blaze.getEntitySenses().canSee(livingentity);
            if (flag) {
               this.field_223527_d = 0;
            } else {
               ++this.field_223527_d;
            }

            double d0 = this.blaze.getDistanceSq(livingentity);
            if (d0 < 4.0D) {
               if (!flag) {
                  return;
               }

               if (this.attackTime <= 0) {
                  this.attackTime = 20;
                  this.blaze.attackEntityAsMob(livingentity);
               }

               this.blaze.getMoveHelper().setMoveTo(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ(), 1.0D);
            } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
               double d1 = livingentity.getPosX() - this.blaze.getPosX();
               double d2 = livingentity.getPosYHeight(0.5D) - this.blaze.getPosYHeight(0.5D);
               double d3 = livingentity.getPosZ() - this.blaze.getPosZ();
               if (this.attackTime <= 0) {
                  ++this.attackStep;
                  if (this.attackStep == 1) {
                     this.attackTime = 60;
                     this.blaze.setOnFire(true);
                  } else if (this.attackStep <= 4) {
                     this.attackTime = 6;
                  } else {
                     this.attackTime = 100;
                     this.attackStep = 0;
                     this.blaze.setOnFire(false);
                  }

                  if (this.attackStep > 1) {
                     float f = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5F;
                     this.blaze.world.playEvent((PlayerEntity)null, 1018, new BlockPos(this.blaze), 0);

                     for(int i = 0; i < 1; ++i) {
                        SmallFireballEntity smallfireballentity = new SmallFireballEntity(this.blaze.world, this.blaze, d1 + this.blaze.getRNG().nextGaussian() * (double)f, d2, d3 + this.blaze.getRNG().nextGaussian() * (double)f);
                        smallfireballentity.setPosition(smallfireballentity.getPosX(), this.blaze.getPosYHeight(0.5D) + 0.5D, smallfireballentity.getPosZ());
                        this.blaze.world.addEntity(smallfireballentity);
                     }
                  }
               }

               this.blaze.getLookController().setLookPositionWithEntity(livingentity, 10.0F, 10.0F);
            } else if (this.field_223527_d < 5) {
               this.blaze.getMoveHelper().setMoveTo(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ(), 1.0D);
            }

            super.tick();
         }
      }

      private double getFollowDistance() {
         return this.blaze.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getValue();
      }
   }
}