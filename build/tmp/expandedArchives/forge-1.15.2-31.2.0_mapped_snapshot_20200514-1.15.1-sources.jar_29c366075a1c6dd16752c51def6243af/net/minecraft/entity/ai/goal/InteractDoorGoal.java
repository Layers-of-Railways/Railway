package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class InteractDoorGoal extends Goal {
   protected MobEntity entity;
   protected BlockPos doorPosition = BlockPos.ZERO;
   protected boolean doorInteract;
   private boolean hasStoppedDoorInteraction;
   private float entityPositionX;
   private float entityPositionZ;

   public InteractDoorGoal(MobEntity entityIn) {
      this.entity = entityIn;
      if (!(entityIn.getNavigator() instanceof GroundPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   protected boolean canDestroy() {
      if (!this.doorInteract) {
         return false;
      } else {
         BlockState blockstate = this.entity.world.getBlockState(this.doorPosition);
         if (!(blockstate.getBlock() instanceof DoorBlock)) {
            this.doorInteract = false;
            return false;
         } else {
            return blockstate.get(DoorBlock.OPEN);
         }
      }
   }

   protected void toggleDoor(boolean open) {
      if (this.doorInteract) {
         BlockState blockstate = this.entity.world.getBlockState(this.doorPosition);
         if (blockstate.getBlock() instanceof DoorBlock) {
            ((DoorBlock)blockstate.getBlock()).toggleDoor(this.entity.world, this.doorPosition, open);
         }
      }

   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      if (!this.entity.collidedHorizontally) {
         return false;
      } else {
         GroundPathNavigator groundpathnavigator = (GroundPathNavigator)this.entity.getNavigator();
         Path path = groundpathnavigator.getPath();
         if (path != null && !path.isFinished() && groundpathnavigator.getEnterDoors()) {
            for(int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
               PathPoint pathpoint = path.getPathPointFromIndex(i);
               this.doorPosition = new BlockPos(pathpoint.x, pathpoint.y + 1, pathpoint.z);
               if (!(this.entity.getDistanceSq((double)this.doorPosition.getX(), this.entity.getPosY(), (double)this.doorPosition.getZ()) > 2.25D)) {
                  this.doorInteract = canOpenDoor(this.entity.world, this.doorPosition);
                  if (this.doorInteract) {
                     return true;
                  }
               }
            }

            this.doorPosition = (new BlockPos(this.entity)).up();
            this.doorInteract = canOpenDoor(this.entity.world, this.doorPosition);
            return this.doorInteract;
         } else {
            return false;
         }
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return !this.hasStoppedDoorInteraction;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.hasStoppedDoorInteraction = false;
      this.entityPositionX = (float)((double)((float)this.doorPosition.getX() + 0.5F) - this.entity.getPosX());
      this.entityPositionZ = (float)((double)((float)this.doorPosition.getZ() + 0.5F) - this.entity.getPosZ());
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      float f = (float)((double)((float)this.doorPosition.getX() + 0.5F) - this.entity.getPosX());
      float f1 = (float)((double)((float)this.doorPosition.getZ() + 0.5F) - this.entity.getPosZ());
      float f2 = this.entityPositionX * f + this.entityPositionZ * f1;
      if (f2 < 0.0F) {
         this.hasStoppedDoorInteraction = true;
      }

   }

   public static boolean canOpenDoor(World p_220695_0_, BlockPos p_220695_1_) {
      BlockState blockstate = p_220695_0_.getBlockState(p_220695_1_);
      return blockstate.getBlock() instanceof DoorBlock && blockstate.getMaterial() == Material.WOOD;
   }
}