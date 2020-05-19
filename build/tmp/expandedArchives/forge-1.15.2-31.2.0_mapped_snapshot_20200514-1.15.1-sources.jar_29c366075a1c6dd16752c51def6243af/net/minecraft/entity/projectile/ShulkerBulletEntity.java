package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShulkerBulletEntity extends Entity {
   private LivingEntity owner;
   private Entity target;
   @Nullable
   private Direction direction;
   private int steps;
   private double targetDeltaX;
   private double targetDeltaY;
   private double targetDeltaZ;
   @Nullable
   private UUID ownerUniqueId;
   private BlockPos ownerBlockPos;
   @Nullable
   private UUID targetUniqueId;
   private BlockPos targetBlockPos;

   public ShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> p_i50161_1_, World p_i50161_2_) {
      super(p_i50161_1_, p_i50161_2_);
      this.noClip = true;
   }

   @OnlyIn(Dist.CLIENT)
   public ShulkerBulletEntity(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
      this(EntityType.SHULKER_BULLET, worldIn);
      this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
      this.setMotion(motionXIn, motionYIn, motionZIn);
   }

   public ShulkerBulletEntity(World worldIn, LivingEntity ownerIn, Entity targetIn, Direction.Axis p_i46772_4_) {
      this(EntityType.SHULKER_BULLET, worldIn);
      this.owner = ownerIn;
      BlockPos blockpos = new BlockPos(ownerIn);
      double d0 = (double)blockpos.getX() + 0.5D;
      double d1 = (double)blockpos.getY() + 0.5D;
      double d2 = (double)blockpos.getZ() + 0.5D;
      this.setLocationAndAngles(d0, d1, d2, this.rotationYaw, this.rotationPitch);
      this.target = targetIn;
      this.direction = Direction.UP;
      this.selectNextMoveDirection(p_i46772_4_);
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected void writeAdditional(CompoundNBT compound) {
      if (this.owner != null) {
         BlockPos blockpos = new BlockPos(this.owner);
         CompoundNBT compoundnbt = NBTUtil.writeUniqueId(this.owner.getUniqueID());
         compoundnbt.putInt("X", blockpos.getX());
         compoundnbt.putInt("Y", blockpos.getY());
         compoundnbt.putInt("Z", blockpos.getZ());
         compound.put("Owner", compoundnbt);
      }

      if (this.target != null) {
         BlockPos blockpos1 = new BlockPos(this.target);
         CompoundNBT compoundnbt1 = NBTUtil.writeUniqueId(this.target.getUniqueID());
         compoundnbt1.putInt("X", blockpos1.getX());
         compoundnbt1.putInt("Y", blockpos1.getY());
         compoundnbt1.putInt("Z", blockpos1.getZ());
         compound.put("Target", compoundnbt1);
      }

      if (this.direction != null) {
         compound.putInt("Dir", this.direction.getIndex());
      }

      compound.putInt("Steps", this.steps);
      compound.putDouble("TXD", this.targetDeltaX);
      compound.putDouble("TYD", this.targetDeltaY);
      compound.putDouble("TZD", this.targetDeltaZ);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(CompoundNBT compound) {
      this.steps = compound.getInt("Steps");
      this.targetDeltaX = compound.getDouble("TXD");
      this.targetDeltaY = compound.getDouble("TYD");
      this.targetDeltaZ = compound.getDouble("TZD");
      if (compound.contains("Dir", 99)) {
         this.direction = Direction.byIndex(compound.getInt("Dir"));
      }

      if (compound.contains("Owner", 10)) {
         CompoundNBT compoundnbt = compound.getCompound("Owner");
         this.ownerUniqueId = NBTUtil.readUniqueId(compoundnbt);
         this.ownerBlockPos = new BlockPos(compoundnbt.getInt("X"), compoundnbt.getInt("Y"), compoundnbt.getInt("Z"));
      }

      if (compound.contains("Target", 10)) {
         CompoundNBT compoundnbt1 = compound.getCompound("Target");
         this.targetUniqueId = NBTUtil.readUniqueId(compoundnbt1);
         this.targetBlockPos = new BlockPos(compoundnbt1.getInt("X"), compoundnbt1.getInt("Y"), compoundnbt1.getInt("Z"));
      }

   }

   protected void registerData() {
   }

   private void setDirection(@Nullable Direction directionIn) {
      this.direction = directionIn;
   }

   private void selectNextMoveDirection(@Nullable Direction.Axis p_184569_1_) {
      double d0 = 0.5D;
      BlockPos blockpos;
      if (this.target == null) {
         blockpos = (new BlockPos(this)).down();
      } else {
         d0 = (double)this.target.getHeight() * 0.5D;
         blockpos = new BlockPos(this.target.getPosX(), this.target.getPosY() + d0, this.target.getPosZ());
      }

      double d1 = (double)blockpos.getX() + 0.5D;
      double d2 = (double)blockpos.getY() + d0;
      double d3 = (double)blockpos.getZ() + 0.5D;
      Direction direction = null;
      if (!blockpos.withinDistance(this.getPositionVec(), 2.0D)) {
         BlockPos blockpos1 = new BlockPos(this);
         List<Direction> list = Lists.newArrayList();
         if (p_184569_1_ != Direction.Axis.X) {
            if (blockpos1.getX() < blockpos.getX() && this.world.isAirBlock(blockpos1.east())) {
               list.add(Direction.EAST);
            } else if (blockpos1.getX() > blockpos.getX() && this.world.isAirBlock(blockpos1.west())) {
               list.add(Direction.WEST);
            }
         }

         if (p_184569_1_ != Direction.Axis.Y) {
            if (blockpos1.getY() < blockpos.getY() && this.world.isAirBlock(blockpos1.up())) {
               list.add(Direction.UP);
            } else if (blockpos1.getY() > blockpos.getY() && this.world.isAirBlock(blockpos1.down())) {
               list.add(Direction.DOWN);
            }
         }

         if (p_184569_1_ != Direction.Axis.Z) {
            if (blockpos1.getZ() < blockpos.getZ() && this.world.isAirBlock(blockpos1.south())) {
               list.add(Direction.SOUTH);
            } else if (blockpos1.getZ() > blockpos.getZ() && this.world.isAirBlock(blockpos1.north())) {
               list.add(Direction.NORTH);
            }
         }

         direction = Direction.random(this.rand);
         if (list.isEmpty()) {
            for(int i = 5; !this.world.isAirBlock(blockpos1.offset(direction)) && i > 0; --i) {
               direction = Direction.random(this.rand);
            }
         } else {
            direction = list.get(this.rand.nextInt(list.size()));
         }

         d1 = this.getPosX() + (double)direction.getXOffset();
         d2 = this.getPosY() + (double)direction.getYOffset();
         d3 = this.getPosZ() + (double)direction.getZOffset();
      }

      this.setDirection(direction);
      double d6 = d1 - this.getPosX();
      double d7 = d2 - this.getPosY();
      double d4 = d3 - this.getPosZ();
      double d5 = (double)MathHelper.sqrt(d6 * d6 + d7 * d7 + d4 * d4);
      if (d5 == 0.0D) {
         this.targetDeltaX = 0.0D;
         this.targetDeltaY = 0.0D;
         this.targetDeltaZ = 0.0D;
      } else {
         this.targetDeltaX = d6 / d5 * 0.15D;
         this.targetDeltaY = d7 / d5 * 0.15D;
         this.targetDeltaZ = d4 / d5 * 0.15D;
      }

      this.isAirBorne = true;
      this.steps = 10 + this.rand.nextInt(5) * 10;
   }

   /**
    * Makes the entity despawn if requirements are reached
    */
   public void checkDespawn() {
      if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (!this.world.isRemote) {
         if (this.target == null && this.targetUniqueId != null) {
            for(LivingEntity livingentity : this.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(this.targetBlockPos.add(-2, -2, -2), this.targetBlockPos.add(2, 2, 2)))) {
               if (livingentity.getUniqueID().equals(this.targetUniqueId)) {
                  this.target = livingentity;
                  break;
               }
            }

            this.targetUniqueId = null;
         }

         if (this.owner == null && this.ownerUniqueId != null) {
            for(LivingEntity livingentity1 : this.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(this.ownerBlockPos.add(-2, -2, -2), this.ownerBlockPos.add(2, 2, 2)))) {
               if (livingentity1.getUniqueID().equals(this.ownerUniqueId)) {
                  this.owner = livingentity1;
                  break;
               }
            }

            this.ownerUniqueId = null;
         }

         if (this.target == null || !this.target.isAlive() || this.target instanceof PlayerEntity && ((PlayerEntity)this.target).isSpectator()) {
            if (!this.hasNoGravity()) {
               this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
            }
         } else {
            this.targetDeltaX = MathHelper.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
            this.targetDeltaY = MathHelper.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
            this.targetDeltaZ = MathHelper.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
            Vec3d vec3d = this.getMotion();
            this.setMotion(vec3d.add((this.targetDeltaX - vec3d.x) * 0.2D, (this.targetDeltaY - vec3d.y) * 0.2D, (this.targetDeltaZ - vec3d.z) * 0.2D));
         }

         RayTraceResult raytraceresult = ProjectileHelper.rayTrace(this, true, false, this.owner, RayTraceContext.BlockMode.COLLIDER);
            if (raytraceresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.bulletHit(raytraceresult);
         }
      }

      Vec3d vec3d1 = this.getMotion();
      this.setPosition(this.getPosX() + vec3d1.x, this.getPosY() + vec3d1.y, this.getPosZ() + vec3d1.z);
      ProjectileHelper.rotateTowardsMovement(this, 0.5F);
      if (this.world.isRemote) {
         this.world.addParticle(ParticleTypes.END_ROD, this.getPosX() - vec3d1.x, this.getPosY() - vec3d1.y + 0.15D, this.getPosZ() - vec3d1.z, 0.0D, 0.0D, 0.0D);
      } else if (this.target != null && !this.target.removed) {
         if (this.steps > 0) {
            --this.steps;
            if (this.steps == 0) {
               this.selectNextMoveDirection(this.direction == null ? null : this.direction.getAxis());
            }
         }

         if (this.direction != null) {
            BlockPos blockpos1 = new BlockPos(this);
            Direction.Axis direction$axis = this.direction.getAxis();
            if (this.world.isTopSolid(blockpos1.offset(this.direction), this)) {
               this.selectNextMoveDirection(direction$axis);
            } else {
               BlockPos blockpos = new BlockPos(this.target);
               if (direction$axis == Direction.Axis.X && blockpos1.getX() == blockpos.getX() || direction$axis == Direction.Axis.Z && blockpos1.getZ() == blockpos.getZ() || direction$axis == Direction.Axis.Y && blockpos1.getY() == blockpos.getY()) {
                  this.selectNextMoveDirection(direction$axis);
               }
            }
         }
      }

   }

   /**
    * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
    */
   public boolean isBurning() {
      return false;
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return distance < 16384.0D;
   }

   /**
    * Gets how bright this entity is.
    */
   public float getBrightness() {
      return 1.0F;
   }

   protected void bulletHit(RayTraceResult result) {
      if (result.getType() == RayTraceResult.Type.ENTITY) {
         Entity entity = ((EntityRayTraceResult)result).getEntity();
         boolean flag = entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.owner).setProjectile(), 4.0F);
         if (flag) {
            this.applyEnchantments(this.owner, entity);
            if (entity instanceof LivingEntity) {
               ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.LEVITATION, 200));
            }
         }
      } else {
         ((ServerWorld)this.world).spawnParticle(ParticleTypes.EXPLOSION, this.getPosX(), this.getPosY(), this.getPosZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
         this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HIT, 1.0F, 1.0F);
      }

      this.remove();
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return true;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!this.world.isRemote) {
         this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0F, 1.0F);
         ((ServerWorld)this.world).spawnParticle(ParticleTypes.CRIT, this.getPosX(), this.getPosY(), this.getPosZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
         this.remove();
      }

      return true;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }
}