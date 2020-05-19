package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class HurtBySensor extends Sensor<LivingEntity> {
   protected void update(ServerWorld worldIn, LivingEntity entityIn) {
      Brain<?> brain = entityIn.getBrain();
      if (entityIn.getLastDamageSource() != null) {
         brain.setMemory(MemoryModuleType.HURT_BY, entityIn.getLastDamageSource());
         Entity entity = brain.getMemory(MemoryModuleType.HURT_BY).get().getTrueSource();
         if (entity instanceof LivingEntity) {
            brain.setMemory(MemoryModuleType.HURT_BY_ENTITY, (LivingEntity)entity);
         }
      } else {
         brain.removeMemory(MemoryModuleType.HURT_BY);
      }

   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
   }
}