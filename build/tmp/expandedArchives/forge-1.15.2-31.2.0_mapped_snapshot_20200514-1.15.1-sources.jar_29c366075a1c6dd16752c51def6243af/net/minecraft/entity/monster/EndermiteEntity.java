package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class EndermiteEntity extends MonsterEntity {
   private int lifetime;
   private boolean playerSpawned;

   public EndermiteEntity(EntityType<? extends EndermiteEntity> type, World worldIn) {
      super(type, worldIn);
      this.experienceValue = 3;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 0.1F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ENDERMITE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ENDERMITE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ENDERMITE_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_ENDERMITE_STEP, 0.15F, 1.0F);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.lifetime = compound.getInt("Lifetime");
      this.playerSpawned = compound.getBoolean("PlayerSpawned");
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("Lifetime", this.lifetime);
      compound.putBoolean("PlayerSpawned", this.playerSpawned);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      this.renderYawOffset = this.rotationYaw;
      super.tick();
   }

   /**
    * Set the render yaw offset
    */
   public void setRenderYawOffset(float offset) {
      this.rotationYaw = offset;
      super.setRenderYawOffset(offset);
   }

   /**
    * Returns the Y Offset of this entity.
    */
   public double getYOffset() {
      return 0.1D;
   }

   public boolean isSpawnedByPlayer() {
      return this.playerSpawned;
   }

   /**
    * Sets if this mob was spawned by a player or not.
    */
   public void setSpawnedByPlayer(boolean spawnedByPlayer) {
      this.playerSpawned = spawnedByPlayer;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      if (this.world.isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.world.addParticle(ParticleTypes.PORTAL, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
         }
      } else {
         if (!this.isNoDespawnRequired()) {
            ++this.lifetime;
         }

         if (this.lifetime >= 2400) {
            this.remove();
         }
      }

   }

   public static boolean func_223328_b(EntityType<EndermiteEntity> p_223328_0_, IWorld p_223328_1_, SpawnReason reason, BlockPos p_223328_3_, Random p_223328_4_) {
      if (canMonsterSpawn(p_223328_0_, p_223328_1_, reason, p_223328_3_, p_223328_4_)) {
         PlayerEntity playerentity = p_223328_1_.getClosestPlayer((double)p_223328_3_.getX() + 0.5D, (double)p_223328_3_.getY() + 0.5D, (double)p_223328_3_.getZ() + 0.5D, 5.0D, true);
         return playerentity == null;
      } else {
         return false;
      }
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ARTHROPOD;
   }
}