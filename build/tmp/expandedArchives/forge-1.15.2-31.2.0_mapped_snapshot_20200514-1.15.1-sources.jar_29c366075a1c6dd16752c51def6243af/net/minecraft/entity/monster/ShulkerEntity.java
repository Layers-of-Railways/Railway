package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
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

public class ShulkerEntity extends GolemEntity implements IMob {
   private static final UUID COVERED_ARMOR_BONUS_ID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
   private static final AttributeModifier COVERED_ARMOR_BONUS_MODIFIER = (new AttributeModifier(COVERED_ARMOR_BONUS_ID, "Covered armor bonus", 20.0D, AttributeModifier.Operation.ADDITION)).setSaved(false);
   protected static final DataParameter<Direction> ATTACHED_FACE = EntityDataManager.createKey(ShulkerEntity.class, DataSerializers.DIRECTION);
   protected static final DataParameter<Optional<BlockPos>> ATTACHED_BLOCK_POS = EntityDataManager.createKey(ShulkerEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
   protected static final DataParameter<Byte> PEEK_TICK = EntityDataManager.createKey(ShulkerEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Byte> COLOR = EntityDataManager.createKey(ShulkerEntity.class, DataSerializers.BYTE);
   private float prevPeekAmount;
   private float peekAmount;
   private BlockPos currentAttachmentPosition;
   private int clientSideTeleportInterpolation;

   public ShulkerEntity(EntityType<? extends ShulkerEntity> p_i50196_1_, World p_i50196_2_) {
      super(p_i50196_1_, p_i50196_2_);
      this.prevRenderYawOffset = 180.0F;
      this.renderYawOffset = 180.0F;
      this.currentAttachmentPosition = null;
      this.experienceValue = 5;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      this.renderYawOffset = 180.0F;
      this.prevRenderYawOffset = 180.0F;
      this.rotationYaw = 180.0F;
      this.prevRotationYaw = 180.0F;
      this.rotationYawHead = 180.0F;
      this.prevRotationYawHead = 180.0F;
      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(4, new ShulkerEntity.AttackGoal());
      this.goalSelector.addGoal(7, new ShulkerEntity.PeekGoal());
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
      this.targetSelector.addGoal(2, new ShulkerEntity.AttackNearestGoal(this));
      this.targetSelector.addGoal(3, new ShulkerEntity.DefenseAttackGoal(this));
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SHULKER_AMBIENT;
   }

   /**
    * Plays living's sound at its position
    */
   public void playAmbientSound() {
      if (!this.isClosed()) {
         super.playAmbientSound();
      }

   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SHULKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isClosed() ? SoundEvents.ENTITY_SHULKER_HURT_CLOSED : SoundEvents.ENTITY_SHULKER_HURT;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(ATTACHED_FACE, Direction.DOWN);
      this.dataManager.register(ATTACHED_BLOCK_POS, Optional.empty());
      this.dataManager.register(PEEK_TICK, (byte)0);
      this.dataManager.register(COLOR, (byte)16);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
   }

   protected BodyController createBodyController() {
      return new ShulkerEntity.BodyHelperController(this);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.dataManager.set(ATTACHED_FACE, Direction.byIndex(compound.getByte("AttachFace")));
      this.dataManager.set(PEEK_TICK, compound.getByte("Peek"));
      this.dataManager.set(COLOR, compound.getByte("Color"));
      if (compound.contains("APX")) {
         int i = compound.getInt("APX");
         int j = compound.getInt("APY");
         int k = compound.getInt("APZ");
         this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(new BlockPos(i, j, k)));
      } else {
         this.dataManager.set(ATTACHED_BLOCK_POS, Optional.empty());
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putByte("AttachFace", (byte)this.dataManager.get(ATTACHED_FACE).getIndex());
      compound.putByte("Peek", this.dataManager.get(PEEK_TICK));
      compound.putByte("Color", this.dataManager.get(COLOR));
      BlockPos blockpos = this.getAttachmentPos();
      if (blockpos != null) {
         compound.putInt("APX", blockpos.getX());
         compound.putInt("APY", blockpos.getY());
         compound.putInt("APZ", blockpos.getZ());
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      BlockPos blockpos = this.dataManager.get(ATTACHED_BLOCK_POS).orElse((BlockPos)null);
      if (blockpos == null && !this.world.isRemote) {
         blockpos = new BlockPos(this);
         this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
      }

      if (this.isPassenger()) {
         blockpos = null;
         float f = this.getRidingEntity().rotationYaw;
         this.rotationYaw = f;
         this.renderYawOffset = f;
         this.prevRenderYawOffset = f;
         this.clientSideTeleportInterpolation = 0;
      } else if (!this.world.isRemote) {
         BlockState blockstate = this.world.getBlockState(blockpos);
         if (!blockstate.isAir(this.world, blockpos)) {
            if (blockstate.getBlock() == Blocks.MOVING_PISTON) {
               Direction direction = blockstate.get(PistonBlock.FACING);
               if (this.world.isAirBlock(blockpos.offset(direction))) {
                  blockpos = blockpos.offset(direction);
                  this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
               } else {
                  this.tryTeleportToNewPosition();
               }
            } else if (blockstate.getBlock() == Blocks.PISTON_HEAD) {
               Direction direction2 = blockstate.get(PistonHeadBlock.FACING);
               if (this.world.isAirBlock(blockpos.offset(direction2))) {
                  blockpos = blockpos.offset(direction2);
                  this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
               } else {
                  this.tryTeleportToNewPosition();
               }
            } else {
               this.tryTeleportToNewPosition();
            }
         }

         BlockPos blockpos1 = blockpos.offset(this.getAttachmentFacing());
         if (!this.world.isTopSolid(blockpos1, this)) {
            boolean flag = false;

            for(Direction direction1 : Direction.values()) {
               blockpos1 = blockpos.offset(direction1);
               if (this.world.isTopSolid(blockpos1, this)) {
                  this.dataManager.set(ATTACHED_FACE, direction1);
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               this.tryTeleportToNewPosition();
            }
         }

         BlockPos blockpos2 = blockpos.offset(this.getAttachmentFacing().getOpposite());
         if (this.world.isTopSolid(blockpos2, this)) {
            this.tryTeleportToNewPosition();
         }
      }

      float f1 = (float)this.getPeekTick() * 0.01F;
      this.prevPeekAmount = this.peekAmount;
      if (this.peekAmount > f1) {
         this.peekAmount = MathHelper.clamp(this.peekAmount - 0.05F, f1, 1.0F);
      } else if (this.peekAmount < f1) {
         this.peekAmount = MathHelper.clamp(this.peekAmount + 0.05F, 0.0F, f1);
      }

      if (blockpos != null) {
         if (this.world.isRemote) {
            if (this.clientSideTeleportInterpolation > 0 && this.currentAttachmentPosition != null) {
               --this.clientSideTeleportInterpolation;
            } else {
               this.currentAttachmentPosition = blockpos;
            }
         }

         this.forceSetPosition((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
         double d0 = 0.5D - (double)MathHelper.sin((0.5F + this.peekAmount) * (float)Math.PI) * 0.5D;
         double d1 = 0.5D - (double)MathHelper.sin((0.5F + this.prevPeekAmount) * (float)Math.PI) * 0.5D;
         if (this.isAddedToWorld() && this.world instanceof net.minecraft.world.server.ServerWorld) ((net.minecraft.world.server.ServerWorld)this.world).chunkCheck(this); // Forge - Process chunk registration after moving.
         Direction direction3 = this.getAttachmentFacing().getOpposite();
         this.setBoundingBox((new AxisAlignedBB(this.getPosX() - 0.5D, this.getPosY(), this.getPosZ() - 0.5D, this.getPosX() + 0.5D, this.getPosY() + 1.0D, this.getPosZ() + 0.5D)).expand((double)direction3.getXOffset() * d0, (double)direction3.getYOffset() * d0, (double)direction3.getZOffset() * d0));
         double d2 = d0 - d1;
         if (d2 > 0.0D) {
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox());
            if (!list.isEmpty()) {
               for(Entity entity : list) {
                  if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
                     entity.move(MoverType.SHULKER, new Vec3d(d2 * (double)direction3.getXOffset(), d2 * (double)direction3.getYOffset(), d2 * (double)direction3.getZOffset()));
                  }
               }
            }
         }
      }

   }

   public void move(MoverType typeIn, Vec3d pos) {
      if (typeIn == MoverType.SHULKER_BOX) {
         this.tryTeleportToNewPosition();
      } else {
         super.move(typeIn, pos);
      }

   }

   /**
    * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
    */
   public void setPosition(double x, double y, double z) {
      super.setPosition(x, y, z);
      if (this.dataManager != null && this.ticksExisted != 0) {
         Optional<BlockPos> optional = this.dataManager.get(ATTACHED_BLOCK_POS);
         Optional<BlockPos> optional1 = Optional.of(new BlockPos(x, y, z));
         if (!optional1.equals(optional)) {
            this.dataManager.set(ATTACHED_BLOCK_POS, optional1);
            this.dataManager.set(PEEK_TICK, (byte)0);
            this.isAirBorne = true;
         }

      }
   }

   protected boolean tryTeleportToNewPosition() {
      if (!this.isAIDisabled() && this.isAlive()) {
         BlockPos blockpos = new BlockPos(this);

         for(int i = 0; i < 5; ++i) {
            BlockPos blockpos1 = blockpos.add(8 - this.rand.nextInt(17), 8 - this.rand.nextInt(17), 8 - this.rand.nextInt(17));
            if (blockpos1.getY() > 0 && this.world.isAirBlock(blockpos1) && this.world.getWorldBorder().contains(blockpos1) && this.world.hasNoCollisions(this, new AxisAlignedBB(blockpos1))) {
               boolean flag = false;

               for(Direction direction : Direction.values()) {
                  if (this.world.isTopSolid(blockpos1.offset(direction), this)) {
                     this.dataManager.set(ATTACHED_FACE, direction);
                     flag = true;
                     break;
                  }
               }

               if (flag) {
                  net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(this, blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), 0);
                  if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) flag = false;
                  blockpos1 = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
               }

               if (flag) {
                  this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
                  this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos1));
                  this.dataManager.set(PEEK_TICK, (byte)0);
                  this.setAttackTarget((LivingEntity)null);
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      this.setMotion(Vec3d.ZERO);
      this.prevRenderYawOffset = 180.0F;
      this.renderYawOffset = 180.0F;
      this.rotationYaw = 180.0F;
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (ATTACHED_BLOCK_POS.equals(key) && this.world.isRemote && !this.isPassenger()) {
         BlockPos blockpos = this.getAttachmentPos();
         if (blockpos != null) {
            if (this.currentAttachmentPosition == null) {
               this.currentAttachmentPosition = blockpos;
            } else {
               this.clientSideTeleportInterpolation = 6;
            }

            this.forceSetPosition((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
         }
      }

      super.notifyDataManagerChange(key);
   }

   /**
    * Sets a target for the client to interpolate towards over the next few ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      this.newPosRotationIncrements = 0;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isClosed()) {
         Entity entity = source.getImmediateSource();
         if (entity instanceof AbstractArrowEntity) {
            return false;
         }
      }

      if (super.attackEntityFrom(source, amount)) {
         if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5D && this.rand.nextInt(4) == 0) {
            this.tryTeleportToNewPosition();
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isClosed() {
      return this.getPeekTick() == 0;
   }

   /**
    * Returns the <b>solid</b> collision bounding box for this entity. Used to make (e.g.) boats solid. Return null if
    * this entity is not solid.
    *  
    * For general purposes, use {@link #width} and {@link #height}.
    *  
    * @see getEntityBoundingBox
    */
   @Nullable
   public AxisAlignedBB getCollisionBoundingBox() {
      return this.isAlive() ? this.getBoundingBox() : null;
   }

   public Direction getAttachmentFacing() {
      return this.dataManager.get(ATTACHED_FACE);
   }

   @Nullable
   public BlockPos getAttachmentPos() {
      return this.dataManager.get(ATTACHED_BLOCK_POS).orElse((BlockPos)null);
   }

   public void setAttachmentPos(@Nullable BlockPos pos) {
      this.dataManager.set(ATTACHED_BLOCK_POS, Optional.ofNullable(pos));
   }

   public int getPeekTick() {
      return this.dataManager.get(PEEK_TICK);
   }

   /**
    * Applies or removes armor modifier
    */
   public void updateArmorModifier(int p_184691_1_) {
      if (!this.world.isRemote) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(COVERED_ARMOR_BONUS_MODIFIER);
         if (p_184691_1_ == 0) {
            this.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier(COVERED_ARMOR_BONUS_MODIFIER);
            this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
         } else {
            this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
         }
      }

      this.dataManager.set(PEEK_TICK, (byte)p_184691_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getClientPeekAmount(float p_184688_1_) {
      return MathHelper.lerp(p_184688_1_, this.prevPeekAmount, this.peekAmount);
   }

   @OnlyIn(Dist.CLIENT)
   public int getClientTeleportInterp() {
      return this.clientSideTeleportInterpolation;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getOldAttachPos() {
      return this.currentAttachmentPosition;
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 0.5F;
   }

   /**
    * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
    * use in wolves.
    */
   public int getVerticalFaceSpeed() {
      return 180;
   }

   public int getHorizontalFaceSpeed() {
      return 180;
   }

   /**
    * Applies a velocity to the entities, to push them away from eachother.
    */
   public void applyEntityCollision(Entity entityIn) {
   }

   public float getCollisionBorderSize() {
      return 0.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAttachedToBlock() {
      return this.currentAttachmentPosition != null && this.getAttachmentPos() != null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public DyeColor getColor() {
      Byte obyte = this.dataManager.get(COLOR);
      return obyte != 16 && obyte <= 15 ? DyeColor.byId(obyte) : null;
   }

   class AttackGoal extends Goal {
      private int attackTime;

      public AttackGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         LivingEntity livingentity = ShulkerEntity.this.getAttackTarget();
         if (livingentity != null && livingentity.isAlive()) {
            return ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL;
         } else {
            return false;
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.attackTime = 20;
         ShulkerEntity.this.updateArmorModifier(100);
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         ShulkerEntity.this.updateArmorModifier(0);
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL) {
            --this.attackTime;
            LivingEntity livingentity = ShulkerEntity.this.getAttackTarget();
            ShulkerEntity.this.getLookController().setLookPositionWithEntity(livingentity, 180.0F, 180.0F);
            double d0 = ShulkerEntity.this.getDistanceSq(livingentity);
            if (d0 < 400.0D) {
               if (this.attackTime <= 0) {
                  this.attackTime = 20 + ShulkerEntity.this.rand.nextInt(10) * 20 / 2;
                  ShulkerEntity.this.world.addEntity(new ShulkerBulletEntity(ShulkerEntity.this.world, ShulkerEntity.this, livingentity, ShulkerEntity.this.getAttachmentFacing().getAxis()));
                  ShulkerEntity.this.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (ShulkerEntity.this.rand.nextFloat() - ShulkerEntity.this.rand.nextFloat()) * 0.2F + 1.0F);
               }
            } else {
               ShulkerEntity.this.setAttackTarget((LivingEntity)null);
            }

            super.tick();
         }
      }
   }

   class AttackNearestGoal extends NearestAttackableTargetGoal<PlayerEntity> {
      public AttackNearestGoal(ShulkerEntity shulker) {
         super(shulker, PlayerEntity.class, true);
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return ShulkerEntity.this.world.getDifficulty() == Difficulty.PEACEFUL ? false : super.shouldExecute();
      }

      protected AxisAlignedBB getTargetableArea(double targetDistance) {
         Direction direction = ((ShulkerEntity)this.goalOwner).getAttachmentFacing();
         if (direction.getAxis() == Direction.Axis.X) {
            return this.goalOwner.getBoundingBox().grow(4.0D, targetDistance, targetDistance);
         } else {
            return direction.getAxis() == Direction.Axis.Z ? this.goalOwner.getBoundingBox().grow(targetDistance, targetDistance, 4.0D) : this.goalOwner.getBoundingBox().grow(targetDistance, 4.0D, targetDistance);
         }
      }
   }

   class BodyHelperController extends BodyController {
      public BodyHelperController(MobEntity p_i50612_2_) {
         super(p_i50612_2_);
      }

      /**
       * Update the Head and Body rendenring angles
       */
      public void updateRenderAngles() {
      }
   }

   static class DefenseAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
      public DefenseAttackGoal(ShulkerEntity shulker) {
         super(shulker, LivingEntity.class, 10, true, false, (p_200826_0_) -> {
            return p_200826_0_ instanceof IMob;
         });
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return this.goalOwner.getTeam() == null ? false : super.shouldExecute();
      }

      protected AxisAlignedBB getTargetableArea(double targetDistance) {
         Direction direction = ((ShulkerEntity)this.goalOwner).getAttachmentFacing();
         if (direction.getAxis() == Direction.Axis.X) {
            return this.goalOwner.getBoundingBox().grow(4.0D, targetDistance, targetDistance);
         } else {
            return direction.getAxis() == Direction.Axis.Z ? this.goalOwner.getBoundingBox().grow(targetDistance, targetDistance, 4.0D) : this.goalOwner.getBoundingBox().grow(targetDistance, 4.0D, targetDistance);
         }
      }
   }

   class PeekGoal extends Goal {
      private int peekTime;

      private PeekGoal() {
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return ShulkerEntity.this.getAttackTarget() == null && ShulkerEntity.this.rand.nextInt(40) == 0;
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return ShulkerEntity.this.getAttackTarget() == null && this.peekTime > 0;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.peekTime = 20 * (1 + ShulkerEntity.this.rand.nextInt(3));
         ShulkerEntity.this.updateArmorModifier(30);
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         if (ShulkerEntity.this.getAttackTarget() == null) {
            ShulkerEntity.this.updateArmorModifier(0);
         }

      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         --this.peekTime;
      }
   }
}