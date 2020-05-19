package net.minecraft.entity.monster;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EvokerEntity extends SpellcastingIllagerEntity {
   private SheepEntity wololoTarget;

   public EvokerEntity(EntityType<? extends EvokerEntity> type, World worldIn) {
      super(type, worldIn);
      this.experienceValue = 10;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new EvokerEntity.CastingSpellGoal());
      this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 0.6D, 1.0D));
      this.goalSelector.addGoal(4, new EvokerEntity.SummonSpellGoal());
      this.goalSelector.addGoal(5, new EvokerEntity.AttackSpellGoal());
      this.goalSelector.addGoal(6, new EvokerEntity.WololoSpellGoal());
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
   }

   protected void registerData() {
      super.registerData();
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
   }

   public SoundEvent getRaidLossSound() {
      return SoundEvents.ENTITY_EVOKER_CELEBRATE;
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
   }

   protected void updateAITasks() {
      super.updateAITasks();
   }

   /**
    * Returns whether this Entity is on the same team as the given Entity.
    */
   public boolean isOnSameTeam(Entity entityIn) {
      if (entityIn == null) {
         return false;
      } else if (entityIn == this) {
         return true;
      } else if (super.isOnSameTeam(entityIn)) {
         return true;
      } else if (entityIn instanceof VexEntity) {
         return this.isOnSameTeam(((VexEntity)entityIn).getOwner());
      } else if (entityIn instanceof LivingEntity && ((LivingEntity)entityIn).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && entityIn.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_EVOKER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_EVOKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_EVOKER_HURT;
   }

   private void setWololoTarget(@Nullable SheepEntity wololoTargetIn) {
      this.wololoTarget = wololoTargetIn;
   }

   @Nullable
   private SheepEntity getWololoTarget() {
      return this.wololoTarget;
   }

   protected SoundEvent getSpellSound() {
      return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
   }

   public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
   }

   class AttackSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private AttackSpellGoal() {
      }

      protected int getCastingTime() {
         return 40;
      }

      protected int getCastingInterval() {
         return 100;
      }

      protected void castSpell() {
         LivingEntity livingentity = EvokerEntity.this.getAttackTarget();
         double d0 = Math.min(livingentity.getPosY(), EvokerEntity.this.getPosY());
         double d1 = Math.max(livingentity.getPosY(), EvokerEntity.this.getPosY()) + 1.0D;
         float f = (float)MathHelper.atan2(livingentity.getPosZ() - EvokerEntity.this.getPosZ(), livingentity.getPosX() - EvokerEntity.this.getPosX());
         if (EvokerEntity.this.getDistanceSq(livingentity) < 9.0D) {
            for(int i = 0; i < 5; ++i) {
               float f1 = f + (float)i * (float)Math.PI * 0.4F;
               this.spawnFangs(EvokerEntity.this.getPosX() + (double)MathHelper.cos(f1) * 1.5D, EvokerEntity.this.getPosZ() + (double)MathHelper.sin(f1) * 1.5D, d0, d1, f1, 0);
            }

            for(int k = 0; k < 8; ++k) {
               float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
               this.spawnFangs(EvokerEntity.this.getPosX() + (double)MathHelper.cos(f2) * 2.5D, EvokerEntity.this.getPosZ() + (double)MathHelper.sin(f2) * 2.5D, d0, d1, f2, 3);
            }
         } else {
            for(int l = 0; l < 16; ++l) {
               double d2 = 1.25D * (double)(l + 1);
               int j = 1 * l;
               this.spawnFangs(EvokerEntity.this.getPosX() + (double)MathHelper.cos(f) * d2, EvokerEntity.this.getPosZ() + (double)MathHelper.sin(f) * d2, d0, d1, f, j);
            }
         }

      }

      private void spawnFangs(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
         BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
         boolean flag = false;
         double d0 = 0.0D;

         while(true) {
            BlockPos blockpos1 = blockpos.down();
            BlockState blockstate = EvokerEntity.this.world.getBlockState(blockpos1);
            if (blockstate.isSolidSide(EvokerEntity.this.world, blockpos1, Direction.UP)) {
               if (!EvokerEntity.this.world.isAirBlock(blockpos)) {
                  BlockState blockstate1 = EvokerEntity.this.world.getBlockState(blockpos);
                  VoxelShape voxelshape = blockstate1.getCollisionShape(EvokerEntity.this.world, blockpos);
                  if (!voxelshape.isEmpty()) {
                     d0 = voxelshape.getEnd(Direction.Axis.Y);
                  }
               }

               flag = true;
               break;
            }

            blockpos = blockpos.down();
            if (blockpos.getY() < MathHelper.floor(p_190876_5_) - 1) {
               break;
            }
         }

         if (flag) {
            EvokerEntity.this.world.addEntity(new EvokerFangsEntity(EvokerEntity.this.world, p_190876_1_, (double)blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, EvokerEntity.this));
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
      }

      protected SpellcastingIllagerEntity.SpellType getSpellType() {
         return SpellcastingIllagerEntity.SpellType.FANGS;
      }
   }

   class CastingSpellGoal extends SpellcastingIllagerEntity.CastingASpellGoal {
      private CastingSpellGoal() {
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (EvokerEntity.this.getAttackTarget() != null) {
            EvokerEntity.this.getLookController().setLookPositionWithEntity(EvokerEntity.this.getAttackTarget(), (float)EvokerEntity.this.getHorizontalFaceSpeed(), (float)EvokerEntity.this.getVerticalFaceSpeed());
         } else if (EvokerEntity.this.getWololoTarget() != null) {
            EvokerEntity.this.getLookController().setLookPositionWithEntity(EvokerEntity.this.getWololoTarget(), (float)EvokerEntity.this.getHorizontalFaceSpeed(), (float)EvokerEntity.this.getVerticalFaceSpeed());
         }

      }
   }

   class SummonSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private final EntityPredicate field_220843_e = (new EntityPredicate()).setDistance(16.0D).setLineOfSiteRequired().setUseInvisibilityCheck().allowInvulnerable().allowFriendlyFire();

      private SummonSpellGoal() {
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (!super.shouldExecute()) {
            return false;
         } else {
            int i = EvokerEntity.this.world.getTargettableEntitiesWithinAABB(VexEntity.class, this.field_220843_e, EvokerEntity.this, EvokerEntity.this.getBoundingBox().grow(16.0D)).size();
            return EvokerEntity.this.rand.nextInt(8) + 1 > i;
         }
      }

      protected int getCastingTime() {
         return 100;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void castSpell() {
         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos = (new BlockPos(EvokerEntity.this)).add(-2 + EvokerEntity.this.rand.nextInt(5), 1, -2 + EvokerEntity.this.rand.nextInt(5));
            VexEntity vexentity = EntityType.VEX.create(EvokerEntity.this.world);
            vexentity.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);
            vexentity.onInitialSpawn(EvokerEntity.this.world, EvokerEntity.this.world.getDifficultyForLocation(blockpos), SpawnReason.MOB_SUMMONED, (ILivingEntityData)null, (CompoundNBT)null);
            vexentity.setOwner(EvokerEntity.this);
            vexentity.setBoundOrigin(blockpos);
            vexentity.setLimitedLife(20 * (30 + EvokerEntity.this.rand.nextInt(90)));
            EvokerEntity.this.world.addEntity(vexentity);
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
      }

      protected SpellcastingIllagerEntity.SpellType getSpellType() {
         return SpellcastingIllagerEntity.SpellType.SUMMON_VEX;
      }
   }

   public class WololoSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private final EntityPredicate field_220845_e = (new EntityPredicate()).setDistance(16.0D).allowInvulnerable().setCustomPredicate((p_220844_0_) -> {
         return ((SheepEntity)p_220844_0_).getFleeceColor() == DyeColor.BLUE;
      });

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (EvokerEntity.this.getAttackTarget() != null) {
            return false;
         } else if (EvokerEntity.this.isSpellcasting()) {
            return false;
         } else if (EvokerEntity.this.ticksExisted < this.spellCooldown) {
            return false;
         } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(EvokerEntity.this.world, EvokerEntity.this)) {
            return false;
         } else {
            List<SheepEntity> list = EvokerEntity.this.world.getTargettableEntitiesWithinAABB(SheepEntity.class, this.field_220845_e, EvokerEntity.this, EvokerEntity.this.getBoundingBox().grow(16.0D, 4.0D, 16.0D));
            if (list.isEmpty()) {
               return false;
            } else {
               EvokerEntity.this.setWololoTarget(list.get(EvokerEntity.this.rand.nextInt(list.size())));
               return true;
            }
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return EvokerEntity.this.getWololoTarget() != null && this.spellWarmup > 0;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         super.resetTask();
         EvokerEntity.this.setWololoTarget((SheepEntity)null);
      }

      protected void castSpell() {
         SheepEntity sheepentity = EvokerEntity.this.getWololoTarget();
         if (sheepentity != null && sheepentity.isAlive()) {
            sheepentity.setFleeceColor(DyeColor.RED);
         }

      }

      protected int getCastWarmupTime() {
         return 40;
      }

      protected int getCastingTime() {
         return 60;
      }

      protected int getCastingInterval() {
         return 140;
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
      }

      protected SpellcastingIllagerEntity.SpellType getSpellType() {
         return SpellcastingIllagerEntity.SpellType.WOLOLO;
      }
   }
}