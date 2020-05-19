package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;

public class LookAtWithoutMovingGoal extends LookAtGoal {
   public LookAtWithoutMovingGoal(MobEntity entitylivingIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, float chanceIn) {
      super(entitylivingIn, watchTargetClass, maxDistance, chanceIn);
      this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
   }
}