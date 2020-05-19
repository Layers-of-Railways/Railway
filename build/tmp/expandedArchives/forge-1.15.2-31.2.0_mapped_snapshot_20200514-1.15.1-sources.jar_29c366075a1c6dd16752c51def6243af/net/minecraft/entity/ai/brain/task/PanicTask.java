package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.world.server.ServerWorld;

public class PanicTask extends Task<VillagerEntity> {
   public PanicTask() {
      super(ImmutableMap.of());
   }

   protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      return hasBeenHurt(entityIn) || hostileNearby(entityIn);
   }

   protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      if (hasBeenHurt(entityIn) || hostileNearby(entityIn)) {
         Brain<?> brain = entityIn.getBrain();
         if (!brain.hasActivity(Activity.PANIC)) {
            brain.removeMemory(MemoryModuleType.PATH);
            brain.removeMemory(MemoryModuleType.WALK_TARGET);
            brain.removeMemory(MemoryModuleType.LOOK_TARGET);
            brain.removeMemory(MemoryModuleType.BREED_TARGET);
            brain.removeMemory(MemoryModuleType.INTERACTION_TARGET);
         }

         brain.switchTo(Activity.PANIC);
      }

   }

   protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
      if (gameTime % 100L == 0L) {
         owner.spawnGolems(gameTime, 3);
      }

   }

   public static boolean hostileNearby(LivingEntity p_220513_0_) {
      return p_220513_0_.getBrain().hasMemory(MemoryModuleType.NEAREST_HOSTILE);
   }

   public static boolean hasBeenHurt(LivingEntity p_220512_0_) {
      return p_220512_0_.getBrain().hasMemory(MemoryModuleType.HURT_BY);
   }
}