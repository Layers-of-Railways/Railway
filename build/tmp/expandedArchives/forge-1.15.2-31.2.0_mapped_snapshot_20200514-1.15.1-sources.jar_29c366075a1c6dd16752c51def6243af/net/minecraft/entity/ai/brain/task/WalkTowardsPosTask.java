package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class WalkTowardsPosTask extends Task<CreatureEntity> {
   private final MemoryModuleType<GlobalPos> field_220581_a;
   private final int field_220582_b;
   private final int field_220583_c;
   private long field_220584_d;

   public WalkTowardsPosTask(MemoryModuleType<GlobalPos> p_i50341_1_, int p_i50341_2_, int p_i50341_3_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, p_i50341_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220581_a = p_i50341_1_;
      this.field_220582_b = p_i50341_2_;
      this.field_220583_c = p_i50341_3_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
      Optional<GlobalPos> optional = owner.getBrain().getMemory(this.field_220581_a);
      return optional.isPresent() && Objects.equals(worldIn.getDimension().getType(), optional.get().getDimension()) && optional.get().getPos().withinDistance(owner.getPositionVec(), (double)this.field_220583_c);
   }

   protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
      if (gameTimeIn > this.field_220584_d) {
         Brain<?> brain = entityIn.getBrain();
         Optional<GlobalPos> optional = brain.getMemory(this.field_220581_a);
         optional.ifPresent((p_220580_2_) -> {
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_220580_2_.getPos(), 0.4F, this.field_220582_b));
         });
         this.field_220584_d = gameTimeIn + 80L;
      }

   }
}