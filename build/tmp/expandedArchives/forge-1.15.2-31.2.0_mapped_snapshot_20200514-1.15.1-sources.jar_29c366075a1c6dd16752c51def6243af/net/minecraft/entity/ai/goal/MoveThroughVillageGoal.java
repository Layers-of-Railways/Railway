package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class MoveThroughVillageGoal extends Goal {
   protected final CreatureEntity entity;
   private final double movementSpeed;
   private Path path;
   private BlockPos field_220735_d;
   private final boolean isNocturnal;
   private final List<BlockPos> doorList = Lists.newArrayList();
   private final int maxDistance;
   private final BooleanSupplier booleanSupplier;

   public MoveThroughVillageGoal(CreatureEntity p_i50324_1_, double speedIn, boolean nocturnal, int maxDistanceIn, BooleanSupplier booleanSupplierIn) {
      this.entity = p_i50324_1_;
      this.movementSpeed = speedIn;
      this.isNocturnal = nocturnal;
      this.maxDistance = maxDistanceIn;
      this.booleanSupplier = booleanSupplierIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      if (!(p_i50324_1_.getNavigator() instanceof GroundPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      this.resizeDoorList();
      if (this.isNocturnal && this.entity.world.isDaytime()) {
         return false;
      } else {
         ServerWorld serverworld = (ServerWorld)this.entity.world;
         BlockPos blockpos = new BlockPos(this.entity);
         if (!serverworld.isCloseToVillage(blockpos, 6)) {
            return false;
         } else {
            Vec3d vec3d = RandomPositionGenerator.func_221024_a(this.entity, 15, 7, (p_220734_3_) -> {
               if (!serverworld.isVillage(p_220734_3_)) {
                  return Double.NEGATIVE_INFINITY;
               } else {
                  Optional<BlockPos> optional1 = serverworld.getPointOfInterestManager().find(PointOfInterestType.MATCH_ANY, this::func_220733_a, p_220734_3_, 10, PointOfInterestManager.Status.IS_OCCUPIED);
                  return !optional1.isPresent() ? Double.NEGATIVE_INFINITY : -optional1.get().distanceSq(blockpos);
               }
            });
            if (vec3d == null) {
               return false;
            } else {
               Optional<BlockPos> optional = serverworld.getPointOfInterestManager().find(PointOfInterestType.MATCH_ANY, this::func_220733_a, new BlockPos(vec3d), 10, PointOfInterestManager.Status.IS_OCCUPIED);
               if (!optional.isPresent()) {
                  return false;
               } else {
                  this.field_220735_d = optional.get().toImmutable();
                  GroundPathNavigator groundpathnavigator = (GroundPathNavigator)this.entity.getNavigator();
                  boolean flag = groundpathnavigator.getEnterDoors();
                  groundpathnavigator.setBreakDoors(this.booleanSupplier.getAsBoolean());
                  this.path = groundpathnavigator.getPathToPos(this.field_220735_d, 0);
                  groundpathnavigator.setBreakDoors(flag);
                  if (this.path == null) {
                     Vec3d vec3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, 10, 7, new Vec3d(this.field_220735_d));
                     if (vec3d1 == null) {
                        return false;
                     }

                     groundpathnavigator.setBreakDoors(this.booleanSupplier.getAsBoolean());
                     this.path = this.entity.getNavigator().getPathToPos(vec3d1.x, vec3d1.y, vec3d1.z, 0);
                     groundpathnavigator.setBreakDoors(flag);
                     if (this.path == null) {
                        return false;
                     }
                  }

                  for(int i = 0; i < this.path.getCurrentPathLength(); ++i) {
                     PathPoint pathpoint = this.path.getPathPointFromIndex(i);
                     BlockPos blockpos1 = new BlockPos(pathpoint.x, pathpoint.y + 1, pathpoint.z);
                     if (InteractDoorGoal.canOpenDoor(this.entity.world, blockpos1)) {
                        this.path = this.entity.getNavigator().getPathToPos((double)pathpoint.x, (double)pathpoint.y, (double)pathpoint.z, 0);
                        break;
                     }
                  }

                  return this.path != null;
               }
            }
         }
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      if (this.entity.getNavigator().noPath()) {
         return false;
      } else {
         return !this.field_220735_d.withinDistance(this.entity.getPositionVec(), (double)(this.entity.getWidth() + (float)this.maxDistance));
      }
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.entity.getNavigator().setPath(this.path, this.movementSpeed);
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      if (this.entity.getNavigator().noPath() || this.field_220735_d.withinDistance(this.entity.getPositionVec(), (double)this.maxDistance)) {
         this.doorList.add(this.field_220735_d);
      }

   }

   private boolean func_220733_a(BlockPos p_220733_1_) {
      for(BlockPos blockpos : this.doorList) {
         if (Objects.equals(p_220733_1_, blockpos)) {
            return false;
         }
      }

      return true;
   }

   private void resizeDoorList() {
      if (this.doorList.size() > 15) {
         this.doorList.remove(0);
      }

   }
}