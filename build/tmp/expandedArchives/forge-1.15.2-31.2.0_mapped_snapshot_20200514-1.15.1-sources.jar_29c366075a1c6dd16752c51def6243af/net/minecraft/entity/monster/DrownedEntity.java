package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class DrownedEntity extends ZombieEntity implements IRangedAttackMob {
   private boolean swimmingUp;
   protected final SwimmerPathNavigator waterNavigator;
   protected final GroundPathNavigator groundNavigator;

   public DrownedEntity(EntityType<? extends DrownedEntity> type, World worldIn) {
      super(type, worldIn);
      this.stepHeight = 1.0F;
      this.moveController = new DrownedEntity.MoveHelperController(this);
      this.setPathPriority(PathNodeType.WATER, 0.0F);
      this.waterNavigator = new SwimmerPathNavigator(this, worldIn);
      this.groundNavigator = new GroundPathNavigator(this, worldIn);
   }

   protected void applyEntityAI() {
      this.goalSelector.addGoal(1, new DrownedEntity.GoToWaterGoal(this, 1.0D));
      this.goalSelector.addGoal(2, new DrownedEntity.TridentAttackGoal(this, 1.0D, 40, 10.0F));
      this.goalSelector.addGoal(2, new DrownedEntity.AttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(5, new DrownedEntity.GoToBeachGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new DrownedEntity.SwimUpGoal(this, 1.0D, this.world.getSeaLevel()));
      this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, DrownedEntity.class)).setCallsForHelp(ZombiePigmanEntity.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAttack));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.TARGET_DRY_BABY));
   }

   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
      if (this.getItemStackFromSlot(EquipmentSlotType.OFFHAND).isEmpty() && this.rand.nextFloat() < 0.03F) {
         this.setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
         this.inventoryHandsDropChances[EquipmentSlotType.OFFHAND.getIndex()] = 2.0F;
      }

      return spawnDataIn;
   }

   public static boolean func_223332_b(EntityType<DrownedEntity> p_223332_0_, IWorld p_223332_1_, SpawnReason reason, BlockPos p_223332_3_, Random p_223332_4_) {
      Biome biome = p_223332_1_.getBiome(p_223332_3_);
      boolean flag = p_223332_1_.getDifficulty() != Difficulty.PEACEFUL && isValidLightLevel(p_223332_1_, p_223332_3_, p_223332_4_) && (reason == SpawnReason.SPAWNER || p_223332_1_.getFluidState(p_223332_3_).isTagged(FluidTags.WATER));
      if (biome != Biomes.RIVER && biome != Biomes.FROZEN_RIVER) {
         return p_223332_4_.nextInt(40) == 0 && func_223333_a(p_223332_1_, p_223332_3_) && flag;
      } else {
         return p_223332_4_.nextInt(15) == 0 && flag;
      }
   }

   private static boolean func_223333_a(IWorld p_223333_0_, BlockPos p_223333_1_) {
      return p_223333_1_.getY() < p_223333_0_.getSeaLevel() - 5;
   }

   protected boolean canBreakDoors() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.ENTITY_DROWNED_AMBIENT_WATER : SoundEvents.ENTITY_DROWNED_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isInWater() ? SoundEvents.ENTITY_DROWNED_HURT_WATER : SoundEvents.ENTITY_DROWNED_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWater() ? SoundEvents.ENTITY_DROWNED_DEATH_WATER : SoundEvents.ENTITY_DROWNED_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_DROWNED_STEP;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_DROWNED_SWIM;
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }

   /**
    * Gives armor or weapon for entity based on given DifficultyInstance
    */
   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      if ((double)this.rand.nextFloat() > 0.9D) {
         int i = this.rand.nextInt(16);
         if (i < 10) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.TRIDENT));
         } else {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.FISHING_ROD));
         }
      }

   }

   protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing, EquipmentSlotType slotTypeIn) {
      if (existing.getItem() == Items.NAUTILUS_SHELL) {
         return false;
      } else if (existing.getItem() == Items.TRIDENT) {
         if (candidate.getItem() == Items.TRIDENT) {
            return candidate.getDamage() < existing.getDamage();
         } else {
            return false;
         }
      } else {
         return candidate.getItem() == Items.TRIDENT ? true : super.shouldExchangeEquipment(candidate, existing, slotTypeIn);
      }
   }

   protected boolean shouldDrown() {
      return false;
   }

   public boolean isNotColliding(IWorldReader worldIn) {
      return worldIn.checkNoEntityCollision(this);
   }

   public boolean shouldAttack(@Nullable LivingEntity p_204714_1_) {
      if (p_204714_1_ != null) {
         return !this.world.isDaytime() || p_204714_1_.isInWater();
      } else {
         return false;
      }
   }

   public boolean isPushedByWater() {
      return !this.isSwimming();
   }

   private boolean func_204715_dF() {
      if (this.swimmingUp) {
         return true;
      } else {
         LivingEntity livingentity = this.getAttackTarget();
         return livingentity != null && livingentity.isInWater();
      }
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isServerWorld() && this.isInWater() && this.func_204715_dF()) {
         this.moveRelative(0.01F, p_213352_1_);
         this.move(MoverType.SELF, this.getMotion());
         this.setMotion(this.getMotion().scale(0.9D));
      } else {
         super.travel(p_213352_1_);
      }

   }

   public void updateSwimming() {
      if (!this.world.isRemote) {
         if (this.isServerWorld() && this.isInWater() && this.func_204715_dF()) {
            this.navigator = this.waterNavigator;
            this.setSwimming(true);
         } else {
            this.navigator = this.groundNavigator;
            this.setSwimming(false);
         }
      }

   }

   protected boolean isCloseToPathTarget() {
      Path path = this.getNavigator().getPath();
      if (path != null) {
         BlockPos blockpos = path.getTarget();
         if (blockpos != null) {
            double d0 = this.getDistanceSq((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
            if (d0 < 4.0D) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * Attack the specified entity using a ranged attack.
    */
   public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
      TridentEntity tridententity = new TridentEntity(this.world, this, new ItemStack(Items.TRIDENT));
      double d0 = target.getPosX() - this.getPosX();
      double d1 = target.getPosYHeight(0.3333333333333333D) - tridententity.getPosY();
      double d2 = target.getPosZ() - this.getPosZ();
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      tridententity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.world.addEntity(tridententity);
   }

   public void setSwimmingUp(boolean p_204713_1_) {
      this.swimmingUp = p_204713_1_;
   }

   static class AttackGoal extends ZombieAttackGoal {
      private final DrownedEntity field_204726_g;

      public AttackGoal(DrownedEntity p_i48913_1_, double p_i48913_2_, boolean p_i48913_4_) {
         super(p_i48913_1_, p_i48913_2_, p_i48913_4_);
         this.field_204726_g = p_i48913_1_;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && this.field_204726_g.shouldAttack(this.field_204726_g.getAttackTarget());
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && this.field_204726_g.shouldAttack(this.field_204726_g.getAttackTarget());
      }
   }

   static class GoToBeachGoal extends MoveToBlockGoal {
      private final DrownedEntity drowned;

      public GoToBeachGoal(DrownedEntity p_i48911_1_, double p_i48911_2_) {
         super(p_i48911_1_, p_i48911_2_, 8, 2);
         this.drowned = p_i48911_1_;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && !this.drowned.world.isDaytime() && this.drowned.isInWater() && this.drowned.getPosY() >= (double)(this.drowned.world.getSeaLevel() - 3);
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting();
      }

      /**
       * Return true to set given position as destination
       */
      protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
         BlockPos blockpos = pos.up();
         return worldIn.isAirBlock(blockpos) && worldIn.isAirBlock(blockpos.up()) ? worldIn.getBlockState(pos).isTopSolid(worldIn, pos, this.drowned) : false;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.drowned.setSwimmingUp(false);
         this.drowned.navigator = this.drowned.groundNavigator;
         super.startExecuting();
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         super.resetTask();
      }
   }

   static class GoToWaterGoal extends Goal {
      private final CreatureEntity field_204730_a;
      private double field_204731_b;
      private double field_204732_c;
      private double field_204733_d;
      private final double field_204734_e;
      private final World field_204735_f;

      public GoToWaterGoal(CreatureEntity p_i48910_1_, double p_i48910_2_) {
         this.field_204730_a = p_i48910_1_;
         this.field_204734_e = p_i48910_2_;
         this.field_204735_f = p_i48910_1_.world;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (!this.field_204735_f.isDaytime()) {
            return false;
         } else if (this.field_204730_a.isInWater()) {
            return false;
         } else {
            Vec3d vec3d = this.func_204729_f();
            if (vec3d == null) {
               return false;
            } else {
               this.field_204731_b = vec3d.x;
               this.field_204732_c = vec3d.y;
               this.field_204733_d = vec3d.z;
               return true;
            }
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return !this.field_204730_a.getNavigator().noPath();
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.field_204730_a.getNavigator().tryMoveToXYZ(this.field_204731_b, this.field_204732_c, this.field_204733_d, this.field_204734_e);
      }

      @Nullable
      private Vec3d func_204729_f() {
         Random random = this.field_204730_a.getRNG();
         BlockPos blockpos = new BlockPos(this.field_204730_a);

         for(int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
            if (this.field_204735_f.getBlockState(blockpos1).getBlock() == Blocks.WATER) {
               return new Vec3d(blockpos1);
            }
         }

         return null;
      }
   }

   static class MoveHelperController extends MovementController {
      private final DrownedEntity drowned;

      public MoveHelperController(DrownedEntity p_i48909_1_) {
         super(p_i48909_1_);
         this.drowned = p_i48909_1_;
      }

      public void tick() {
         LivingEntity livingentity = this.drowned.getAttackTarget();
         if (this.drowned.func_204715_dF() && this.drowned.isInWater()) {
            if (livingentity != null && livingentity.getPosY() > this.drowned.getPosY() || this.drowned.swimmingUp) {
               this.drowned.setMotion(this.drowned.getMotion().add(0.0D, 0.002D, 0.0D));
            }

            if (this.action != MovementController.Action.MOVE_TO || this.drowned.getNavigator().noPath()) {
               this.drowned.setAIMoveSpeed(0.0F);
               return;
            }

            double d0 = this.posX - this.drowned.getPosX();
            double d1 = this.posY - this.drowned.getPosY();
            double d2 = this.posZ - this.drowned.getPosZ();
            double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 = d1 / d3;
            float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.drowned.rotationYaw = this.limitAngle(this.drowned.rotationYaw, f, 90.0F);
            this.drowned.renderYawOffset = this.drowned.rotationYaw;
            float f1 = (float)(this.speed * this.drowned.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            float f2 = MathHelper.lerp(0.125F, this.drowned.getAIMoveSpeed(), f1);
            this.drowned.setAIMoveSpeed(f2);
            this.drowned.setMotion(this.drowned.getMotion().add((double)f2 * d0 * 0.005D, (double)f2 * d1 * 0.1D, (double)f2 * d2 * 0.005D));
         } else {
            if (!this.drowned.onGround) {
               this.drowned.setMotion(this.drowned.getMotion().add(0.0D, -0.008D, 0.0D));
            }

            super.tick();
         }

      }
   }

   static class SwimUpGoal extends Goal {
      private final DrownedEntity field_204736_a;
      private final double field_204737_b;
      private final int targetY;
      private boolean obstructed;

      public SwimUpGoal(DrownedEntity p_i48908_1_, double p_i48908_2_, int p_i48908_4_) {
         this.field_204736_a = p_i48908_1_;
         this.field_204737_b = p_i48908_2_;
         this.targetY = p_i48908_4_;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return !this.field_204736_a.world.isDaytime() && this.field_204736_a.isInWater() && this.field_204736_a.getPosY() < (double)(this.targetY - 2);
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return this.shouldExecute() && !this.obstructed;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.field_204736_a.getPosY() < (double)(this.targetY - 1) && (this.field_204736_a.getNavigator().noPath() || this.field_204736_a.isCloseToPathTarget())) {
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_204736_a, 4, 8, new Vec3d(this.field_204736_a.getPosX(), (double)(this.targetY - 1), this.field_204736_a.getPosZ()));
            if (vec3d == null) {
               this.obstructed = true;
               return;
            }

            this.field_204736_a.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, this.field_204737_b);
         }

      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.field_204736_a.setSwimmingUp(true);
         this.obstructed = false;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.field_204736_a.setSwimmingUp(false);
      }
   }

   static class TridentAttackGoal extends RangedAttackGoal {
      private final DrownedEntity field_204728_a;

      public TridentAttackGoal(IRangedAttackMob p_i48907_1_, double p_i48907_2_, int p_i48907_4_, float p_i48907_5_) {
         super(p_i48907_1_, p_i48907_2_, p_i48907_4_, p_i48907_5_);
         this.field_204728_a = (DrownedEntity)p_i48907_1_;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && this.field_204728_a.getHeldItemMainhand().getItem() == Items.TRIDENT;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         super.startExecuting();
         this.field_204728_a.setAggroed(true);
         this.field_204728_a.setActiveHand(Hand.MAIN_HAND);
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         super.resetTask();
         this.field_204728_a.resetActiveHand();
         this.field_204728_a.setAggroed(false);
      }
   }
}