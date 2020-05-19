package net.minecraft.entity.ai.controller;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;

public class MovementController {
   protected final MobEntity mob;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected double speed;
   protected float moveForward;
   protected float moveStrafe;
   protected MovementController.Action action = MovementController.Action.WAIT;

   public MovementController(MobEntity mob) {
      this.mob = mob;
   }

   public boolean isUpdating() {
      return this.action == MovementController.Action.MOVE_TO;
   }

   public double getSpeed() {
      return this.speed;
   }

   /**
    * Sets the speed and location to move to
    */
   public void setMoveTo(double x, double y, double z, double speedIn) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      this.speed = speedIn;
      if (this.action != MovementController.Action.JUMPING) {
         this.action = MovementController.Action.MOVE_TO;
      }

   }

   public void strafe(float forward, float strafe) {
      this.action = MovementController.Action.STRAFE;
      this.moveForward = forward;
      this.moveStrafe = strafe;
      this.speed = 0.25D;
   }

   public void tick() {
      if (this.action == MovementController.Action.STRAFE) {
         float f = (float)this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
         float f1 = (float)this.speed * f;
         float f2 = this.moveForward;
         float f3 = this.moveStrafe;
         float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);
         if (f4 < 1.0F) {
            f4 = 1.0F;
         }

         f4 = f1 / f4;
         f2 = f2 * f4;
         f3 = f3 * f4;
         float f5 = MathHelper.sin(this.mob.rotationYaw * ((float)Math.PI / 180F));
         float f6 = MathHelper.cos(this.mob.rotationYaw * ((float)Math.PI / 180F));
         float f7 = f2 * f6 - f3 * f5;
         float f8 = f3 * f6 + f2 * f5;
         PathNavigator pathnavigator = this.mob.getNavigator();
         if (pathnavigator != null) {
            NodeProcessor nodeprocessor = pathnavigator.getNodeProcessor();
            if (nodeprocessor != null && nodeprocessor.getPathNodeType(this.mob.world, MathHelper.floor(this.mob.getPosX() + (double)f7), MathHelper.floor(this.mob.getPosY()), MathHelper.floor(this.mob.getPosZ() + (double)f8)) != PathNodeType.WALKABLE) {
               this.moveForward = 1.0F;
               this.moveStrafe = 0.0F;
               f1 = f;
            }
         }

         this.mob.setAIMoveSpeed(f1);
         this.mob.setMoveForward(this.moveForward);
         this.mob.setMoveStrafing(this.moveStrafe);
         this.action = MovementController.Action.WAIT;
      } else if (this.action == MovementController.Action.MOVE_TO) {
         this.action = MovementController.Action.WAIT;
         double d0 = this.posX - this.mob.getPosX();
         double d1 = this.posZ - this.mob.getPosZ();
         double d2 = this.posY - this.mob.getPosY();
         double d3 = d0 * d0 + d2 * d2 + d1 * d1;
         if (d3 < (double)2.5000003E-7F) {
            this.mob.setMoveForward(0.0F);
            return;
         }

         float f9 = (float)(MathHelper.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
         this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, f9, 90.0F);
         this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
         BlockPos blockpos = new BlockPos(this.mob);
         BlockState blockstate = this.mob.world.getBlockState(blockpos);
         Block block = blockstate.getBlock();
         VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.world, blockpos);
         if (d2 > (double)this.mob.stepHeight && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getWidth()) || !voxelshape.isEmpty() && this.mob.getPosY() < voxelshape.getEnd(Direction.Axis.Y) + (double)blockpos.getY() && !block.isIn(BlockTags.DOORS) && !block.isIn(BlockTags.FENCES)) {
            this.mob.getJumpController().setJumping();
            this.action = MovementController.Action.JUMPING;
         }
      } else if (this.action == MovementController.Action.JUMPING) {
         this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
         if (this.mob.onGround) {
            this.action = MovementController.Action.WAIT;
         }
      } else {
         this.mob.setMoveForward(0.0F);
      }

   }

   /**
    * Attempt to rotate the first angle to become the second angle, but only allow overall direction change to at max be
    * third parameter
    */
   protected float limitAngle(float sourceAngle, float targetAngle, float maximumChange) {
      float f = MathHelper.wrapDegrees(targetAngle - sourceAngle);
      if (f > maximumChange) {
         f = maximumChange;
      }

      if (f < -maximumChange) {
         f = -maximumChange;
      }

      float f1 = sourceAngle + f;
      if (f1 < 0.0F) {
         f1 += 360.0F;
      } else if (f1 > 360.0F) {
         f1 -= 360.0F;
      }

      return f1;
   }

   public double getX() {
      return this.posX;
   }

   public double getY() {
      return this.posY;
   }

   public double getZ() {
      return this.posZ;
   }

   public static enum Action {
      WAIT,
      MOVE_TO,
      STRAFE,
      JUMPING;
   }
}