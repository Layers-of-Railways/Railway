package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class ForgetRaidTask extends Task<LivingEntity> {
   public ForgetRaidTask() {
      super(ImmutableMap.of());
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      return worldIn.rand.nextInt(20) == 0;
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      Brain<?> brain = entityIn.getBrain();
      Raid raid = worldIn.findRaid(new BlockPos(entityIn));
      if (raid == null || raid.isStopped() || raid.isLoss()) {
         brain.setFallbackActivity(Activity.IDLE);
         brain.updateActivity(worldIn.getDayTime(), worldIn.getGameTime());
      }

   }
}