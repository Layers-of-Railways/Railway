package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageAtNightGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FoxEntity extends AnimalEntity {
   private static final DataParameter<Integer> FOX_TYPE = EntityDataManager.createKey(FoxEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Byte> FOX_FLAGS = EntityDataManager.createKey(FoxEntity.class, DataSerializers.BYTE);
   private static final DataParameter<Optional<UUID>> TRUSTED_UUID_SECONDARY = EntityDataManager.createKey(FoxEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   private static final DataParameter<Optional<UUID>> TRUSTED_UUID_MAIN = EntityDataManager.createKey(FoxEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   private static final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (p_213489_0_) -> {
      return !p_213489_0_.cannotPickup() && p_213489_0_.isAlive();
   };
   private static final Predicate<Entity> STALKABLE_PREY = (p_213470_0_) -> {
      if (!(p_213470_0_ instanceof LivingEntity)) {
         return false;
      } else {
         LivingEntity livingentity = (LivingEntity)p_213470_0_;
         return livingentity.getLastAttackedEntity() != null && livingentity.getLastAttackedEntityTime() < livingentity.ticksExisted + 600;
      }
   };
   private static final Predicate<Entity> IS_PREY = (p_213498_0_) -> {
      return p_213498_0_ instanceof ChickenEntity || p_213498_0_ instanceof RabbitEntity;
   };
   private static final Predicate<Entity> SHOULD_AVOID = (p_213463_0_) -> {
      return !p_213463_0_.isDiscrete() && EntityPredicates.CAN_AI_TARGET.test(p_213463_0_);
   };
   private Goal attackAnimals;
   private Goal attackTurtles;
   private Goal attackFish;
   private float interestedAngle;
   private float interestedAngleO;
   private float crouchAmount;
   private float crouchAmountO;
   private int eatTicks;

   public FoxEntity(EntityType<? extends FoxEntity> type, World worldIn) {
      super(type, worldIn);
      this.lookController = new FoxEntity.LookHelperController();
      this.moveController = new FoxEntity.MoveHelperController();
      this.setPathPriority(PathNodeType.DANGER_OTHER, 0.0F);
      this.setPathPriority(PathNodeType.DAMAGE_OTHER, 0.0F);
      this.setCanPickUpLoot(true);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(TRUSTED_UUID_SECONDARY, Optional.empty());
      this.dataManager.register(TRUSTED_UUID_MAIN, Optional.empty());
      this.dataManager.register(FOX_TYPE, 0);
      this.dataManager.register(FOX_FLAGS, (byte)0);
   }

   protected void registerGoals() {
      this.attackAnimals = new NearestAttackableTargetGoal<>(this, AnimalEntity.class, 10, false, false, (p_213487_0_) -> {
         return p_213487_0_ instanceof ChickenEntity || p_213487_0_ instanceof RabbitEntity;
      });
      this.attackTurtles = new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, false, false, TurtleEntity.TARGET_DRY_BABY);
      this.attackFish = new NearestAttackableTargetGoal<>(this, AbstractFishEntity.class, 20, false, false, (p_213456_0_) -> {
         return p_213456_0_ instanceof AbstractGroupFishEntity;
      });
      this.goalSelector.addGoal(0, new FoxEntity.SwimGoal());
      this.goalSelector.addGoal(1, new FoxEntity.JumpGoal());
      this.goalSelector.addGoal(2, new FoxEntity.PanicGoal(2.2D));
      this.goalSelector.addGoal(3, new FoxEntity.MateGoal(1.0D));
      this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PlayerEntity.class, 16.0F, 1.6D, 1.4D, (p_213497_1_) -> {
         return SHOULD_AVOID.test(p_213497_1_) && !this.isTrustedUUID(p_213497_1_.getUniqueID()) && !this.isFoxAggroed();
      }));
      this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, WolfEntity.class, 8.0F, 1.6D, 1.4D, (p_213469_1_) -> {
         return !((WolfEntity)p_213469_1_).isTamed() && !this.isFoxAggroed();
      }));
      this.goalSelector.addGoal(5, new FoxEntity.FollowTargetGoal());
      this.goalSelector.addGoal(6, new FoxEntity.PounceGoal());
      this.goalSelector.addGoal(6, new FoxEntity.FindShelterGoal(1.25D));
      this.goalSelector.addGoal(7, new FoxEntity.BiteGoal((double)1.2F, true));
      this.goalSelector.addGoal(7, new FoxEntity.SleepGoal());
      this.goalSelector.addGoal(8, new FoxEntity.FollowGoal(this, 1.25D));
      this.goalSelector.addGoal(9, new FoxEntity.StrollGoal(32, 200));
      this.goalSelector.addGoal(10, new FoxEntity.EatBerriesGoal((double)1.2F, 12, 2));
      this.goalSelector.addGoal(10, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(11, new FoxEntity.FindItemsGoal());
      this.goalSelector.addGoal(12, new FoxEntity.WatchGoal(this, PlayerEntity.class, 24.0F));
      this.goalSelector.addGoal(13, new FoxEntity.SitAndLookGoal());
      this.targetSelector.addGoal(3, new FoxEntity.RevengeGoal(LivingEntity.class, false, false, (p_213493_1_) -> {
         return STALKABLE_PREY.test(p_213493_1_) && !this.isTrustedUUID(p_213493_1_.getUniqueID());
      }));
   }

   public SoundEvent getEatSound(ItemStack itemStackIn) {
      return SoundEvents.ENTITY_FOX_EAT;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      if (!this.world.isRemote && this.isAlive() && this.isServerWorld()) {
         ++this.eatTicks;
         ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
         if (this.canEatItem(itemstack)) {
            if (this.eatTicks > 600) {
               ItemStack itemstack1 = itemstack.onItemUseFinish(this.world, this);
               if (!itemstack1.isEmpty()) {
                  this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack1);
               }

               this.eatTicks = 0;
            } else if (this.eatTicks > 560 && this.rand.nextFloat() < 0.1F) {
               this.playSound(this.getEatSound(itemstack), 1.0F, 1.0F);
               this.world.setEntityState(this, (byte)45);
            }
         }

         LivingEntity livingentity = this.getAttackTarget();
         if (livingentity == null || !livingentity.isAlive()) {
            this.setCrouching(false);
            this.func_213502_u(false);
         }
      }

      if (this.isSleeping() || this.isMovementBlocked()) {
         this.isJumping = false;
         this.moveStrafing = 0.0F;
         this.moveForward = 0.0F;
      }

      super.livingTick();
      if (this.isFoxAggroed() && this.rand.nextFloat() < 0.05F) {
         this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
      }

   }

   /**
    * Dead and sleeping entities cannot move
    */
   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F;
   }

   private boolean canEatItem(ItemStack itemStackIn) {
      return itemStackIn.getItem().isFood() && this.getAttackTarget() == null && this.onGround && !this.isSleeping();
   }

   /**
    * Gives armor or weapon for entity based on given DifficultyInstance
    */
   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      if (this.rand.nextFloat() < 0.2F) {
         float f = this.rand.nextFloat();
         ItemStack itemstack;
         if (f < 0.05F) {
            itemstack = new ItemStack(Items.EMERALD);
         } else if (f < 0.2F) {
            itemstack = new ItemStack(Items.EGG);
         } else if (f < 0.4F) {
            itemstack = this.rand.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
         } else if (f < 0.6F) {
            itemstack = new ItemStack(Items.WHEAT);
         } else if (f < 0.8F) {
            itemstack = new ItemStack(Items.LEATHER);
         } else {
            itemstack = new ItemStack(Items.FEATHER);
         }

         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
      }

   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 45) {
         ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
         if (!itemstack.isEmpty()) {
            for(int i = 0; i < 8; ++i) {
               Vec3d vec3d = (new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F)).rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
               this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getPosX() + this.getLookVec().x / 2.0D, this.getPosY(), this.getPosZ() + this.getLookVec().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
            }
         }
      } else {
         super.handleStatusUpdate(id);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
   }

   public FoxEntity createChild(AgeableEntity ageable) {
      FoxEntity foxentity = EntityType.FOX.create(this.world);
      foxentity.setVariantType(this.rand.nextBoolean() ? this.getVariantType() : ((FoxEntity)ageable).getVariantType());
      return foxentity;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      Biome biome = worldIn.getBiome(new BlockPos(this));
      FoxEntity.Type foxentity$type = FoxEntity.Type.getTypeByBiome(biome);
      boolean flag = false;
      if (spawnDataIn instanceof FoxEntity.FoxData) {
         foxentity$type = ((FoxEntity.FoxData)spawnDataIn).field_220366_a;
         if (((FoxEntity.FoxData)spawnDataIn).func_226257_a_() >= 2) {
            flag = true;
         }
      } else {
         spawnDataIn = new FoxEntity.FoxData(foxentity$type);
      }

      this.setVariantType(foxentity$type);
      if (flag) {
         this.setGrowingAge(-24000);
      }

      this.setAttackGoals();
      this.setEquipmentBasedOnDifficulty(difficultyIn);
      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   private void setAttackGoals() {
      if (this.getVariantType() == FoxEntity.Type.RED) {
         this.targetSelector.addGoal(4, this.attackAnimals);
         this.targetSelector.addGoal(4, this.attackTurtles);
         this.targetSelector.addGoal(6, this.attackFish);
      } else {
         this.targetSelector.addGoal(4, this.attackFish);
         this.targetSelector.addGoal(6, this.attackAnimals);
         this.targetSelector.addGoal(6, this.attackTurtles);
      }

   }

   /**
    * Decreases ItemStack size by one
    */
   protected void consumeItemFromStack(PlayerEntity player, ItemStack stack) {
      if (this.isBreedingItem(stack)) {
         this.playSound(this.getEatSound(stack), 1.0F, 1.0F);
      }

      super.consumeItemFromStack(player, stack);
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return this.isChild() ? sizeIn.height * 0.85F : 0.4F;
   }

   public FoxEntity.Type getVariantType() {
      return FoxEntity.Type.getTypeByIndex(this.dataManager.get(FOX_TYPE));
   }

   private void setVariantType(FoxEntity.Type typeIn) {
      this.dataManager.set(FOX_TYPE, typeIn.getIndex());
   }

   private List<UUID> getTrustedUUIDs() {
      List<UUID> list = Lists.newArrayList();
      list.add(this.dataManager.get(TRUSTED_UUID_SECONDARY).orElse((UUID)null));
      list.add(this.dataManager.get(TRUSTED_UUID_MAIN).orElse((UUID)null));
      return list;
   }

   private void addTrustedUUID(@Nullable UUID uuidIn) {
      if (this.dataManager.get(TRUSTED_UUID_SECONDARY).isPresent()) {
         this.dataManager.set(TRUSTED_UUID_MAIN, Optional.ofNullable(uuidIn));
      } else {
         this.dataManager.set(TRUSTED_UUID_SECONDARY, Optional.ofNullable(uuidIn));
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      List<UUID> list = this.getTrustedUUIDs();
      ListNBT listnbt = new ListNBT();

      for(UUID uuid : list) {
         if (uuid != null) {
            listnbt.add(NBTUtil.writeUniqueId(uuid));
         }
      }

      compound.put("TrustedUUIDs", listnbt);
      compound.putBoolean("Sleeping", this.isSleeping());
      compound.putString("Type", this.getVariantType().getName());
      compound.putBoolean("Sitting", this.isSitting());
      compound.putBoolean("Crouching", this.isCrouching());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      ListNBT listnbt = compound.getList("TrustedUUIDs", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         this.addTrustedUUID(NBTUtil.readUniqueId(listnbt.getCompound(i)));
      }

      this.setSleeping(compound.getBoolean("Sleeping"));
      this.setVariantType(FoxEntity.Type.getTypeByName(compound.getString("Type")));
      this.setSitting(compound.getBoolean("Sitting"));
      this.setCrouching(compound.getBoolean("Crouching"));
      this.setAttackGoals();
   }

   public boolean isSitting() {
      return this.getFoxFlag(1);
   }

   public void setSitting(boolean p_213466_1_) {
      this.setFoxFlag(1, p_213466_1_);
   }

   public boolean isStuck() {
      return this.getFoxFlag(64);
   }

   private void setStuck(boolean p_213492_1_) {
      this.setFoxFlag(64, p_213492_1_);
   }

   private boolean isFoxAggroed() {
      return this.getFoxFlag(128);
   }

   private void setFoxAggroed(boolean p_213482_1_) {
      this.setFoxFlag(128, p_213482_1_);
   }

   /**
    * Returns whether player is sleeping or not
    */
   public boolean isSleeping() {
      return this.getFoxFlag(32);
   }

   private void setSleeping(boolean p_213485_1_) {
      this.setFoxFlag(32, p_213485_1_);
   }

   private void setFoxFlag(int p_213505_1_, boolean p_213505_2_) {
      if (p_213505_2_) {
         this.dataManager.set(FOX_FLAGS, (byte)(this.dataManager.get(FOX_FLAGS) | p_213505_1_));
      } else {
         this.dataManager.set(FOX_FLAGS, (byte)(this.dataManager.get(FOX_FLAGS) & ~p_213505_1_));
      }

   }

   private boolean getFoxFlag(int p_213507_1_) {
      return (this.dataManager.get(FOX_FLAGS) & p_213507_1_) != 0;
   }

   public boolean canPickUpItem(ItemStack itemstackIn) {
      EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstackIn);
      if (!this.getItemStackFromSlot(equipmentslottype).isEmpty()) {
         return false;
      } else {
         return equipmentslottype == EquipmentSlotType.MAINHAND && super.canPickUpItem(itemstackIn);
      }
   }

   protected boolean canEquipItem(ItemStack stack) {
      Item item = stack.getItem();
      ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
      return itemstack.isEmpty() || this.eatTicks > 0 && item.isFood() && !itemstack.getItem().isFood();
   }

   private void spitOutItem(ItemStack stackIn) {
      if (!stackIn.isEmpty() && !this.world.isRemote) {
         ItemEntity itementity = new ItemEntity(this.world, this.getPosX() + this.getLookVec().x, this.getPosY() + 1.0D, this.getPosZ() + this.getLookVec().z, stackIn);
         itementity.setPickupDelay(40);
         itementity.setThrowerId(this.getUniqueID());
         this.playSound(SoundEvents.ENTITY_FOX_SPIT, 1.0F, 1.0F);
         this.world.addEntity(itementity);
      }
   }

   private void spawnItem(ItemStack stackIn) {
      ItemEntity itementity = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), stackIn);
      this.world.addEntity(itementity);
   }

   /**
    * Tests if this entity should pickup a weapon or an armor. Entity drops current weapon or armor if the new one is
    * better.
    */
   protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
      ItemStack itemstack = itemEntity.getItem();
      if (this.canEquipItem(itemstack)) {
         int i = itemstack.getCount();
         if (i > 1) {
            this.spawnItem(itemstack.split(i - 1));
         }

         this.spitOutItem(this.getItemStackFromSlot(EquipmentSlotType.MAINHAND));
         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack.split(1));
         this.inventoryHandsDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
         this.onItemPickup(itemEntity, itemstack.getCount());
         itemEntity.remove();
         this.eatTicks = 0;
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.isServerWorld()) {
         boolean flag = this.isInWater();
         if (flag || this.getAttackTarget() != null || this.world.isThundering()) {
            this.func_213454_em();
         }

         if (flag || this.isSleeping()) {
            this.setSitting(false);
         }

         if (this.isStuck() && this.world.rand.nextFloat() < 0.2F) {
            BlockPos blockpos = new BlockPos(this);
            BlockState blockstate = this.world.getBlockState(blockpos);
            this.world.playEvent(2001, blockpos, Block.getStateId(blockstate));
         }
      }

      this.interestedAngleO = this.interestedAngle;
      if (this.func_213467_eg()) {
         this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
      } else {
         this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
      }

      this.crouchAmountO = this.crouchAmount;
      if (this.isCrouching()) {
         this.crouchAmount += 0.2F;
         if (this.crouchAmount > 3.0F) {
            this.crouchAmount = 3.0F;
         }
      } else {
         this.crouchAmount = 0.0F;
      }

   }

   /**
    * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
    * the animal type)
    */
   public boolean isBreedingItem(ItemStack stack) {
      return stack.getItem() == Items.SWEET_BERRIES;
   }

   protected void onChildSpawnFromEgg(PlayerEntity playerIn, AgeableEntity child) {
      ((FoxEntity)child).addTrustedUUID(playerIn.getUniqueID());
   }

   public boolean func_213480_dY() {
      return this.getFoxFlag(16);
   }

   public void func_213461_s(boolean p_213461_1_) {
      this.setFoxFlag(16, p_213461_1_);
   }

   public boolean func_213490_ee() {
      return this.crouchAmount == 3.0F;
   }

   public void setCrouching(boolean p_213451_1_) {
      this.setFoxFlag(4, p_213451_1_);
   }

   public boolean isCrouching() {
      return this.getFoxFlag(4);
   }

   public void func_213502_u(boolean p_213502_1_) {
      this.setFoxFlag(8, p_213502_1_);
   }

   public boolean func_213467_eg() {
      return this.getFoxFlag(8);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213475_v(float p_213475_1_) {
      return MathHelper.lerp(p_213475_1_, this.interestedAngleO, this.interestedAngle) * 0.11F * (float)Math.PI;
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213503_w(float p_213503_1_) {
      return MathHelper.lerp(p_213503_1_, this.crouchAmountO, this.crouchAmount);
   }

   /**
    * Sets the active target the Task system uses for tracking
    */
   public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
      if (this.isFoxAggroed() && entitylivingbaseIn == null) {
         this.setFoxAggroed(false);
      }

      super.setAttackTarget(entitylivingbaseIn);
   }

   protected int calculateFallDamage(float p_225508_1_, float p_225508_2_) {
      return MathHelper.ceil((p_225508_1_ - 5.0F) * p_225508_2_);
   }

   private void func_213454_em() {
      this.setSleeping(false);
   }

   private void func_213499_en() {
      this.func_213502_u(false);
      this.setCrouching(false);
      this.setSitting(false);
      this.setSleeping(false);
      this.setFoxAggroed(false);
      this.setStuck(false);
   }

   private boolean func_213478_eo() {
      return !this.isSleeping() && !this.isSitting() && !this.isStuck();
   }

   /**
    * Plays living's sound at its position
    */
   public void playAmbientSound() {
      SoundEvent soundevent = this.getAmbientSound();
      if (soundevent == SoundEvents.ENTITY_FOX_SCREECH) {
         this.playSound(soundevent, 2.0F, this.getSoundPitch());
      } else {
         super.playAmbientSound();
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return SoundEvents.ENTITY_FOX_SLEEP;
      } else {
         if (!this.world.isDaytime() && this.rand.nextFloat() < 0.1F) {
            List<PlayerEntity> list = this.world.getEntitiesWithinAABB(PlayerEntity.class, this.getBoundingBox().grow(16.0D, 16.0D, 16.0D), EntityPredicates.NOT_SPECTATING);
            if (list.isEmpty()) {
               return SoundEvents.ENTITY_FOX_SCREECH;
            }
         }

         return SoundEvents.ENTITY_FOX_AMBIENT;
      }
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_FOX_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_FOX_DEATH;
   }

   private boolean isTrustedUUID(UUID p_213468_1_) {
      return this.getTrustedUUIDs().contains(p_213468_1_);
   }

   protected void spawnDrops(DamageSource damageSourceIn) {
      ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
      if (!itemstack.isEmpty()) {
         this.entityDropItem(itemstack);
         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
      }

      super.spawnDrops(damageSourceIn);
   }

   public static boolean func_213481_a(FoxEntity p_213481_0_, LivingEntity p_213481_1_) {
      double d0 = p_213481_1_.getPosZ() - p_213481_0_.getPosZ();
      double d1 = p_213481_1_.getPosX() - p_213481_0_.getPosX();
      double d2 = d0 / d1;
      int i = 6;

      for(int j = 0; j < 6; ++j) {
         double d3 = d2 == 0.0D ? 0.0D : d0 * (double)((float)j / 6.0F);
         double d4 = d2 == 0.0D ? d1 * (double)((float)j / 6.0F) : d3 / d2;

         for(int k = 1; k < 4; ++k) {
            if (!p_213481_0_.world.getBlockState(new BlockPos(p_213481_0_.getPosX() + d4, p_213481_0_.getPosY() + (double)k, p_213481_0_.getPosZ() + d3)).getMaterial().isReplaceable()) {
               return false;
            }
         }
      }

      return true;
   }

   public class AlertablePredicate implements Predicate<LivingEntity> {
      public boolean test(LivingEntity p_test_1_) {
         if (p_test_1_ instanceof FoxEntity) {
            return false;
         } else if (!(p_test_1_ instanceof ChickenEntity) && !(p_test_1_ instanceof RabbitEntity) && !(p_test_1_ instanceof MonsterEntity)) {
            if (p_test_1_ instanceof TameableEntity) {
               return !((TameableEntity)p_test_1_).isTamed();
            } else if (!(p_test_1_ instanceof PlayerEntity) || !p_test_1_.isSpectator() && !((PlayerEntity)p_test_1_).isCreative()) {
               if (FoxEntity.this.isTrustedUUID(p_test_1_.getUniqueID())) {
                  return false;
               } else {
                  return !p_test_1_.isSleeping() && !p_test_1_.isDiscrete();
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   abstract class BaseGoal extends Goal {
      private final EntityPredicate field_220816_b = (new EntityPredicate()).setDistance(12.0D).setLineOfSiteRequired().setCustomPredicate(FoxEntity.this.new AlertablePredicate());

      private BaseGoal() {
      }

      protected boolean func_220813_g() {
         BlockPos blockpos = new BlockPos(FoxEntity.this);
         return !FoxEntity.this.world.canSeeSky(blockpos) && FoxEntity.this.getBlockPathWeight(blockpos) >= 0.0F;
      }

      protected boolean func_220814_h() {
         return !FoxEntity.this.world.getTargettableEntitiesWithinAABB(LivingEntity.class, this.field_220816_b, FoxEntity.this, FoxEntity.this.getBoundingBox().grow(12.0D, 6.0D, 12.0D)).isEmpty();
      }
   }

   class BiteGoal extends MeleeAttackGoal {
      public BiteGoal(double p_i50731_2_, boolean p_i50731_4_) {
         super(FoxEntity.this, p_i50731_2_, p_i50731_4_);
      }

      protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
         double d0 = this.getAttackReachSqr(enemy);
         if (distToEnemySqr <= d0 && this.attackTick <= 0) {
            this.attackTick = 20;
            this.attacker.attackEntityAsMob(enemy);
            FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_BITE, 1.0F, 1.0F);
         }

      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         FoxEntity.this.func_213502_u(false);
         super.startExecuting();
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return !FoxEntity.this.isSitting() && !FoxEntity.this.isSleeping() && !FoxEntity.this.isCrouching() && !FoxEntity.this.isStuck() && super.shouldExecute();
      }
   }

   public class EatBerriesGoal extends MoveToBlockGoal {
      protected int field_220731_g;

      public EatBerriesGoal(double p_i50737_2_, int p_i50737_4_, int p_i50737_5_) {
         super(FoxEntity.this, p_i50737_2_, p_i50737_4_, p_i50737_5_);
      }

      public double getTargetDistanceSq() {
         return 2.0D;
      }

      public boolean shouldMove() {
         return this.timeoutCounter % 100 == 0;
      }

      /**
       * Return true to set given position as destination
       */
      protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
         BlockState blockstate = worldIn.getBlockState(pos);
         return blockstate.getBlock() == Blocks.SWEET_BERRY_BUSH && blockstate.get(SweetBerryBushBlock.AGE) >= 2;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.getIsAboveDestination()) {
            if (this.field_220731_g >= 40) {
               this.eatBerry();
            } else {
               ++this.field_220731_g;
            }
         } else if (!this.getIsAboveDestination() && FoxEntity.this.rand.nextFloat() < 0.05F) {
            FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_SNIFF, 1.0F, 1.0F);
         }

         super.tick();
      }

      protected void eatBerry() {
         if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(FoxEntity.this.world, FoxEntity.this)) {
            BlockState blockstate = FoxEntity.this.world.getBlockState(this.destinationBlock);
            if (blockstate.getBlock() == Blocks.SWEET_BERRY_BUSH) {
               int i = blockstate.get(SweetBerryBushBlock.AGE);
               blockstate.with(SweetBerryBushBlock.AGE, Integer.valueOf(1));
               int j = 1 + FoxEntity.this.world.rand.nextInt(2) + (i == 3 ? 1 : 0);
               ItemStack itemstack = FoxEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
               if (itemstack.isEmpty()) {
                  FoxEntity.this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
                  --j;
               }

               if (j > 0) {
                  Block.spawnAsEntity(FoxEntity.this.world, this.destinationBlock, new ItemStack(Items.SWEET_BERRIES, j));
               }

               FoxEntity.this.playSound(SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, 1.0F, 1.0F);
               FoxEntity.this.world.setBlockState(this.destinationBlock, blockstate.with(SweetBerryBushBlock.AGE, Integer.valueOf(1)), 2);
            }
         }
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return !FoxEntity.this.isSleeping() && super.shouldExecute();
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.field_220731_g = 0;
         FoxEntity.this.setSitting(false);
         super.startExecuting();
      }
   }

   class FindItemsGoal extends Goal {
      public FindItemsGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (!FoxEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
            return false;
         } else if (FoxEntity.this.getAttackTarget() == null && FoxEntity.this.getRevengeTarget() == null) {
            if (!FoxEntity.this.func_213478_eo()) {
               return false;
            } else if (FoxEntity.this.getRNG().nextInt(10) != 0) {
               return false;
            } else {
               List<ItemEntity> list = FoxEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, FoxEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), FoxEntity.TRUSTED_TARGET_SELECTOR);
               return !list.isEmpty() && FoxEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty();
            }
         } else {
            return false;
         }
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         List<ItemEntity> list = FoxEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, FoxEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), FoxEntity.TRUSTED_TARGET_SELECTOR);
         ItemStack itemstack = FoxEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
         if (itemstack.isEmpty() && !list.isEmpty()) {
            FoxEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), (double)1.2F);
         }

      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         List<ItemEntity> list = FoxEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, FoxEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), FoxEntity.TRUSTED_TARGET_SELECTOR);
         if (!list.isEmpty()) {
            FoxEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), (double)1.2F);
         }

      }
   }

   class FindShelterGoal extends FleeSunGoal {
      private int cooldown = 100;

      public FindShelterGoal(double p_i50724_2_) {
         super(FoxEntity.this, p_i50724_2_);
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (!FoxEntity.this.isSleeping() && this.creature.getAttackTarget() == null) {
            if (FoxEntity.this.world.isThundering()) {
               return true;
            } else if (this.cooldown > 0) {
               --this.cooldown;
               return false;
            } else {
               this.cooldown = 100;
               BlockPos blockpos = new BlockPos(this.creature);
               return FoxEntity.this.world.isDaytime() && FoxEntity.this.world.canSeeSky(blockpos) && !((ServerWorld)FoxEntity.this.world).isVillage(blockpos) && this.func_220702_g();
            }
         } else {
            return false;
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         FoxEntity.this.func_213499_en();
         super.startExecuting();
      }
   }

   class FollowGoal extends FollowParentGoal {
      private final FoxEntity owner;

      public FollowGoal(FoxEntity p_i50735_2_, double p_i50735_3_) {
         super(p_i50735_2_, p_i50735_3_);
         this.owner = p_i50735_2_;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return !this.owner.isFoxAggroed() && super.shouldExecute();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return !this.owner.isFoxAggroed() && super.shouldContinueExecuting();
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.owner.func_213499_en();
         super.startExecuting();
      }
   }

   class FollowTargetGoal extends Goal {
      public FollowTargetGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (FoxEntity.this.isSleeping()) {
            return false;
         } else {
            LivingEntity livingentity = FoxEntity.this.getAttackTarget();
            return livingentity != null && livingentity.isAlive() && FoxEntity.IS_PREY.test(livingentity) && FoxEntity.this.getDistanceSq(livingentity) > 36.0D && !FoxEntity.this.isCrouching() && !FoxEntity.this.func_213467_eg() && !FoxEntity.this.isJumping;
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         FoxEntity.this.setSitting(false);
         FoxEntity.this.setStuck(false);
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         LivingEntity livingentity = FoxEntity.this.getAttackTarget();
         if (livingentity != null && FoxEntity.func_213481_a(FoxEntity.this, livingentity)) {
            FoxEntity.this.func_213502_u(true);
            FoxEntity.this.setCrouching(true);
            FoxEntity.this.getNavigator().clearPath();
            FoxEntity.this.getLookController().setLookPositionWithEntity(livingentity, (float)FoxEntity.this.getHorizontalFaceSpeed(), (float)FoxEntity.this.getVerticalFaceSpeed());
         } else {
            FoxEntity.this.func_213502_u(false);
            FoxEntity.this.setCrouching(false);
         }

      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         LivingEntity livingentity = FoxEntity.this.getAttackTarget();
         FoxEntity.this.getLookController().setLookPositionWithEntity(livingentity, (float)FoxEntity.this.getHorizontalFaceSpeed(), (float)FoxEntity.this.getVerticalFaceSpeed());
         if (FoxEntity.this.getDistanceSq(livingentity) <= 36.0D) {
            FoxEntity.this.func_213502_u(true);
            FoxEntity.this.setCrouching(true);
            FoxEntity.this.getNavigator().clearPath();
         } else {
            FoxEntity.this.getNavigator().tryMoveToEntityLiving(livingentity, 1.5D);
         }

      }
   }

   public static class FoxData extends AgeableEntity.AgeableData {
      public final FoxEntity.Type field_220366_a;

      public FoxData(FoxEntity.Type p_i50734_1_) {
         this.func_226259_a_(false);
         this.field_220366_a = p_i50734_1_;
      }
   }

   class JumpGoal extends Goal {
      int delay;

      public JumpGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return FoxEntity.this.isStuck();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return this.shouldExecute() && this.delay > 0;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.delay = 40;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         FoxEntity.this.setStuck(false);
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         --this.delay;
      }
   }

   public class LookHelperController extends LookController {
      public LookHelperController() {
         super(FoxEntity.this);
      }

      /**
       * Updates look
       */
      public void tick() {
         if (!FoxEntity.this.isSleeping()) {
            super.tick();
         }

      }

      protected boolean func_220680_b() {
         return !FoxEntity.this.func_213480_dY() && !FoxEntity.this.isCrouching() && !FoxEntity.this.func_213467_eg() & !FoxEntity.this.isStuck();
      }
   }

   class MateGoal extends BreedGoal {
      public MateGoal(double p_i50738_2_) {
         super(FoxEntity.this, p_i50738_2_);
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         ((FoxEntity)this.animal).func_213499_en();
         ((FoxEntity)this.targetMate).func_213499_en();
         super.startExecuting();
      }

      /**
       * Spawns a baby animal of the same type.
       */
      protected void spawnBaby() {
         FoxEntity foxentity = (FoxEntity)this.animal.createChild(this.targetMate);
         if (foxentity != null) {
            ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
            ServerPlayerEntity serverplayerentity1 = this.targetMate.getLoveCause();
            ServerPlayerEntity serverplayerentity2 = serverplayerentity;
            if (serverplayerentity != null) {
               foxentity.addTrustedUUID(serverplayerentity.getUniqueID());
            } else {
               serverplayerentity2 = serverplayerentity1;
            }

            if (serverplayerentity1 != null && serverplayerentity != serverplayerentity1) {
               foxentity.addTrustedUUID(serverplayerentity1.getUniqueID());
            }

            if (serverplayerentity2 != null) {
               serverplayerentity2.addStat(Stats.ANIMALS_BRED);
               CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity2, this.animal, this.targetMate, foxentity);
            }

            int i = 6000;
            this.animal.setGrowingAge(6000);
            this.targetMate.setGrowingAge(6000);
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            foxentity.setGrowingAge(-24000);
            foxentity.setLocationAndAngles(this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), 0.0F, 0.0F);
            this.world.addEntity(foxentity);
            this.world.setEntityState(this.animal, (byte)18);
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
               this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), this.animal.getRNG().nextInt(7) + 1));
            }

         }
      }
   }

   class MoveHelperController extends MovementController {
      public MoveHelperController() {
         super(FoxEntity.this);
      }

      public void tick() {
         if (FoxEntity.this.func_213478_eo()) {
            super.tick();
         }

      }
   }

   class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      public PanicGoal(double p_i50729_2_) {
         super(FoxEntity.this, p_i50729_2_);
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return !FoxEntity.this.isFoxAggroed() && super.shouldExecute();
      }
   }

   public class PounceGoal extends net.minecraft.entity.ai.goal.JumpGoal {
      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (!FoxEntity.this.func_213490_ee()) {
            return false;
         } else {
            LivingEntity livingentity = FoxEntity.this.getAttackTarget();
            if (livingentity != null && livingentity.isAlive()) {
               if (livingentity.getAdjustedHorizontalFacing() != livingentity.getHorizontalFacing()) {
                  return false;
               } else {
                  boolean flag = FoxEntity.func_213481_a(FoxEntity.this, livingentity);
                  if (!flag) {
                     FoxEntity.this.getNavigator().getPathToEntity(livingentity, 0);
                     FoxEntity.this.setCrouching(false);
                     FoxEntity.this.func_213502_u(false);
                  }

                  return flag;
               }
            } else {
               return false;
            }
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         LivingEntity livingentity = FoxEntity.this.getAttackTarget();
         if (livingentity != null && livingentity.isAlive()) {
            double d0 = FoxEntity.this.getMotion().y;
            return (!(d0 * d0 < (double)0.05F) || !(Math.abs(FoxEntity.this.rotationPitch) < 15.0F) || !FoxEntity.this.onGround) && !FoxEntity.this.isStuck();
         } else {
            return false;
         }
      }

      public boolean isPreemptible() {
         return false;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         FoxEntity.this.setJumping(true);
         FoxEntity.this.func_213461_s(true);
         FoxEntity.this.func_213502_u(false);
         LivingEntity livingentity = FoxEntity.this.getAttackTarget();
         FoxEntity.this.getLookController().setLookPositionWithEntity(livingentity, 60.0F, 30.0F);
         Vec3d vec3d = (new Vec3d(livingentity.getPosX() - FoxEntity.this.getPosX(), livingentity.getPosY() - FoxEntity.this.getPosY(), livingentity.getPosZ() - FoxEntity.this.getPosZ())).normalize();
         FoxEntity.this.setMotion(FoxEntity.this.getMotion().add(vec3d.x * 0.8D, 0.9D, vec3d.z * 0.8D));
         FoxEntity.this.getNavigator().clearPath();
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         FoxEntity.this.setCrouching(false);
         FoxEntity.this.crouchAmount = 0.0F;
         FoxEntity.this.crouchAmountO = 0.0F;
         FoxEntity.this.func_213502_u(false);
         FoxEntity.this.func_213461_s(false);
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         LivingEntity livingentity = FoxEntity.this.getAttackTarget();
         if (livingentity != null) {
            FoxEntity.this.getLookController().setLookPositionWithEntity(livingentity, 60.0F, 30.0F);
         }

         if (!FoxEntity.this.isStuck()) {
            Vec3d vec3d = FoxEntity.this.getMotion();
            if (vec3d.y * vec3d.y < (double)0.03F && FoxEntity.this.rotationPitch != 0.0F) {
               FoxEntity.this.rotationPitch = MathHelper.rotLerp(FoxEntity.this.rotationPitch, 0.0F, 0.2F);
            } else {
               double d0 = Math.sqrt(Entity.horizontalMag(vec3d));
               double d1 = Math.signum(-vec3d.y) * Math.acos(d0 / vec3d.length()) * (double)(180F / (float)Math.PI);
               FoxEntity.this.rotationPitch = (float)d1;
            }
         }

         if (livingentity != null && FoxEntity.this.getDistance(livingentity) <= 2.0F) {
            FoxEntity.this.attackEntityAsMob(livingentity);
         } else if (FoxEntity.this.rotationPitch > 0.0F && FoxEntity.this.onGround && (float)FoxEntity.this.getMotion().y != 0.0F && FoxEntity.this.world.getBlockState(new BlockPos(FoxEntity.this)).getBlock() == Blocks.SNOW) {
            FoxEntity.this.rotationPitch = 60.0F;
            FoxEntity.this.setAttackTarget((LivingEntity)null);
            FoxEntity.this.setStuck(true);
         }

      }
   }

   class RevengeGoal extends NearestAttackableTargetGoal<LivingEntity> {
      @Nullable
      private LivingEntity field_220786_j;
      private LivingEntity field_220787_k;
      private int field_220788_l;

      public RevengeGoal(Class<LivingEntity> p_i50743_2_, boolean p_i50743_3_, boolean p_i50743_4_, @Nullable Predicate<LivingEntity> p_i50743_5_) {
         super(FoxEntity.this, p_i50743_2_, 10, p_i50743_3_, p_i50743_4_, p_i50743_5_);
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (this.targetChance > 0 && this.goalOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
         } else {
            for(UUID uuid : FoxEntity.this.getTrustedUUIDs()) {
               if (uuid != null && FoxEntity.this.world instanceof ServerWorld) {
                  Entity entity = ((ServerWorld)FoxEntity.this.world).getEntityByUuid(uuid);
                  if (entity instanceof LivingEntity) {
                     LivingEntity livingentity = (LivingEntity)entity;
                     this.field_220787_k = livingentity;
                     this.field_220786_j = livingentity.getRevengeTarget();
                     int i = livingentity.getRevengeTimer();
                     return i != this.field_220788_l && this.isSuitableTarget(this.field_220786_j, this.targetEntitySelector);
                  }
               }
            }

            return false;
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         FoxEntity.this.setAttackTarget(this.field_220786_j);
         this.nearestTarget = this.field_220786_j;
         if (this.field_220787_k != null) {
            this.field_220788_l = this.field_220787_k.getRevengeTimer();
         }

         FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
         FoxEntity.this.setFoxAggroed(true);
         FoxEntity.this.func_213454_em();
         super.startExecuting();
      }
   }

   class SitAndLookGoal extends FoxEntity.BaseGoal {
      private double field_220819_c;
      private double field_220820_d;
      private int field_220821_e;
      private int field_220822_f;

      public SitAndLookGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return FoxEntity.this.getRevengeTarget() == null && FoxEntity.this.getRNG().nextFloat() < 0.02F && !FoxEntity.this.isSleeping() && FoxEntity.this.getAttackTarget() == null && FoxEntity.this.getNavigator().noPath() && !this.func_220814_h() && !FoxEntity.this.func_213480_dY() && !FoxEntity.this.isCrouching();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return this.field_220822_f > 0;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.func_220817_j();
         this.field_220822_f = 2 + FoxEntity.this.getRNG().nextInt(3);
         FoxEntity.this.setSitting(true);
         FoxEntity.this.getNavigator().clearPath();
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         FoxEntity.this.setSitting(false);
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         --this.field_220821_e;
         if (this.field_220821_e <= 0) {
            --this.field_220822_f;
            this.func_220817_j();
         }

         FoxEntity.this.getLookController().setLookPosition(FoxEntity.this.getPosX() + this.field_220819_c, FoxEntity.this.getPosYEye(), FoxEntity.this.getPosZ() + this.field_220820_d, (float)FoxEntity.this.getHorizontalFaceSpeed(), (float)FoxEntity.this.getVerticalFaceSpeed());
      }

      private void func_220817_j() {
         double d0 = (Math.PI * 2D) * FoxEntity.this.getRNG().nextDouble();
         this.field_220819_c = Math.cos(d0);
         this.field_220820_d = Math.sin(d0);
         this.field_220821_e = 80 + FoxEntity.this.getRNG().nextInt(20);
      }
   }

   class SleepGoal extends FoxEntity.BaseGoal {
      private int field_220825_c = FoxEntity.this.rand.nextInt(140);

      public SleepGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (FoxEntity.this.moveStrafing == 0.0F && FoxEntity.this.moveVertical == 0.0F && FoxEntity.this.moveForward == 0.0F) {
            return this.func_220823_j() || FoxEntity.this.isSleeping();
         } else {
            return false;
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return this.func_220823_j();
      }

      private boolean func_220823_j() {
         if (this.field_220825_c > 0) {
            --this.field_220825_c;
            return false;
         } else {
            return FoxEntity.this.world.isDaytime() && this.func_220813_g() && !this.func_220814_h();
         }
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.field_220825_c = FoxEntity.this.rand.nextInt(140);
         FoxEntity.this.func_213499_en();
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         FoxEntity.this.setSitting(false);
         FoxEntity.this.setCrouching(false);
         FoxEntity.this.func_213502_u(false);
         FoxEntity.this.setJumping(false);
         FoxEntity.this.setSleeping(true);
         FoxEntity.this.getNavigator().clearPath();
         FoxEntity.this.getMoveHelper().setMoveTo(FoxEntity.this.getPosX(), FoxEntity.this.getPosY(), FoxEntity.this.getPosZ(), 0.0D);
      }
   }

   class StrollGoal extends MoveThroughVillageAtNightGoal {
      public StrollGoal(int p_i50726_2_, int p_i50726_3_) {
         super(FoxEntity.this, p_i50726_3_);
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         FoxEntity.this.func_213499_en();
         super.startExecuting();
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && this.func_220759_g();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && this.func_220759_g();
      }

      private boolean func_220759_g() {
         return !FoxEntity.this.isSleeping() && !FoxEntity.this.isSitting() && !FoxEntity.this.isFoxAggroed() && FoxEntity.this.getAttackTarget() == null;
      }
   }

   class SwimGoal extends net.minecraft.entity.ai.goal.SwimGoal {
      public SwimGoal() {
         super(FoxEntity.this);
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         super.startExecuting();
         FoxEntity.this.func_213499_en();
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return FoxEntity.this.isInWater() && FoxEntity.this.getSubmergedHeight() > 0.25D || FoxEntity.this.isInLava();
      }
   }

   public static enum Type {
      RED(0, "red", Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.GIANT_SPRUCE_TAIGA_HILLS),
      SNOW(1, "snow", Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS);

      private static final FoxEntity.Type[] field_221088_c = Arrays.stream(values()).sorted(Comparator.comparingInt(FoxEntity.Type::getIndex)).toArray((p_221084_0_) -> {
         return new FoxEntity.Type[p_221084_0_];
      });
      private static final Map<String, FoxEntity.Type> TYPES_BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(FoxEntity.Type::getName, (p_221081_0_) -> {
         return p_221081_0_;
      }));
      private final int index;
      private final String name;
      private final List<Biome> spawnBiomes;

      private Type(int indexIn, String nameIn, Biome... spawnBiomesIn) {
         this.index = indexIn;
         this.name = nameIn;
         this.spawnBiomes = Arrays.asList(spawnBiomesIn);
      }

      public String getName() {
         return this.name;
      }

      public List<Biome> getSpawnBiomes() {
         return this.spawnBiomes;
      }

      public int getIndex() {
         return this.index;
      }

      public static FoxEntity.Type getTypeByName(String nameIn) {
         return TYPES_BY_NAME.getOrDefault(nameIn, RED);
      }

      public static FoxEntity.Type getTypeByIndex(int indexIn) {
         if (indexIn < 0 || indexIn > field_221088_c.length) {
            indexIn = 0;
         }

         return field_221088_c[indexIn];
      }

      /**
       * Gets the type of fox that can spawn in this biome. The default type is red fox.
       */
      public static FoxEntity.Type getTypeByBiome(Biome biomeIn) {
         return SNOW.getSpawnBiomes().contains(biomeIn) ? SNOW : RED;
      }
   }

   class WatchGoal extends LookAtGoal {
      public WatchGoal(MobEntity p_i50733_2_, Class<? extends LivingEntity> p_i50733_3_, float p_i50733_4_) {
         super(p_i50733_2_, p_i50733_3_, p_i50733_4_);
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && !FoxEntity.this.isStuck() && !FoxEntity.this.func_213467_eg();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && !FoxEntity.this.isStuck() && !FoxEntity.this.func_213467_eg();
      }
   }
}