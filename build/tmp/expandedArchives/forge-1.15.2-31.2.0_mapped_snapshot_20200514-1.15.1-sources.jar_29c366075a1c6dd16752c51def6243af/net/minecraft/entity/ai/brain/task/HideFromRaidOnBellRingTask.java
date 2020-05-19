package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class HideFromRaidOnBellRingTask extends Task<LivingEntity> {
   public HideFromRaidOnBellRingTask() {
      super(ImmutableMap.of(MemoryModuleType.HEARD_BELL_TIME, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      Brain<?> brain = entityIn.getBrain();
      Raid raid = worldIn.findRaid(new BlockPos(entityIn));
      if (raid == null) {
         brain.switchTo(Activity.HIDE);
      }

   }
}