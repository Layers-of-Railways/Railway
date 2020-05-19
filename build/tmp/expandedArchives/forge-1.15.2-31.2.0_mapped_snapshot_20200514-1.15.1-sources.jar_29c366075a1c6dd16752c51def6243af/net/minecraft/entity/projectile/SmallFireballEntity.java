package net.minecraft.entity.projectile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SmallFireballEntity extends AbstractFireballEntity {
   public SmallFireballEntity(EntityType<? extends SmallFireballEntity> p_i50160_1_, World p_i50160_2_) {
      super(p_i50160_1_, p_i50160_2_);
   }

   public SmallFireballEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
      super(EntityType.SMALL_FIREBALL, shooter, accelX, accelY, accelZ, worldIn);
   }

   public SmallFireballEntity(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
      super(EntityType.SMALL_FIREBALL, x, y, z, accelX, accelY, accelZ, worldIn);
   }

   /**
    * Called when this EntityFireball hits a block or entity.
    */
   protected void onImpact(RayTraceResult result) {
      super.onImpact(result);
      if (!this.world.isRemote) {
         if (result.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult)result).getEntity();
            if (!entity.isImmuneToFire()) {
               int i = entity.getFireTimer();
               entity.setFire(5);
               boolean flag = entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F);
               if (flag) {
                  this.applyEnchantments(this.shootingEntity, entity);
               } else {
                  entity.setFireTimer(i);
               }
            }
         } else if (this.shootingEntity == null || !(this.shootingEntity instanceof MobEntity) || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity)) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)result;
            BlockPos blockpos = blockraytraceresult.getPos().offset(blockraytraceresult.getFace());
            if (this.world.isAirBlock(blockpos)) {
               this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
            }
         }

         this.remove();
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
}