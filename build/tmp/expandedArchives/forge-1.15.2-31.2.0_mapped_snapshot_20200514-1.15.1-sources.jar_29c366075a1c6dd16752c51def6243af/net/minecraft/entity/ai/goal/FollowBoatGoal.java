package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FollowBoatGoal extends Goal {
   private int field_205143_a;
   private final CreatureEntity field_205144_b;
   private LivingEntity field_205145_c;
   private BoatGoals field_205146_d;

   public FollowBoatGoal(CreatureEntity p_i48939_1_) {
      this.field_205144_b = p_i48939_1_;
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      List<BoatEntity> list = this.field_205144_b.world.getEntitiesWithinAABB(BoatEntity.class, this.field_205144_b.getBoundingBox().grow(5.0D));
      boolean flag = false;

      for(BoatEntity boatentity : list) {
         Entity entity = boatentity.getControllingPassenger();
         if (entity instanceof LivingEntity && (MathHelper.abs(((LivingEntity)entity).moveStrafing) > 0.0F || MathHelper.abs(((LivingEntity)entity).moveForward) > 0.0F)) {
            flag = true;
            break;
         }
      }

      return this.field_205145_c != null && (MathHelper.abs(this.field_205145_c.moveStrafing) > 0.0F || MathHelper.abs(this.field_205145_c.moveForward) > 0.0F) || flag;
   }

   public boolean isPreemptible() {
      return true;
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.field_205145_c != null && this.field_205145_c.isPassenger() && (MathHelper.abs(this.field_205145_c.moveStrafing) > 0.0F || MathHelper.abs(this.field_205145_c.moveForward) > 0.0F);
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      for(BoatEntity boatentity : this.field_205144_b.world.getEntitiesWithinAABB(BoatEntity.class, this.field_205144_b.getBoundingBox().grow(5.0D))) {
         if (boatentity.getControllingPassenger() != null && boatentity.getControllingPassenger() instanceof LivingEntity) {
            this.field_205145_c = (LivingEntity)boatentity.getControllingPassenger();
            break;
         }
      }

      this.field_205143_a = 0;
      this.field_205146_d = BoatGoals.GO_TO_BOAT;
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.field_205145_c = null;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      boolean flag = MathHelper.abs(this.field_205145_c.moveStrafing) > 0.0F || MathHelper.abs(this.field_205145_c.moveForward) > 0.0F;
      float f = this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION ? (flag ? 0.17999999F : 0.0F) : 0.135F;
      this.field_205144_b.moveRelative(f, new Vec3d((double)this.field_205144_b.moveStrafing, (double)this.field_205144_b.moveVertical, (double)this.field_205144_b.moveForward));
      this.field_205144_b.move(MoverType.SELF, this.field_205144_b.getMotion());
      if (--this.field_205143_a <= 0) {
         this.field_205143_a = 10;
         if (this.field_205146_d == BoatGoals.GO_TO_BOAT) {
            BlockPos blockpos = (new BlockPos(this.field_205145_c)).offset(this.field_205145_c.getHorizontalFacing().getOpposite());
            blockpos = blockpos.add(0, -1, 0);
            this.field_205144_b.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D);
            if (this.field_205144_b.getDistance(this.field_205145_c) < 4.0F) {
               this.field_205143_a = 0;
               this.field_205146_d = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
         } else if (this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION) {
            Direction direction = this.field_205145_c.getAdjustedHorizontalFacing();
            BlockPos blockpos1 = (new BlockPos(this.field_205145_c)).offset(direction, 10);
            this.field_205144_b.getNavigator().tryMoveToXYZ((double)blockpos1.getX(), (double)(blockpos1.getY() - 1), (double)blockpos1.getZ(), 1.0D);
            if (this.field_205144_b.getDistance(this.field_205145_c) > 12.0F) {
               this.field_205143_a = 0;
               this.field_205146_d = BoatGoals.GO_TO_BOAT;
            }
         }

      }
   }
}