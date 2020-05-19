package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class FindHidingPlaceTask extends Task<LivingEntity> {
   private final float field_220457_a;
   private final int field_220458_b;
   private final int field_220459_c;
   private Optional<BlockPos> field_220460_d = Optional.empty();

   public FindHidingPlaceTask(int p_i50361_1_, float p_i50361_2_, int p_i50361_3_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryModuleStatus.REGISTERED, MemoryModuleType.HIDING_PLACE, MemoryModuleStatus.REGISTERED));
      this.field_220458_b = p_i50361_1_;
      this.field_220457_a = p_i50361_2_;
      this.field_220459_c = p_i50361_3_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      Optional<BlockPos> optional = worldIn.getPointOfInterestManager().find((p_220454_0_) -> {
         return p_220454_0_ == PointOfInterestType.HOME;
      }, (p_220456_0_) -> {
         return true;
      }, new BlockPos(owner), this.field_220459_c + 1, PointOfInterestManager.Status.ANY);
      if (optional.isPresent() && optional.get().withinDistance(owner.getPositionVec(), (double)this.field_220459_c)) {
         this.field_220460_d = optional;
      } else {
         this.field_220460_d = Optional.empty();
      }

      return true;
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      Brain<?> brain = entityIn.getBrain();
      Optional<BlockPos> optional = this.field_220460_d;
      if (!optional.isPresent()) {
         optional = worldIn.getPointOfInterestManager().getRandom((p_220453_0_) -> {
            return p_220453_0_ == PointOfInterestType.HOME;
         }, (p_220455_0_) -> {
            return true;
         }, PointOfInterestManager.Status.ANY, new BlockPos(entityIn), this.field_220458_b, entityIn.getRNG());
         if (!optional.isPresent()) {
            Optional<GlobalPos> optional1 = brain.getMemory(MemoryModuleType.HOME);
            if (optional1.isPresent()) {
               optional = Optional.of(optional1.get().getPos());
            }
         }
      }

      if (optional.isPresent()) {
         brain.removeMemory(MemoryModuleType.PATH);
         brain.removeMemory(MemoryModuleType.LOOK_TARGET);
         brain.removeMemory(MemoryModuleType.BREED_TARGET);
         brain.removeMemory(MemoryModuleType.INTERACTION_TARGET);
         brain.setMemory(MemoryModuleType.HIDING_PLACE, GlobalPos.of(worldIn.getDimension().getType(), optional.get()));
         if (!optional.get().withinDistance(entityIn.getPositionVec(), (double)this.field_220459_c)) {
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(optional.get(), this.field_220457_a, this.field_220459_c));
         }
      }

   }
}