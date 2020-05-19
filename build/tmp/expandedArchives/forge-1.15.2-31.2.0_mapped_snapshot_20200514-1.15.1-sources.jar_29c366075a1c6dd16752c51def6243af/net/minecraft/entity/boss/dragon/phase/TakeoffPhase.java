package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class TakeoffPhase extends Phase {
   private boolean firstTick;
   private Path currentPath;
   private Vec3d targetLocation;

   public TakeoffPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   /**
    * Gives the phase a chance to update its status.
    * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
    */
   public void serverTick() {
      if (!this.firstTick && this.currentPath != null) {
         BlockPos blockpos = this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         if (!blockpos.withinDistance(this.dragon.getPositionVec(), 10.0D)) {
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
         }
      } else {
         this.firstTick = false;
         this.findNewTarget();
      }

   }

   /**
    * Called when this phase is set to active
    */
   public void initPhase() {
      this.firstTick = true;
      this.currentPath = null;
      this.targetLocation = null;
   }

   private void findNewTarget() {
      int i = this.dragon.initPathPoints();
      Vec3d vec3d = this.dragon.getHeadLookVec(1.0F);
      int j = this.dragon.getNearestPpIdx(-vec3d.x * 40.0D, 105.0D, -vec3d.z * 40.0D);
      if (this.dragon.getFightManager() != null && this.dragon.getFightManager().getNumAliveCrystals() > 0) {
         j = j % 12;
         if (j < 0) {
            j += 12;
         }
      } else {
         j = j - 12;
         j = j & 7;
         j = j + 12;
      }

      this.currentPath = this.dragon.findPath(i, j, (PathPoint)null);
      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null) {
         this.currentPath.incrementPathIndex();
         if (!this.currentPath.isFinished()) {
            Vec3d vec3d = this.currentPath.getCurrentPos();
            this.currentPath.incrementPathIndex();

            double d0;
            while(true) {
               d0 = vec3d.y + (double)(this.dragon.getRNG().nextFloat() * 20.0F);
               if (!(d0 < vec3d.y)) {
                  break;
               }
            }

            this.targetLocation = new Vec3d(vec3d.x, d0, vec3d.z);
         }
      }

   }

   /**
    * Returns the location the dragon is flying toward
    */
   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<TakeoffPhase> getType() {
      return PhaseType.TAKEOFF;
   }
}