package net.minecraft.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SpiderEntity extends MonsterEntity {
   private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(SpiderEntity.class, DataSerializers.BYTE);

   public SpiderEntity(EntityType<? extends SpiderEntity> type, World worldIn) {
      super(type, worldIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new SpiderEntity.AttackGoal(this));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new SpiderEntity.TargetGoal<>(this, PlayerEntity.class));
      this.targetSelector.addGoal(3, new SpiderEntity.TargetGoal<>(this, IronGolemEntity.class));
   }

   /**
    * Returns the Y offset from the entity's position for any entity riding this one.
    */
   public double getMountedYOffset() {
      return (double)(this.getHeight() * 0.5F);
   }

   /**
    * Returns new PathNavigateGround instance
    */
   protected PathNavigator createNavigator(World worldIn) {
      return new ClimberPathNavigator(this, worldIn);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CLIMBING, (byte)0);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (!this.world.isRemote) {
         this.setBesideClimbableBlock(this.collidedHorizontally);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SPIDER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SPIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SPIDER_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
   }

   /**
    * Returns true if this entity should move as if it were on a ladder (either because it's actually on a ladder, or
    * for AI reasons)
    */
   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   public void setMotionMultiplier(BlockState state, Vec3d motionMultiplierIn) {
      if (state.getBlock() != Blocks.COBWEB) {
         super.setMotionMultiplier(state, motionMultiplierIn);
      }

   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ARTHROPOD;
   }

   public boolean isPotionApplicable(EffectInstance potioneffectIn) {
      if (potioneffectIn.getPotion() == Effects.POISON) {
         net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(this, potioneffectIn);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
         return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
      }
      return super.isPotionApplicable(potioneffectIn);
   }

   /**
    * Returns true if the WatchableObject (Byte) is 0x01 otherwise returns false. The WatchableObject is updated using
    * setBesideClimableBlock.
    */
   public boolean isBesideClimbableBlock() {
      return (this.dataManager.get(CLIMBING) & 1) != 0;
   }

   /**
    * Updates the WatchableObject (Byte) created in entityInit(), setting it to 0x01 if par1 is true or 0x00 if it is
    * false.
    */
   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = this.dataManager.get(CLIMBING);
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.dataManager.set(CLIMBING, b0);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
      if (worldIn.getRandom().nextInt(100) == 0) {
         SkeletonEntity skeletonentity = EntityType.SKELETON.create(this.world);
         skeletonentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, 0.0F);
         skeletonentity.onInitialSpawn(worldIn, difficultyIn, reason, (ILivingEntityData)null, (CompoundNBT)null);
         worldIn.addEntity(skeletonentity);
         skeletonentity.startRiding(this);
      }

      if (spawnDataIn == null) {
         spawnDataIn = new SpiderEntity.GroupData();
         if (worldIn.getDifficulty() == Difficulty.HARD && worldIn.getRandom().nextFloat() < 0.1F * difficultyIn.getClampedAdditionalDifficulty()) {
            ((SpiderEntity.GroupData)spawnDataIn).setRandomEffect(worldIn.getRandom());
         }
      }

      if (spawnDataIn instanceof SpiderEntity.GroupData) {
         Effect effect = ((SpiderEntity.GroupData)spawnDataIn).effect;
         if (effect != null) {
            this.addPotionEffect(new EffectInstance(effect, Integer.MAX_VALUE));
         }
      }

      return spawnDataIn;
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 0.65F;
   }

   static class AttackGoal extends MeleeAttackGoal {
      public AttackGoal(SpiderEntity spider) {
         super(spider, 1.0D, true);
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && !this.attacker.isBeingRidden();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         float f = this.attacker.getBrightness();
         if (f >= 0.5F && this.attacker.getRNG().nextInt(100) == 0) {
            this.attacker.setAttackTarget((LivingEntity)null);
            return false;
         } else {
            return super.shouldContinueExecuting();
         }
      }

      protected double getAttackReachSqr(LivingEntity attackTarget) {
         return (double)(4.0F + attackTarget.getWidth());
      }
   }

   public static class GroupData implements ILivingEntityData {
      public Effect effect;

      public void setRandomEffect(Random rand) {
         int i = rand.nextInt(5);
         if (i <= 1) {
            this.effect = Effects.SPEED;
         } else if (i <= 2) {
            this.effect = Effects.STRENGTH;
         } else if (i <= 3) {
            this.effect = Effects.REGENERATION;
         } else if (i <= 4) {
            this.effect = Effects.INVISIBILITY;
         }

      }
   }

   static class TargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
      public TargetGoal(SpiderEntity spider, Class<T> classTarget) {
         super(spider, classTarget, true);
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         float f = this.goalOwner.getBrightness();
         return f >= 0.5F ? false : super.shouldExecute();
      }
   }
}