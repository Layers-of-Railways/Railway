package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class NearestLivingEntitiesSensor extends Sensor<LivingEntity> {
   private static final EntityPredicate field_220982_b = (new EntityPredicate()).setDistance(16.0D).allowFriendlyFire().setSkipAttackChecks().setLineOfSiteRequired();

   protected void update(ServerWorld worldIn, LivingEntity entityIn) {
      List<LivingEntity> list = worldIn.getEntitiesWithinAABB(LivingEntity.class, entityIn.getBoundingBox().grow(16.0D, 16.0D, 16.0D), (p_220980_1_) -> {
         return p_220980_1_ != entityIn && p_220980_1_.isAlive();
      });
      list.sort(Comparator.comparingDouble(entityIn::getDistanceSq));
      Brain<?> brain = entityIn.getBrain();
      brain.setMemory(MemoryModuleType.MOBS, list);
      brain.setMemory(MemoryModuleType.VISIBLE_MOBS, list.stream().filter((p_220981_1_) -> {
         return field_220982_b.canTarget(entityIn, p_220981_1_);
      }).filter(entityIn::canEntityBeSeen).collect(Collectors.toList()));
   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS);
   }
}