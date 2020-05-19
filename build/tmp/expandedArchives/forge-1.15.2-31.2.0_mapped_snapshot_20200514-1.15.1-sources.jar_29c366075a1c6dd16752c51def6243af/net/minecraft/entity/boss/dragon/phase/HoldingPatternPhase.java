package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class HoldingPatternPhase extends Phase {
   private static final EntityPredicate field_221117_b = (new EntityPredicate()).setDistance(64.0D);
   private Path currentPath;
   private Vec3d targetLocation;
   private boolean clockwise;

   public HoldingPatternPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   public PhaseType<HoldingPatternPhase> getType() {
      return PhaseType.HOLDING_PATTERN;
   }

   /**
    * Gives the phase a chance to update its status.
    * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
    */
   public void serverTick() {
      double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.squareDistanceTo(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());
      if (d0 < 100.0D || d0 > 22500.0D || this.dragon.collidedHorizontally || this.dragon.collidedVertically) {
         this.findNewTarget();
      }

   }

   /**
    * Called when this phase is set to active
    */
   public void initPhase() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   /**
    * Returns the location the dragon is flying toward
    */
   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget() {
      if (this.currentPath != null && this.currentPath.isFinished()) {
         BlockPos blockpos = this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
         int i = this.dragon.getFightManager() == null ? 0 : this.dragon.getFightManager().getNumAliveCrystals();
         if (this.dragon.getRNG().nextInt(i + 3) == 0) {
            this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
            return;
         }

         double d0 = 64.0D;
         PlayerEntity playerentity = this.dragon.world.getClosestPlayer(field_221117_b, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
         if (playerentity != null) {
            d0 = blockpos.distanceSq(playerentity.getPositionVec(), true) / 512.0D;
         }

         if (playerentity != null && !playerentity.abilities.disableDamage && (this.dragon.getRNG().nextInt(MathHelper.abs((int)d0) + 2) == 0 || this.dragon.getRNG().nextInt(i + 2) == 0)) {
            this.strafePlayer(playerentity);
            return;
         }
      }

      if (this.currentPath == null || this.currentPath.isFinished()) {
         int j = this.dragon.initPathPoints();
         int k = j;
         if (this.dragon.getRNG().nextInt(8) == 0) {
            this.clockwise = !this.clockwise;
            k = j + 6;
         }

         if (this.clockwise) {
            ++k;
         } else {
            --k;
         }

         if (this.dragon.getFightManager() != null && this.dragon.getFightManager().getNumAliveCrystals() >= 0) {
            k = k % 12;
            if (k < 0) {
               k += 12;
            }
         } else {
            k = k - 12;
            k = k & 7;
            k = k + 12;
         }

         this.currentPath = this.dragon.findPath(j, k, (PathPoint)null);
         if (this.currentPath != null) {
            this.currentPath.incrementPathIndex();
         }
      }

      this.navigateToNextPathNode();
   }

   private void strafePlayer(PlayerEntity player) {
      this.dragon.getPhaseManager().setPhase(PhaseType.STRAFE_PLAYER);
      this.dragon.getPhaseManager().getPhase(PhaseType.STRAFE_PLAYER).setTarget(player);
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isFinished()) {
         Vec3d vec3d = this.currentPath.getCurrentPos();
         this.currentPath.incrementPathIndex();
         double d0 = vec3d.x;
         double d1 = vec3d.z;

         double d2;
         while(true) {
            d2 = vec3d.y + (double)(this.dragon.getRNG().nextFloat() * 20.0F);
            if (!(d2 < vec3d.y)) {
               break;
            }
         }

         this.targetLocation = new Vec3d(d0, d2, d1);
      }

   }

   public void onCrystalDestroyed(EnderCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable PlayerEntity plyr) {
      if (plyr != null && !plyr.abilities.disableDamage) {
         this.strafePlayer(plyr);
      }

   }
}