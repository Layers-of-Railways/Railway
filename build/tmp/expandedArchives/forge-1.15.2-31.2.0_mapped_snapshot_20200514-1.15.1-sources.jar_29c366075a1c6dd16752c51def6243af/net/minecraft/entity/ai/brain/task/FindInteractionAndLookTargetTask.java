package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class FindInteractionAndLookTargetTask extends Task<LivingEntity> {
   private final EntityType<?> field_220533_a;
   private final int field_220534_b;
   private final Predicate<LivingEntity> field_220535_c;
   private final Predicate<LivingEntity> field_220536_d;

   public FindInteractionAndLookTargetTask(EntityType<?> p_i50347_1_, int p_i50347_2_, Predicate<LivingEntity> p_i50347_3_, Predicate<LivingEntity> p_i50347_4_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220533_a = p_i50347_1_;
      this.field_220534_b = p_i50347_2_ * p_i50347_2_;
      this.field_220535_c = p_i50347_4_;
      this.field_220536_d = p_i50347_3_;
   }

   public FindInteractionAndLookTargetTask(EntityType<?> p_i50348_1_, int p_i50348_2_) {
      this(p_i50348_1_, p_i50348_2_, (p_220528_0_) -> {
         return true;
      }, (p_220531_0_) -> {
         return true;
      });
   }

   public boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      return this.field_220536_d.test(owner) && this.func_220530_b(owner).stream().anyMatch(this::func_220532_a);
   }

   public void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      super.startExecuting(worldIn, entityIn, gameTimeIn);
      Brain<?> brain = entityIn.getBrain();
      brain.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((p_220526_3_) -> {
         p_220526_3_.stream().filter((p_220529_2_) -> {
            return p_220529_2_.getDistanceSq(entityIn) <= (double)this.field_220534_b;
         }).filter(this::func_220532_a).findFirst().ifPresent((p_220527_1_) -> {
            brain.setMemory(MemoryModuleType.INTERACTION_TARGET, p_220527_1_);
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220527_1_));
         });
      });
   }

   private boolean func_220532_a(LivingEntity p_220532_1_) {
      return this.field_220533_a.equals(p_220532_1_.getType()) && this.field_220535_c.test(p_220532_1_);
   }

   private List<LivingEntity> func_220530_b(LivingEntity p_220530_1_) {
      return p_220530_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).get();
   }
}