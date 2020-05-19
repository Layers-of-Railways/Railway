package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class FleeTask extends Task<CreatureEntity> {
   private final MemoryModuleType<? extends Entity> field_220541_a;
   private final float field_220542_b;

   public FleeTask(MemoryModuleType<? extends Entity> p_i50346_1_, float p_i50346_2_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, p_i50346_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220541_a = p_i50346_1_;
      this.field_220542_b = p_i50346_2_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
      Entity entity = owner.getBrain().getMemory(this.field_220541_a).get();
      return owner.getDistanceSq(entity) < 36.0D;
   }

   protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
      Entity entity = entityIn.getBrain().getMemory(this.field_220541_a).get();
      func_220540_a(entityIn, entity, this.field_220542_b);
   }

   public static void func_220540_a(CreatureEntity p_220540_0_, Entity p_220540_1_, float speedIn) {
      for(int i = 0; i < 10; ++i) {
         Vec3d vec3d = RandomPositionGenerator.func_223548_b(p_220540_0_, 16, 7, p_220540_1_.getPositionVec());
         if (vec3d != null) {
            p_220540_0_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, speedIn, 0));
            return;
         }
      }

   }
}