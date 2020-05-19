package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.world.server.ServerWorld;

public class ClearHurtTask extends Task<VillagerEntity> {
   public ClearHurtTask() {
      super(ImmutableMap.of());
   }

   protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      boolean flag = PanicTask.hasBeenHurt(entityIn) || PanicTask.hostileNearby(entityIn) || func_220394_a(entityIn);
      if (!flag) {
         entityIn.getBrain().removeMemory(MemoryModuleType.HURT_BY);
         entityIn.getBrain().removeMemory(MemoryModuleType.HURT_BY_ENTITY);
         entityIn.getBrain().updateActivity(worldIn.getDayTime(), worldIn.getGameTime());
      }

   }

   private static boolean func_220394_a(VillagerEntity p_220394_0_) {
      return p_220394_0_.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).filter((p_223523_1_) -> {
         return p_223523_1_.getDistanceSq(p_220394_0_) <= 36.0D;
      }).isPresent();
   }
}