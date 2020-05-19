package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class TNTEntity extends Entity {
   private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(TNTEntity.class, DataSerializers.VARINT);
   @Nullable
   private LivingEntity tntPlacedBy;
   private int fuse = 80;

   public TNTEntity(EntityType<? extends TNTEntity> type, World worldIn) {
      super(type, worldIn);
      this.preventEntitySpawning = true;
   }

   public TNTEntity(World worldIn, double x, double y, double z, @Nullable LivingEntity igniter) {
      this(EntityType.TNT, worldIn);
      this.setPosition(x, y, z);
      double d0 = worldIn.rand.nextDouble() * (double)((float)Math.PI * 2F);
      this.setMotion(-Math.sin(d0) * 0.02D, (double)0.2F, -Math.cos(d0) * 0.02D);
      this.setFuse(80);
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
      this.tntPlacedBy = igniter;
   }

   protected void registerData() {
      this.dataManager.register(FUSE, 80);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return !this.removed;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (!this.hasNoGravity()) {
         this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
      }

      this.move(MoverType.SELF, this.getMotion());
      this.setMotion(this.getMotion().scale(0.98D));
      if (this.onGround) {
         this.setMotion(this.getMotion().mul(0.7D, -0.5D, 0.7D));
      }

      --this.fuse;
      if (this.fuse <= 0) {
         this.remove();
         if (!this.world.isRemote) {
            this.explode();
         }
      } else {
         this.handleWaterMovement();
         if (this.world.isRemote) {
            this.world.addParticle(ParticleTypes.SMOKE, this.getPosX(), this.getPosY() + 0.5D, this.getPosZ(), 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void explode() {
      float f = 4.0F;
      this.world.createExplosion(this, this.getPosX(), this.getPosYHeight(0.0625D), this.getPosZ(), 4.0F, Explosion.Mode.BREAK);
   }

   protected void writeAdditional(CompoundNBT compound) {
      compound.putShort("Fuse", (short)this.getFuse());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(CompoundNBT compound) {
      this.setFuse(compound.getShort("Fuse"));
   }

   /**
    * returns null or the entityliving it was placed or ignited by
    */
   @Nullable
   public LivingEntity getTntPlacedBy() {
      return this.tntPlacedBy;
   }

   protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 0.0F;
   }

   public void setFuse(int fuseIn) {
      this.dataManager.set(FUSE, fuseIn);
      this.fuse = fuseIn;
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (FUSE.equals(key)) {
         this.fuse = this.getFuseDataManager();
      }

   }

   /**
    * Gets the fuse from the data manager
    */
   public int getFuseDataManager() {
      return this.dataManager.get(FUSE);
   }

   public int getFuse() {
      return this.fuse;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }
}