package net.minecraft.entity.passive.fish;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class PufferfishEntity extends AbstractFishEntity {
   private static final DataParameter<Integer> PUFF_STATE = EntityDataManager.createKey(PufferfishEntity.class, DataSerializers.VARINT);
   private int puffTimer;
   private int deflateTimer;
   private static final Predicate<LivingEntity> ENEMY_MATCHER = (p_210139_0_) -> {
      if (p_210139_0_ == null) {
         return false;
      } else if (!(p_210139_0_ instanceof PlayerEntity) || !p_210139_0_.isSpectator() && !((PlayerEntity)p_210139_0_).isCreative()) {
         return p_210139_0_.getCreatureAttribute() != CreatureAttribute.WATER;
      } else {
         return false;
      }
   };

   public PufferfishEntity(EntityType<? extends PufferfishEntity> p_i50248_1_, World p_i50248_2_) {
      super(p_i50248_1_, p_i50248_2_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(PUFF_STATE, 0);
   }

   public int getPuffState() {
      return this.dataManager.get(PUFF_STATE);
   }

   public void setPuffState(int p_203714_1_) {
      this.dataManager.set(PUFF_STATE, p_203714_1_);
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (PUFF_STATE.equals(key)) {
         this.recalculateSize();
      }

      super.notifyDataManagerChange(key);
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("PuffState", this.getPuffState());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setPuffState(compound.getInt("PuffState"));
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.PUFFERFISH_BUCKET);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new PufferfishEntity.PuffGoal(this));
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (!this.world.isRemote && this.isAlive() && this.isServerWorld()) {
         if (this.puffTimer > 0) {
            if (this.getPuffState() == 0) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(1);
            } else if (this.puffTimer > 40 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(2);
            }

            ++this.puffTimer;
         } else if (this.getPuffState() != 0) {
            if (this.deflateTimer > 60 && this.getPuffState() == 2) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(1);
            } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(0);
            }

            ++this.deflateTimer;
         }
      }

      super.tick();
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      if (this.isAlive() && this.getPuffState() > 0) {
         for(MobEntity mobentity : this.world.getEntitiesWithinAABB(MobEntity.class, this.getBoundingBox().grow(0.3D), ENEMY_MATCHER)) {
            if (mobentity.isAlive()) {
               this.attack(mobentity);
            }
         }
      }

   }

   private void attack(MobEntity p_205719_1_) {
      int i = this.getPuffState();
      if (p_205719_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(1 + i))) {
         p_205719_1_.addPotionEffect(new EffectInstance(Effects.POISON, 60 * i, 0));
         this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
      }

   }

   /**
    * Called by a player entity when they collide with an entity
    */
   public void onCollideWithPlayer(PlayerEntity entityIn) {
      int i = this.getPuffState();
      if (entityIn instanceof ServerPlayerEntity && i > 0 && entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(1 + i))) {
         ((ServerPlayerEntity)entityIn).connection.sendPacket(new SChangeGameStatePacket(9, 0.0F));
         entityIn.addPotionEffect(new EffectInstance(Effects.POISON, 60 * i, 0));
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_PUFFER_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_FLOP;
   }

   public EntitySize getSize(Pose poseIn) {
      return super.getSize(poseIn).scale(getPuffSize(this.getPuffState()));
   }

   private static float getPuffSize(int p_213806_0_) {
      switch(p_213806_0_) {
      case 0:
         return 0.5F;
      case 1:
         return 0.7F;
      default:
         return 1.0F;
      }
   }

   static class PuffGoal extends Goal {
      private final PufferfishEntity fish;

      public PuffGoal(PufferfishEntity fish) {
         this.fish = fish;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         List<LivingEntity> list = this.fish.world.getEntitiesWithinAABB(LivingEntity.class, this.fish.getBoundingBox().grow(2.0D), PufferfishEntity.ENEMY_MATCHER);
         return !list.isEmpty();
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.fish.puffTimer = 1;
         this.fish.deflateTimer = 0;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.fish.puffTimer = 0;
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         List<LivingEntity> list = this.fish.world.getEntitiesWithinAABB(LivingEntity.class, this.fish.getBoundingBox().grow(2.0D), PufferfishEntity.ENEMY_MATCHER);
         return !list.isEmpty();
      }
   }
}