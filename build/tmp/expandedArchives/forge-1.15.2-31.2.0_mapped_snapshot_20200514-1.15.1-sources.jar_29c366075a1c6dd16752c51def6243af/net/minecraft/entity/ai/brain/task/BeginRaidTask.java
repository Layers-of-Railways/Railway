package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class BeginRaidTask extends Task<LivingEntity> {
   public BeginRaidTask() {
      super(ImmutableMap.of());
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      return worldIn.rand.nextInt(20) == 0;
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      Brain<?> brain = entityIn.getBrain();
      Raid raid = worldIn.findRaid(new BlockPos(entityIn));
      if (raid != null) {
         if (raid.func_221297_c() && !raid.isBetweenWaves()) {
            brain.setFallbackActivity(Activity.RAID);
            brain.switchTo(Activity.RAID);
         } else {
            brain.setFallbackActivity(Activity.PRE_RAID);
            brain.switchTo(Activity.PRE_RAID);
         }
      }

   }
}