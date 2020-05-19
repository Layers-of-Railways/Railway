package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.Vec3d;

public class AvoidEntityGoal<T extends LivingEntity> extends Goal {
   protected final CreatureEntity entity;
   private final double farSpeed;
   private final double nearSpeed;
   protected T avoidTarget;
   protected final float avoidDistance;
   protected Path path;
   protected final PathNavigator navigation;
   /** Class of entity this behavior seeks to avoid */
   protected final Class<T> classToAvoid;
   protected final Predicate<LivingEntity> avoidTargetSelector;
   protected final Predicate<LivingEntity> field_203784_k;
   private final EntityPredicate builtTargetSelector;

   public AvoidEntityGoal(CreatureEntity entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
      this(entityIn, classToAvoidIn, (p_200828_0_) -> {
         return true;
      }, avoidDistanceIn, farSpeedIn, nearSpeedIn, EntityPredicates.CAN_AI_TARGET::test);
   }

   public AvoidEntityGoal(CreatureEntity entityIn, Class<T> avoidClass, Predicate<LivingEntity> targetPredicate, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> p_i48859_9_) {
      this.entity = entityIn;
      this.classToAvoid = avoidClass;
      this.avoidTargetSelector = targetPredicate;
      this.avoidDistance = distance;
      this.farSpeed = nearSpeedIn;
      this.nearSpeed = farSpeedIn;
      this.field_203784_k = p_i48859_9_;
      this.navigation = entityIn.getNavigator();
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      this.builtTargetSelector = (new EntityPredicate()).setDistance((double)distance).setCustomPredicate(p_i48859_9_.and(targetPredicate));
   }

   public AvoidEntityGoal(CreatureEntity entityIn, Class<T> avoidClass, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> targetPredicate) {
      this(entityIn, avoidClass, (p_203782_0_) -> {
         return true;
      }, distance, nearSpeedIn, farSpeedIn, targetPredicate);
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      this.avoidTarget = this.entity.world.func_225318_b(this.classToAvoid, this.builtTargetSelector, this.entity, this.entity.getPosX(), this.entity.getPosY(), this.entity.getPosZ(), this.entity.getBoundingBox().grow((double)this.avoidDistance, 3.0D, (double)this.avoidDistance));
      if (this.avoidTarget == null) {
         return false;
      } else {
         Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, this.avoidTarget.getPositionVec());
         if (vec3d == null) {
            return false;
         } else if (this.avoidTarget.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < this.avoidTarget.getDistanceSq(this.entity)) {
            return false;
         } else {
            this.path = this.navigation.getPathToPos(vec3d.x, vec3d.y, vec3d.z, 0);
            return this.path != null;
         }
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return !this.navigation.noPath();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.navigation.setPath(this.path, this.farSpeed);
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.avoidTarget = null;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (this.entity.getDistanceSq(this.avoidTarget) < 49.0D) {
         this.entity.getNavigator().setSpeed(this.nearSpeed);
      } else {
         this.entity.getNavigator().setSpeed(this.farSpeed);
      }

   }
}