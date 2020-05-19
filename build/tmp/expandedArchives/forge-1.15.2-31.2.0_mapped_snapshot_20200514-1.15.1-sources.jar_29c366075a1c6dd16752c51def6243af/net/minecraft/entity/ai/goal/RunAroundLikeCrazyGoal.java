package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class RunAroundLikeCrazyGoal extends Goal {
   private final AbstractHorseEntity horseHost;
   private final double speed;
   private double targetX;
   private double targetY;
   private double targetZ;

   public RunAroundLikeCrazyGoal(AbstractHorseEntity horse, double speedIn) {
      this.horseHost = horse;
      this.speed = speedIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      if (!this.horseHost.isTame() && this.horseHost.isBeingRidden()) {
         Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.horseHost, 5, 4);
         if (vec3d == null) {
            return false;
         } else {
            this.targetX = vec3d.x;
            this.targetY = vec3d.y;
            this.targetZ = vec3d.z;
            return true;
         }
      } else {
         return false;
      }
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.horseHost.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.speed);
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return !this.horseHost.isTame() && !this.horseHost.getNavigator().noPath() && this.horseHost.isBeingRidden();
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (!this.horseHost.isTame() && this.horseHost.getRNG().nextInt(50) == 0) {
         Entity entity = this.horseHost.getPassengers().get(0);
         if (entity == null) {
            return;
         }

         if (entity instanceof PlayerEntity) {
            int i = this.horseHost.getTemper();
            int j = this.horseHost.getMaxTemper();
            if (j > 0 && this.horseHost.getRNG().nextInt(j) < i && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(horseHost, (PlayerEntity)entity)) {
               this.horseHost.setTamedBy((PlayerEntity)entity);
               return;
            }

            this.horseHost.increaseTemper(5);
         }

         this.horseHost.removePassengers();
         this.horseHost.makeMad();
         this.horseHost.world.setEntityState(this.horseHost, (byte)6);
      }

   }
}