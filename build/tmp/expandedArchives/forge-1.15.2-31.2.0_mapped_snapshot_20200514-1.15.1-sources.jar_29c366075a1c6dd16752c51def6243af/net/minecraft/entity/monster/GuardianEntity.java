package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GuardianEntity extends MonsterEntity {
   private static final DataParameter<Boolean> MOVING = EntityDataManager.createKey(GuardianEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(GuardianEntity.class, DataSerializers.VARINT);
   protected float clientSideTailAnimation;
   protected float clientSideTailAnimationO;
   protected float clientSideTailAnimationSpeed;
   protected float clientSideSpikesAnimation;
   protected float clientSideSpikesAnimationO;
   private LivingEntity targetedEntity;
   private int clientSideAttackTime;
   private boolean clientSideTouchedGround;
   protected RandomWalkingGoal wander;

   public GuardianEntity(EntityType<? extends GuardianEntity> type, World worldIn) {
      super(type, worldIn);
      this.experienceValue = 10;
      this.setPathPriority(PathNodeType.WATER, 0.0F);
      this.moveController = new GuardianEntity.MoveHelperController(this);
      this.clientSideTailAnimation = this.rand.nextFloat();
      this.clientSideTailAnimationO = this.clientSideTailAnimation;
   }

   protected void registerGoals() {
      MoveTowardsRestrictionGoal movetowardsrestrictiongoal = new MoveTowardsRestrictionGoal(this, 1.0D);
      this.wander = new RandomWalkingGoal(this, 1.0D, 80);
      this.goalSelector.addGoal(4, new GuardianEntity.AttackGoal(this));
      this.goalSelector.addGoal(5, movetowardsrestrictiongoal);
      this.goalSelector.addGoal(7, this.wander);
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAtGoal(this, GuardianEntity.class, 12.0F, 0.01F));
      this.goalSelector.addGoal(9, new LookRandomlyGoal(this));
      this.wander.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      movetowardsrestrictiongoal.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, new GuardianEntity.TargetPredicate(this)));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
   }

   /**
    * Returns new PathNavigateGround instance
    */
   protected PathNavigator createNavigator(World worldIn) {
      return new SwimmerPathNavigator(this, worldIn);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(MOVING, false);
      this.dataManager.register(TARGET_ENTITY, 0);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.WATER;
   }

   public boolean isMoving() {
      return this.dataManager.get(MOVING);
   }

   private void setMoving(boolean moving) {
      this.dataManager.set(MOVING, moving);
   }

   public int getAttackDuration() {
      return 80;
   }

   private void setTargetedEntity(int entityId) {
      this.dataManager.set(TARGET_ENTITY, entityId);
   }

   public boolean hasTargetedEntity() {
      return this.dataManager.get(TARGET_ENTITY) != 0;
   }

   @Nullable
   public LivingEntity getTargetedEntity() {
      if (!this.hasTargetedEntity()) {
         return null;
      } else if (this.world.isRemote) {
         if (this.targetedEntity != null) {
            return this.targetedEntity;
         } else {
            Entity entity = this.world.getEntityByID(this.dataManager.get(TARGET_ENTITY));
            if (entity instanceof LivingEntity) {
               this.targetedEntity = (LivingEntity)entity;
               return this.targetedEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.getAttackTarget();
      }
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      super.notifyDataManagerChange(key);
      if (TARGET_ENTITY.equals(key)) {
         this.clientSideAttackTime = 0;
         this.targetedEntity = null;
      }

   }

   /**
    * Get number of ticks, at least during which the living entity will be silent.
    */
   public int getTalkInterval() {
      return 160;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_AMBIENT : SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_HURT : SoundEvents.ENTITY_GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_DEATH : SoundEvents.ENTITY_GUARDIAN_DEATH_LAND;
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return sizeIn.height * 0.5F;
   }

   public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
      return worldIn.getFluidState(pos).isTagged(FluidTags.WATER) ? 10.0F + worldIn.getBrightness(pos) - 0.5F : super.getBlockPathWeight(pos, worldIn);
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      if (this.isAlive()) {
         if (this.world.isRemote) {
            this.clientSideTailAnimationO = this.clientSideTailAnimation;
            if (!this.isInWater()) {
               this.clientSideTailAnimationSpeed = 2.0F;
               Vec3d vec3d = this.getMotion();
               if (vec3d.y > 0.0D && this.clientSideTouchedGround && !this.isSilent()) {
                  this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), this.getFlopSound(), this.getSoundCategory(), 1.0F, 1.0F, false);
               }

               this.clientSideTouchedGround = vec3d.y < 0.0D && this.world.isTopSolid((new BlockPos(this)).down(), this);
            } else if (this.isMoving()) {
               if (this.clientSideTailAnimationSpeed < 0.5F) {
                  this.clientSideTailAnimationSpeed = 4.0F;
               } else {
                  this.clientSideTailAnimationSpeed += (0.5F - this.clientSideTailAnimationSpeed) * 0.1F;
               }
            } else {
               this.clientSideTailAnimationSpeed += (0.125F - this.clientSideTailAnimationSpeed) * 0.2F;
            }

            this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
            this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
            if (!this.isInWaterOrBubbleColumn()) {
               this.clientSideSpikesAnimation = this.rand.nextFloat();
            } else if (this.isMoving()) {
               this.clientSideSpikesAnimation += (0.0F - this.clientSideSpikesAnimation) * 0.25F;
            } else {
               this.clientSideSpikesAnimation += (1.0F - this.clientSideSpikesAnimation) * 0.06F;
            }

            if (this.isMoving() && this.isInWater()) {
               Vec3d vec3d1 = this.getLook(0.0F);

               for(int i = 0; i < 2; ++i) {
                  this.world.addParticle(ParticleTypes.BUBBLE, this.getPosXRandom(0.5D) - vec3d1.x * 1.5D, this.getPosYRandom() - vec3d1.y * 1.5D, this.getPosZRandom(0.5D) - vec3d1.z * 1.5D, 0.0D, 0.0D, 0.0D);
               }
            }

            if (this.hasTargetedEntity()) {
               if (this.clientSideAttackTime < this.getAttackDuration()) {
                  ++this.clientSideAttackTime;
               }

               LivingEntity livingentity = this.getTargetedEntity();
               if (livingentity != null) {
                  this.getLookController().setLookPositionWithEntity(livingentity, 90.0F, 90.0F);
                  this.getLookController().tick();
                  double d5 = (double)this.getAttackAnimationScale(0.0F);
                  double d0 = livingentity.getPosX() - this.getPosX();
                  double d1 = livingentity.getPosYHeight(0.5D) - this.getPosYEye();
                  double d2 = livingentity.getPosZ() - this.getPosZ();
                  double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                  d0 = d0 / d3;
                  d1 = d1 / d3;
                  d2 = d2 / d3;
                  double d4 = this.rand.nextDouble();

                  while(d4 < d3) {
                     d4 += 1.8D - d5 + this.rand.nextDouble() * (1.7D - d5);
                     this.world.addParticle(ParticleTypes.BUBBLE, this.getPosX() + d0 * d4, this.getPosYEye() + d1 * d4, this.getPosZ() + d2 * d4, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         }

         if (this.isInWaterOrBubbleColumn()) {
            this.setAir(300);
         } else if (this.onGround) {
            this.setMotion(this.getMotion().add((double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F), 0.5D, (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F)));
            this.rotationYaw = this.rand.nextFloat() * 360.0F;
            this.onGround = false;
            this.isAirBorne = true;
         }

         if (this.hasTargetedEntity()) {
            this.rotationYaw = this.rotationYawHead;
         }
      }

      super.livingTick();
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_GUARDIAN_FLOP;
   }

   @OnlyIn(Dist.CLIENT)
   public float getTailAnimation(float p_175471_1_) {
      return MathHelper.lerp(p_175471_1_, this.clientSideTailAnimationO, this.clientSideTailAnimation);
   }

   @OnlyIn(Dist.CLIENT)
   public float getSpikesAnimation(float p_175469_1_) {
      return MathHelper.lerp(p_175469_1_, this.clientSideSpikesAnimationO, this.clientSideSpikesAnimation);
   }

   public float getAttackAnimationScale(float p_175477_1_) {
      return ((float)this.clientSideAttackTime + p_175477_1_) / (float)this.getAttackDuration();
   }

   public boolean isNotColliding(IWorldReader worldIn) {
      return worldIn.checkNoEntityCollision(this);
   }

   public static boolean func_223329_b(EntityType<? extends GuardianEntity> p_223329_0_, IWorld p_223329_1_, SpawnReason reason, BlockPos p_223329_3_, Random p_223329_4_) {
      return (p_223329_4_.nextInt(20) == 0 || !p_223329_1_.canBlockSeeSky(p_223329_3_)) && p_223329_1_.getDifficulty() != Difficulty.PEACEFUL && (reason == SpawnReason.SPAWNER || p_223329_1_.getFluidState(p_223329_3_).isTagged(FluidTags.WATER));
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!this.isMoving() && !source.isMagicDamage() && source.getImmediateSource() instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)source.getImmediateSource();
         if (!source.isExplosion()) {
            livingentity.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0F);
         }
      }

      if (this.wander != null) {
         this.wander.makeUpdate();
      }

      return super.attackEntityFrom(source, amount);
   }

   /**
    * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
    * use in wolves.
    */
   public int getVerticalFaceSpeed() {
      return 180;
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isServerWorld() && this.isInWater()) {
         this.moveRelative(0.1F, p_213352_1_);
         this.move(MoverType.SELF, this.getMotion());
         this.setMotion(this.getMotion().scale(0.9D));
         if (!this.isMoving() && this.getAttackTarget() == null) {
            this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(p_213352_1_);
      }

   }

   static class AttackGoal extends Goal {
      private final GuardianEntity guardian;
      private int tickCounter;
      private final boolean isElder;

      public AttackGoal(GuardianEntity guardian) {
         this.guardian = guardian;
         this.isElder = guardian instanceof ElderGuardianEntity;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         LivingEntity livingentity = this.guardian.getAttackTarget();
         return livingentity != null && livingentity.isAlive();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && (this.isElder || this.guardian.getDistanceSq(this.guardian.getAttackTarget()) > 9.0D);
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.tickCounter = -10;
         this.guardian.getNavigator().clearPath();
         this.guardian.getLookController().setLookPositionWithEntity(this.guardian.getAttackTarget(), 90.0F, 90.0F);
         this.guardian.isAirBorne = true;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.guardian.setTargetedEntity(0);
         this.guardian.setAttackTarget((LivingEntity)null);
         this.guardian.wander.makeUpdate();
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         LivingEntity livingentity = this.guardian.getAttackTarget();
         this.guardian.getNavigator().clearPath();
         this.guardian.getLookController().setLookPositionWithEntity(livingentity, 90.0F, 90.0F);
         if (!this.guardian.canEntityBeSeen(livingentity)) {
            this.guardian.setAttackTarget((LivingEntity)null);
         } else {
            ++this.tickCounter;
            if (this.tickCounter == 0) {
               this.guardian.setTargetedEntity(this.guardian.getAttackTarget().getEntityId());
               this.guardian.world.setEntityState(this.guardian, (byte)21);
            } else if (this.tickCounter >= this.guardian.getAttackDuration()) {
               float f = 1.0F;
               if (this.guardian.world.getDifficulty() == Difficulty.HARD) {
                  f += 2.0F;
               }

               if (this.isElder) {
                  f += 2.0F;
               }

               livingentity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.guardian, this.guardian), f);
               livingentity.attackEntityFrom(DamageSource.causeMobDamage(this.guardian), (float)this.guardian.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());
               this.guardian.setAttackTarget((LivingEntity)null);
            }

            super.tick();
         }
      }
   }

   static class MoveHelperController extends MovementController {
      private final GuardianEntity entityGuardian;

      public MoveHelperController(GuardianEntity guardian) {
         super(guardian);
         this.entityGuardian = guardian;
      }

      public void tick() {
         if (this.action == MovementController.Action.MOVE_TO && !this.entityGuardian.getNavigator().noPath()) {
            Vec3d vec3d = new Vec3d(this.posX - this.entityGuardian.getPosX(), this.posY - this.entityGuardian.getPosY(), this.posZ - this.entityGuardian.getPosZ());
            double d0 = vec3d.length();
            double d1 = vec3d.x / d0;
            double d2 = vec3d.y / d0;
            double d3 = vec3d.z / d0;
            float f = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.entityGuardian.rotationYaw = this.limitAngle(this.entityGuardian.rotationYaw, f, 90.0F);
            this.entityGuardian.renderYawOffset = this.entityGuardian.rotationYaw;
            float f1 = (float)(this.speed * this.entityGuardian.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            float f2 = MathHelper.lerp(0.125F, this.entityGuardian.getAIMoveSpeed(), f1);
            this.entityGuardian.setAIMoveSpeed(f2);
            double d4 = Math.sin((double)(this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.5D) * 0.05D;
            double d5 = Math.cos((double)(this.entityGuardian.rotationYaw * ((float)Math.PI / 180F)));
            double d6 = Math.sin((double)(this.entityGuardian.rotationYaw * ((float)Math.PI / 180F)));
            double d7 = Math.sin((double)(this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.75D) * 0.05D;
            this.entityGuardian.setMotion(this.entityGuardian.getMotion().add(d4 * d5, d7 * (d6 + d5) * 0.25D + (double)f2 * d2 * 0.1D, d4 * d6));
            LookController lookcontroller = this.entityGuardian.getLookController();
            double d8 = this.entityGuardian.getPosX() + d1 * 2.0D;
            double d9 = this.entityGuardian.getPosYEye() + d2 / d0;
            double d10 = this.entityGuardian.getPosZ() + d3 * 2.0D;
            double d11 = lookcontroller.getLookPosX();
            double d12 = lookcontroller.getLookPosY();
            double d13 = lookcontroller.getLookPosZ();
            if (!lookcontroller.getIsLooking()) {
               d11 = d8;
               d12 = d9;
               d13 = d10;
            }

            this.entityGuardian.getLookController().setLookPosition(MathHelper.lerp(0.125D, d11, d8), MathHelper.lerp(0.125D, d12, d9), MathHelper.lerp(0.125D, d13, d10), 10.0F, 40.0F);
            this.entityGuardian.setMoving(true);
         } else {
            this.entityGuardian.setAIMoveSpeed(0.0F);
            this.entityGuardian.setMoving(false);
         }
      }
   }

   static class TargetPredicate implements Predicate<LivingEntity> {
      private final GuardianEntity parentEntity;

      public TargetPredicate(GuardianEntity guardian) {
         this.parentEntity = guardian;
      }

      public boolean test(@Nullable LivingEntity p_test_1_) {
         return (p_test_1_ instanceof PlayerEntity || p_test_1_ instanceof SquidEntity) && p_test_1_.getDistanceSq(this.parentEntity) > 9.0D;
      }
   }
}