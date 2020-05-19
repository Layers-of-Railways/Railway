package net.minecraft.entity.item;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class HangingEntity extends Entity {
   protected static final Predicate<Entity> IS_HANGING_ENTITY = (p_210144_0_) -> {
      return p_210144_0_ instanceof HangingEntity;
   };
   private int tickCounter1;
   protected BlockPos hangingPosition;
   /** The direction the entity is facing */
   protected Direction facingDirection = Direction.SOUTH;

   protected HangingEntity(EntityType<? extends HangingEntity> type, World p_i48561_2_) {
      super(type, p_i48561_2_);
   }

   protected HangingEntity(EntityType<? extends HangingEntity> type, World p_i48562_2_, BlockPos p_i48562_3_) {
      this(type, p_i48562_2_);
      this.hangingPosition = p_i48562_3_;
   }

   protected void registerData() {
   }

   /**
    * Updates facing and bounding box based on it
    */
   protected void updateFacingWithBoundingBox(Direction facingDirectionIn) {
      Validate.notNull(facingDirectionIn);
      Validate.isTrue(facingDirectionIn.getAxis().isHorizontal());
      this.facingDirection = facingDirectionIn;
      this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
      this.prevRotationYaw = this.rotationYaw;
      this.updateBoundingBox();
   }

   /**
    * Updates the entity bounding box based on current facing
    */
   protected void updateBoundingBox() {
      if (this.facingDirection != null) {
         double d0 = (double)this.hangingPosition.getX() + 0.5D;
         double d1 = (double)this.hangingPosition.getY() + 0.5D;
         double d2 = (double)this.hangingPosition.getZ() + 0.5D;
         double d3 = 0.46875D;
         double d4 = this.offs(this.getWidthPixels());
         double d5 = this.offs(this.getHeightPixels());
         d0 = d0 - (double)this.facingDirection.getXOffset() * 0.46875D;
         d2 = d2 - (double)this.facingDirection.getZOffset() * 0.46875D;
         d1 = d1 + d5;
         Direction direction = this.facingDirection.rotateYCCW();
         d0 = d0 + d4 * (double)direction.getXOffset();
         d2 = d2 + d4 * (double)direction.getZOffset();
         this.setRawPosition(d0, d1, d2);
         double d6 = (double)this.getWidthPixels();
         double d7 = (double)this.getHeightPixels();
         double d8 = (double)this.getWidthPixels();
         if (this.facingDirection.getAxis() == Direction.Axis.Z) {
            d8 = 1.0D;
         } else {
            d6 = 1.0D;
         }

         d6 = d6 / 32.0D;
         d7 = d7 / 32.0D;
         d8 = d8 / 32.0D;
         this.setBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
      }
   }

   private double offs(int p_190202_1_) {
      return p_190202_1_ % 32 == 0 ? 0.5D : 0.0D;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (this.tickCounter1++ == 100 && !this.world.isRemote) {
         this.tickCounter1 = 0;
         if (!this.removed && !this.onValidSurface()) {
            this.remove();
            this.onBroken((Entity)null);
         }
      }

   }

   /**
    * checks to make sure painting can be placed there
    */
   public boolean onValidSurface() {
      if (!this.world.hasNoCollisions(this)) {
         return false;
      } else {
         int i = Math.max(1, this.getWidthPixels() / 16);
         int j = Math.max(1, this.getHeightPixels() / 16);
         BlockPos blockpos = this.hangingPosition.offset(this.facingDirection.getOpposite());
         Direction direction = this.facingDirection.rotateYCCW();
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(int k = 0; k < i; ++k) {
            for(int l = 0; l < j; ++l) {
               int i1 = (i - 1) / -2;
               int j1 = (j - 1) / -2;
               blockpos$mutable.setPos(blockpos).move(direction, k + i1).move(Direction.UP, l + j1);
               BlockState blockstate = this.world.getBlockState(blockpos$mutable);
               if (net.minecraft.block.Block.hasEnoughSolidSide(this.world, blockpos$mutable, this.facingDirection))
                  continue;
               if (!blockstate.getMaterial().isSolid() && !RedstoneDiodeBlock.isDiode(blockstate)) {
                  return false;
               }
            }
         }

         return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), IS_HANGING_ENTITY).isEmpty();
      }
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return true;
   }

   /**
    * Called when a player attacks an entity. If this returns true the attack will not happen.
    */
   public boolean hitByEntity(Entity entityIn) {
      if (entityIn instanceof PlayerEntity) {
         PlayerEntity playerentity = (PlayerEntity)entityIn;
         return !this.world.isBlockModifiable(playerentity, this.hangingPosition) ? true : this.attackEntityFrom(DamageSource.causePlayerDamage(playerentity), 0.0F);
      } else {
         return false;
      }
   }

   /**
    * Gets the horizontal facing direction of this Entity.
    */
   public Direction getHorizontalFacing() {
      return this.facingDirection;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         if (!this.removed && !this.world.isRemote) {
            this.remove();
            this.markVelocityChanged();
            this.onBroken(source.getTrueSource());
         }

         return true;
      }
   }

   public void move(MoverType typeIn, Vec3d pos) {
      if (!this.world.isRemote && !this.removed && pos.lengthSquared() > 0.0D) {
         this.remove();
         this.onBroken((Entity)null);
      }

   }

   /**
    * Adds to the current velocity of the entity, and sets {@link #isAirBorne} to true.
    */
   public void addVelocity(double x, double y, double z) {
      if (!this.world.isRemote && !this.removed && x * x + y * y + z * z > 0.0D) {
         this.remove();
         this.onBroken((Entity)null);
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      compound.putByte("Facing", (byte)this.facingDirection.getHorizontalIndex());
      BlockPos blockpos = this.getHangingPosition();
      compound.putInt("TileX", blockpos.getX());
      compound.putInt("TileY", blockpos.getY());
      compound.putInt("TileZ", blockpos.getZ());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      this.hangingPosition = new BlockPos(compound.getInt("TileX"), compound.getInt("TileY"), compound.getInt("TileZ"));
      this.facingDirection = Direction.byHorizontalIndex(compound.getByte("Facing"));
   }

   public abstract int getWidthPixels();

   public abstract int getHeightPixels();

   /**
    * Called when this entity is broken. Entity parameter may be null.
    */
   public abstract void onBroken(@Nullable Entity brokenEntity);

   public abstract void playPlaceSound();

   /**
    * Drops an item at the position of the entity.
    */
   public ItemEntity entityDropItem(ItemStack stack, float offsetY) {
      ItemEntity itementity = new ItemEntity(this.world, this.getPosX() + (double)((float)this.facingDirection.getXOffset() * 0.15F), this.getPosY() + (double)offsetY, this.getPosZ() + (double)((float)this.facingDirection.getZOffset() * 0.15F), stack);
      itementity.setDefaultPickupDelay();
      this.world.addEntity(itementity);
      return itementity;
   }

   protected boolean shouldSetPosAfterLoading() {
      return false;
   }

   /**
    * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
    */
   public void setPosition(double x, double y, double z) {
      this.hangingPosition = new BlockPos(x, y, z);
      this.updateBoundingBox();
      this.isAirBorne = true;
   }

   public BlockPos getHangingPosition() {
      return this.hangingPosition;
   }

   /**
    * Transforms the entity's current yaw with the given Rotation and returns it. This does not have a side-effect.
    */
   public float getRotatedYaw(Rotation transformRotation) {
      if (this.facingDirection.getAxis() != Direction.Axis.Y) {
         switch(transformRotation) {
         case CLOCKWISE_180:
            this.facingDirection = this.facingDirection.getOpposite();
            break;
         case COUNTERCLOCKWISE_90:
            this.facingDirection = this.facingDirection.rotateYCCW();
            break;
         case CLOCKWISE_90:
            this.facingDirection = this.facingDirection.rotateY();
         }
      }

      float f = MathHelper.wrapDegrees(this.rotationYaw);
      switch(transformRotation) {
      case CLOCKWISE_180:
         return f + 180.0F;
      case COUNTERCLOCKWISE_90:
         return f + 90.0F;
      case CLOCKWISE_90:
         return f + 270.0F;
      default:
         return f;
      }
   }

   /**
    * Transforms the entity's current yaw with the given Mirror and returns it. This does not have a side-effect.
    */
   public float getMirroredYaw(Mirror transformMirror) {
      return this.getRotatedYaw(transformMirror.toRotation(this.facingDirection));
   }

   /**
    * Called when a lightning bolt hits the entity.
    */
   public void onStruckByLightning(LightningBoltEntity lightningBolt) {
   }

   public void recalculateSize() {
   }
}