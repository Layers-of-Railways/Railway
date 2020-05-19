package net.minecraft.pathfinding;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SwimmerPathNavigator extends PathNavigator {
   private boolean field_205155_i;

   public SwimmerPathNavigator(MobEntity entitylivingIn, World worldIn) {
      super(entitylivingIn, worldIn);
   }

   protected PathFinder getPathFinder(int p_179679_1_) {
      this.field_205155_i = this.entity instanceof DolphinEntity;
      this.nodeProcessor = new SwimNodeProcessor(this.field_205155_i);
      return new PathFinder(this.nodeProcessor, p_179679_1_);
   }

   /**
    * If on ground or swimming and can swim
    */
   protected boolean canNavigate() {
      return this.field_205155_i || this.isInLiquid();
   }

   protected Vec3d getEntityPosition() {
      return new Vec3d(this.entity.getPosX(), this.entity.getPosYHeight(0.5D), this.entity.getPosZ());
   }

   public void tick() {
      ++this.totalTicks;
      if (this.tryUpdatePath) {
         this.updatePath();
      }

      if (!this.noPath()) {
         if (this.canNavigate()) {
            this.pathFollow();
         } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
            Vec3d vec3d = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());
            if (MathHelper.floor(this.entity.getPosX()) == MathHelper.floor(vec3d.x) && MathHelper.floor(this.entity.getPosY()) == MathHelper.floor(vec3d.y) && MathHelper.floor(this.entity.getPosZ()) == MathHelper.floor(vec3d.z)) {
               this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
         }

         DebugPacketSender.sendPath(this.world, this.entity, this.currentPath, this.maxDistanceToWaypoint);
         if (!this.noPath()) {
            Vec3d vec3d1 = this.currentPath.getPosition(this.entity);
            this.entity.getMoveHelper().setMoveTo(vec3d1.x, vec3d1.y, vec3d1.z, this.speed);
         }
      }
   }

   protected void pathFollow() {
      if (this.currentPath != null) {
         Vec3d vec3d = this.getEntityPosition();
         float f = this.entity.getWidth();
         float f1 = f > 0.75F ? f / 2.0F : 0.75F - f / 2.0F;
         Vec3d vec3d1 = this.entity.getMotion();
         if (Math.abs(vec3d1.x) > 0.2D || Math.abs(vec3d1.z) > 0.2D) {
            f1 = (float)((double)f1 * vec3d1.length() * 6.0D);
         }

         int i = 6;
         Vec3d vec3d2 = this.currentPath.getCurrentPos();
         // Forge: fix MC-94054
         if (Math.abs(this.entity.getPosX() - (vec3d2.x + ((int)(this.entity.getWidth() + 1) / 2D))) < (double)f1 && Math.abs(this.entity.getPosZ() - (vec3d2.z + ((int)(this.entity.getWidth() + 1) / 2D))) < (double)f1 && Math.abs(this.entity.getPosY() - vec3d2.y) < (double)(f1 * 2.0F)) {
            this.currentPath.incrementPathIndex();
         }

         for(int j = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j) {
            vec3d2 = this.currentPath.getVectorFromIndex(this.entity, j);
            if (!(vec3d2.squareDistanceTo(vec3d) > 36.0D) && this.isDirectPathBetweenPoints(vec3d, vec3d2, 0, 0, 0)) {
               this.currentPath.setCurrentPathIndex(j);
               break;
            }
         }

         this.checkForStuck(vec3d);
      }
   }

   /**
    * Checks if entity haven't been moved when last checked and if so, clears current {@link
    * net.minecraft.pathfinding.PathEntity}
    */
   protected void checkForStuck(Vec3d positionVec3) {
      if (this.totalTicks - this.ticksAtLastPos > 100) {
         if (positionVec3.squareDistanceTo(this.lastPosCheck) < 2.25D) {
            this.clearPath();
         }

         this.ticksAtLastPos = this.totalTicks;
         this.lastPosCheck = positionVec3;
      }

      if (this.currentPath != null && !this.currentPath.isFinished()) {
         Vec3d vec3d = this.currentPath.getCurrentPos();
         if (vec3d.equals(this.timeoutCachedNode)) {
            this.timeoutTimer += Util.milliTime() - this.lastTimeoutCheck;
         } else {
            this.timeoutCachedNode = vec3d;
            double d0 = positionVec3.distanceTo(this.timeoutCachedNode);
            this.timeoutLimit = this.entity.getAIMoveSpeed() > 0.0F ? d0 / (double)this.entity.getAIMoveSpeed() * 100.0D : 0.0D;
         }

         if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 2.0D) {
            this.timeoutCachedNode = Vec3d.ZERO;
            this.timeoutTimer = 0L;
            this.timeoutLimit = 0.0D;
            this.clearPath();
         }

         this.lastTimeoutCheck = Util.milliTime();
      }

   }

   /**
    * Checks if the specified entity can safely walk to the specified location.
    */
   protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
      Vec3d vec3d = new Vec3d(posVec32.x, posVec32.y + (double)this.entity.getHeight() * 0.5D, posVec32.z);
      return this.world.rayTraceBlocks(new RayTraceContext(posVec31, vec3d, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.entity)).getType() == RayTraceResult.Type.MISS;
   }

   public boolean canEntityStandOnPos(BlockPos pos) {
      return !this.world.getBlockState(pos).isOpaqueCube(this.world, pos);
   }

   public void setCanSwim(boolean canSwim) {
   }
}