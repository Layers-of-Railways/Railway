package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IllusionerEntity extends SpellcastingIllagerEntity implements IRangedAttackMob {
   private int ghostTime;
   private final Vec3d[][] renderLocations;

   public IllusionerEntity(EntityType<? extends IllusionerEntity> type, World worldIn) {
      super(type, worldIn);
      this.experienceValue = 5;
      this.renderLocations = new Vec3d[2][4];

      for(int i = 0; i < 4; ++i) {
         this.renderLocations[0][i] = Vec3d.ZERO;
         this.renderLocations[1][i] = Vec3d.ZERO;
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new SpellcastingIllagerEntity.CastingASpellGoal());
      this.goalSelector.addGoal(4, new IllusionerEntity.MirrorSpellGoal());
      this.goalSelector.addGoal(5, new IllusionerEntity.BlindnessSpellGoal());
      this.goalSelector.addGoal(6, new RangedBowAttackGoal<>(this, 0.5D, 20, 15.0F));
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false)).setUnseenMemoryTicks(300));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(18.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(32.0D);
   }

   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   protected void registerData() {
      super.registerData();
   }

   /**
    * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained by
    * a minecart, such as a command block).
    */
   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getBoundingBox().grow(3.0D, 0.0D, 3.0D);
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      if (this.world.isRemote && this.isInvisible()) {
         --this.ghostTime;
         if (this.ghostTime < 0) {
            this.ghostTime = 0;
         }

         if (this.hurtTime != 1 && this.ticksExisted % 1200 != 0) {
            if (this.hurtTime == this.maxHurtTime - 1) {
               this.ghostTime = 3;

               for(int k = 0; k < 4; ++k) {
                  this.renderLocations[0][k] = this.renderLocations[1][k];
                  this.renderLocations[1][k] = new Vec3d(0.0D, 0.0D, 0.0D);
               }
            }
         } else {
            this.ghostTime = 3;
            float f = -6.0F;
            int i = 13;

            for(int j = 0; j < 4; ++j) {
               this.renderLocations[0][j] = this.renderLocations[1][j];
               this.renderLocations[1][j] = new Vec3d((double)(-6.0F + (float)this.rand.nextInt(13)) * 0.5D, (double)Math.max(0, this.rand.nextInt(6) - 4), (double)(-6.0F + (float)this.rand.nextInt(13)) * 0.5D);
            }

            for(int l = 0; l < 16; ++l) {
               this.world.addParticle(ParticleTypes.CLOUD, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZWidth(0.5D), 0.0D, 0.0D, 0.0D);
            }

            this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, this.getSoundCategory(), 1.0F, 1.0F, false);
         }
      }

   }

   public SoundEvent getRaidLossSound() {
      return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d[] getRenderLocations(float p_193098_1_) {
      if (this.ghostTime <= 0) {
         return this.renderLocations[1];
      } else {
         double d0 = (double)(((float)this.ghostTime - p_193098_1_) / 3.0F);
         d0 = Math.pow(d0, 0.25D);
         Vec3d[] avec3d = new Vec3d[4];

         for(int i = 0; i < 4; ++i) {
            avec3d[i] = this.renderLocations[1][i].scale(1.0D - d0).add(this.renderLocations[0][i].scale(d0));
         }

         return avec3d;
      }
   }

   /**
    * Returns whether this Entity is on the same team as the given Entity.
    */
   public boolean isOnSameTeam(Entity entityIn) {
      if (super.isOnSameTeam(entityIn)) {
         return true;
      } else if (entityIn instanceof LivingEntity && ((LivingEntity)entityIn).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && entityIn.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ILLUSIONER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ILLUSIONER_HURT;
   }

   protected SoundEvent getSpellSound() {
      return SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL;
   }

   public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
   }

   /**
    * Attack the specified entity using a ranged attack.
    */
   public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
      ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
      AbstractArrowEntity abstractarrowentity = ProjectileHelper.fireArrow(this, itemstack, distanceFactor);
      if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
         abstractarrowentity = ((net.minecraft.item.BowItem)this.getHeldItemMainhand().getItem()).customeArrow(abstractarrowentity);
      double d0 = target.getPosX() - this.getPosX();
      double d1 = target.getPosYHeight(0.3333333333333333D) - abstractarrowentity.getPosY();
      double d2 = target.getPosZ() - this.getPosZ();
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.world.addEntity(abstractarrowentity);
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllagerEntity.ArmPose getArmPose() {
      if (this.isSpellcasting()) {
         return AbstractIllagerEntity.ArmPose.SPELLCASTING;
      } else {
         return this.isAggressive() ? AbstractIllagerEntity.ArmPose.BOW_AND_ARROW : AbstractIllagerEntity.ArmPose.CROSSED;
      }
   }

   class BlindnessSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private int lastTargetId;

      private BlindnessSpellGoal() {
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (!super.shouldExecute()) {
            return false;
         } else if (IllusionerEntity.this.getAttackTarget() == null) {
            return false;
         } else if (IllusionerEntity.this.getAttackTarget().getEntityId() == this.lastTargetId) {
            return false;
         } else {
            return IllusionerEntity.this.world.getDifficultyForLocation(new BlockPos(IllusionerEntity.this)).isHarderThan((float)Difficulty.NORMAL.ordinal());
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         super.startExecuting();
         this.lastTargetId = IllusionerEntity.this.getAttackTarget().getEntityId();
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 180;
      }

      protected void castSpell() {
         IllusionerEntity.this.getAttackTarget().addPotionEffect(new EffectInstance(Effects.BLINDNESS, 400));
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS;
      }

      protected SpellcastingIllagerEntity.SpellType getSpellType() {
         return SpellcastingIllagerEntity.SpellType.BLINDNESS;
      }
   }

   class MirrorSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private MirrorSpellGoal() {
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (!super.shouldExecute()) {
            return false;
         } else {
            return !IllusionerEntity.this.isPotionActive(Effects.INVISIBILITY);
         }
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void castSpell() {
         IllusionerEntity.this.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 1200));
      }

      @Nullable
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR;
      }

      protected SpellcastingIllagerEntity.SpellType getSpellType() {
         return SpellcastingIllagerEntity.SpellType.DISAPPEAR;
      }
   }
}