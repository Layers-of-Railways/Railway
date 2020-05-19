package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class DummySensor extends Sensor<LivingEntity> {
   protected void update(ServerWorld worldIn, LivingEntity entityIn) {
   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of();
   }
}