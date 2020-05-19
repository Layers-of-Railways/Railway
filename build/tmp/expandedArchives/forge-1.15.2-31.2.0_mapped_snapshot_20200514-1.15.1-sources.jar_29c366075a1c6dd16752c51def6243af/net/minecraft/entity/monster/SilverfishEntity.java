package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class SilverfishEntity extends MonsterEntity {
   private SilverfishEntity.SummonSilverfishGoal summonSilverfish;

   public SilverfishEntity(EntityType<? extends SilverfishEntity> typeIn, World worldIn) {
      super(typeIn, worldIn);
   }

   protected void registerGoals() {
      this.summonSilverfish = new SilverfishEntity.SummonSilverfishGoal(this);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(3, this.summonSilverfish);
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(5, new SilverfishEntity.HideInStoneGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
   }

   /**
    * Returns the Y Offset of this entity.
    */
   public double getYOffset() {
      return 0.1D;
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 0.1F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SILVERFISH_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SILVERFISH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SILVERFISH_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.15F, 1.0F);
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         if ((source instanceof EntityDamageSource || source == DamageSource.MAGIC) && this.summonSilverfish != null) {
            this.summonSilverfish.notifyHurt();
         }

         return super.attackEntityFrom(source, amount);
      }
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

   public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
      return SilverfishBlock.canContainSilverfish(worldIn.getBlockState(pos.down())) ? 10.0F : super.getBlockPathWeight(pos, worldIn);
   }

   public static boolean func_223331_b(EntityType<SilverfishEntity> p_223331_0_, IWorld p_223331_1_, SpawnReason reason, BlockPos p_223331_3_, Random p_223331_4_) {
      if (canMonsterSpawn(p_223331_0_, p_223331_1_, reason, p_223331_3_, p_223331_4_)) {
         PlayerEntity playerentity = p_223331_1_.getClosestPlayer((double)p_223331_3_.getX() + 0.5D, (double)p_223331_3_.getY() + 0.5D, (double)p_223331_3_.getZ() + 0.5D, 5.0D, true);
         return playerentity == null;
      } else {
         return false;
      }
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ARTHROPOD;
   }

   static class HideInStoneGoal extends RandomWalkingGoal {
      private Direction facing;
      private boolean doMerge;

      public HideInStoneGoal(SilverfishEntity silverfishIn) {
         super(silverfishIn, 1.0D, 10);
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (this.creature.getAttackTarget() != null) {
            return false;
         } else if (!this.creature.getNavigator().noPath()) {
            return false;
         } else {
            Random random = this.creature.getRNG();
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.creature.world, this.creature) && random.nextInt(10) == 0) {
               this.facing = Direction.random(random);
               BlockPos blockpos = (new BlockPos(this.creature.getPosX(), this.creature.getPosY() + 0.5D, this.creature.getPosZ())).offset(this.facing);
               BlockState blockstate = this.creature.world.getBlockState(blockpos);
               if (SilverfishBlock.canContainSilverfish(blockstate)) {
                  this.doMerge = true;
                  return true;
               }
            }

            this.doMerge = false;
            return super.shouldExecute();
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return this.doMerge ? false : super.shouldContinueExecuting();
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         if (!this.doMerge) {
            super.startExecuting();
         } else {
            IWorld iworld = this.creature.world;
            BlockPos blockpos = (new BlockPos(this.creature.getPosX(), this.creature.getPosY() + 0.5D, this.creature.getPosZ())).offset(this.facing);
            BlockState blockstate = iworld.getBlockState(blockpos);
            if (SilverfishBlock.canContainSilverfish(blockstate)) {
               iworld.setBlockState(blockpos, SilverfishBlock.infest(blockstate.getBlock()), 3);
               this.creature.spawnExplosionParticle();
               this.creature.remove();
            }

         }
      }
   }

   static class SummonSilverfishGoal extends Goal {
      private final SilverfishEntity silverfish;
      private int lookForFriends;

      public SummonSilverfishGoal(SilverfishEntity silverfishIn) {
         this.silverfish = silverfishIn;
      }

      public void notifyHurt() {
         if (this.lookForFriends == 0) {
            this.lookForFriends = 20;
         }

      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return this.lookForFriends > 0;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         --this.lookForFriends;
         if (this.lookForFriends <= 0) {
            World world = this.silverfish.world;
            Random random = this.silverfish.getRNG();
            BlockPos blockpos = new BlockPos(this.silverfish);

            for(int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
               for(int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
                  for(int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
                     BlockPos blockpos1 = blockpos.add(j, i, k);
                     BlockState blockstate = world.getBlockState(blockpos1);
                     Block block = blockstate.getBlock();
                     if (block instanceof SilverfishBlock) {
                        if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, this.silverfish)) {
                           world.destroyBlock(blockpos1, true, this.silverfish);
                        } else {
                           world.setBlockState(blockpos1, ((SilverfishBlock)block).getMimickedBlock().getDefaultState(), 3);
                        }

                        if (random.nextBoolean()) {
                           return;
                        }
                     }
                  }
               }
            }
         }

      }
   }
}