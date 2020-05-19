package net.minecraft.entity.monster;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetExpiringGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.ToggleableNearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WitchEntity extends AbstractRaiderEntity implements IRangedAttackMob {
   private static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
   private static final AttributeModifier MODIFIER = (new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION)).setSaved(false);
   private static final DataParameter<Boolean> IS_DRINKING = EntityDataManager.createKey(WitchEntity.class, DataSerializers.BOOLEAN);
   private int potionUseTimer;
   private NearestAttackableTargetExpiringGoal<AbstractRaiderEntity> field_213694_bC;
   private ToggleableNearestAttackableTargetGoal<PlayerEntity> field_213695_bD;

   public WitchEntity(EntityType<? extends WitchEntity> typeIn, World worldIn) {
      super(typeIn, worldIn);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.field_213694_bC = new NearestAttackableTargetExpiringGoal<>(this, AbstractRaiderEntity.class, true, (p_213693_1_) -> {
         return p_213693_1_ != null && this.isRaidActive() && p_213693_1_.getType() != EntityType.WITCH;
      });
      this.field_213695_bD = new ToggleableNearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, (Predicate<LivingEntity>)null);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 60, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, AbstractRaiderEntity.class));
      this.targetSelector.addGoal(2, this.field_213694_bC);
      this.targetSelector.addGoal(3, this.field_213695_bD);
   }

   protected void registerData() {
      super.registerData();
      this.getDataManager().register(IS_DRINKING, false);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_WITCH_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_WITCH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WITCH_DEATH;
   }

   /**
    * Set whether this witch is aggressive at an entity.
    */
   public void setDrinkingPotion(boolean drinkingPotion) {
      this.getDataManager().set(IS_DRINKING, drinkingPotion);
   }

   public boolean isDrinkingPotion() {
      return this.getDataManager().get(IS_DRINKING);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      if (!this.world.isRemote && this.isAlive()) {
         this.field_213694_bC.func_220780_j();
         if (this.field_213694_bC.func_220781_h() <= 0) {
            this.field_213695_bD.func_220783_a(true);
         } else {
            this.field_213695_bD.func_220783_a(false);
         }

         if (this.isDrinkingPotion()) {
            if (this.potionUseTimer-- <= 0) {
               this.setDrinkingPotion(false);
               ItemStack itemstack = this.getHeldItemMainhand();
               this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
               if (itemstack.getItem() == Items.POTION) {
                  List<EffectInstance> list = PotionUtils.getEffectsFromStack(itemstack);
                  if (list != null) {
                     for(EffectInstance effectinstance : list) {
                        this.addPotionEffect(new EffectInstance(effectinstance));
                     }
                  }
               }

               this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MODIFIER);
            }
         } else {
            Potion potion = null;
            if (this.rand.nextFloat() < 0.15F && this.areEyesInFluid(FluidTags.WATER) && !this.isPotionActive(Effects.WATER_BREATHING)) {
               potion = Potions.WATER_BREATHING;
            } else if (this.rand.nextFloat() < 0.15F && (this.isBurning() || this.getLastDamageSource() != null && this.getLastDamageSource().isFireDamage()) && !this.isPotionActive(Effects.FIRE_RESISTANCE)) {
               potion = Potions.FIRE_RESISTANCE;
            } else if (this.rand.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
               potion = Potions.HEALING;
            } else if (this.rand.nextFloat() < 0.5F && this.getAttackTarget() != null && !this.isPotionActive(Effects.SPEED) && this.getAttackTarget().getDistanceSq(this) > 121.0D) {
               potion = Potions.SWIFTNESS;
            }

            if (potion != null) {
               this.setItemStackToSlot(EquipmentSlotType.MAINHAND, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), potion));
               this.potionUseTimer = this.getHeldItemMainhand().getUseDuration();
               this.setDrinkingPotion(true);
               this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
               IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
               iattributeinstance.removeModifier(MODIFIER);
               iattributeinstance.applyModifier(MODIFIER);
            }
         }

         if (this.rand.nextFloat() < 7.5E-4F) {
            this.world.setEntityState(this, (byte)15);
         }
      }

      super.livingTick();
   }

   public SoundEvent getRaidLossSound() {
      return SoundEvents.ENTITY_WITCH_CELEBRATE;
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 15) {
         for(int i = 0; i < this.rand.nextInt(35) + 10; ++i) {
            this.world.addParticle(ParticleTypes.WITCH, this.getPosX() + this.rand.nextGaussian() * (double)0.13F, this.getBoundingBox().maxY + 0.5D + this.rand.nextGaussian() * (double)0.13F, this.getPosZ() + this.rand.nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
         }
      } else {
         super.handleStatusUpdate(id);
      }

   }

   /**
    * Reduces damage, depending on potions
    */
   protected float applyPotionDamageCalculations(DamageSource source, float damage) {
      damage = super.applyPotionDamageCalculations(source, damage);
      if (source.getTrueSource() == this) {
         damage = 0.0F;
      }

      if (source.isMagicDamage()) {
         damage = (float)((double)damage * 0.15D);
      }

      return damage;
   }

   /**
    * Attack the specified entity using a ranged attack.
    */
   public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
      if (!this.isDrinkingPotion()) {
         Vec3d vec3d = target.getMotion();
         double d0 = target.getPosX() + vec3d.x - this.getPosX();
         double d1 = target.getPosYEye() - (double)1.1F - this.getPosY();
         double d2 = target.getPosZ() + vec3d.z - this.getPosZ();
         float f = MathHelper.sqrt(d0 * d0 + d2 * d2);
         Potion potion = Potions.HARMING;
         if (target instanceof AbstractRaiderEntity) {
            if (target.getHealth() <= 4.0F) {
               potion = Potions.HEALING;
            } else {
               potion = Potions.REGENERATION;
            }

            this.setAttackTarget((LivingEntity)null);
         } else if (f >= 8.0F && !target.isPotionActive(Effects.SLOWNESS)) {
            potion = Potions.SLOWNESS;
         } else if (target.getHealth() >= 8.0F && !target.isPotionActive(Effects.POISON)) {
            potion = Potions.POISON;
         } else if (f <= 3.0F && !target.isPotionActive(Effects.WEAKNESS) && this.rand.nextFloat() < 0.25F) {
            potion = Potions.WEAKNESS;
         }

         PotionEntity potionentity = new PotionEntity(this.world, this);
         potionentity.setItem(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potion));
         potionentity.rotationPitch -= -20.0F;
         potionentity.shoot(d0, d1 + (double)(f * 0.2F), d2, 0.75F, 8.0F);
         this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
         this.world.addEntity(potionentity);
      }
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 1.62F;
   }

   public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
   }

   public boolean canBeLeader() {
      return false;
   }
}