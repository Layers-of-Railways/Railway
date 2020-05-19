package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class InteractWithEntityTask<E extends LivingEntity, T extends LivingEntity> extends Task<E> {
   private final int field_220446_a;
   private final float field_220447_b;
   private final EntityType<? extends T> field_220448_c;
   private final int field_220449_d;
   private final Predicate<T> field_220450_e;
   private final Predicate<E> field_220451_f;
   private final MemoryModuleType<T> field_220452_g;

   public InteractWithEntityTask(EntityType<? extends T> p_i50363_1_, int p_i50363_2_, Predicate<E> p_i50363_3_, Predicate<T> p_i50363_4_, MemoryModuleType<T> p_i50363_5_, float p_i50363_6_, int p_i50363_7_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, p_i50363_5_, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220448_c = p_i50363_1_;
      this.field_220447_b = p_i50363_6_;
      this.field_220449_d = p_i50363_2_ * p_i50363_2_;
      this.field_220446_a = p_i50363_7_;
      this.field_220450_e = p_i50363_4_;
      this.field_220451_f = p_i50363_3_;
      this.field_220452_g = p_i50363_5_;
   }

   public static <T extends LivingEntity> InteractWithEntityTask<LivingEntity, T> func_220445_a(EntityType<? extends T> p_220445_0_, int p_220445_1_, MemoryModuleType<T> p_220445_2_, float p_220445_3_, int p_220445_4_) {
      return new InteractWithEntityTask<>(p_220445_0_, p_220445_1_, (p_220441_0_) -> {
         return true;
      }, (p_220442_0_) -> {
         return true;
      }, p_220445_2_, p_220445_3_, p_220445_4_);
   }

   protected boolean shouldExecute(ServerWorld worldIn, E owner) {
      return this.field_220451_f.test(owner) && owner.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch((p_220444_1_) -> {
         return this.field_220448_c.equals(p_220444_1_.getType()) && this.field_220450_e.test((T)p_220444_1_);
      });
   }

   protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn) {
      Brain<?> brain = entityIn.getBrain();
      brain.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((p_220437_3_) -> {
         p_220437_3_.stream().filter((p_220440_1_) -> {
            return this.field_220448_c.equals(p_220440_1_.getType());
         }).map((p_220439_0_) -> {
            return (T)p_220439_0_;
         }).filter((p_220443_2_) -> {
            return p_220443_2_.getDistanceSq(entityIn) <= (double)this.field_220449_d;
         }).filter(this.field_220450_e).findFirst().ifPresent((p_220438_2_) -> {
            brain.setMemory(this.field_220452_g, (T)p_220438_2_);
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220438_2_));
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(p_220438_2_), this.field_220447_b, this.field_220446_a));
         });
      });
   }
}