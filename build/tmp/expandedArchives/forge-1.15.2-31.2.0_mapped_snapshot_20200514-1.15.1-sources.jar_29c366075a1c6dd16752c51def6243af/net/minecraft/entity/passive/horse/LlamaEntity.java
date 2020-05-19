package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LlamaEntity extends AbstractChestedHorseEntity implements IRangedAttackMob {
   private static final DataParameter<Integer> DATA_STRENGTH_ID = EntityDataManager.createKey(LlamaEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> DATA_COLOR_ID = EntityDataManager.createKey(LlamaEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> DATA_VARIANT_ID = EntityDataManager.createKey(LlamaEntity.class, DataSerializers.VARINT);
   private boolean didSpit;
   @Nullable
   private LlamaEntity caravanHead;
   @Nullable
   private LlamaEntity caravanTail;

   public LlamaEntity(EntityType<? extends LlamaEntity> type, World worldIn) {
      super(type, worldIn);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isTraderLlama() {
      return false;
   }

   private void setStrength(int strengthIn) {
      this.dataManager.set(DATA_STRENGTH_ID, Math.max(1, Math.min(5, strengthIn)));
   }

   private void setRandomStrength() {
      int i = this.rand.nextFloat() < 0.04F ? 5 : 3;
      this.setStrength(1 + this.rand.nextInt(i));
   }

   public int getStrength() {
      return this.dataManager.get(DATA_STRENGTH_ID);
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("Variant", this.getVariant());
      compound.putInt("Strength", this.getStrength());
      if (!this.horseChest.getStackInSlot(1).isEmpty()) {
         compound.put("DecorItem", this.horseChest.getStackInSlot(1).write(new CompoundNBT()));
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      this.setStrength(compound.getInt("Strength"));
      super.readAdditional(compound);
      this.setVariant(compound.getInt("Variant"));
      if (compound.contains("DecorItem", 10)) {
         this.horseChest.setInventorySlotContents(1, ItemStack.read(compound.getCompound("DecorItem")));
      }

      this.updateHorseSlots();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
      this.goalSelector.addGoal(2, new LlamaFollowCaravanGoal(this, (double)2.1F));
      this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25D, 40, 20.0F));
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.7D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new LlamaEntity.HurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new LlamaEntity.DefendTargetGoal(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(DATA_STRENGTH_ID, 0);
      this.dataManager.register(DATA_COLOR_ID, -1);
      this.dataManager.register(DATA_VARIANT_ID, 0);
   }

   public int getVariant() {
      return MathHelper.clamp(this.dataManager.get(DATA_VARIANT_ID), 0, 3);
   }

   public void setVariant(int variantIn) {
      this.dataManager.set(DATA_VARIANT_ID, variantIn);
   }

   protected int getInventorySize() {
      return this.hasChest() ? 2 + 3 * this.getInventoryColumns() : super.getInventorySize();
   }

   public void updatePassenger(Entity passenger) {
      if (this.isPassenger(passenger)) {
         float f = MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180F));
         float f1 = MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180F));
         float f2 = 0.3F;
         passenger.setPosition(this.getPosX() + (double)(0.3F * f1), this.getPosY() + this.getMountedYOffset() + passenger.getYOffset(), this.getPosZ() - (double)(0.3F * f));
      }
   }

   /**
    * Returns the Y offset from the entity's position for any entity riding this one.
    */
   public double getMountedYOffset() {
      return (double)this.getHeight() * 0.67D;
   }

   /**
    * returns true if all the conditions for steering the entity are met. For pigs, this is true if it is being ridden
    * by a player and the player is holding a carrot-on-a-stick
    */
   public boolean canBeSteered() {
      return false;
   }

   protected boolean handleEating(PlayerEntity player, ItemStack stack) {
      int i = 0;
      int j = 0;
      float f = 0.0F;
      boolean flag = false;
      Item item = stack.getItem();
      if (item == Items.WHEAT) {
         i = 10;
         j = 3;
         f = 2.0F;
      } else if (item == Blocks.HAY_BLOCK.asItem()) {
         i = 90;
         j = 6;
         f = 10.0F;
         if (this.isTame() && this.getGrowingAge() == 0 && this.canBreed()) {
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

      if (flag && !this.isSilent()) {
         this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LLAMA_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      }

      return flag;
   }

   /**
    * Dead and sleeping entities cannot move
    */
   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F || this.isEatingHaystack();
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      this.setRandomStrength();
      int i;
      if (spawnDataIn instanceof LlamaEntity.LlamaData) {
         i = ((LlamaEntity.LlamaData)spawnDataIn).variant;
      } else {
         i = this.rand.nextInt(4);
         spawnDataIn = new LlamaEntity.LlamaData(i);
      }

      this.setVariant(i);
      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.ENTITY_LLAMA_ANGRY;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_LLAMA_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_LLAMA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_LLAMA_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_LLAMA_STEP, 0.15F, 1.0F);
   }

   protected void playChestEquipSound() {
      this.playSound(SoundEvents.ENTITY_LLAMA_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
   }

   public void makeMad() {
      SoundEvent soundevent = this.getAngrySound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   public int getInventoryColumns() {
      return this.getStrength();
   }

   public boolean wearsArmor() {
      return true;
   }

   public boolean isArmor(ItemStack stack) {
      Item item = stack.getItem();
      return ItemTags.CARPETS.contains(item);
   }

   public boolean canBeSaddled() {
      return false;
   }

   /**
    * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
    */
   public void onInventoryChanged(IInventory invBasic) {
      DyeColor dyecolor = this.getColor();
      super.onInventoryChanged(invBasic);
      DyeColor dyecolor1 = this.getColor();
      if (this.ticksExisted > 20 && dyecolor1 != null && dyecolor1 != dyecolor) {
         this.playSound(SoundEvents.ENTITY_LLAMA_SWAG, 0.5F, 1.0F);
      }

   }

   /**
    * Updates the items in the saddle and armor slots of the horse's inventory.
    */
   protected void updateHorseSlots() {
      if (!this.world.isRemote) {
         super.updateHorseSlots();
         this.setColor(getCarpetColor(this.horseChest.getStackInSlot(1)));
      }
   }

   private void setColor(@Nullable DyeColor color) {
      this.dataManager.set(DATA_COLOR_ID, color == null ? -1 : color.getId());
   }

   @Nullable
   private static DyeColor getCarpetColor(ItemStack p_195403_0_) {
      Block block = Block.getBlockFromItem(p_195403_0_.getItem());
      return block instanceof CarpetBlock ? ((CarpetBlock)block).getColor() : null;
   }

   @Nullable
   public DyeColor getColor() {
      int i = this.dataManager.get(DATA_COLOR_ID);
      return i == -1 ? null : DyeColor.byId(i);
   }

   public int getMaxTemper() {
      return 30;
   }

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
   public boolean canMateWith(AnimalEntity otherAnimal) {
      return otherAnimal != this && otherAnimal instanceof LlamaEntity && this.canMate() && ((LlamaEntity)otherAnimal).canMate();
   }

   public LlamaEntity createChild(AgeableEntity ageable) {
      LlamaEntity llamaentity = this.createChild();
      this.setOffspringAttributes(ageable, llamaentity);
      LlamaEntity llamaentity1 = (LlamaEntity)ageable;
      int i = this.rand.nextInt(Math.max(this.getStrength(), llamaentity1.getStrength())) + 1;
      if (this.rand.nextFloat() < 0.03F) {
         ++i;
      }

      llamaentity.setStrength(i);
      llamaentity.setVariant(this.rand.nextBoolean() ? this.getVariant() : llamaentity1.getVariant());
      return llamaentity;
   }

   protected LlamaEntity createChild() {
      return EntityType.LLAMA.create(this.world);
   }

   private void spit(LivingEntity target) {
      LlamaSpitEntity llamaspitentity = new LlamaSpitEntity(this.world, this);
      double d0 = target.getPosX() - this.getPosX();
      double d1 = target.getPosYHeight(0.3333333333333333D) - llamaspitentity.getPosY();
      double d2 = target.getPosZ() - this.getPosZ();
      float f = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
      llamaspitentity.shoot(d0, d1 + (double)f, d2, 1.5F, 10.0F);
      this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LLAMA_SPIT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      this.world.addEntity(llamaspitentity);
      this.didSpit = true;
   }

   private void setDidSpit(boolean didSpitIn) {
      this.didSpit = didSpitIn;
   }

   public boolean onLivingFall(float distance, float damageMultiplier) {
      int i = this.calculateFallDamage(distance, damageMultiplier);
      if (i <= 0) {
         return false;
      } else {
         if (distance >= 6.0F) {
            this.attackEntityFrom(DamageSource.FALL, (float)i);
            if (this.isBeingRidden()) {
               for(Entity entity : this.getRecursivePassengers()) {
                  entity.attackEntityFrom(DamageSource.FALL, (float)i);
               }
            }
         }

         this.playFallSound();
         return true;
      }
   }

   public void leaveCaravan() {
      if (this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(LlamaEntity caravanHeadIn) {
      this.caravanHead = caravanHeadIn;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTrail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   @Nullable
   public LlamaEntity getCaravanHead() {
      return this.caravanHead;
   }

   protected double followLeashSpeed() {
      return 2.0D;
   }

   protected void followMother() {
      if (!this.inCaravan() && this.isChild()) {
         super.followMother();
      }

   }

   public boolean canEatGrass() {
      return false;
   }

   /**
    * Attack the specified entity using a ranged attack.
    */
   public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
      this.spit(target);
   }

   static class DefendTargetGoal extends NearestAttackableTargetGoal<WolfEntity> {
      public DefendTargetGoal(LlamaEntity llama) {
         super(llama, WolfEntity.class, 16, false, true, (p_220789_0_) -> {
            return !((WolfEntity)p_220789_0_).isTamed();
         });
      }

      protected double getTargetDistance() {
         return super.getTargetDistance() * 0.25D;
      }
   }

   static class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
      public HurtByTargetGoal(LlamaEntity llama) {
         super(llama);
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         if (this.goalOwner instanceof LlamaEntity) {
            LlamaEntity llamaentity = (LlamaEntity)this.goalOwner;
            if (llamaentity.didSpit) {
               llamaentity.setDidSpit(false);
               return false;
            }
         }

         return super.shouldContinueExecuting();
      }
   }

   static class LlamaData extends AgeableEntity.AgeableData {
      public final int variant;

      private LlamaData(int variantIn) {
         this.variant = variantIn;
      }
   }
}