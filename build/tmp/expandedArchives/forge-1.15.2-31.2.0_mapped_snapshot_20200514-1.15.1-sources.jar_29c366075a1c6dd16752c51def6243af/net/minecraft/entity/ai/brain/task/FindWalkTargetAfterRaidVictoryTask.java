package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class FindWalkTargetAfterRaidVictoryTask extends FindWalkTargetTask {
   public FindWalkTargetAfterRaidVictoryTask(float p_i50337_1_) {
      super(p_i50337_1_);
   }

   protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
      Raid raid = worldIn.findRaid(new BlockPos(owner));
      return raid != null && raid.isVictory() && super.shouldExecute(worldIn, owner);
   }
}