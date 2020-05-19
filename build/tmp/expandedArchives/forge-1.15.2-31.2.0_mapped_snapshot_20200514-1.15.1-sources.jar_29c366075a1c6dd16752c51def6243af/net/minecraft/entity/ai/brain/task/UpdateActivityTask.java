package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public class UpdateActivityTask extends Task<LivingEntity> {
   public UpdateActivityTask() {
      super(ImmutableMap.of());
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      entityIn.getBrain().updateActivity(worldIn.getDayTime(), worldIn.getGameTime());
   }
}