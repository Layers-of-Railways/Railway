package com.railwayteam.railways.goals;

import com.railwayteam.railways.util.EntityUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.List;

public class MoveTowardsClosestEntityGoal<T extends Entity> extends TargetGoal {
  protected final Class<T> targetClass;
  protected final int targetChance;
  public final int aabbSize;
  public final double speed;
  public final float minDistance;
  protected T nearestTarget;
  /** This filter is applied to the Entity search. Only matching entities will be targeted. */

  public MoveTowardsClosestEntityGoal(CreatureEntity entity, Class<T> targetClass, double speed, int targetChance, int aabbSize, float minDistance) {
    super(entity, true);
    this.targetChance = targetChance;
    this.targetClass = targetClass;
    this.aabbSize = aabbSize;
    this.speed = speed;
    this.minDistance = minDistance;
  }

  public MoveTowardsClosestEntityGoal(CreatureEntity entity, Class<T> targetClass, double speed, int aabbSize, float minDistance) {
    this(entity, targetClass, speed, 0, aabbSize, minDistance);
  }

  public boolean checkTarget(T entity) {
    return entity.getDistanceSq(goalOwner) > 0.010000000000000002D;
  }

  public boolean shouldExecute() {
    if (this.targetChance > 0 && this.goalOwner.getRNG().nextInt(this.targetChance) != 0) {
      return false;
    } else {
      this.findNearestTarget();
      return this.nearestTarget != null;
    }
  }

  public AxisAlignedBB getTargetableArea(double p_188511_1_) {
    return this.goalOwner.getBoundingBox().grow(p_188511_1_, 4.0D, p_188511_1_);
  }

  protected void findNearestTarget() {
    if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
      List<T> entities = this.goalOwner.world.getEntitiesWithinAABB(targetClass, getTargetableArea(aabbSize));
//      if(entities.size() > 0) {
//        this.nearestTarget = entities.get(0);
//      } else {
//        this.nearestTarget = null;
//      }

      this.nearestTarget = EntityUtils.getClosestEntity(goalOwner, entities, this::checkTarget);
    } else {
      this.nearestTarget = (T) this.goalOwner.world.getClosestPlayer(getEntityPredicate(), this.goalOwner, this.goalOwner.getX(), this.goalOwner.getEyeY(), this.goalOwner.getZ());
//      this.nearestTarget = (T) ;
    }
  }

  protected EntityPredicate getEntityPredicate() {
    return new EntityPredicate().setDistance(10).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks().setCustomPredicate((e) -> checkTarget((T) e));
  }

  @Override
  public void startExecuting() {

//    Vector3d vector3d = RandomPositionGenerator.findRandomTargetBlockTowards((CreatureEntity) this.goalOwner, 16, 7, this.nearestTarget.getPositionVec());
    this.goalOwner.getNavigator().tryMoveToXYZ(nearestTarget.getX(), nearestTarget.getY(), nearestTarget.getZ(), this.speed);
    super.startExecuting();
    afterExecuted();
  }

  protected void afterExecuted() { }

  public void setTargetEntity(@Nullable T p_234054_1_) {
    this.nearestTarget = p_234054_1_;
  }

//  public final int speed;
//
//  public MoveTowardsClosestEntityGoal(MobEntity entity, Class target, int speed) {
//    super(entity, target, true, true);
//    this.speed = speed;
//  }
//
//  @Override
//  public void startExecuting() {
//    Vector3d vector3d = RandomPositionGenerator.findRandomTargetBlockTowards((CreatureEntity) this.goalOwner, 16, 7, this.nearestTarget.getPositionVec());
//    if (vector3d != null) {
//      this.goalOwner.getNavigator().tryMoveToXYZ(vector3d.x, vector3d.y, vector3d.z, this.speed);
//    }
//    super.startExecuting();
//    afterExecuted();
//  }
//
//  protected void afterExecuted() { }
}