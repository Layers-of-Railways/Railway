package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlyingPathNavigator extends PathNavigator {
   public FlyingPathNavigator(MobEntity entityIn, World worldIn) {
      super(entityIn, worldIn);
   }

   protected PathFinder getPathFinder(int p_179679_1_) {
      this.nodeProcessor = new FlyingNodeProcessor();
      this.nodeProcessor.setCanEnterDoors(true);
      return new PathFinder(this.nodeProcessor, p_179679_1_);
   }

   /**
    * If on ground or swimming and can swim
    */
   protected boolean canNavigate() {
      return this.getCanSwim() && this.isInLiquid() || !this.entity.isPassenger();
   }

   protected Vec3d getEntityPosition() {
      return this.entity.getPositionVec();
   }

   /**
    * Returns the path to the given EntityLiving. Args : entity
    */
   public Path getPathToEntity(Entity entityIn, int p_75494_2_) {
      return this.getPathToPos(new BlockPos(entityIn), p_75494_2_);
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

   /**
    * Checks if the specified entity can safely walk to the specified location.
    */
   protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
      int i = MathHelper.floor(posVec31.x);
      int j = MathHelper.floor(posVec31.y);
      int k = MathHelper.floor(posVec31.z);
      double d0 = posVec32.x - posVec31.x;
      double d1 = posVec32.y - posVec31.y;
      double d2 = posVec32.z - posVec31.z;
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      if (d3 < 1.0E-8D) {
         return false;
      } else {
         double d4 = 1.0D / Math.sqrt(d3);
         d0 = d0 * d4;
         d1 = d1 * d4;
         d2 = d2 * d4;
         double d5 = 1.0D / Math.abs(d0);
         double d6 = 1.0D / Math.abs(d1);
         double d7 = 1.0D / Math.abs(d2);
         double d8 = (double)i - posVec31.x;
         double d9 = (double)j - posVec31.y;
         double d10 = (double)k - posVec31.z;
         if (d0 >= 0.0D) {
            ++d8;
         }

         if (d1 >= 0.0D) {
            ++d9;
         }

         if (d2 >= 0.0D) {
            ++d10;
         }

         d8 = d8 / d0;
         d9 = d9 / d1;
         d10 = d10 / d2;
         int l = d0 < 0.0D ? -1 : 1;
         int i1 = d1 < 0.0D ? -1 : 1;
         int j1 = d2 < 0.0D ? -1 : 1;
         int k1 = MathHelper.floor(posVec32.x);
         int l1 = MathHelper.floor(posVec32.y);
         int i2 = MathHelper.floor(posVec32.z);
         int j2 = k1 - i;
         int k2 = l1 - j;
         int l2 = i2 - k;

         while(j2 * l > 0 || k2 * i1 > 0 || l2 * j1 > 0) {
            if (d8 < d10 && d8 <= d9) {
               d8 += d5;
               i += l;
               j2 = k1 - i;
            } else if (d9 < d8 && d9 <= d10) {
               d9 += d6;
               j += i1;
               k2 = l1 - j;
            } else {
               d10 += d7;
               k += j1;
               l2 = i2 - k;
            }
         }

         return true;
      }
   }

   public void setCanOpenDoors(boolean canOpenDoorsIn) {
      this.nodeProcessor.setCanOpenDoors(canOpenDoorsIn);
   }

   public void setCanEnterDoors(boolean canEnterDoorsIn) {
      this.nodeProcessor.setCanEnterDoors(canEnterDoorsIn);
   }

   public boolean canEntityStandOnPos(BlockPos pos) {
      return this.world.getBlockState(pos).isTopSolid(this.world, pos, this.entity);
   }
}