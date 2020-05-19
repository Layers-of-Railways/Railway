package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DragonFireballEntity extends DamagingProjectileEntity {
   public DragonFireballEntity(EntityType<? extends DragonFireballEntity> p_i50171_1_, World p_i50171_2_) {
      super(p_i50171_1_, p_i50171_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public DragonFireballEntity(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
      super(EntityType.DRAGON_FIREBALL, x, y, z, accelX, accelY, accelZ, worldIn);
   }

   public DragonFireballEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
      super(EntityType.DRAGON_FIREBALL, shooter, accelX, accelY, accelZ, worldIn);
   }

   /**
    * Called when this EntityFireball hits a block or entity.
    */
   protected void onImpact(RayTraceResult result) {
      super.onImpact(result);
      if (result.getType() != RayTraceResult.Type.ENTITY || !((EntityRayTraceResult)result).getEntity().isEntityEqual(this.shootingEntity)) {
         if (!this.world.isRemote) {
            List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(4.0D, 2.0D, 4.0D));
            AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ());
            areaeffectcloudentity.setOwner(this.shootingEntity);
            areaeffectcloudentity.setParticleData(ParticleTypes.DRAGON_BREATH);
            areaeffectcloudentity.setRadius(3.0F);
            areaeffectcloudentity.setDuration(600);
            areaeffectcloudentity.setRadiusPerTick((7.0F - areaeffectcloudentity.getRadius()) / (float)areaeffectcloudentity.getDuration());
            areaeffectcloudentity.addEffect(new EffectInstance(Effects.INSTANT_DAMAGE, 1, 1));
            if (!list.isEmpty()) {
               for(LivingEntity livingentity : list) {
                  double d0 = this.getDistanceSq(livingentity);
                  if (d0 < 16.0D) {
                     areaeffectcloudentity.setPosition(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ());
                     break;
                  }
               }
            }

            this.world.playEvent(2006, new BlockPos(this), 0);
            this.world.addEntity(areaeffectcloudentity);
            this.remove();
         }

      }
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return false;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }

   protected IParticleData getParticle() {
      return ParticleTypes.DRAGON_BREATH;
   }

   protected boolean isFireballFiery() {
      return false;
   }
}