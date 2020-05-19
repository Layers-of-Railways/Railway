package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class WalkTowardsLookTargetTask extends Task<LivingEntity> {
   private final float field_220543_a;
   private final int field_220544_b;

   public WalkTowardsLookTargetTask(float p_i50344_1_, int p_i50344_2_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220543_a = p_i50344_1_;
      this.field_220544_b = p_i50344_2_;
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      Brain<?> brain = entityIn.getBrain();
      IPosWrapper iposwrapper = brain.getMemory(MemoryModuleType.LOOK_TARGET).get();
      brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(iposwrapper, this.field_220543_a, this.field_220544_b));
   }
}