package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PandaEntity extends AnimalEntity {
   private static final DataParameter<Integer> UNHAPPY_COUNTER = EntityDataManager.createKey(PandaEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> SNEEZE_COUNTER = EntityDataManager.createKey(PandaEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> EAT_COUNTER = EntityDataManager.createKey(PandaEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Byte> MAIN_GENE = EntityDataManager.createKey(PandaEntity.class, DataSerializers.BYTE);
   private static final DataParameter<Byte> HIDDEN_GENE = EntityDataManager.createKey(PandaEntity.class, DataSerializers.BYTE);
   private static final DataParameter<Byte> PANDA_FLAGS = EntityDataManager.createKey(PandaEntity.class, DataSerializers.BYTE);
   private static final EntityPredicate BREED_TARGETING = (new EntityPredicate()).setDistance(8.0D).allowFriendlyFire().allowInvulnerable();
   private boolean gotBamboo;
   private boolean didBite;
   public int rollCounter;
   private Vec3d rollDelta;
   private float sitAmount;
   private float sitAmountO;
   private float onBackAmount;
   private float onBackAmountO;
   private float rollAmount;
   private float rollAmountO;
   private PandaEntity.WatchGoal watchGoal;
   private static final Predicate<ItemEntity> PANDA_ITEMS = (p_213575_0_) -> {
      Item item = p_213575_0_.getItem().getItem();
      return (item == Blocks.BAMBOO.asItem() || item == Blocks.CAKE.asItem()) && p_213575_0_.isAlive() && !p_213575_0_.cannotPickup();
   };

   public PandaEntity(EntityType<? extends PandaEntity> type, World worldIn) {
      super(type, worldIn);
      this.moveController = new PandaEntity.MoveHelperController(this);
      if (!this.isChild()) {
         this.setCanPickUpLoot(true);
      }

   }

   public boolean canPickUpItem(ItemStack itemstackIn) {
      EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstackIn);
      if (!this.getItemStackFromSlot(equipmentslottype).isEmpty()) {
         return false;
      } else {
         return equipmentslottype == EquipmentSlotType.MAINHAND && super.canPickUpItem(itemstackIn);
      }
   }

   public int getUnhappyCounter() {
      return this.dataManager.get(UNHAPPY_COUNTER);
   }

   public void setUnhappyCounter(int p_213588_1_) {
      this.dataManager.set(UNHAPPY_COUNTER, p_213588_1_);
   }

   public boolean func_213539_dW() {
      return this.getPandaFlag(2);
   }

   public boolean func_213556_dX() {
      return this.getPandaFlag(8);
   }

   public void func_213553_r(boolean p_213553_1_) {
      this.setPandaFlag(8, p_213553_1_);
   }

   public boolean func_213567_dY() {
      return this.getPandaFlag(16);
   }

   public void func_213542_s(boolean p_213542_1_) {
      this.setPandaFlag(16, p_213542_1_);
   }

   public boolean func_213578_dZ() {
      return this.dataManager.get(EAT_COUNTER) > 0;
   }

   public void func_213534_t(boolean p_213534_1_) {
      this.dataManager.set(EAT_COUNTER, p_213534_1_ ? 1 : 0);
   }

   private int getEatCounter() {
      return this.dataManager.get(EAT_COUNTER);
   }

   private void setEatCounter(int p_213571_1_) {
      this.dataManager.set(EAT_COUNTER, p_213571_1_);
   }

   public void func_213581_u(boolean p_213581_1_) {
      this.setPandaFlag(2, p_213581_1_);
      if (!p_213581_1_) {
         this.setSneezeCounter(0);
      }

   }

   public int getSneezeCounter() {
      return this.dataManager.get(SNEEZE_COUNTER);
   }

   public void setSneezeCounter(int p_213562_1_) {
      this.dataManager.set(SNEEZE_COUNTER, p_213562_1_);
   }

   public PandaEntity.Type getMainGene() {
      return PandaEntity.Type.byIndex(this.dataManager.get(MAIN_GENE));
   }

   public void setMainGene(PandaEntity.Type pandaType) {
      if (pandaType.getIndex() > 6) {
         pandaType = PandaEntity.Type.getRandomType(this.rand);
      }

      this.dataManager.set(MAIN_GENE, (byte)pandaType.getIndex());
   }

   public PandaEntity.Type getHiddenGene() {
      return PandaEntity.Type.byIndex(this.dataManager.get(HIDDEN_GENE));
   }

   public void setHiddenGene(PandaEntity.Type pandaType) {
      if (pandaType.getIndex() > 6) {
         pandaType = PandaEntity.Type.getRandomType(this.rand);
      }

      this.dataManager.set(HIDDEN_GENE, (byte)pandaType.getIndex());
   }

   public boolean func_213564_eh() {
      return this.getPandaFlag(4);
   }

   public void func_213576_v(boolean p_213576_1_) {
      this.setPandaFlag(4, p_213576_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(UNHAPPY_COUNTER, 0);
      this.dataManager.register(SNEEZE_COUNTER, 0);
      this.dataManager.register(MAIN_GENE, (byte)0);
      this.dataManager.register(HIDDEN_GENE, (byte)0);
      this.dataManager.register(PANDA_FLAGS, (byte)0);
      this.dataManager.register(EAT_COUNTER, 0);
   }

   private boolean getPandaFlag(int flagId) {
      return (this.dataManager.get(PANDA_FLAGS) & flagId) != 0;
   }

   private void setPandaFlag(int flagId, boolean p_213587_2_) {
      byte b0 = this.dataManager.get(PANDA_FLAGS);
      if (p_213587_2_) {
         this.dataManager.set(PANDA_FLAGS, (byte)(b0 | flagId));
      } else {
         this.dataManager.set(PANDA_FLAGS, (byte)(b0 & ~flagId));
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putString("MainGene", this.getMainGene().getName());
      compound.putString("HiddenGene", this.getHiddenGene().getName());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setMainGene(PandaEntity.Type.byName(compound.getString("MainGene")));
      this.setHiddenGene(PandaEntity.Type.byName(compound.getString("HiddenGene")));
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity ageable) {
      PandaEntity pandaentity = EntityType.PANDA.create(this.world);
      if (ageable instanceof PandaEntity) {
         pandaentity.getGenesForChildFromParents(this, (PandaEntity)ageable);
      }

      pandaentity.setAttributes();
      return pandaentity;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(2, new PandaEntity.PanicGoal(this, 2.0D));
      this.goalSelector.addGoal(2, new PandaEntity.MateGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new PandaEntity.AttackGoal(this, (double)1.2F, true));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.fromItems(Blocks.BAMBOO.asItem()), false));
      this.goalSelector.addGoal(6, new PandaEntity.AvoidGoal<>(this, PlayerEntity.class, 8.0F, 2.0D, 2.0D));
      this.goalSelector.addGoal(6, new PandaEntity.AvoidGoal<>(this, MonsterEntity.class, 4.0F, 2.0D, 2.0D));
      this.goalSelector.addGoal(7, new PandaEntity.SitGoal());
      this.goalSelector.addGoal(8, new PandaEntity.LieBackGoal(this));
      this.goalSelector.addGoal(8, new PandaEntity.ChildPlayGoal(this));
      this.watchGoal = new PandaEntity.WatchGoal(this, PlayerEntity.class, 6.0F);
      this.goalSelector.addGoal(9, this.watchGoal);
      this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
      this.goalSelector.addGoal(12, new PandaEntity.RollGoal(this));
      this.goalSelector.addGoal(13, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(14, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new PandaEntity.RevengeGoal(this)).setCallsForHelp(new Class[0]));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.15F);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
   }

   public PandaEntity.Type func_213590_ei() {
      return PandaEntity.Type.func_221101_b(this.getMainGene(), this.getHiddenGene());
   }

   public boolean isLazy() {
      return this.func_213590_ei() == PandaEntity.Type.LAZY;
   }

   public boolean isWorried() {
      return this.func_213590_ei() == PandaEntity.Type.WORRIED;
   }

   public boolean isPlayful() {
      return this.func_213590_ei() == PandaEntity.Type.PLAYFUL;
   }

   public boolean isWeak() {
      return this.func_213590_ei() == PandaEntity.Type.WEAK;
   }

   public boolean isAggressive() {
      return this.func_213590_ei() == PandaEntity.Type.AGGRESSIVE;
   }

   public boolean canBeLeashedTo(PlayerEntity player) {
      return false;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      this.playSound(SoundEvents.ENTITY_PANDA_BITE, 1.0F, 1.0F);
      if (!this.isAggressive()) {
         this.didBite = true;
      }

      return super.attackEntityAsMob(entityIn);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.isWorried()) {
         if (this.world.isThundering() && !this.isInWater()) {
            this.func_213553_r(true);
            this.func_213534_t(false);
         } else if (!this.func_213578_dZ()) {
            this.func_213553_r(false);
         }
      }

      if (this.getAttackTarget() == null) {
         this.gotBamboo = false;
         this.didBite = false;
      }

      if (this.getUnhappyCounter() > 0) {
         if (this.getAttackTarget() != null) {
            this.faceEntity(this.getAttackTarget(), 90.0F, 90.0F);
         }

         if (this.getUnhappyCounter() == 29 || this.getUnhappyCounter() == 14) {
            this.playSound(SoundEvents.ENTITY_PANDA_CANT_BREED, 1.0F, 1.0F);
         }

         this.setUnhappyCounter(this.getUnhappyCounter() - 1);
      }

      if (this.func_213539_dW()) {
         this.setSneezeCounter(this.getSneezeCounter() + 1);
         if (this.getSneezeCounter() > 20) {
            this.func_213581_u(false);
            this.func_213577_ez();
         } else if (this.getSneezeCounter() == 1) {
            this.playSound(SoundEvents.ENTITY_PANDA_PRE_SNEEZE, 1.0F, 1.0F);
         }
      }

      if (this.func_213564_eh()) {
         this.func_213535_ey();
      } else {
         this.rollCounter = 0;
      }

      if (this.func_213556_dX()) {
         this.rotationPitch = 0.0F;
      }

      this.func_213574_ev();
      this.func_213546_et();
      this.func_213563_ew();
      this.func_213550_ex();
   }

   public boolean func_213566_eo() {
      return this.isWorried() && this.world.isThundering();
   }

   private void func_213546_et() {
      if (!this.func_213578_dZ() && this.func_213556_dX() && !this.func_213566_eo() && !this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() && this.rand.nextInt(80) == 1) {
         this.func_213534_t(true);
      } else if (this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() || !this.func_213556_dX()) {
         this.func_213534_t(false);
      }

      if (this.func_213578_dZ()) {
         this.func_213533_eu();
         if (!this.world.isRemote && this.getEatCounter() > 80 && this.rand.nextInt(20) == 1) {
            if (this.getEatCounter() > 100 && this.isBreedingItemOrCake(this.getItemStackFromSlot(EquipmentSlotType.MAINHAND))) {
               if (!this.world.isRemote) {
                  this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
               }

               this.func_213553_r(false);
            }

            this.func_213534_t(false);
            return;
         }

         this.setEatCounter(this.getEatCounter() + 1);
      }

   }

   private void func_213533_eu() {
      if (this.getEatCounter() % 5 == 0) {
         this.playSound(SoundEvents.ENTITY_PANDA_EAT, 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);

         for(int i = 0; i < 6; ++i) {
            Vec3d vec3d = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double)this.rand.nextFloat() - 0.5D) * 0.1D);
            vec3d = vec3d.rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F));
            vec3d = vec3d.rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
            double d0 = (double)(-this.rand.nextFloat()) * 0.6D - 0.3D;
            Vec3d vec3d1 = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.8D, d0, 1.0D + ((double)this.rand.nextFloat() - 0.5D) * 0.4D);
            vec3d1 = vec3d1.rotateYaw(-this.renderYawOffset * ((float)Math.PI / 180F));
            vec3d1 = vec3d1.add(this.getPosX(), this.getPosYEye() + 1.0D, this.getPosZ());
            this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItemStackFromSlot(EquipmentSlotType.MAINHAND)), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
         }
      }

   }

   private void func_213574_ev() {
      this.sitAmountO = this.sitAmount;
      if (this.func_213556_dX()) {
         this.sitAmount = Math.min(1.0F, this.sitAmount + 0.15F);
      } else {
         this.sitAmount = Math.max(0.0F, this.sitAmount - 0.19F);
      }

   }

   private void func_213563_ew() {
      this.onBackAmountO = this.onBackAmount;
      if (this.func_213567_dY()) {
         this.onBackAmount = Math.min(1.0F, this.onBackAmount + 0.15F);
      } else {
         this.onBackAmount = Math.max(0.0F, this.onBackAmount - 0.19F);
      }

   }

   private void func_213550_ex() {
      this.rollAmountO = this.rollAmount;
      if (this.func_213564_eh()) {
         this.rollAmount = Math.min(1.0F, this.rollAmount + 0.15F);
      } else {
         this.rollAmount = Math.max(0.0F, this.rollAmount - 0.19F);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float func_213561_v(float p_213561_1_) {
      return MathHelper.lerp(p_213561_1_, this.sitAmountO, this.sitAmount);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213583_w(float p_213583_1_) {
      return MathHelper.lerp(p_213583_1_, this.onBackAmountO, this.onBackAmount);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213591_x(float p_213591_1_) {
      return MathHelper.lerp(p_213591_1_, this.rollAmountO, this.rollAmount);
   }

   private void func_213535_ey() {
      ++this.rollCounter;
      if (this.rollCounter > 32) {
         this.func_213576_v(false);
      } else {
         if (!this.world.isRemote) {
            Vec3d vec3d = this.getMotion();
            if (this.rollCounter == 1) {
               float f = this.rotationYaw * ((float)Math.PI / 180F);
               float f1 = this.isChild() ? 0.1F : 0.2F;
               this.rollDelta = new Vec3d(vec3d.x + (double)(-MathHelper.sin(f) * f1), 0.0D, vec3d.z + (double)(MathHelper.cos(f) * f1));
               this.setMotion(this.rollDelta.add(0.0D, 0.27D, 0.0D));
            } else if ((float)this.rollCounter != 7.0F && (float)this.rollCounter != 15.0F && (float)this.rollCounter != 23.0F) {
               this.setMotion(this.rollDelta.x, vec3d.y, this.rollDelta.z);
            } else {
               this.setMotion(0.0D, this.onGround ? 0.27D : vec3d.y, 0.0D);
            }
         }

      }
   }

   private void func_213577_ez() {
      Vec3d vec3d = this.getMotion();
      this.world.addParticle(ParticleTypes.SNEEZE, this.getPosX() - (double)(this.getWidth() + 1.0F) * 0.5D * (double)MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180F)), this.getPosYEye() - (double)0.1F, this.getPosZ() + (double)(this.getWidth() + 1.0F) * 0.5D * (double)MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180F)), vec3d.x, 0.0D, vec3d.z);
      this.playSound(SoundEvents.ENTITY_PANDA_SNEEZE, 1.0F, 1.0F);

      for(PandaEntity pandaentity : this.world.getEntitiesWithinAABB(PandaEntity.class, this.getBoundingBox().grow(10.0D))) {
         if (!pandaentity.isChild() && pandaentity.onGround && !pandaentity.isInWater() && pandaentity.canPerformAction()) {
            pandaentity.jump();
         }
      }

      if (!this.world.isRemote() && this.rand.nextInt(700) == 0 && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
         this.entityDropItem(Items.SLIME_BALL);
      }

   }

   /**
    * Tests if this entity should pickup a weapon or an armor. Entity drops current weapon or armor if the new one is
    * better.
    */
   protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
      if (this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() && PANDA_ITEMS.test(itemEntity)) {
         ItemStack itemstack = itemEntity.getItem();
         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
         this.inventoryHandsDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
         this.onItemPickup(itemEntity, itemstack.getCount());
         itemEntity.remove();
      }

   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      this.func_213553_r(false);
      return super.attackEntityFrom(source, amount);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      this.setMainGene(PandaEntity.Type.getRandomType(this.rand));
      this.setHiddenGene(PandaEntity.Type.getRandomType(this.rand));
      this.setAttributes();
      if (spawnDataIn == null) {
         spawnDataIn = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)spawnDataIn).func_226258_a_(0.2F);
      }

      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public void getGenesForChildFromParents(PandaEntity father, @Nullable PandaEntity mother) {
      if (mother == null) {
         if (this.rand.nextBoolean()) {
            this.setMainGene(father.getOneOfGenesRandomly());
            this.setHiddenGene(PandaEntity.Type.getRandomType(this.rand));
         } else {
            this.setMainGene(PandaEntity.Type.getRandomType(this.rand));
            this.setHiddenGene(father.getOneOfGenesRandomly());
         }
      } else if (this.rand.nextBoolean()) {
         this.setMainGene(father.getOneOfGenesRandomly());
         this.setHiddenGene(mother.getOneOfGenesRandomly());
      } else {
         this.setMainGene(mother.getOneOfGenesRandomly());
         this.setHiddenGene(father.getOneOfGenesRandomly());
      }

      if (this.rand.nextInt(32) == 0) {
         this.setMainGene(PandaEntity.Type.getRandomType(this.rand));
      }

      if (this.rand.nextInt(32) == 0) {
         this.setHiddenGene(PandaEntity.Type.getRandomType(this.rand));
      }

   }

   private PandaEntity.Type getOneOfGenesRandomly() {
      return this.rand.nextBoolean() ? this.getMainGene() : this.getHiddenGene();
   }

   public void setAttributes() {
      if (this.isWeak()) {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      }

      if (this.isLazy()) {
         this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.07F);
      }

   }

   private void tryToSit() {
      if (!this.isInWater()) {
         this.setMoveForward(0.0F);
         this.getNavigator().clearPath();
         this.func_213553_r(true);
      }

   }

   public boolean processInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() instanceof SpawnEggItem) {
         return super.processInteract(player, hand);
      } else if (this.func_213566_eo()) {
         return false;
      } else if (this.func_213567_dY()) {
         this.func_213542_s(false);
         return true;
      } else if (this.isBreedingItem(itemstack)) {
         if (this.getAttackTarget() != null) {
            this.gotBamboo = true;
         }

         if (this.isChild()) {
            this.consumeItemFromStack(player, itemstack);
            this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
         } else if (!this.world.isRemote && this.getGrowingAge() == 0 && this.canBreed()) {
            this.consumeItemFromStack(player, itemstack);
            this.setInLove(player);
         } else {
            if (this.world.isRemote || this.func_213556_dX() || this.isInWater()) {
               return false;
            }

            this.tryToSit();
            this.func_213534_t(true);
            ItemStack itemstack1 = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            if (!itemstack1.isEmpty() && !player.abilities.isCreativeMode) {
               this.entityDropItem(itemstack1);
            }

            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(itemstack.getItem(), 1));
            this.consumeItemFromStack(player, itemstack);
         }

         player.swing(hand, true);
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isAggressive()) {
         return SoundEvents.ENTITY_PANDA_AGGRESSIVE_AMBIENT;
      } else {
         return this.isWorried() ? SoundEvents.ENTITY_PANDA_WORRIED_AMBIENT : SoundEvents.ENTITY_PANDA_AMBIENT;
      }
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_PANDA_STEP, 0.15F, 1.0F);
   }

   /**
    * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
    * the animal type)
    */
   public boolean isBreedingItem(ItemStack stack) {
      return stack.getItem() == Blocks.BAMBOO.asItem();
   }

   private boolean isBreedingItemOrCake(ItemStack stack) {
      return this.isBreedingItem(stack) || stack.getItem() == Blocks.CAKE.asItem();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PANDA_DEATH;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_PANDA_HURT;
   }

   public boolean canPerformAction() {
      return !this.func_213567_dY() && !this.func_213566_eo() && !this.func_213578_dZ() && !this.func_213564_eh() && !this.func_213556_dX();
   }

   static class AttackGoal extends MeleeAttackGoal {
      private final PandaEntity panda;

      public AttackGoal(PandaEntity pandaIn, double speedIn, boolean useLongMemory) {
         super(pandaIn, speedIn, useLongMemory);
         this.panda = pandaIn;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return this.panda.canPerformAction() && super.shouldExecute();
      }
   }

   static class AvoidGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final PandaEntity panda;

      public AvoidGoal(PandaEntity pandaIn, Class<T> entityClassToAvoidIn, float distance, double nearSpeedIn, double farSpeedIn) {
         super(pandaIn, entityClassToAvoidIn, distance, nearSpeedIn, farSpeedIn, EntityPredicates.NOT_SPECTATING::test);
         this.panda = pandaIn;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return this.panda.isWorried() && this.panda.canPerformAction() && super.shouldExecute();
      }
   }

   static class ChildPlayGoal extends Goal {
      private final PandaEntity panda;

      public ChildPlayGoal(PandaEntity pandaIn) {
         this.panda = pandaIn;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (this.panda.isChild() && this.panda.canPerformAction()) {
            if (this.panda.isWeak() && this.panda.rand.nextInt(500) == 1) {
               return true;
            } else {
               return this.panda.rand.nextInt(6000) == 1;
            }
         } else {
            return false;
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return false;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.panda.func_213581_u(true);
      }
   }

   static class LieBackGoal extends Goal {
      private final PandaEntity panda;
      private int field_220829_b;

      public LieBackGoal(PandaEntity pandaIn) {
         this.panda = pandaIn;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return this.field_220829_b < this.panda.ticksExisted && this.panda.isLazy() && this.panda.canPerformAction() && this.panda.rand.nextInt(400) == 1;
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         if (!this.panda.isInWater() && (this.panda.isLazy() || this.panda.rand.nextInt(600) != 1)) {
            return this.panda.rand.nextInt(2000) != 1;
         } else {
            return false;
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.panda.func_213542_s(true);
         this.field_220829_b = 0;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.panda.func_213542_s(false);
         this.field_220829_b = this.panda.ticksExisted + 200;
      }
   }

   class MateGoal extends BreedGoal {
      private final PandaEntity panda;
      private int field_220694_f;

      public MateGoal(PandaEntity pandaIn, double speedIn) {
         super(pandaIn, speedIn);
         this.panda = pandaIn;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (super.shouldExecute() && this.panda.getUnhappyCounter() == 0) {
            if (!this.func_220691_h()) {
               if (this.field_220694_f <= this.panda.ticksExisted) {
                  this.panda.setUnhappyCounter(32);
                  this.field_220694_f = this.panda.ticksExisted + 600;
                  if (this.panda.isServerWorld()) {
                     PlayerEntity playerentity = this.world.getClosestPlayer(PandaEntity.BREED_TARGETING, this.panda);
                     this.panda.watchGoal.func_229975_a_(playerentity);
                  }
               }

               return false;
            } else {
               return true;
            }
         } else {
            return false;
         }
      }

      private boolean func_220691_h() {
         BlockPos blockpos = new BlockPos(this.panda);
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 8; ++j) {
               for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                  for(int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                     blockpos$mutable.setPos(blockpos).move(k, i, l);
                     if (this.world.getBlockState(blockpos$mutable).getBlock() == Blocks.BAMBOO) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   static class MoveHelperController extends MovementController {
      private final PandaEntity panda;

      public MoveHelperController(PandaEntity pandaIn) {
         super(pandaIn);
         this.panda = pandaIn;
      }

      public void tick() {
         if (this.panda.canPerformAction()) {
            super.tick();
         }
      }
   }

   static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      private final PandaEntity panda;

      public PanicGoal(PandaEntity pandaIn, double speedIn) {
         super(pandaIn, speedIn);
         this.panda = pandaIn;
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (!this.panda.isBurning()) {
            return false;
         } else {
            BlockPos blockpos = this.getRandPos(this.creature.world, this.creature, 5, 4);
            if (blockpos != null) {
               this.randPosX = (double)blockpos.getX();
               this.randPosY = (double)blockpos.getY();
               this.randPosZ = (double)blockpos.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         if (this.panda.func_213556_dX()) {
            this.panda.getNavigator().clearPath();
            return false;
         } else {
            return super.shouldContinueExecuting();
         }
      }
   }

   static class RevengeGoal extends HurtByTargetGoal {
      private final PandaEntity panda;

      public RevengeGoal(PandaEntity pandaIn, Class<?>... p_i51462_2_) {
         super(pandaIn, p_i51462_2_);
         this.panda = pandaIn;
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         if (!this.panda.gotBamboo && !this.panda.didBite) {
            return super.shouldContinueExecuting();
         } else {
            this.panda.setAttackTarget((LivingEntity)null);
            return false;
         }
      }

      protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
         if (mobIn instanceof PandaEntity && ((PandaEntity)mobIn).isAggressive()) {
            mobIn.setAttackTarget(targetIn);
         }

      }
   }

   static class RollGoal extends Goal {
      private final PandaEntity panda;

      public RollGoal(PandaEntity pandaIn) {
         this.panda = pandaIn;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if ((this.panda.isChild() || this.panda.isPlayful()) && this.panda.onGround) {
            if (!this.panda.canPerformAction()) {
               return false;
            } else {
               float f = this.panda.rotationYaw * ((float)Math.PI / 180F);
               int i = 0;
               int j = 0;
               float f1 = -MathHelper.sin(f);
               float f2 = MathHelper.cos(f);
               if ((double)Math.abs(f1) > 0.5D) {
                  i = (int)((float)i + f1 / Math.abs(f1));
               }

               if ((double)Math.abs(f2) > 0.5D) {
                  j = (int)((float)j + f2 / Math.abs(f2));
               }

               if (this.panda.world.getBlockState((new BlockPos(this.panda)).add(i, -1, j)).isAir()) {
                  return true;
               } else if (this.panda.isPlayful() && this.panda.rand.nextInt(60) == 1) {
                  return true;
               } else {
                  return this.panda.rand.nextInt(500) == 1;
               }
            }
         } else {
            return false;
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return false;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.panda.func_213576_v(true);
      }

      public boolean isPreemptible() {
         return false;
      }
   }

   class SitGoal extends Goal {
      private int field_220832_b;

      public SitGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (this.field_220832_b <= PandaEntity.this.ticksExisted && !PandaEntity.this.isChild() && !PandaEntity.this.isInWater() && PandaEntity.this.canPerformAction() && PandaEntity.this.getUnhappyCounter() <= 0) {
            List<ItemEntity> list = PandaEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, PandaEntity.this.getBoundingBox().grow(6.0D, 6.0D, 6.0D), PandaEntity.PANDA_ITEMS);
            return !list.isEmpty() || !PandaEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty();
         } else {
            return false;
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         if (!PandaEntity.this.isInWater() && (PandaEntity.this.isLazy() || PandaEntity.this.rand.nextInt(600) != 1)) {
            return PandaEntity.this.rand.nextInt(2000) != 1;
         } else {
            return false;
         }
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (!PandaEntity.this.func_213556_dX() && !PandaEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
            PandaEntity.this.tryToSit();
         }

      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         List<ItemEntity> list = PandaEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, PandaEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), PandaEntity.PANDA_ITEMS);
         if (!list.isEmpty() && PandaEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
            PandaEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), (double)1.2F);
         } else if (!PandaEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
            PandaEntity.this.tryToSit();
         }

         this.field_220832_b = 0;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         ItemStack itemstack = PandaEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
         if (!itemstack.isEmpty()) {
            PandaEntity.this.entityDropItem(itemstack);
            PandaEntity.this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            int i = PandaEntity.this.isLazy() ? PandaEntity.this.rand.nextInt(50) + 10 : PandaEntity.this.rand.nextInt(150) + 10;
            this.field_220832_b = PandaEntity.this.ticksExisted + i * 20;
         }

         PandaEntity.this.func_213553_r(false);
      }
   }

   public static enum Type {
      NORMAL(0, "normal", false),
      LAZY(1, "lazy", false),
      WORRIED(2, "worried", false),
      PLAYFUL(3, "playful", false),
      BROWN(4, "brown", true),
      WEAK(5, "weak", true),
      AGGRESSIVE(6, "aggressive", false);

      private static final PandaEntity.Type[] field_221109_h = Arrays.stream(values()).sorted(Comparator.comparingInt(PandaEntity.Type::getIndex)).toArray((p_221102_0_) -> {
         return new PandaEntity.Type[p_221102_0_];
      });
      private final int index;
      private final String name;
      private final boolean field_221112_k;

      private Type(int p_i51468_3_, String p_i51468_4_, boolean p_i51468_5_) {
         this.index = p_i51468_3_;
         this.name = p_i51468_4_;
         this.field_221112_k = p_i51468_5_;
      }

      public int getIndex() {
         return this.index;
      }

      public String getName() {
         return this.name;
      }

      public boolean func_221107_c() {
         return this.field_221112_k;
      }

      private static PandaEntity.Type func_221101_b(PandaEntity.Type mainGene, PandaEntity.Type hiddenGene) {
         if (mainGene.func_221107_c()) {
            return mainGene == hiddenGene ? mainGene : NORMAL;
         } else {
            return mainGene;
         }
      }

      public static PandaEntity.Type byIndex(int indexIn) {
         if (indexIn < 0 || indexIn >= field_221109_h.length) {
            indexIn = 0;
         }

         return field_221109_h[indexIn];
      }

      public static PandaEntity.Type byName(String p_221108_0_) {
         for(PandaEntity.Type pandaentity$type : values()) {
            if (pandaentity$type.name.equals(p_221108_0_)) {
               return pandaentity$type;
            }
         }

         return NORMAL;
      }

      public static PandaEntity.Type getRandomType(Random randIn) {
         int i = randIn.nextInt(16);
         if (i == 0) {
            return LAZY;
         } else if (i == 1) {
            return WORRIED;
         } else if (i == 2) {
            return PLAYFUL;
         } else if (i == 4) {
            return AGGRESSIVE;
         } else if (i < 9) {
            return WEAK;
         } else {
            return i < 11 ? BROWN : NORMAL;
         }
      }
   }

   static class WatchGoal extends LookAtGoal {
      private final PandaEntity field_220718_f;

      public WatchGoal(PandaEntity p_i51458_1_, Class<? extends LivingEntity> p_i51458_2_, float p_i51458_3_) {
         super(p_i51458_1_, p_i51458_2_, p_i51458_3_);
         this.field_220718_f = p_i51458_1_;
      }

      public void func_229975_a_(LivingEntity p_229975_1_) {
         this.closestEntity = p_229975_1_;
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return this.closestEntity != null && super.shouldContinueExecuting();
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (this.entity.getRNG().nextFloat() >= this.chance) {
            return false;
         } else {
            if (this.closestEntity == null) {
               if (this.watchedClass == PlayerEntity.class) {
                  this.closestEntity = this.entity.world.getClosestPlayer(this.field_220716_e, this.entity, this.entity.getPosX(), this.entity.getPosYEye(), this.entity.getPosZ());
               } else {
                  this.closestEntity = this.entity.world.func_225318_b(this.watchedClass, this.field_220716_e, this.entity, this.entity.getPosX(), this.entity.getPosYEye(), this.entity.getPosZ(), this.entity.getBoundingBox().grow((double)this.maxDistance, 3.0D, (double)this.maxDistance));
               }
            }

            return this.field_220718_f.canPerformAction() && this.closestEntity != null;
         }
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.closestEntity != null) {
            super.tick();
         }

      }
   }
}