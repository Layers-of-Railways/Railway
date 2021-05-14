package com.railwayteam.railways.goals;

import com.railwayteam.railways.util.EntityUtils;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;

public class WalkToAndSitInNearestMinecart extends MoveTowardsClosestEntityGoal<MinecartEntity> {
    public final int sitDistance;

    public WalkToAndSitInNearestMinecart(CreatureEntity entity, double speed, int targetChance, int aabbSize, int sitDistance) {
        super(entity, MinecartEntity.class, speed, targetChance, aabbSize);
        this.sitDistance = sitDistance;
    }

    public WalkToAndSitInNearestMinecart(CreatureEntity entity, double speed, int aabbSize, int sitDistance) {
        this(entity, speed, 0, aabbSize, sitDistance);
    }

    @Override
    public boolean checkTarget(MinecartEntity entity) {
        return EntityUtils.canEntitySitInMinecart(goalOwner, entity);
    }

    @Override
    protected void afterExecuted() {
        if (goalOwner.getDistance(nearestTarget) <= sitDistance) {
            goalOwner.startRiding(nearestTarget);
        }
        super.afterExecuted();
    }

    public boolean isEntityRidingSomething() {
        return goalOwner.getRidingEntity() != null;
    }

    @Override
    public boolean shouldExecute() {
        return super.shouldExecute() && !isEntityRidingSomething();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && !isEntityRidingSomething();
    }

    //    private static Class<MinecartEntity> minecartClass;


//    public WalkToAndSitInNearestMinecart(CreatureEntity entity, int speed) {
//      super(entity, MinecartEntity.class, speed);
//    }
//
//    protected MinecartEntity nearestTarget;
//
//    @Override
//    protected void findNearestTarget() {
//      super.nearestTargetx
//      if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
//        this.nearestTarget = this.goalOwner.world.getClosestEntityIncludingUngeneratedChunks(minecartClass, this.targetEntitySelector, this.goalOwner, this.goalOwner.getX(), this.goalOwner.getEyeY(), this.goalOwner.getZ(), this.getTargetableArea(this.getTargetDistance()));
//      } else {
//        this.nearestTarget = this.goalOwner.world.getClosestPlayer(this.targetEntitySelector, this.goalOwner, this.goalOwner.getX(), this.goalOwner.getEyeY(), this.goalOwner.getZ());
//      }
//      super.findNearestTarget();
//    }

//    @Override
//    protected void afterExecuted() {
//      if(this.nearestTarget != null) {
//        nearestTarget)
//      }
//      super.afterExecuted();
//    }
}
