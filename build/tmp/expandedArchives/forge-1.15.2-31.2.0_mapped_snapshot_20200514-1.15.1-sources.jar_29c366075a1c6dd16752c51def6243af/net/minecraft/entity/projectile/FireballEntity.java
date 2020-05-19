package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireballEntity extends AbstractFireballEntity {
   public int explosionPower = 1;

   public FireballEntity(EntityType<? extends FireballEntity> p_i50163_1_, World p_i50163_2_) {
      super(p_i50163_1_, p_i50163_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public FireballEntity(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
      super(EntityType.FIREBALL, x, y, z, accelX, accelY, accelZ, worldIn);
   }

   public FireballEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
      super(EntityType.FIREBALL, shooter, accelX, accelY, accelZ, worldIn);
   }

   /**
    * Called when this EntityFireball hits a block or entity.
    */
   protected void onImpact(RayTraceResult result) {
      super.onImpact(result);
      if (!this.world.isRemote) {
         if (result.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult)result).getEntity();
            entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 6.0F);
            this.applyEnchantments(this.shootingEntity, entity);
         }

         boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
         this.world.createExplosion((Entity)null, this.getPosX(), this.getPosY(), this.getPosZ(), (float)this.explosionPower, flag, flag ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
         this.remove();
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("ExplosionPower", this.explosionPower);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      if (compound.contains("ExplosionPower", 99)) {
         this.explosionPower = compound.getInt("ExplosionPower");
      }

   }
}