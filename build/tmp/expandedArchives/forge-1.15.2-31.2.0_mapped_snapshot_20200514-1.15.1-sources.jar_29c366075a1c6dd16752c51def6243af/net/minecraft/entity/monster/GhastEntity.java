package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GhastEntity extends FlyingEntity implements IMob {
   private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(GhastEntity.class, DataSerializers.BOOLEAN);
   private int explosionStrength = 1;

   public GhastEntity(EntityType<? extends GhastEntity> type, World worldIn) {
      super(type, worldIn);
      this.experienceValue = 5;
      this.moveController = new GhastEntity.MoveHelperController(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(5, new GhastEntity.RandomFlyGoal(this));
      this.goalSelector.addGoal(7, new GhastEntity.LookAroundGoal(this));
      this.goalSelector.addGoal(7, new GhastEntity.FireballAttackGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, (p_213812_1_) -> {
         return Math.abs(p_213812_1_.getPosY() - this.getPosY()) <= 4.0D;
      }));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAttacking() {
      return this.dataManager.get(ATTACKING);
   }

   public void setAttacking(boolean attacking) {
      this.dataManager.set(ATTACKING, attacking);
   }

   public int getFireballStrength() {
      return this.explosionStrength;
   }

   protected boolean isDespawnPeaceful() {
      return true;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (source.getImmediateSource() instanceof FireballEntity && source.getTrueSource() instanceof PlayerEntity) {
         super.attackEntityFrom(source, 1000.0F);
         return true;
      } else {
         return super.attackEntityFrom(source, amount);
      }
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(ATTACKING, false);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_GHAST_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_GHAST_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_GHAST_DEATH;
   }

   /**
    * Returns the volume for the sounds this mob makes.
    */
   protected float getSoundVolume() {
      return 10.0F;
   }

   public static boolean func_223368_b(EntityType<GhastEntity> p_223368_0_, IWorld p_223368_1_, SpawnReason reason, BlockPos p_223368_3_, Random p_223368_4_) {
      return p_223368_1_.getDifficulty() != Difficulty.PEACEFUL && p_223368_4_.nextInt(20) == 0 && canSpawnOn(p_223368_0_, p_223368_1_, reason, p_223368_3_, p_223368_4_);
   }

   /**
    * Will return how many at most can spawn in a chunk at once.
    */
   public int getMaxSpawnedInChunk() {
      return 1;
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("ExplosionPower", this.explosionStrength);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      if (compound.contains("ExplosionPower", 99)) {
         this.explosionStrength = compound.getInt("ExplosionPower");
      }

   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 2.6F;
   }

   static class FireballAttackGoal extends Goal {
      private final GhastEntity parentEntity;
      public int attackTimer;

      public FireballAttackGoal(GhastEntity ghast) {
         this.parentEntity = ghast;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return this.parentEntity.getAttackTarget() != null;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.attackTimer = 0;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.parentEntity.setAttacking(false);
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         LivingEntity livingentity = this.parentEntity.getAttackTarget();
         double d0 = 64.0D;
         if (livingentity.getDistanceSq(this.parentEntity) < 4096.0D && this.parentEntity.canEntityBeSeen(livingentity)) {
            World world = this.parentEntity.world;
            ++this.attackTimer;
            if (this.attackTimer == 10) {
               world.playEvent((PlayerEntity)null, 1015, new BlockPos(this.parentEntity), 0);
            }

            if (this.attackTimer == 20) {
               double d1 = 4.0D;
               Vec3d vec3d = this.parentEntity.getLook(1.0F);
               double d2 = livingentity.getPosX() - (this.parentEntity.getPosX() + vec3d.x * 4.0D);
               double d3 = livingentity.getPosYHeight(0.5D) - (0.5D + this.parentEntity.getPosYHeight(0.5D));
               double d4 = livingentity.getPosZ() - (this.parentEntity.getPosZ() + vec3d.z * 4.0D);
               world.playEvent((PlayerEntity)null, 1016, new BlockPos(this.parentEntity), 0);
               FireballEntity fireballentity = new FireballEntity(world, this.parentEntity, d2, d3, d4);
               fireballentity.explosionPower = this.parentEntity.getFireballStrength();
               fireballentity.setPosition(this.parentEntity.getPosX() + vec3d.x * 4.0D, this.parentEntity.getPosYHeight(0.5D) + 0.5D, fireballentity.getPosZ() + vec3d.z * 4.0D);
               world.addEntity(fireballentity);
               this.attackTimer = -40;
            }
         } else if (this.attackTimer > 0) {
            --this.attackTimer;
         }

         this.parentEntity.setAttacking(this.attackTimer > 10);
      }
   }

   static class LookAroundGoal extends Goal {
      private final GhastEntity parentEntity;

      public LookAroundGoal(GhastEntity ghast) {
         this.parentEntity = ghast;
         this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return true;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.parentEntity.getAttackTarget() == null) {
            Vec3d vec3d = this.parentEntity.getMotion();
            this.parentEntity.rotationYaw = -((float)MathHelper.atan2(vec3d.x, vec3d.z)) * (180F / (float)Math.PI);
            this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
         } else {
            LivingEntity livingentity = this.parentEntity.getAttackTarget();
            double d0 = 64.0D;
            if (livingentity.getDistanceSq(this.parentEntity) < 4096.0D) {
               double d1 = livingentity.getPosX() - this.parentEntity.getPosX();
               double d2 = livingentity.getPosZ() - this.parentEntity.getPosZ();
               this.parentEntity.rotationYaw = -((float)MathHelper.atan2(d1, d2)) * (180F / (float)Math.PI);
               this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
            }
         }

      }
   }

   static class MoveHelperController extends MovementController {
      private final GhastEntity parentEntity;
      private int courseChangeCooldown;

      public MoveHelperController(GhastEntity ghast) {
         super(ghast);
         this.parentEntity = ghast;
      }

      public void tick() {
         if (this.action == MovementController.Action.MOVE_TO) {
            if (this.courseChangeCooldown-- <= 0) {
               this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
               Vec3d vec3d = new Vec3d(this.posX - this.parentEntity.getPosX(), this.posY - this.parentEntity.getPosY(), this.posZ - this.parentEntity.getPosZ());
               double d0 = vec3d.length();
               vec3d = vec3d.normalize();
               if (this.func_220673_a(vec3d, MathHelper.ceil(d0))) {
                  this.parentEntity.setMotion(this.parentEntity.getMotion().add(vec3d.scale(0.1D)));
               } else {
                  this.action = MovementController.Action.WAIT;
               }
            }

         }
      }

      private boolean func_220673_a(Vec3d p_220673_1_, int p_220673_2_) {
         AxisAlignedBB axisalignedbb = this.parentEntity.getBoundingBox();

         for(int i = 1; i < p_220673_2_; ++i) {
            axisalignedbb = axisalignedbb.offset(p_220673_1_);
            if (!this.parentEntity.world.hasNoCollisions(this.parentEntity, axisalignedbb)) {
               return false;
            }
         }

         return true;
      }
   }

   static class RandomFlyGoal extends Goal {
      private final GhastEntity parentEntity;

      public RandomFlyGoal(GhastEntity ghast) {
         this.parentEntity = ghast;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         MovementController movementcontroller = this.parentEntity.getMoveHelper();
         if (!movementcontroller.isUpdating()) {
            return true;
         } else {
            double d0 = movementcontroller.getX() - this.parentEntity.getPosX();
            double d1 = movementcontroller.getY() - this.parentEntity.getPosY();
            double d2 = movementcontroller.getZ() - this.parentEntity.getPosZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return false;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         Random random = this.parentEntity.getRNG();
         double d0 = this.parentEntity.getPosX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double d1 = this.parentEntity.getPosY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double d2 = this.parentEntity.getPosZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.0D);
      }
   }
}