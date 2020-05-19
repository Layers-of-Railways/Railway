package net.minecraft.entity.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class DamagingProjectileEntity extends Entity {
   public LivingEntity shootingEntity;
   private int ticksAlive;
   private int ticksInAir;
   public double accelerationX;
   public double accelerationY;
   public double accelerationZ;

   protected DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> p_i50173_1_, World p_i50173_2_) {
      super(p_i50173_1_, p_i50173_2_);
   }

   public DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> p_i50174_1_, double p_i50174_2_, double p_i50174_4_, double p_i50174_6_, double p_i50174_8_, double p_i50174_10_, double p_i50174_12_, World p_i50174_14_) {
      this(p_i50174_1_, p_i50174_14_);
      this.setLocationAndAngles(p_i50174_2_, p_i50174_4_, p_i50174_6_, this.rotationYaw, this.rotationPitch);
      this.setPosition(p_i50174_2_, p_i50174_4_, p_i50174_6_);
      double d0 = (double)MathHelper.sqrt(p_i50174_8_ * p_i50174_8_ + p_i50174_10_ * p_i50174_10_ + p_i50174_12_ * p_i50174_12_);
      this.accelerationX = p_i50174_8_ / d0 * 0.1D;
      this.accelerationY = p_i50174_10_ / d0 * 0.1D;
      this.accelerationZ = p_i50174_12_ / d0 * 0.1D;
   }

   public DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> p_i50175_1_, LivingEntity p_i50175_2_, double p_i50175_3_, double p_i50175_5_, double p_i50175_7_, World p_i50175_9_) {
      this(p_i50175_1_, p_i50175_9_);
      this.shootingEntity = p_i50175_2_;
      this.setLocationAndAngles(p_i50175_2_.getPosX(), p_i50175_2_.getPosY(), p_i50175_2_.getPosZ(), p_i50175_2_.rotationYaw, p_i50175_2_.rotationPitch);
      this.recenterBoundingBox();
      this.setMotion(Vec3d.ZERO);
      p_i50175_3_ = p_i50175_3_ + this.rand.nextGaussian() * 0.4D;
      p_i50175_5_ = p_i50175_5_ + this.rand.nextGaussian() * 0.4D;
      p_i50175_7_ = p_i50175_7_ + this.rand.nextGaussian() * 0.4D;
      double d0 = (double)MathHelper.sqrt(p_i50175_3_ * p_i50175_3_ + p_i50175_5_ * p_i50175_5_ + p_i50175_7_ * p_i50175_7_);
      this.accelerationX = p_i50175_3_ / d0 * 0.1D;
      this.accelerationY = p_i50175_5_ / d0 * 0.1D;
      this.accelerationZ = p_i50175_7_ / d0 * 0.1D;
   }

   protected void registerData() {
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 = d0 * 64.0D;
      return distance < d0 * d0;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (this.world.isRemote || (this.shootingEntity == null || !this.shootingEntity.removed) && this.world.isBlockLoaded(new BlockPos(this))) {
         super.tick();
         if (this.isFireballFiery()) {
            this.setFire(1);
         }

         ++this.ticksInAir;
         RayTraceResult raytraceresult = ProjectileHelper.rayTrace(this, true, this.ticksInAir >= 25, this.shootingEntity, RayTraceContext.BlockMode.COLLIDER);
         if (raytraceresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onImpact(raytraceresult);
         }

         Vec3d vec3d = this.getMotion();
         double d0 = this.getPosX() + vec3d.x;
         double d1 = this.getPosY() + vec3d.y;
         double d2 = this.getPosZ() + vec3d.z;
         ProjectileHelper.rotateTowardsMovement(this, 0.2F);
         float f = this.getMotionFactor();
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               float f1 = 0.25F;
               this.world.addParticle(ParticleTypes.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
            }

            f = 0.8F;
         }

         this.setMotion(vec3d.add(this.accelerationX, this.accelerationY, this.accelerationZ).scale((double)f));
         this.world.addParticle(this.getParticle(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
         this.setPosition(d0, d1, d2);
      } else {
         this.remove();
      }
   }

   protected boolean isFireballFiery() {
      return true;
   }

   protected IParticleData getParticle() {
      return ParticleTypes.SMOKE;
   }

   /**
    * Return the motion factor for this projectile. The factor is multiplied by the original motion.
    */
   protected float getMotionFactor() {
      return 0.95F;
   }

   /**
    * Called when this EntityFireball hits a block or entity.
    */
   protected void onImpact(RayTraceResult result) {
      RayTraceResult.Type raytraceresult$type = result.getType();
      if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
         BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)result;
         BlockState blockstate = this.world.getBlockState(blockraytraceresult.getPos());
         blockstate.onProjectileCollision(this.world, blockstate, blockraytraceresult, this);
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      Vec3d vec3d = this.getMotion();
      compound.put("direction", this.newDoubleNBTList(new double[]{vec3d.x, vec3d.y, vec3d.z}));
      compound.put("power", this.newDoubleNBTList(new double[]{this.accelerationX, this.accelerationY, this.accelerationZ}));
      compound.putInt("life", this.ticksAlive);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      if (compound.contains("power", 9)) {
         ListNBT listnbt = compound.getList("power", 6);
         if (listnbt.size() == 3) {
            this.accelerationX = listnbt.getDouble(0);
            this.accelerationY = listnbt.getDouble(1);
            this.accelerationZ = listnbt.getDouble(2);
         }
      }

      this.ticksAlive = compound.getInt("life");
      if (compound.contains("direction", 9) && compound.getList("direction", 6).size() == 3) {
         ListNBT listnbt1 = compound.getList("direction", 6);
         this.setMotion(listnbt1.getDouble(0), listnbt1.getDouble(1), listnbt1.getDouble(2));
      } else {
         this.remove();
      }

   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return true;
   }

   public float getCollisionBorderSize() {
      return 1.0F;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         this.markVelocityChanged();
         if (source.getTrueSource() != null) {
            Vec3d vec3d = source.getTrueSource().getLookVec();
            this.setMotion(vec3d);
            this.accelerationX = vec3d.x * 0.1D;
            this.accelerationY = vec3d.y * 0.1D;
            this.accelerationZ = vec3d.z * 0.1D;
            if (source.getTrueSource() instanceof LivingEntity) {
               this.shootingEntity = (LivingEntity)source.getTrueSource();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   /**
    * Gets how bright this entity is.
    */
   public float getBrightness() {
      return 1.0F;
   }

   public IPacket<?> createSpawnPacket() {
      int i = this.shootingEntity == null ? 0 : this.shootingEntity.getEntityId();
      return new SSpawnObjectPacket(this.getEntityId(), this.getUniqueID(), this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationPitch, this.rotationYaw, this.getType(), i, new Vec3d(this.accelerationX, this.accelerationY, this.accelerationZ));
   }
}