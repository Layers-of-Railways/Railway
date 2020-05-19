package net.minecraft.entity.monster;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveTowardsRaidGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractRaiderEntity extends PatrollerEntity {
   protected static final DataParameter<Boolean> field_213666_c = EntityDataManager.createKey(AbstractRaiderEntity.class, DataSerializers.BOOLEAN);
   private static final Predicate<ItemEntity> field_213665_b = (p_213647_0_) -> {
      return !p_213647_0_.cannotPickup() && p_213647_0_.isAlive() && ItemStack.areItemStacksEqual(p_213647_0_.getItem(), Raid.createIllagerBanner());
   };
   @Nullable
   protected Raid raid;
   private int wave;
   private boolean canJoinRaid;
   private int field_213664_bB;

   protected AbstractRaiderEntity(EntityType<? extends AbstractRaiderEntity> type, World worldIn) {
      super(type, worldIn);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new AbstractRaiderEntity.PromoteLeaderGoal<>(this));
      this.goalSelector.addGoal(3, new MoveTowardsRaidGoal<>(this));
      this.goalSelector.addGoal(4, new AbstractRaiderEntity.InvadeHomeGoal(this, (double)1.05F, 1));
      this.goalSelector.addGoal(5, new AbstractRaiderEntity.CelebrateRaidLossGoal(this));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(field_213666_c, false);
   }

   public abstract void func_213660_a(int p_213660_1_, boolean p_213660_2_);

   public boolean func_213658_ej() {
      return this.canJoinRaid;
   }

   public void func_213644_t(boolean p_213644_1_) {
      this.canJoinRaid = p_213644_1_;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      if (this.world instanceof ServerWorld && this.isAlive()) {
         Raid raid = this.getRaid();
         if (this.func_213658_ej()) {
            if (raid == null) {
               if (this.world.getGameTime() % 20L == 0L) {
                  Raid raid1 = ((ServerWorld)this.world).findRaid(new BlockPos(this));
                  if (raid1 != null && RaidManager.canJoinRaid(this, raid1)) {
                     raid1.joinRaid(raid1.getGroupsSpawned(), this, (BlockPos)null, true);
                  }
               }
            } else {
               LivingEntity livingentity = this.getAttackTarget();
               if (livingentity != null && (livingentity.getType() == EntityType.PLAYER || livingentity.getType() == EntityType.IRON_GOLEM)) {
                  this.idleTime = 0;
               }
            }
         }
      }

      super.livingTick();
   }

   protected void func_213623_ec() {
      this.idleTime += 2;
   }

   /**
    * Called when the mob's health reaches 0.
    */
   public void onDeath(DamageSource cause) {
      if (this.world instanceof ServerWorld) {
         Entity entity = cause.getTrueSource();
         Raid raid = this.getRaid();
         if (raid != null) {
            if (this.isLeader()) {
               raid.removeLeader(this.func_213642_em());
            }

            if (entity != null && entity.getType() == EntityType.PLAYER) {
               raid.addHero(entity);
            }

            raid.leaveRaid(this, false);
         }

         if (this.isLeader() && raid == null && ((ServerWorld)this.world).findRaid(new BlockPos(this)) == null) {
            ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
            PlayerEntity playerentity = null;
            if (entity instanceof PlayerEntity) {
               playerentity = (PlayerEntity)entity;
            } else if (entity instanceof WolfEntity) {
               WolfEntity wolfentity = (WolfEntity)entity;
               LivingEntity livingentity = wolfentity.getOwner();
               if (wolfentity.isTamed() && livingentity instanceof PlayerEntity) {
                  playerentity = (PlayerEntity)livingentity;
               }
            }

            if (!itemstack.isEmpty() && ItemStack.areItemStacksEqual(itemstack, Raid.createIllagerBanner()) && playerentity != null) {
               EffectInstance effectinstance1 = playerentity.getActivePotionEffect(Effects.BAD_OMEN);
               int i = 1;
               if (effectinstance1 != null) {
                  i += effectinstance1.getAmplifier();
                  playerentity.removeActivePotionEffect(Effects.BAD_OMEN);
               } else {
                  --i;
               }

               i = MathHelper.clamp(i, 0, 5);
               EffectInstance effectinstance = new EffectInstance(Effects.BAD_OMEN, 120000, i, false, false, true);
               if (!this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
                  playerentity.addPotionEffect(effectinstance);
               }
            }
         }
      }

      super.onDeath(cause);
   }

   public boolean func_213634_ed() {
      return !this.isRaidActive();
   }

   public void setRaid(@Nullable Raid p_213652_1_) {
      this.raid = p_213652_1_;
   }

   @Nullable
   public Raid getRaid() {
      return this.raid;
   }

   public boolean isRaidActive() {
      return this.getRaid() != null && this.getRaid().isActive();
   }

   public void setWave(int p_213651_1_) {
      this.wave = p_213651_1_;
   }

   public int func_213642_em() {
      return this.wave;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_213656_en() {
      return this.dataManager.get(field_213666_c);
   }

   public void func_213655_u(boolean p_213655_1_) {
      this.dataManager.set(field_213666_c, p_213655_1_);
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("Wave", this.wave);
      compound.putBoolean("CanJoinRaid", this.canJoinRaid);
      if (this.raid != null) {
         compound.putInt("RaidId", this.raid.getId());
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.wave = compound.getInt("Wave");
      this.canJoinRaid = compound.getBoolean("CanJoinRaid");
      if (compound.contains("RaidId", 3)) {
         if (this.world instanceof ServerWorld) {
            this.raid = ((ServerWorld)this.world).getRaids().get(compound.getInt("RaidId"));
         }

         if (this.raid != null) {
            this.raid.joinRaid(this.wave, this, false);
            if (this.isLeader()) {
               this.raid.setLeader(this.wave, this);
            }
         }
      }

   }

   /**
    * Tests if this entity should pickup a weapon or an armor. Entity drops current weapon or armor if the new one is
    * better.
    */
   protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
      ItemStack itemstack = itemEntity.getItem();
      boolean flag = this.isRaidActive() && this.getRaid().getLeader(this.func_213642_em()) != null;
      if (this.isRaidActive() && !flag && ItemStack.areItemStacksEqual(itemstack, Raid.createIllagerBanner())) {
         EquipmentSlotType equipmentslottype = EquipmentSlotType.HEAD;
         ItemStack itemstack1 = this.getItemStackFromSlot(equipmentslottype);
         double d0 = (double)this.getDropChance(equipmentslottype);
         if (!itemstack1.isEmpty() && (double)Math.max(this.rand.nextFloat() - 0.1F, 0.0F) < d0) {
            this.entityDropItem(itemstack1);
         }

         this.setItemStackToSlot(equipmentslottype, itemstack);
         this.onItemPickup(itemEntity, itemstack.getCount());
         itemEntity.remove();
         this.getRaid().setLeader(this.func_213642_em(), this);
         this.setLeader(true);
      } else {
         super.updateEquipmentIfNeeded(itemEntity);
      }

   }

   public boolean canDespawn(double distanceToClosestPlayer) {
      return this.getRaid() == null ? super.canDespawn(distanceToClosestPlayer) : false;
   }

   public boolean preventDespawn() {
      return this.getRaid() != null;
   }

   public int func_213661_eo() {
      return this.field_213664_bB;
   }

   public void func_213653_b(int p_213653_1_) {
      this.field_213664_bB = p_213653_1_;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isRaidActive()) {
         this.getRaid().updateBarPercentage();
      }

      return super.attackEntityFrom(source, amount);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      this.func_213644_t(this.getType() != EntityType.WITCH || reason != SpawnReason.NATURAL);
      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public abstract SoundEvent getRaidLossSound();

   public class CelebrateRaidLossGoal extends Goal {
      private final AbstractRaiderEntity field_220858_b;

      CelebrateRaidLossGoal(AbstractRaiderEntity p_i50571_2_) {
         this.field_220858_b = p_i50571_2_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         Raid raid = this.field_220858_b.getRaid();
         return this.field_220858_b.isAlive() && this.field_220858_b.getAttackTarget() == null && raid != null && raid.isLoss();
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.field_220858_b.func_213655_u(true);
         super.startExecuting();
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.field_220858_b.func_213655_u(false);
         super.resetTask();
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (!this.field_220858_b.isSilent() && this.field_220858_b.rand.nextInt(100) == 0) {
            AbstractRaiderEntity.this.playSound(AbstractRaiderEntity.this.getRaidLossSound(), AbstractRaiderEntity.this.getSoundVolume(), AbstractRaiderEntity.this.getSoundPitch());
         }

         if (!this.field_220858_b.isPassenger() && this.field_220858_b.rand.nextInt(50) == 0) {
            this.field_220858_b.getJumpController().setJumping();
         }

         super.tick();
      }
   }

   public class FindTargetGoal extends Goal {
      private final AbstractRaiderEntity field_220853_c;
      private final float field_220854_d;
      public final EntityPredicate field_220851_a = (new EntityPredicate()).setDistance(8.0D).setSkipAttackChecks().allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired().setUseInvisibilityCheck();

      public FindTargetGoal(AbstractIllagerEntity p_i50573_2_, float p_i50573_3_) {
         this.field_220853_c = p_i50573_2_;
         this.field_220854_d = p_i50573_3_ * p_i50573_3_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         LivingEntity livingentity = this.field_220853_c.getRevengeTarget();
         return this.field_220853_c.getRaid() == null && this.field_220853_c.isPatrolling() && this.field_220853_c.getAttackTarget() != null && !this.field_220853_c.isAggressive() && (livingentity == null || livingentity.getType() != EntityType.PLAYER);
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         super.startExecuting();
         this.field_220853_c.getNavigator().clearPath();

         for(AbstractRaiderEntity abstractraiderentity : this.field_220853_c.world.getTargettableEntitiesWithinAABB(AbstractRaiderEntity.class, this.field_220851_a, this.field_220853_c, this.field_220853_c.getBoundingBox().grow(8.0D, 8.0D, 8.0D))) {
            abstractraiderentity.setAttackTarget(this.field_220853_c.getAttackTarget());
         }

      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         super.resetTask();
         LivingEntity livingentity = this.field_220853_c.getAttackTarget();
         if (livingentity != null) {
            for(AbstractRaiderEntity abstractraiderentity : this.field_220853_c.world.getTargettableEntitiesWithinAABB(AbstractRaiderEntity.class, this.field_220851_a, this.field_220853_c, this.field_220853_c.getBoundingBox().grow(8.0D, 8.0D, 8.0D))) {
               abstractraiderentity.setAttackTarget(livingentity);
               abstractraiderentity.setAggroed(true);
            }

            this.field_220853_c.setAggroed(true);
         }

      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         LivingEntity livingentity = this.field_220853_c.getAttackTarget();
         if (livingentity != null) {
            if (this.field_220853_c.getDistanceSq(livingentity) > (double)this.field_220854_d) {
               this.field_220853_c.getLookController().setLookPositionWithEntity(livingentity, 30.0F, 30.0F);
               if (this.field_220853_c.rand.nextInt(50) == 0) {
                  this.field_220853_c.playAmbientSound();
               }
            } else {
               this.field_220853_c.setAggroed(true);
            }

            super.tick();
         }
      }
   }

   static class InvadeHomeGoal extends Goal {
      private final AbstractRaiderEntity field_220864_a;
      private final double field_220865_b;
      private BlockPos field_220866_c;
      private final List<BlockPos> field_220867_d = Lists.newArrayList();
      private final int field_220868_e;
      private boolean field_220869_f;

      public InvadeHomeGoal(AbstractRaiderEntity p_i50570_1_, double p_i50570_2_, int p_i50570_4_) {
         this.field_220864_a = p_i50570_1_;
         this.field_220865_b = p_i50570_2_;
         this.field_220868_e = p_i50570_4_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         this.func_220861_j();
         return this.func_220862_g() && this.func_220863_h() && this.field_220864_a.getAttackTarget() == null;
      }

      private boolean func_220862_g() {
         return this.field_220864_a.isRaidActive() && !this.field_220864_a.getRaid().isOver();
      }

      private boolean func_220863_h() {
         ServerWorld serverworld = (ServerWorld)this.field_220864_a.world;
         BlockPos blockpos = new BlockPos(this.field_220864_a);
         Optional<BlockPos> optional = serverworld.getPointOfInterestManager().getRandom((p_220859_0_) -> {
            return p_220859_0_ == PointOfInterestType.HOME;
         }, this::func_220860_a, PointOfInterestManager.Status.ANY, blockpos, 48, this.field_220864_a.rand);
         if (!optional.isPresent()) {
            return false;
         } else {
            this.field_220866_c = optional.get().toImmutable();
            return true;
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         if (this.field_220864_a.getNavigator().noPath()) {
            return false;
         } else {
            return this.field_220864_a.getAttackTarget() == null && !this.field_220866_c.withinDistance(this.field_220864_a.getPositionVec(), (double)(this.field_220864_a.getWidth() + (float)this.field_220868_e)) && !this.field_220869_f;
         }
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         if (this.field_220866_c.withinDistance(this.field_220864_a.getPositionVec(), (double)this.field_220868_e)) {
            this.field_220867_d.add(this.field_220866_c);
         }

      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         super.startExecuting();
         this.field_220864_a.setIdleTime(0);
         this.field_220864_a.getNavigator().tryMoveToXYZ((double)this.field_220866_c.getX(), (double)this.field_220866_c.getY(), (double)this.field_220866_c.getZ(), this.field_220865_b);
         this.field_220869_f = false;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.field_220864_a.getNavigator().noPath()) {
            Vec3d vec3d = new Vec3d(this.field_220866_c);
            Vec3d vec3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.field_220864_a, 16, 7, vec3d, (double)((float)Math.PI / 10F));
            if (vec3d1 == null) {
               vec3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_220864_a, 8, 7, vec3d);
            }

            if (vec3d1 == null) {
               this.field_220869_f = true;
               return;
            }

            this.field_220864_a.getNavigator().tryMoveToXYZ(vec3d1.x, vec3d1.y, vec3d1.z, this.field_220865_b);
         }

      }

      private boolean func_220860_a(BlockPos p_220860_1_) {
         for(BlockPos blockpos : this.field_220867_d) {
            if (Objects.equals(p_220860_1_, blockpos)) {
               return false;
            }
         }

         return true;
      }

      private void func_220861_j() {
         if (this.field_220867_d.size() > 2) {
            this.field_220867_d.remove(0);
         }

      }
   }

   public class PromoteLeaderGoal<T extends AbstractRaiderEntity> extends Goal {
      private final T field_220856_b;

      public PromoteLeaderGoal(T p_i50572_2_) {
         this.field_220856_b = p_i50572_2_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         Raid raid = this.field_220856_b.getRaid();
         if (this.field_220856_b.isRaidActive() && !this.field_220856_b.getRaid().isOver() && this.field_220856_b.canBeLeader() && !ItemStack.areItemStacksEqual(this.field_220856_b.getItemStackFromSlot(EquipmentSlotType.HEAD), Raid.createIllagerBanner())) {
            AbstractRaiderEntity abstractraiderentity = raid.getLeader(this.field_220856_b.func_213642_em());
            if (abstractraiderentity == null || !abstractraiderentity.isAlive()) {
               List<ItemEntity> list = this.field_220856_b.world.getEntitiesWithinAABB(ItemEntity.class, this.field_220856_b.getBoundingBox().grow(16.0D, 8.0D, 16.0D), AbstractRaiderEntity.field_213665_b);
               if (!list.isEmpty()) {
                  return this.field_220856_b.getNavigator().tryMoveToEntityLiving(list.get(0), (double)1.15F);
               }
            }

            return false;
         } else {
            return false;
         }
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.field_220856_b.getNavigator().getTargetPos().withinDistance(this.field_220856_b.getPositionVec(), 1.414D)) {
            List<ItemEntity> list = this.field_220856_b.world.getEntitiesWithinAABB(ItemEntity.class, this.field_220856_b.getBoundingBox().grow(4.0D, 4.0D, 4.0D), AbstractRaiderEntity.field_213665_b);
            if (!list.isEmpty()) {
               this.field_220856_b.updateEquipmentIfNeeded(list.get(0));
            }
         }

      }
   }
}