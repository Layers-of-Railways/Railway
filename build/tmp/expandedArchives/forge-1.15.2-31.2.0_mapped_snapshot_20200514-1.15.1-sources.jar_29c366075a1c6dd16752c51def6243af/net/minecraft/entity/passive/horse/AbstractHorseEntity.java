package net.minecraft.entity.passive.horse;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractHorseEntity extends AnimalEntity implements IInventoryChangedListener, IJumpingMount {
   private static final Predicate<LivingEntity> IS_HORSE_BREEDING = (p_213617_0_) -> {
      return p_213617_0_ instanceof AbstractHorseEntity && ((AbstractHorseEntity)p_213617_0_).isBreeding();
   };
   private static final EntityPredicate MOMMY_TARGETING = (new EntityPredicate()).setDistance(16.0D).allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired().setCustomPredicate(IS_HORSE_BREEDING);
   protected static final IAttribute JUMP_STRENGTH = (new RangedAttribute((IAttribute)null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).setDescription("Jump Strength").setShouldWatch(true);
   private static final DataParameter<Byte> STATUS = EntityDataManager.createKey(AbstractHorseEntity.class, DataSerializers.BYTE);
   private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(AbstractHorseEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   private int eatingCounter;
   private int openMouthCounter;
   private int jumpRearingCounter;
   public int tailCounter;
   public int sprintCounter;
   protected boolean horseJumping;
   protected Inventory horseChest;
   /** The higher this value, the more likely the horse is to be tamed next time a player rides it. */
   protected int temper;
   protected float jumpPower;
   private boolean allowStandSliding;
   private float headLean;
   private float prevHeadLean;
   private float rearingAmount;
   private float prevRearingAmount;
   private float mouthOpenness;
   private float prevMouthOpenness;
   protected boolean canGallop = true;
   /** Used to determine the sound that the horse should make when it steps */
   protected int gallopTime;

   protected AbstractHorseEntity(EntityType<? extends AbstractHorseEntity> type, World worldIn) {
      super(type, worldIn);
      this.stepHeight = 1.0F;
      this.initHorseChest();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorseEntity.class));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.7D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.initExtraAI();
   }

   protected void initExtraAI() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(STATUS, (byte)0);
      this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
   }

   protected boolean getHorseWatchableBoolean(int p_110233_1_) {
      return (this.dataManager.get(STATUS) & p_110233_1_) != 0;
   }

   protected void setHorseWatchableBoolean(int p_110208_1_, boolean p_110208_2_) {
      byte b0 = this.dataManager.get(STATUS);
      if (p_110208_2_) {
         this.dataManager.set(STATUS, (byte)(b0 | p_110208_1_));
      } else {
         this.dataManager.set(STATUS, (byte)(b0 & ~p_110208_1_));
      }

   }

   public boolean isTame() {
      return this.getHorseWatchableBoolean(2);
   }

   @Nullable
   public UUID getOwnerUniqueId() {
      return this.dataManager.get(OWNER_UNIQUE_ID).orElse((UUID)null);
   }

   public void setOwnerUniqueId(@Nullable UUID uniqueId) {
      this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(uniqueId));
   }

   public boolean isHorseJumping() {
      return this.horseJumping;
   }

   public void setHorseTamed(boolean tamed) {
      this.setHorseWatchableBoolean(2, tamed);
   }

   public void setHorseJumping(boolean jumping) {
      this.horseJumping = jumping;
   }

   public boolean canBeLeashedTo(PlayerEntity player) {
      return super.canBeLeashedTo(player) && this.getCreatureAttribute() != CreatureAttribute.UNDEAD;
   }

   protected void onLeashDistance(float p_142017_1_) {
      if (p_142017_1_ > 6.0F && this.isEatingHaystack()) {
         this.setEatingHaystack(false);
      }

   }

   public boolean isEatingHaystack() {
      return this.getHorseWatchableBoolean(16);
   }

   public boolean isRearing() {
      return this.getHorseWatchableBoolean(32);
   }

   public boolean isBreeding() {
      return this.getHorseWatchableBoolean(8);
   }

   public void setBreeding(boolean breeding) {
      this.setHorseWatchableBoolean(8, breeding);
   }

   public void setHorseSaddled(boolean saddled) {
      this.setHorseWatchableBoolean(4, saddled);
   }

   public int getTemper() {
      return this.temper;
   }

   public void setTemper(int temperIn) {
      this.temper = temperIn;
   }

   public int increaseTemper(int p_110198_1_) {
      int i = MathHelper.clamp(this.getTemper() + p_110198_1_, 0, this.getMaxTemper());
      this.setTemper(i);
      return i;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      Entity entity = source.getTrueSource();
      return this.isBeingRidden() && entity != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(source, amount);
   }

   /**
    * Returns true if this entity should push and be pushed by other entities when colliding.
    */
   public boolean canBePushed() {
      return !this.isBeingRidden();
   }

   private void eatingHorse() {
      this.openHorseMouth();
      if (!this.isSilent()) {
         this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_HORSE_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      }

   }

   public boolean onLivingFall(float distance, float damageMultiplier) {
      if (distance > 1.0F) {
         this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4F, 1.0F);
      }

      int i = this.calculateFallDamage(distance, damageMultiplier);
      if (i <= 0) {
         return false;
      } else {
         this.attackEntityFrom(DamageSource.FALL, (float)i);
         if (this.isBeingRidden()) {
            for(Entity entity : this.getRecursivePassengers()) {
               entity.attackEntityFrom(DamageSource.FALL, (float)i);
            }
         }

         this.playFallSound();
         return true;
      }
   }

   protected int calculateFallDamage(float p_225508_1_, float p_225508_2_) {
      return MathHelper.ceil((p_225508_1_ * 0.5F - 3.0F) * p_225508_2_);
   }

   protected int getInventorySize() {
      return 2;
   }

   protected void initHorseChest() {
      Inventory inventory = this.horseChest;
      this.horseChest = new Inventory(this.getInventorySize());
      if (inventory != null) {
         inventory.removeListener(this);
         int i = Math.min(inventory.getSizeInventory(), this.horseChest.getSizeInventory());

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = inventory.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               this.horseChest.setInventorySlotContents(j, itemstack.copy());
            }
         }
      }

      this.horseChest.addListener(this);
      this.updateHorseSlots();
      this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.horseChest));
   }

   /**
    * Updates the items in the saddle and armor slots of the horse's inventory.
    */
   protected void updateHorseSlots() {
      if (!this.world.isRemote) {
         this.setHorseSaddled(!this.horseChest.getStackInSlot(0).isEmpty() && this.canBeSaddled());
      }
   }

   /**
    * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
    */
   public void onInventoryChanged(IInventory invBasic) {
      boolean flag = this.isHorseSaddled();
      this.updateHorseSlots();
      if (this.ticksExisted > 20 && !flag && this.isHorseSaddled()) {
         this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
      }

   }

   public double getHorseJumpStrength() {
      return this.getAttribute(JUMP_STRENGTH).getValue();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      if (this.rand.nextInt(3) == 0) {
         this.makeHorseRear();
      }

      return null;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked()) {
         this.makeHorseRear();
      }

      return null;
   }

   public boolean canBeSaddled() {
      return true;
   }

   public boolean isHorseSaddled() {
      return this.getHorseWatchableBoolean(4);
   }

   @Nullable
   protected SoundEvent getAngrySound() {
      this.makeHorseRear();
      return null;
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      if (!blockIn.getMaterial().isLiquid()) {
         BlockState blockstate = this.world.getBlockState(pos.up());
         SoundType soundtype = blockIn.getSoundType(world, pos, this);
         if (blockstate.getBlock() == Blocks.SNOW) {
            soundtype = blockstate.getSoundType(world, pos, this);
         }

         if (this.isBeingRidden() && this.canGallop) {
            ++this.gallopTime;
            if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
               this.playGallopSound(soundtype);
            } else if (this.gallopTime <= 5) {
               this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
         } else if (soundtype == SoundType.WOOD) {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
         } else {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
         }

      }
   }

   protected void playGallopSound(SoundType p_190680_1_) {
      this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, p_190680_1_.getVolume() * 0.15F, p_190680_1_.getPitch());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(JUMP_STRENGTH);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(53.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.225F);
   }

   /**
    * Will return how many at most can spawn in a chunk at once.
    */
   public int getMaxSpawnedInChunk() {
      return 6;
   }

   public int getMaxTemper() {
      return 100;
   }

   /**
    * Returns the volume for the sounds this mob makes.
    */
   protected float getSoundVolume() {
      return 0.8F;
   }

   /**
    * Get number of ticks, at least during which the living entity will be silent.
    */
   public int getTalkInterval() {
      return 400;
   }

   public void openGUI(PlayerEntity playerEntity) {
      if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(playerEntity)) && this.isTame()) {
         playerEntity.openHorseInventory(this, this.horseChest);
      }

   }

   protected boolean handleEating(PlayerEntity player, ItemStack stack) {
      boolean flag = false;
      float f = 0.0F;
      int i = 0;
      int j = 0;
      Item item = stack.getItem();
      if (item == Items.WHEAT) {
         f = 2.0F;
         i = 20;
         j = 3;
      } else if (item == Items.SUGAR) {
         f = 1.0F;
         i = 30;
         j = 3;
      } else if (item == Blocks.HAY_BLOCK.asItem()) {
         f = 20.0F;
         i = 180;
      } else if (item == Items.APPLE) {
         f = 3.0F;
         i = 60;
         j = 3;
      } else if (item == Items.GOLDEN_CARROT) {
         f = 4.0F;
         i = 60;
         j = 5;
         if (this.isTame() && this.getGrowingAge() == 0 && !this.isInLove()) {
            flag = true;
            this.setInLove(player);
         }
      } else if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
         f = 10.0F;
         i = 240;
         j = 10;
         if (this.isTame() && this.getGrowingAge() == 0 && !this.isInLove()) {
            flag = true;
            this.setInLove(player);
         }
      }

      if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
         this.heal(f);
         flag = true;
      }

      if (this.isChild() && i > 0) {
         this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), 0.0D, 0.0D, 0.0D);
         if (!this.world.isRemote) {
            this.addGrowth(i);
         }

         flag = true;
      }

      if (j > 0 && (flag || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
         flag = true;
         if (!this.world.isRemote) {
            this.increaseTemper(j);
         }
      }

      if (flag) {
         this.eatingHorse();
      }

      return flag;
   }

   protected void mountTo(PlayerEntity player) {
      this.setEatingHaystack(false);
      this.setRearing(false);
      if (!this.world.isRemote) {
         player.rotationYaw = this.rotationYaw;
         player.rotationPitch = this.rotationPitch;
         player.startRiding(this);
      }

   }

   /**
    * Dead and sleeping entities cannot move
    */
   protected boolean isMovementBlocked() {
      return super.isMovementBlocked() && this.isBeingRidden() && this.isHorseSaddled() || this.isEatingHaystack() || this.isRearing();
   }

   /**
    * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
    * the animal type)
    */
   public boolean isBreedingItem(ItemStack stack) {
      return false;
   }

   private void moveTail() {
      this.tailCounter = 1;
   }

   protected void dropInventory() {
      super.dropInventory();
      if (this.horseChest != null) {
         for(int i = 0; i < this.horseChest.getSizeInventory(); ++i) {
            ItemStack itemstack = this.horseChest.getStackInSlot(i);
            if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
               this.entityDropItem(itemstack);
            }
         }

      }
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      if (this.rand.nextInt(200) == 0) {
         this.moveTail();
      }

      super.livingTick();
      if (!this.world.isRemote && this.isAlive()) {
         if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }

         if (this.canEatGrass()) {
            if (!this.isEatingHaystack() && !this.isBeingRidden() && this.rand.nextInt(300) == 0 && this.world.getBlockState((new BlockPos(this)).down()).getBlock() == Blocks.GRASS_BLOCK) {
               this.setEatingHaystack(true);
            }

            if (this.isEatingHaystack() && ++this.eatingCounter > 50) {
               this.eatingCounter = 0;
               this.setEatingHaystack(false);
            }
         }

         this.followMother();
      }
   }

   protected void followMother() {
      if (this.isBreeding() && this.isChild() && !this.isEatingHaystack()) {
         LivingEntity livingentity = this.world.getClosestEntityWithinAABB(AbstractHorseEntity.class, MOMMY_TARGETING, this, this.getPosX(), this.getPosY(), this.getPosZ(), this.getBoundingBox().grow(16.0D));
         if (livingentity != null && this.getDistanceSq(livingentity) > 4.0D) {
            this.navigator.getPathToEntity(livingentity, 0);
         }
      }

   }

   public boolean canEatGrass() {
      return true;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30) {
         this.openMouthCounter = 0;
         this.setHorseWatchableBoolean(64, false);
      }

      if ((this.canPassengerSteer() || this.isServerWorld()) && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20) {
         this.jumpRearingCounter = 0;
         this.setRearing(false);
      }

      if (this.tailCounter > 0 && ++this.tailCounter > 8) {
         this.tailCounter = 0;
      }

      if (this.sprintCounter > 0) {
         ++this.sprintCounter;
         if (this.sprintCounter > 300) {
            this.sprintCounter = 0;
         }
      }

      this.prevHeadLean = this.headLean;
      if (this.isEatingHaystack()) {
         this.headLean += (1.0F - this.headLean) * 0.4F + 0.05F;
         if (this.headLean > 1.0F) {
            this.headLean = 1.0F;
         }
      } else {
         this.headLean += (0.0F - this.headLean) * 0.4F - 0.05F;
         if (this.headLean < 0.0F) {
            this.headLean = 0.0F;
         }
      }

      this.prevRearingAmount = this.rearingAmount;
      if (this.isRearing()) {
         this.headLean = 0.0F;
         this.prevHeadLean = this.headLean;
         this.rearingAmount += (1.0F - this.rearingAmount) * 0.4F + 0.05F;
         if (this.rearingAmount > 1.0F) {
            this.rearingAmount = 1.0F;
         }
      } else {
         this.allowStandSliding = false;
         this.rearingAmount += (0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F - 0.05F;
         if (this.rearingAmount < 0.0F) {
            this.rearingAmount = 0.0F;
         }
      }

      this.prevMouthOpenness = this.mouthOpenness;
      if (this.getHorseWatchableBoolean(64)) {
         this.mouthOpenness += (1.0F - this.mouthOpenness) * 0.7F + 0.05F;
         if (this.mouthOpenness > 1.0F) {
            this.mouthOpenness = 1.0F;
         }
      } else {
         this.mouthOpenness += (0.0F - this.mouthOpenness) * 0.7F - 0.05F;
         if (this.mouthOpenness < 0.0F) {
            this.mouthOpenness = 0.0F;
         }
      }

   }

   private void openHorseMouth() {
      if (!this.world.isRemote) {
         this.openMouthCounter = 1;
         this.setHorseWatchableBoolean(64, true);
      }

   }

   public void setEatingHaystack(boolean p_110227_1_) {
      this.setHorseWatchableBoolean(16, p_110227_1_);
   }

   public void setRearing(boolean rearing) {
      if (rearing) {
         this.setEatingHaystack(false);
      }

      this.setHorseWatchableBoolean(32, rearing);
   }

   private void makeHorseRear() {
      if (this.canPassengerSteer() || this.isServerWorld()) {
         this.jumpRearingCounter = 1;
         this.setRearing(true);
      }

   }

   public void makeMad() {
      this.makeHorseRear();
      SoundEvent soundevent = this.getAngrySound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   public boolean setTamedBy(PlayerEntity player) {
      this.setOwnerUniqueId(player.getUniqueID());
      this.setHorseTamed(true);
      if (player instanceof ServerPlayerEntity) {
         CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
      }

      this.world.setEntityState(this, (byte)7);
      return true;
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isAlive()) {
         if (this.isBeingRidden() && this.canBeSteered() && this.isHorseSaddled()) {
            LivingEntity livingentity = (LivingEntity)this.getControllingPassenger();
            this.rotationYaw = livingentity.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = livingentity.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.renderYawOffset;
            float f = livingentity.moveStrafing * 0.5F;
            float f1 = livingentity.moveForward;
            if (f1 <= 0.0F) {
               f1 *= 0.25F;
               this.gallopTime = 0;
            }

            if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.allowStandSliding) {
               f = 0.0F;
               f1 = 0.0F;
            }

            if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
               double d0 = this.getHorseJumpStrength() * (double)this.jumpPower * (double)this.getJumpFactor();
               double d1;
               if (this.isPotionActive(Effects.JUMP_BOOST)) {
                  d1 = d0 + (double)((float)(this.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
               } else {
                  d1 = d0;
               }

               Vec3d vec3d = this.getMotion();
               this.setMotion(vec3d.x, d1, vec3d.z);
               this.setHorseJumping(true);
               this.isAirBorne = true;
               if (f1 > 0.0F) {
                  float f2 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F));
                  float f3 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F));
                  this.setMotion(this.getMotion().add((double)(-0.4F * f2 * this.jumpPower), 0.0D, (double)(0.4F * f3 * this.jumpPower)));
                  this.playJumpSound();
               }

               this.jumpPower = 0.0F;
            }

            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
            if (this.canPassengerSteer()) {
               this.setAIMoveSpeed((float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
               super.travel(new Vec3d((double)f, p_213352_1_.y, (double)f1));
            } else if (livingentity instanceof PlayerEntity) {
               this.setMotion(Vec3d.ZERO);
            }

            if (this.onGround) {
               this.jumpPower = 0.0F;
               this.setHorseJumping(false);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d2 = this.getPosX() - this.prevPosX;
            double d3 = this.getPosZ() - this.prevPosZ;
            float f4 = MathHelper.sqrt(d2 * d2 + d3 * d3) * 4.0F;
            if (f4 > 1.0F) {
               f4 = 1.0F;
            }

            this.limbSwingAmount += (f4 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
         } else {
            this.jumpMovementFactor = 0.02F;
            super.travel(p_213352_1_);
         }
      }
   }

   protected void playJumpSound() {
      this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putBoolean("EatingHaystack", this.isEatingHaystack());
      compound.putBoolean("Bred", this.isBreeding());
      compound.putInt("Temper", this.getTemper());
      compound.putBoolean("Tame", this.isTame());
      if (this.getOwnerUniqueId() != null) {
         compound.putString("OwnerUUID", this.getOwnerUniqueId().toString());
      }

      if (!this.horseChest.getStackInSlot(0).isEmpty()) {
         compound.put("SaddleItem", this.horseChest.getStackInSlot(0).write(new CompoundNBT()));
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setEatingHaystack(compound.getBoolean("EatingHaystack"));
      this.setBreeding(compound.getBoolean("Bred"));
      this.setTemper(compound.getInt("Temper"));
      this.setHorseTamed(compound.getBoolean("Tame"));
      String s;
      if (compound.contains("OwnerUUID", 8)) {
         s = compound.getString("OwnerUUID");
      } else {
         String s1 = compound.getString("Owner");
         s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
      }

      if (!s.isEmpty()) {
         this.setOwnerUniqueId(UUID.fromString(s));
      }

      IAttributeInstance iattributeinstance = this.getAttributes().getAttributeInstanceByName("Speed");
      if (iattributeinstance != null) {
         this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(iattributeinstance.getBaseValue() * 0.25D);
      }

      if (compound.contains("SaddleItem", 10)) {
         ItemStack itemstack = ItemStack.read(compound.getCompound("SaddleItem"));
         if (itemstack.getItem() == Items.SADDLE) {
            this.horseChest.setInventorySlotContents(0, itemstack);
         }
      }

      this.updateHorseSlots();
   }

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
   public boolean canMateWith(AnimalEntity otherAnimal) {
      return false;
   }

   /**
    * Return true if the horse entity ready to mate. (no rider, not riding, tame, adult, not steril...)
    */
   protected boolean canMate() {
      return !this.isBeingRidden() && !this.isPassenger() && this.isTame() && !this.isChild() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity ageable) {
      return null;
   }

   protected void setOffspringAttributes(AgeableEntity p_190681_1_, AbstractHorseEntity p_190681_2_) {
      double d0 = this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + p_190681_1_.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + (double)this.getModifiedMaxHealth();
      p_190681_2_.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(d0 / 3.0D);
      double d1 = this.getAttribute(JUMP_STRENGTH).getBaseValue() + p_190681_1_.getAttribute(JUMP_STRENGTH).getBaseValue() + this.getModifiedJumpStrength();
      p_190681_2_.getAttribute(JUMP_STRENGTH).setBaseValue(d1 / 3.0D);
      double d2 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + p_190681_1_.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getModifiedMovementSpeed();
      p_190681_2_.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(d2 / 3.0D);
   }

   /**
    * returns true if all the conditions for steering the entity are met. For pigs, this is true if it is being ridden
    * by a player and the player is holding a carrot-on-a-stick
    */
   public boolean canBeSteered() {
      return this.getControllingPassenger() instanceof LivingEntity;
   }

   @OnlyIn(Dist.CLIENT)
   public float getGrassEatingAmount(float p_110258_1_) {
      return MathHelper.lerp(p_110258_1_, this.prevHeadLean, this.headLean);
   }

   @OnlyIn(Dist.CLIENT)
   public float getRearingAmount(float p_110223_1_) {
      return MathHelper.lerp(p_110223_1_, this.prevRearingAmount, this.rearingAmount);
   }

   @OnlyIn(Dist.CLIENT)
   public float getMouthOpennessAngle(float p_110201_1_) {
      return MathHelper.lerp(p_110201_1_, this.prevMouthOpenness, this.mouthOpenness);
   }

   @OnlyIn(Dist.CLIENT)
   public void setJumpPower(int jumpPowerIn) {
      if (this.isHorseSaddled()) {
         if (jumpPowerIn < 0) {
            jumpPowerIn = 0;
         } else {
            this.allowStandSliding = true;
            this.makeHorseRear();
         }

         if (jumpPowerIn >= 90) {
            this.jumpPower = 1.0F;
         } else {
            this.jumpPower = 0.4F + 0.4F * (float)jumpPowerIn / 90.0F;
         }

      }
   }

   public boolean canJump() {
      return this.isHorseSaddled();
   }

   public void handleStartJump(int p_184775_1_) {
      this.allowStandSliding = true;
      this.makeHorseRear();
   }

   public void handleStopJump() {
   }

   /**
    * "Spawns particles for the horse entity. par1 tells whether to spawn hearts. If it is false, it spawns smoke."
    */
   @OnlyIn(Dist.CLIENT)
   protected void spawnHorseParticles(boolean p_110216_1_) {
      IParticleData iparticledata = p_110216_1_ ? ParticleTypes.HEART : ParticleTypes.SMOKE;

      for(int i = 0; i < 7; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.addParticle(iparticledata, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
      }

   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 7) {
         this.spawnHorseParticles(true);
      } else if (id == 6) {
         this.spawnHorseParticles(false);
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public void updatePassenger(Entity passenger) {
      super.updatePassenger(passenger);
      if (passenger instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)passenger;
         this.renderYawOffset = mobentity.renderYawOffset;
      }

      if (this.prevRearingAmount > 0.0F) {
         float f3 = MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180F));
         float f = MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180F));
         float f1 = 0.7F * this.prevRearingAmount;
         float f2 = 0.15F * this.prevRearingAmount;
         passenger.setPosition(this.getPosX() + (double)(f1 * f3), this.getPosY() + this.getMountedYOffset() + passenger.getYOffset() + (double)f2, this.getPosZ() - (double)(f1 * f));
         if (passenger instanceof LivingEntity) {
            ((LivingEntity)passenger).renderYawOffset = this.renderYawOffset;
         }
      }

   }

   /**
    * Returns randomized max health
    */
   protected float getModifiedMaxHealth() {
      return 15.0F + (float)this.rand.nextInt(8) + (float)this.rand.nextInt(9);
   }

   /**
    * Returns randomized jump strength
    */
   protected double getModifiedJumpStrength() {
      return (double)0.4F + this.rand.nextDouble() * 0.2D + this.rand.nextDouble() * 0.2D + this.rand.nextDouble() * 0.2D;
   }

   /**
    * Returns randomized movement speed
    */
   protected double getModifiedMovementSpeed() {
      return ((double)0.45F + this.rand.nextDouble() * 0.3D + this.rand.nextDouble() * 0.3D + this.rand.nextDouble() * 0.3D) * 0.25D;
   }

   /**
    * Returns true if this entity should move as if it were on a ladder (either because it's actually on a ladder, or
    * for AI reasons)
    */
   public boolean isOnLadder() {
      return false;
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return sizeIn.height * 0.95F;
   }

   public boolean wearsArmor() {
      return false;
   }

   public boolean isArmor(ItemStack stack) {
      return false;
   }

   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      int i = inventorySlot - 400;
      if (i >= 0 && i < 2 && i < this.horseChest.getSizeInventory()) {
         if (i == 0 && itemStackIn.getItem() != Items.SADDLE) {
            return false;
         } else if (i != 1 || this.wearsArmor() && this.isArmor(itemStackIn)) {
            this.horseChest.setInventorySlotContents(i, itemStackIn);
            this.updateHorseSlots();
            return true;
         } else {
            return false;
         }
      } else {
         int j = inventorySlot - 500 + 2;
         if (j >= 2 && j < this.horseChest.getSizeInventory()) {
            this.horseChest.setInventorySlotContents(j, itemStackIn);
            return true;
         } else {
            return false;
         }
      }
   }

   /**
    * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
    * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
    */
   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      if (spawnDataIn == null) {
         spawnDataIn = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)spawnDataIn).func_226258_a_(0.2F);
      }

      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.Direction facing) {
      if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler != null)
         return itemHandler.cast();
      return super.getCapability(capability, facing);
   }

   @Override
   public void remove(boolean keepData) {
      super.remove(keepData);
      if (!keepData && itemHandler != null) {
         itemHandler.invalidate();
         itemHandler = null;
      }
   }
}