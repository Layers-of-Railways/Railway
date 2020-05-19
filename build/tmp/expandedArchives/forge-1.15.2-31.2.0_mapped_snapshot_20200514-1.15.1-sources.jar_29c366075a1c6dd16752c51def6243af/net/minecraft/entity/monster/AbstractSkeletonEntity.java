package net.minecraft.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractSkeletonEntity extends MonsterEntity implements IRangedAttackMob {
   private final RangedBowAttackGoal<AbstractSkeletonEntity> aiArrowAttack = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
   private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.2D, false) {
      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         super.resetTask();
         AbstractSkeletonEntity.this.setAggroed(false);
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         super.startExecuting();
         AbstractSkeletonEntity.this.setAggroed(true);
      }
   };

   protected AbstractSkeletonEntity(EntityType<? extends AbstractSkeletonEntity> type, World worldIn) {
      super(type, worldIn);
      this.setCombatTask();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(2, new RestrictSunGoal(this));
      this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, WolfEntity.class, 6.0F, 1.0D, 1.2D));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.TARGET_DRY_BABY));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   protected abstract SoundEvent getStepSound();

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      boolean flag = this.isInDaylight();
      if (flag) {
         ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
         if (!itemstack.isEmpty()) {
            if (itemstack.isDamageable()) {
               itemstack.setDamage(itemstack.getDamage() + this.rand.nextInt(2));
               if (itemstack.getDamage() >= itemstack.getMaxDamage()) {
                  this.sendBreakAnimation(EquipmentSlotType.HEAD);
                  this.setItemStackToSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
               }
            }

            flag = false;
         }

         if (flag) {
            this.setFire(8);
         }
      }

      super.livingTick();
   }

   /**
    * Handles updating while riding another entity
    */
   public void updateRidden() {
      super.updateRidden();
      if (this.getRidingEntity() instanceof CreatureEntity) {
         CreatureEntity creatureentity = (CreatureEntity)this.getRidingEntity();
         this.renderYawOffset = creatureentity.renderYawOffset;
      }

   }

   /**
    * Gives armor or weapon for entity based on given DifficultyInstance
    */
   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      super.setEquipmentBasedOnDifficulty(difficulty);
      this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
      this.setEquipmentBasedOnDifficulty(difficultyIn);
      this.setEnchantmentBasedOnDifficulty(difficultyIn);
      this.setCombatTask();
      this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * difficultyIn.getClampedAdditionalDifficulty());
      if (this.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) {
         LocalDate localdate = LocalDate.now();
         int i = localdate.get(ChronoField.DAY_OF_MONTH);
         int j = localdate.get(ChronoField.MONTH_OF_YEAR);
         if (j == 10 && i == 31 && this.rand.nextFloat() < 0.25F) {
            this.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.inventoryArmorDropChances[EquipmentSlotType.HEAD.getIndex()] = 0.0F;
         }
      }

      return spawnDataIn;
   }

   /**
    * sets this entity's combat AI.
    */
   public void setCombatTask() {
      if (this.world != null && !this.world.isRemote) {
         this.goalSelector.removeGoal(this.aiAttackOnCollide);
         this.goalSelector.removeGoal(this.aiArrowAttack);
         ItemStack itemstack = this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW));
         if (itemstack.getItem() instanceof net.minecraft.item.BowItem) {
            int i = 20;
            if (this.world.getDifficulty() != Difficulty.HARD) {
               i = 40;
            }

            this.aiArrowAttack.setAttackCooldown(i);
            this.goalSelector.addGoal(4, this.aiArrowAttack);
         } else {
            this.goalSelector.addGoal(4, this.aiAttackOnCollide);
         }

      }
   }

   /**
    * Attack the specified entity using a ranged attack.
    */
   public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
      ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
      AbstractArrowEntity abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
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

   /**
    * Fires an arrow
    */
   protected AbstractArrowEntity fireArrow(ItemStack arrowStack, float distanceFactor) {
      return ProjectileHelper.fireArrow(this, arrowStack, distanceFactor);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setCombatTask();
   }

   public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
      super.setItemStackToSlot(slotIn, stack);
      if (!this.world.isRemote) {
         this.setCombatTask();
      }

   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 1.74F;
   }

   /**
    * Returns the Y Offset of this entity.
    */
   public double getYOffset() {
      return -0.6D;
   }
}