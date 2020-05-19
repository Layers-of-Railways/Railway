package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NonTamedTargetGoal;
import net.minecraft.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CatEntity extends TameableEntity {
   private static final Ingredient BREEDING_ITEMS = Ingredient.fromItems(Items.COD, Items.SALMON);
   private static final DataParameter<Integer> CAT_TYPE = EntityDataManager.createKey(CatEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> field_213428_bG = EntityDataManager.createKey(CatEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> field_213429_bH = EntityDataManager.createKey(CatEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(CatEntity.class, DataSerializers.VARINT);
   public static final Map<Integer, ResourceLocation> TEXTURE_BY_ID = Util.make(Maps.newHashMap(), (p_213410_0_) -> {
      p_213410_0_.put(0, new ResourceLocation("textures/entity/cat/tabby.png"));
      p_213410_0_.put(1, new ResourceLocation("textures/entity/cat/black.png"));
      p_213410_0_.put(2, new ResourceLocation("textures/entity/cat/red.png"));
      p_213410_0_.put(3, new ResourceLocation("textures/entity/cat/siamese.png"));
      p_213410_0_.put(4, new ResourceLocation("textures/entity/cat/british_shorthair.png"));
      p_213410_0_.put(5, new ResourceLocation("textures/entity/cat/calico.png"));
      p_213410_0_.put(6, new ResourceLocation("textures/entity/cat/persian.png"));
      p_213410_0_.put(7, new ResourceLocation("textures/entity/cat/ragdoll.png"));
      p_213410_0_.put(8, new ResourceLocation("textures/entity/cat/white.png"));
      p_213410_0_.put(9, new ResourceLocation("textures/entity/cat/jellie.png"));
      p_213410_0_.put(10, new ResourceLocation("textures/entity/cat/all_black.png"));
   });
   private CatEntity.AvoidPlayerGoal<PlayerEntity> avoidPlayerGoal;
   private net.minecraft.entity.ai.goal.TemptGoal temptGoal;
   private float field_213433_bL;
   private float field_213434_bM;
   private float field_213435_bN;
   private float field_213436_bO;
   private float field_213437_bP;
   private float field_213438_bQ;

   public CatEntity(EntityType<? extends CatEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public ResourceLocation getCatTypeName() {
      return TEXTURE_BY_ID.getOrDefault(this.getCatType(), TEXTURE_BY_ID.get(0));
   }

   protected void registerGoals() {
      this.sitGoal = new SitGoal(this);
      this.temptGoal = new CatEntity.TemptGoal(this, 0.6D, BREEDING_ITEMS, true);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(1, new CatEntity.MorningGiftGoal(this));
      this.goalSelector.addGoal(2, this.sitGoal);
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1D, 8));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));
      this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8D));
      this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
      this.goalSelector.addGoal(10, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, 0.8D, 1.0000001E-5F));
      this.goalSelector.addGoal(12, new LookAtGoal(this, PlayerEntity.class, 10.0F));
      this.targetSelector.addGoal(1, new NonTamedTargetGoal<>(this, RabbitEntity.class, false, (Predicate<LivingEntity>)null));
      this.targetSelector.addGoal(1, new NonTamedTargetGoal<>(this, TurtleEntity.class, false, TurtleEntity.TARGET_DRY_BABY));
   }

   public int getCatType() {
      return this.dataManager.get(CAT_TYPE);
   }

   public void setCatType(int type) {
      if (type < 0 || type >= 11) {
         type = this.rand.nextInt(10);
      }

      this.dataManager.set(CAT_TYPE, type);
   }

   public void func_213419_u(boolean p_213419_1_) {
      this.dataManager.set(field_213428_bG, p_213419_1_);
   }

   public boolean func_213416_eg() {
      return this.dataManager.get(field_213428_bG);
   }

   public void func_213415_v(boolean p_213415_1_) {
      this.dataManager.set(field_213429_bH, p_213415_1_);
   }

   public boolean func_213409_eh() {
      return this.dataManager.get(field_213429_bH);
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId(this.dataManager.get(COLLAR_COLOR));
   }

   public void setCollarColor(DyeColor color) {
      this.dataManager.set(COLLAR_COLOR, color.getId());
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CAT_TYPE, 1);
      this.dataManager.register(field_213428_bG, false);
      this.dataManager.register(field_213429_bH, false);
      this.dataManager.register(COLLAR_COLOR, DyeColor.RED.getId());
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("CatType", this.getCatType());
      compound.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setCatType(compound.getInt("CatType"));
      if (compound.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));
      }

   }

   public void updateAITasks() {
      if (this.getMoveHelper().isUpdating()) {
         double d0 = this.getMoveHelper().getSpeed();
         if (d0 == 0.6D) {
            this.setPose(Pose.CROUCHING);
            this.setSprinting(false);
         } else if (d0 == 1.33D) {
            this.setPose(Pose.STANDING);
            this.setSprinting(true);
         } else {
            this.setPose(Pose.STANDING);
            this.setSprinting(false);
         }
      } else {
         this.setPose(Pose.STANDING);
         this.setSprinting(false);
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isTamed()) {
         if (this.isInLove()) {
            return SoundEvents.ENTITY_CAT_PURR;
         } else {
            return this.rand.nextInt(4) == 0 ? SoundEvents.ENTITY_CAT_PURREOW : SoundEvents.ENTITY_CAT_AMBIENT;
         }
      } else {
         return SoundEvents.ENTITY_CAT_STRAY_AMBIENT;
      }
   }

   /**
    * Get number of ticks, at least during which the living entity will be silent.
    */
   public int getTalkInterval() {
      return 120;
   }

   public void func_213420_ej() {
      this.playSound(SoundEvents.ENTITY_CAT_HISS, this.getSoundVolume(), this.getSoundPitch());
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_CAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CAT_DEATH;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
   }

   public boolean onLivingFall(float distance, float damageMultiplier) {
      return false;
   }

   /**
    * Decreases ItemStack size by one
    */
   protected void consumeItemFromStack(PlayerEntity player, ItemStack stack) {
      if (this.isBreedingItem(stack)) {
         this.playSound(SoundEvents.ENTITY_CAT_EAT, 1.0F, 1.0F);
      }

      super.consumeItemFromStack(player, stack);
   }

   private float func_226510_eF_() {
      return (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_226510_eF_());
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.temptGoal != null && this.temptGoal.isRunning() && !this.isTamed() && this.ticksExisted % 100 == 0) {
         this.playSound(SoundEvents.ENTITY_CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
      }

      this.func_213412_ek();
   }

   private void func_213412_ek() {
      if ((this.func_213416_eg() || this.func_213409_eh()) && this.ticksExisted % 5 == 0) {
         this.playSound(SoundEvents.ENTITY_CAT_PURR, 0.6F + 0.4F * (this.rand.nextFloat() - this.rand.nextFloat()), 1.0F);
      }

      this.func_213418_el();
      this.func_213411_em();
   }

   private void func_213418_el() {
      this.field_213434_bM = this.field_213433_bL;
      this.field_213436_bO = this.field_213435_bN;
      if (this.func_213416_eg()) {
         this.field_213433_bL = Math.min(1.0F, this.field_213433_bL + 0.15F);
         this.field_213435_bN = Math.min(1.0F, this.field_213435_bN + 0.08F);
      } else {
         this.field_213433_bL = Math.max(0.0F, this.field_213433_bL - 0.22F);
         this.field_213435_bN = Math.max(0.0F, this.field_213435_bN - 0.13F);
      }

   }

   private void func_213411_em() {
      this.field_213438_bQ = this.field_213437_bP;
      if (this.func_213409_eh()) {
         this.field_213437_bP = Math.min(1.0F, this.field_213437_bP + 0.1F);
      } else {
         this.field_213437_bP = Math.max(0.0F, this.field_213437_bP - 0.13F);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float func_213408_v(float partialTicks) {
      return MathHelper.lerp(partialTicks, this.field_213434_bM, this.field_213433_bL);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213421_w(float partialTicks) {
      return MathHelper.lerp(partialTicks, this.field_213436_bO, this.field_213435_bN);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213424_x(float partialTicks) {
      return MathHelper.lerp(partialTicks, this.field_213438_bQ, this.field_213437_bP);
   }

   public CatEntity createChild(AgeableEntity ageable) {
      CatEntity catentity = EntityType.CAT.create(this.world);
      if (ageable instanceof CatEntity) {
         if (this.rand.nextBoolean()) {
            catentity.setCatType(this.getCatType());
         } else {
            catentity.setCatType(((CatEntity)ageable).getCatType());
         }

         if (this.isTamed()) {
            catentity.setOwnerId(this.getOwnerId());
            catentity.setTamed(true);
            if (this.rand.nextBoolean()) {
               catentity.setCollarColor(this.getCollarColor());
            } else {
               catentity.setCollarColor(((CatEntity)ageable).getCollarColor());
            }
         }
      }

      return catentity;
   }

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
   public boolean canMateWith(AnimalEntity otherAnimal) {
      if (!this.isTamed()) {
         return false;
      } else if (!(otherAnimal instanceof CatEntity)) {
         return false;
      } else {
         CatEntity catentity = (CatEntity)otherAnimal;
         return catentity.isTamed() && super.canMateWith(otherAnimal);
      }
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
      if (worldIn.getCurrentMoonPhaseFactor() > 0.9F) {
         this.setCatType(this.rand.nextInt(11));
      } else {
         this.setCatType(this.rand.nextInt(10));
      }

      if (Feature.SWAMP_HUT.isPositionInsideStructure(worldIn, new BlockPos(this))) {
         this.setCatType(10);
         this.enablePersistence();
      }

      return spawnDataIn;
   }

   public boolean processInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      Item item = itemstack.getItem();
      if (itemstack.getItem() instanceof SpawnEggItem) {
         return super.processInteract(player, hand);
      } else if (this.world.isRemote) {
         return this.isTamed() && this.isOwner(player) || this.isBreedingItem(itemstack);
      } else {
         if (this.isTamed()) {
            if (this.isOwner(player)) {
               if (!(item instanceof DyeItem)) {
                  if (item.isFood() && this.isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
                     this.consumeItemFromStack(player, itemstack);
                     this.heal((float)item.getFood().getHealing());
                     return true;
                  }

                  boolean flag = super.processInteract(player, hand);
                  if (!flag || this.isChild()) {
                     this.sitGoal.setSitting(!this.isSitting());
                  }

                  return flag;
               }

               DyeColor dyecolor = ((DyeItem)item).getDyeColor();
               if (dyecolor != this.getCollarColor()) {
                  this.setCollarColor(dyecolor);
                  if (!player.abilities.isCreativeMode) {
                     itemstack.shrink(1);
                  }

                  this.enablePersistence();
                  return true;
               }
            }
         } else if (this.isBreedingItem(itemstack)) {
            this.consumeItemFromStack(player, itemstack);
            if (this.rand.nextInt(3) == 0) {
               this.setTamedBy(player);
               this.sitGoal.setSitting(true);
               this.world.setEntityState(this, (byte)7);
            } else {
               this.world.setEntityState(this, (byte)6);
            }

            this.enablePersistence();
            return true;
         }

         boolean flag1 = super.processInteract(player, hand);
         if (flag1) {
            this.enablePersistence();
         }

         return flag1;
      }
   }

   /**
    * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
    * the animal type)
    */
   public boolean isBreedingItem(ItemStack stack) {
      return BREEDING_ITEMS.test(stack);
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return sizeIn.height * 0.5F;
   }

   public boolean canDespawn(double distanceToClosestPlayer) {
      return !this.isTamed() && this.ticksExisted > 2400;
   }

   protected void setupTamedAI() {
      if (this.avoidPlayerGoal == null) {
         this.avoidPlayerGoal = new CatEntity.AvoidPlayerGoal<>(this, PlayerEntity.class, 16.0F, 0.8D, 1.33D);
      }

      this.goalSelector.removeGoal(this.avoidPlayerGoal);
      if (!this.isTamed()) {
         this.goalSelector.addGoal(4, this.avoidPlayerGoal);
      }

   }

   static class AvoidPlayerGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final CatEntity cat;

      public AvoidPlayerGoal(CatEntity catIn, Class<T> entityClassToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
         super(catIn, entityClassToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn, EntityPredicates.CAN_AI_TARGET::test);
         this.cat = catIn;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return !this.cat.isTamed() && super.shouldExecute();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return !this.cat.isTamed() && super.shouldContinueExecuting();
      }
   }

   static class MorningGiftGoal extends Goal {
      private final CatEntity cat;
      private PlayerEntity owner;
      private BlockPos bedPos;
      private int tickCounter;

      public MorningGiftGoal(CatEntity catIn) {
         this.cat = catIn;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (!this.cat.isTamed()) {
            return false;
         } else if (this.cat.isSitting()) {
            return false;
         } else {
            LivingEntity livingentity = this.cat.getOwner();
            if (livingentity instanceof PlayerEntity) {
               this.owner = (PlayerEntity)livingentity;
               if (!livingentity.isSleeping()) {
                  return false;
               }

               if (this.cat.getDistanceSq(this.owner) > 100.0D) {
                  return false;
               }

               BlockPos blockpos = new BlockPos(this.owner);
               BlockState blockstate = this.cat.world.getBlockState(blockpos);
               if (blockstate.getBlock().isIn(BlockTags.BEDS)) {
                  Direction direction = blockstate.get(BedBlock.HORIZONTAL_FACING);
                  this.bedPos = new BlockPos(blockpos.getX() - direction.getXOffset(), blockpos.getY(), blockpos.getZ() - direction.getZOffset());
                  return !this.func_220805_g();
               }
            }

            return false;
         }
      }

      private boolean func_220805_g() {
         for(CatEntity catentity : this.cat.world.getEntitiesWithinAABB(CatEntity.class, (new AxisAlignedBB(this.bedPos)).grow(2.0D))) {
            if (catentity != this.cat && (catentity.func_213416_eg() || catentity.func_213409_eh())) {
               return true;
            }
         }

         return false;
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return this.cat.isTamed() && !this.cat.isSitting() && this.owner != null && this.owner.isSleeping() && this.bedPos != null && !this.func_220805_g();
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         if (this.bedPos != null) {
            this.cat.getAISit().setSitting(false);
            this.cat.getNavigator().tryMoveToXYZ((double)this.bedPos.getX(), (double)this.bedPos.getY(), (double)this.bedPos.getZ(), (double)1.1F);
         }

      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.cat.func_213419_u(false);
         float f = this.cat.world.getCelestialAngle(1.0F);
         if (this.owner.getSleepTimer() >= 100 && (double)f > 0.77D && (double)f < 0.8D && (double)this.cat.world.getRandom().nextFloat() < 0.7D) {
            this.func_220804_h();
         }

         this.tickCounter = 0;
         this.cat.func_213415_v(false);
         this.cat.getNavigator().clearPath();
      }

      private void func_220804_h() {
         Random random = this.cat.getRNG();
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
         blockpos$mutable.setPos(this.cat);
         this.cat.attemptTeleport((double)(blockpos$mutable.getX() + random.nextInt(11) - 5), (double)(blockpos$mutable.getY() + random.nextInt(5) - 2), (double)(blockpos$mutable.getZ() + random.nextInt(11) - 5), false);
         blockpos$mutable.setPos(this.cat);
         LootTable loottable = this.cat.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_CAT_MORNING_GIFT);
         LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.cat.world)).withParameter(LootParameters.POSITION, blockpos$mutable).withParameter(LootParameters.THIS_ENTITY, this.cat).withRandom(random);

         for(ItemStack itemstack : loottable.generate(lootcontext$builder.build(LootParameterSets.GIFT))) {
            this.cat.world.addEntity(new ItemEntity(this.cat.world, (double)((float)blockpos$mutable.getX() - MathHelper.sin(this.cat.renderYawOffset * ((float)Math.PI / 180F))), (double)blockpos$mutable.getY(), (double)((float)blockpos$mutable.getZ() + MathHelper.cos(this.cat.renderYawOffset * ((float)Math.PI / 180F))), itemstack));
         }

      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.owner != null && this.bedPos != null) {
            this.cat.getAISit().setSitting(false);
            this.cat.getNavigator().tryMoveToXYZ((double)this.bedPos.getX(), (double)this.bedPos.getY(), (double)this.bedPos.getZ(), (double)1.1F);
            if (this.cat.getDistanceSq(this.owner) < 2.5D) {
               ++this.tickCounter;
               if (this.tickCounter > 16) {
                  this.cat.func_213419_u(true);
                  this.cat.func_213415_v(false);
               } else {
                  this.cat.faceEntity(this.owner, 45.0F, 45.0F);
                  this.cat.func_213415_v(true);
               }
            } else {
               this.cat.func_213419_u(false);
            }
         }

      }
   }

   static class TemptGoal extends net.minecraft.entity.ai.goal.TemptGoal {
      @Nullable
      private PlayerEntity temptingPlayer;
      private final CatEntity cat;

      public TemptGoal(CatEntity catIn, double speedIn, Ingredient temptItemsIn, boolean scaredByPlayerMovementIn) {
         super(catIn, speedIn, temptItemsIn, scaredByPlayerMovementIn);
         this.cat = catIn;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         super.tick();
         if (this.temptingPlayer == null && this.creature.getRNG().nextInt(600) == 0) {
            this.temptingPlayer = this.closestPlayer;
         } else if (this.creature.getRNG().nextInt(500) == 0) {
            this.temptingPlayer = null;
         }

      }

      protected boolean isScaredByPlayerMovement() {
         return this.temptingPlayer != null && this.temptingPlayer.equals(this.closestPlayer) ? false : super.isScaredByPlayerMovement();
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && !this.cat.isTamed();
      }
   }
}