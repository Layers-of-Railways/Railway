package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PhantomEntity extends FlyingEntity implements IMob {
   private static final DataParameter<Integer> SIZE = EntityDataManager.createKey(PhantomEntity.class, DataSerializers.VARINT);
   private Vec3d orbitOffset = Vec3d.ZERO;
   private BlockPos orbitPosition = BlockPos.ZERO;
   private PhantomEntity.AttackPhase attackPhase = PhantomEntity.AttackPhase.CIRCLE;

   public PhantomEntity(EntityType<? extends PhantomEntity> type, World worldIn) {
      super(type, worldIn);
      this.experienceValue = 5;
      this.moveController = new PhantomEntity.MoveHelperController(this);
      this.lookController = new PhantomEntity.LookHelperController(this);
   }

   protected BodyController createBodyController() {
      return new PhantomEntity.BodyHelperController(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new PhantomEntity.PickAttackGoal());
      this.goalSelector.addGoal(2, new PhantomEntity.SweepAttackGoal());
      this.goalSelector.addGoal(3, new PhantomEntity.OrbitPointGoal());
      this.targetSelector.addGoal(1, new PhantomEntity.AttackPlayerGoal());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(SIZE, 0);
   }

   public void setPhantomSize(int sizeIn) {
      this.dataManager.set(SIZE, MathHelper.clamp(sizeIn, 0, 64));
   }

   private void updatePhantomSize() {
      this.recalculateSize();
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)(6 + this.getPhantomSize()));
   }

   public int getPhantomSize() {
      return this.dataManager.get(SIZE);
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return sizeIn.height * 0.35F;
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (SIZE.equals(key)) {
         this.updatePhantomSize();
      }

      super.notifyDataManagerChange(key);
   }

   protected boolean isDespawnPeaceful() {
      return true;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         float f = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted) * 0.13F + (float)Math.PI);
         float f1 = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted + 1) * 0.13F + (float)Math.PI);
         if (f > 0.0F && f1 <= 0.0F) {
            this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95F + this.rand.nextFloat() * 0.05F, 0.95F + this.rand.nextFloat() * 0.05F, false);
         }

         int i = this.getPhantomSize();
         float f2 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
         float f3 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
         float f4 = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
         this.world.addParticle(ParticleTypes.MYCELIUM, this.getPosX() + (double)f2, this.getPosY() + (double)f4, this.getPosZ() + (double)f3, 0.0D, 0.0D, 0.0D);
         this.world.addParticle(ParticleTypes.MYCELIUM, this.getPosX() - (double)f2, this.getPosY() + (double)f4, this.getPosZ() - (double)f3, 0.0D, 0.0D, 0.0D);
      }

   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      if (this.isAlive() && this.isInDaylight()) {
         this.setFire(8);
      }

      super.livingTick();
   }

   protected void updateAITasks() {
      super.updateAITasks();
   }

   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      this.orbitPosition = (new BlockPos(this)).up(5);
      this.setPhantomSize(0);
      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      if (compound.contains("AX")) {
         this.orbitPosition = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
      }

      this.setPhantomSize(compound.getInt("Size"));
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("AX", this.orbitPosition.getX());
      compound.putInt("AY", this.orbitPosition.getY());
      compound.putInt("AZ", this.orbitPosition.getZ());
      compound.putInt("Size", this.getPhantomSize());
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return true;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PHANTOM_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_PHANTOM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PHANTOM_DEATH;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   /**
    * Returns the volume for the sounds this mob makes.
    */
   protected float getSoundVolume() {
      return 1.0F;
   }

   public boolean canAttack(EntityType<?> typeIn) {
      return true;
   }

   public EntitySize getSize(Pose poseIn) {
      int i = this.getPhantomSize();
      EntitySize entitysize = super.getSize(poseIn);
      float f = (entitysize.width + 0.2F * (float)i) / entitysize.width;
      return entitysize.scale(f);
   }

   static enum AttackPhase {
      CIRCLE,
      SWOOP;
   }

   class AttackPlayerGoal extends Goal {
      private final EntityPredicate field_220842_b = (new EntityPredicate()).setDistance(64.0D);
      private int tickDelay = 20;

      private AttackPlayerGoal() {
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (this.tickDelay > 0) {
            --this.tickDelay;
            return false;
         } else {
            this.tickDelay = 60;
            List<PlayerEntity> list = PhantomEntity.this.world.getTargettablePlayersWithinAABB(this.field_220842_b, PhantomEntity.this, PhantomEntity.this.getBoundingBox().grow(16.0D, 64.0D, 16.0D));
            if (!list.isEmpty()) {
               list.sort((p_203140_0_, p_203140_1_) -> {
                  return p_203140_0_.getPosY() > p_203140_1_.getPosY() ? -1 : 1;
               });

               for(PlayerEntity playerentity : list) {
                  if (PhantomEntity.this.canAttack(playerentity, EntityPredicate.DEFAULT)) {
                     PhantomEntity.this.setAttackTarget(playerentity);
                     return true;
                  }
               }
            }

            return false;
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         LivingEntity livingentity = PhantomEntity.this.getAttackTarget();
         return livingentity != null ? PhantomEntity.this.canAttack(livingentity, EntityPredicate.DEFAULT) : false;
      }
   }

   class BodyHelperController extends BodyController {
      public BodyHelperController(MobEntity mob) {
         super(mob);
      }

      /**
       * Update the Head and Body rendenring angles
       */
      public void updateRenderAngles() {
         PhantomEntity.this.rotationYawHead = PhantomEntity.this.renderYawOffset;
         PhantomEntity.this.renderYawOffset = PhantomEntity.this.rotationYaw;
      }
   }

   class LookHelperController extends LookController {
      public LookHelperController(MobEntity entityIn) {
         super(entityIn);
      }

      /**
       * Updates look
       */
      public void tick() {
      }
   }

   abstract class MoveGoal extends Goal {
      public MoveGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      protected boolean func_203146_f() {
         return PhantomEntity.this.orbitOffset.squareDistanceTo(PhantomEntity.this.getPosX(), PhantomEntity.this.getPosY(), PhantomEntity.this.getPosZ()) < 4.0D;
      }
   }

   class MoveHelperController extends MovementController {
      private float speedFactor = 0.1F;

      public MoveHelperController(MobEntity entityIn) {
         super(entityIn);
      }

      public void tick() {
         if (PhantomEntity.this.collidedHorizontally) {
            PhantomEntity.this.rotationYaw += 180.0F;
            this.speedFactor = 0.1F;
         }

         float f = (float)(PhantomEntity.this.orbitOffset.x - PhantomEntity.this.getPosX());
         float f1 = (float)(PhantomEntity.this.orbitOffset.y - PhantomEntity.this.getPosY());
         float f2 = (float)(PhantomEntity.this.orbitOffset.z - PhantomEntity.this.getPosZ());
         double d0 = (double)MathHelper.sqrt(f * f + f2 * f2);
         double d1 = 1.0D - (double)MathHelper.abs(f1 * 0.7F) / d0;
         f = (float)((double)f * d1);
         f2 = (float)((double)f2 * d1);
         d0 = (double)MathHelper.sqrt(f * f + f2 * f2);
         double d2 = (double)MathHelper.sqrt(f * f + f2 * f2 + f1 * f1);
         float f3 = PhantomEntity.this.rotationYaw;
         float f4 = (float)MathHelper.atan2((double)f2, (double)f);
         float f5 = MathHelper.wrapDegrees(PhantomEntity.this.rotationYaw + 90.0F);
         float f6 = MathHelper.wrapDegrees(f4 * (180F / (float)Math.PI));
         PhantomEntity.this.rotationYaw = MathHelper.approachDegrees(f5, f6, 4.0F) - 90.0F;
         PhantomEntity.this.renderYawOffset = PhantomEntity.this.rotationYaw;
         if (MathHelper.degreesDifferenceAbs(f3, PhantomEntity.this.rotationYaw) < 3.0F) {
            this.speedFactor = MathHelper.approach(this.speedFactor, 1.8F, 0.005F * (1.8F / this.speedFactor));
         } else {
            this.speedFactor = MathHelper.approach(this.speedFactor, 0.2F, 0.025F);
         }

         float f7 = (float)(-(MathHelper.atan2((double)(-f1), d0) * (double)(180F / (float)Math.PI)));
         PhantomEntity.this.rotationPitch = f7;
         float f8 = PhantomEntity.this.rotationYaw + 90.0F;
         double d3 = (double)(this.speedFactor * MathHelper.cos(f8 * ((float)Math.PI / 180F))) * Math.abs((double)f / d2);
         double d4 = (double)(this.speedFactor * MathHelper.sin(f8 * ((float)Math.PI / 180F))) * Math.abs((double)f2 / d2);
         double d5 = (double)(this.speedFactor * MathHelper.sin(f7 * ((float)Math.PI / 180F))) * Math.abs((double)f1 / d2);
         Vec3d vec3d = PhantomEntity.this.getMotion();
         PhantomEntity.this.setMotion(vec3d.add((new Vec3d(d3, d5, d4)).subtract(vec3d).scale(0.2D)));
      }
   }

   class OrbitPointGoal extends PhantomEntity.MoveGoal {
      private float field_203150_c;
      private float field_203151_d;
      private float field_203152_e;
      private float field_203153_f;

      private OrbitPointGoal() {
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return PhantomEntity.this.getAttackTarget() == null || PhantomEntity.this.attackPhase == PhantomEntity.AttackPhase.CIRCLE;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.field_203151_d = 5.0F + PhantomEntity.this.rand.nextFloat() * 10.0F;
         this.field_203152_e = -4.0F + PhantomEntity.this.rand.nextFloat() * 9.0F;
         this.field_203153_f = PhantomEntity.this.rand.nextBoolean() ? 1.0F : -1.0F;
         this.func_203148_i();
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (PhantomEntity.this.rand.nextInt(350) == 0) {
            this.field_203152_e = -4.0F + PhantomEntity.this.rand.nextFloat() * 9.0F;
         }

         if (PhantomEntity.this.rand.nextInt(250) == 0) {
            ++this.field_203151_d;
            if (this.field_203151_d > 15.0F) {
               this.field_203151_d = 5.0F;
               this.field_203153_f = -this.field_203153_f;
            }
         }

         if (PhantomEntity.this.rand.nextInt(450) == 0) {
            this.field_203150_c = PhantomEntity.this.rand.nextFloat() * 2.0F * (float)Math.PI;
            this.func_203148_i();
         }

         if (this.func_203146_f()) {
            this.func_203148_i();
         }

         if (PhantomEntity.this.orbitOffset.y < PhantomEntity.this.getPosY() && !PhantomEntity.this.world.isAirBlock((new BlockPos(PhantomEntity.this)).down(1))) {
            this.field_203152_e = Math.max(1.0F, this.field_203152_e);
            this.func_203148_i();
         }

         if (PhantomEntity.this.orbitOffset.y > PhantomEntity.this.getPosY() && !PhantomEntity.this.world.isAirBlock((new BlockPos(PhantomEntity.this)).up(1))) {
            this.field_203152_e = Math.min(-1.0F, this.field_203152_e);
            this.func_203148_i();
         }

      }

      private void func_203148_i() {
         if (BlockPos.ZERO.equals(PhantomEntity.this.orbitPosition)) {
            PhantomEntity.this.orbitPosition = new BlockPos(PhantomEntity.this);
         }

         this.field_203150_c += this.field_203153_f * 15.0F * ((float)Math.PI / 180F);
         PhantomEntity.this.orbitOffset = (new Vec3d(PhantomEntity.this.orbitPosition)).add((double)(this.field_203151_d * MathHelper.cos(this.field_203150_c)), (double)(-4.0F + this.field_203152_e), (double)(this.field_203151_d * MathHelper.sin(this.field_203150_c)));
      }
   }

   class PickAttackGoal extends Goal {
      private int tickDelay;

      private PickAttackGoal() {
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         LivingEntity livingentity = PhantomEntity.this.getAttackTarget();
         return livingentity != null ? PhantomEntity.this.canAttack(PhantomEntity.this.getAttackTarget(), EntityPredicate.DEFAULT) : false;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.tickDelay = 10;
         PhantomEntity.this.attackPhase = PhantomEntity.AttackPhase.CIRCLE;
         this.func_203143_f();
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         PhantomEntity.this.orbitPosition = PhantomEntity.this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, PhantomEntity.this.orbitPosition).up(10 + PhantomEntity.this.rand.nextInt(20));
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (PhantomEntity.this.attackPhase == PhantomEntity.AttackPhase.CIRCLE) {
            --this.tickDelay;
            if (this.tickDelay <= 0) {
               PhantomEntity.this.attackPhase = PhantomEntity.AttackPhase.SWOOP;
               this.func_203143_f();
               this.tickDelay = (8 + PhantomEntity.this.rand.nextInt(4)) * 20;
               PhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + PhantomEntity.this.rand.nextFloat() * 0.1F);
            }
         }

      }

      private void func_203143_f() {
         PhantomEntity.this.orbitPosition = (new BlockPos(PhantomEntity.this.getAttackTarget())).up(20 + PhantomEntity.this.rand.nextInt(20));
         if (PhantomEntity.this.orbitPosition.getY() < PhantomEntity.this.world.getSeaLevel()) {
            PhantomEntity.this.orbitPosition = new BlockPos(PhantomEntity.this.orbitPosition.getX(), PhantomEntity.this.world.getSeaLevel() + 1, PhantomEntity.this.orbitPosition.getZ());
         }

      }
   }

   class SweepAttackGoal extends PhantomEntity.MoveGoal {
      private SweepAttackGoal() {
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return PhantomEntity.this.getAttackTarget() != null && PhantomEntity.this.attackPhase == PhantomEntity.AttackPhase.SWOOP;
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         LivingEntity livingentity = PhantomEntity.this.getAttackTarget();
         if (livingentity == null) {
            return false;
         } else if (!livingentity.isAlive()) {
            return false;
         } else if (!(livingentity instanceof PlayerEntity) || !((PlayerEntity)livingentity).isSpectator() && !((PlayerEntity)livingentity).isCreative()) {
            if (!this.shouldExecute()) {
               return false;
            } else {
               if (PhantomEntity.this.ticksExisted % 20 == 0) {
                  List<CatEntity> list = PhantomEntity.this.world.getEntitiesWithinAABB(CatEntity.class, PhantomEntity.this.getBoundingBox().grow(16.0D), EntityPredicates.IS_ALIVE);
                  if (!list.isEmpty()) {
                     for(CatEntity catentity : list) {
                        catentity.func_213420_ej();
                     }

                     return false;
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         PhantomEntity.this.setAttackTarget((LivingEntity)null);
         PhantomEntity.this.attackPhase = PhantomEntity.AttackPhase.CIRCLE;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         LivingEntity livingentity = PhantomEntity.this.getAttackTarget();
         PhantomEntity.this.orbitOffset = new Vec3d(livingentity.getPosX(), livingentity.getPosYHeight(0.5D), livingentity.getPosZ());
         if (PhantomEntity.this.getBoundingBox().grow((double)0.2F).intersects(livingentity.getBoundingBox())) {
            PhantomEntity.this.attackEntityAsMob(livingentity);
            PhantomEntity.this.attackPhase = PhantomEntity.AttackPhase.CIRCLE;
            PhantomEntity.this.world.playEvent(1039, new BlockPos(PhantomEntity.this), 0);
         } else if (PhantomEntity.this.collidedHorizontally || PhantomEntity.this.hurtTime > 0) {
            PhantomEntity.this.attackPhase = PhantomEntity.AttackPhase.CIRCLE;
         }

      }
   }
}