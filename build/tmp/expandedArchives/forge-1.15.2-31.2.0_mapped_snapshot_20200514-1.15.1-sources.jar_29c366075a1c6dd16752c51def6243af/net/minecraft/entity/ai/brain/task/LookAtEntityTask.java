package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class LookAtEntityTask extends Task<LivingEntity> {
   private final Predicate<LivingEntity> field_220519_a;
   private final float field_220520_b;

   public LookAtEntityTask(EntityClassification p_i50350_1_, float p_i50350_2_) {
      this((p_220514_1_) -> {
         return p_i50350_1_.equals(p_220514_1_.getType().getClassification());
      }, p_i50350_2_);
   }

   public LookAtEntityTask(EntityType<?> p_i50351_1_, float p_i50351_2_) {
      this((p_220518_1_) -> {
         return p_i50351_1_.equals(p_220518_1_.getType());
      }, p_i50351_2_);
   }

   public LookAtEntityTask(Predicate<LivingEntity> p_i50352_1_, float p_i50352_2_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220519_a = p_i50352_1_;
      this.field_220520_b = p_i50352_2_ * p_i50352_2_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      return owner.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch(this.field_220519_a);
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      Brain<?> brain = entityIn.getBrain();
      brain.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((p_220515_3_) -> {
         p_220515_3_.stream().filter(this.field_220519_a).filter((p_220517_2_) -> {
            return p_220517_2_.getDistanceSq(entityIn) <= (double)this.field_220520_b;
         }).findFirst().ifPresent((p_220516_1_) -> {
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220516_1_));
         });
      });
   }
}