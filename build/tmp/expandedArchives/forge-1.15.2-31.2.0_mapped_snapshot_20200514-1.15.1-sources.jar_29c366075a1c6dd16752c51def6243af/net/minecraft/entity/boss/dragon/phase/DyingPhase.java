package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class DyingPhase extends Phase {
   private Vec3d targetLocation;
   private int time;

   public DyingPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   /**
    * Generates particle effects appropriate to the phase (or sometimes sounds).
    * Called by dragon's onLivingUpdate. Only used when worldObj.isRemote.
    */
   public void clientTick() {
      if (this.time++ % 10 == 0) {
         float f = (this.dragon.getRNG().nextFloat() - 0.5F) * 8.0F;
         float f1 = (this.dragon.getRNG().nextFloat() - 0.5F) * 4.0F;
         float f2 = (this.dragon.getRNG().nextFloat() - 0.5F) * 8.0F;
         this.dragon.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.dragon.getPosX() + (double)f, this.dragon.getPosY() + 2.0D + (double)f1, this.dragon.getPosZ() + (double)f2, 0.0D, 0.0D, 0.0D);
      }

   }

   /**
    * Gives the phase a chance to update its status.
    * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
    */
   public void serverTick() {
      ++this.time;
      if (this.targetLocation == null) {
         BlockPos blockpos = this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION);
         this.targetLocation = new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
      }

      double d0 = this.targetLocation.squareDistanceTo(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());
      if (!(d0 < 100.0D) && !(d0 > 22500.0D) && !this.dragon.collidedHorizontally && !this.dragon.collidedVertically) {
         this.dragon.setHealth(1.0F);
      } else {
         this.dragon.setHealth(0.0F);
      }

   }

   /**
    * Called when this phase is set to active
    */
   public void initPhase() {
      this.targetLocation = null;
      this.time = 0;
   }

   /**
    * Returns the maximum amount dragon may rise or fall during this phase
    */
   public float getMaxRiseOrFall() {
      return 3.0F;
   }

   /**
    * Returns the location the dragon is flying toward
    */
   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<DyingPhase> getType() {
      return PhaseType.DYING;
   }
}