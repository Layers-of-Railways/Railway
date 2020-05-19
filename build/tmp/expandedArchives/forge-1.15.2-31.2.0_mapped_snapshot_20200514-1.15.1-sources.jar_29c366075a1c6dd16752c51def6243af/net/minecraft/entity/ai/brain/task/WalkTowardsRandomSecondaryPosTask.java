package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class WalkTowardsRandomSecondaryPosTask extends Task<VillagerEntity> {
   private final MemoryModuleType<List<GlobalPos>> field_220573_a;
   private final MemoryModuleType<GlobalPos> field_220574_b;
   private final float field_220575_c;
   private final int field_220576_d;
   private final int field_220577_e;
   private long field_220578_f;
   @Nullable
   private GlobalPos field_220579_g;

   public WalkTowardsRandomSecondaryPosTask(MemoryModuleType<List<GlobalPos>> p_i50340_1_, float p_i50340_2_, int p_i50340_3_, int p_i50340_4_, MemoryModuleType<GlobalPos> p_i50340_5_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, p_i50340_1_, MemoryModuleStatus.VALUE_PRESENT, p_i50340_5_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220573_a = p_i50340_1_;
      this.field_220575_c = p_i50340_2_;
      this.field_220576_d = p_i50340_3_;
      this.field_220577_e = p_i50340_4_;
      this.field_220574_b = p_i50340_5_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      Optional<List<GlobalPos>> optional = owner.getBrain().getMemory(this.field_220573_a);
      Optional<GlobalPos> optional1 = owner.getBrain().getMemory(this.field_220574_b);
      if (optional.isPresent() && optional1.isPresent()) {
         List<GlobalPos> list = optional.get();
         if (!list.isEmpty()) {
            this.field_220579_g = list.get(worldIn.getRandom().nextInt(list.size()));
            return this.field_220579_g != null && Objects.equals(worldIn.getDimension().getType(), this.field_220579_g.getDimension()) && optional1.get().getPos().withinDistance(owner.getPositionVec(), (double)this.field_220577_e);
         }
      }

      return false;
   }

   protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      if (gameTimeIn > this.field_220578_f && this.field_220579_g != null) {
         entityIn.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.field_220579_g.getPos(), this.field_220575_c, this.field_220576_d));
         this.field_220578_f = gameTimeIn + 100L;
      }

   }
}