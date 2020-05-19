package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class FindWalkTargetTask extends Task<CreatureEntity> {
   private final float speed;
   private final int maxXZ;
   private final int maxY;

   public FindWalkTargetTask(float speedIn) {
      this(speedIn, 10, 7);
   }

   public FindWalkTargetTask(float speedIn, int maxDistanceXZ, int maxDistanceY) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.speed = speedIn;
      this.maxXZ = maxDistanceXZ;
      this.maxY = maxDistanceY;
   }

   protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
      BlockPos blockpos = new BlockPos(entityIn);
      if (worldIn.isVillage(blockpos)) {
         this.func_220593_a(entityIn);
      } else {
         SectionPos sectionpos = SectionPos.from(blockpos);
         SectionPos sectionpos1 = BrainUtil.func_220617_a(worldIn, sectionpos, 2);
         if (sectionpos1 != sectionpos) {
            this.func_220594_a(entityIn, sectionpos1);
         } else {
            this.func_220593_a(entityIn);
         }
      }

   }

   private void func_220594_a(CreatureEntity p_220594_1_, SectionPos p_220594_2_) {
      Optional<Vec3d> optional = Optional.ofNullable(RandomPositionGenerator.findRandomTargetBlockTowards(p_220594_1_, this.maxXZ, this.maxY, new Vec3d(p_220594_2_.getCenter())));
      p_220594_1_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220596_1_) -> {
         return new WalkTarget(p_220596_1_, this.speed, 0);
      }));
   }

   private void func_220593_a(CreatureEntity p_220593_1_) {
      Optional<Vec3d> optional = Optional.ofNullable(RandomPositionGenerator.getLandPos(p_220593_1_, this.maxXZ, this.maxY));
      p_220593_1_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220595_1_) -> {
         return new WalkTarget(p_220595_1_, this.speed, 0);
      }));
   }
}