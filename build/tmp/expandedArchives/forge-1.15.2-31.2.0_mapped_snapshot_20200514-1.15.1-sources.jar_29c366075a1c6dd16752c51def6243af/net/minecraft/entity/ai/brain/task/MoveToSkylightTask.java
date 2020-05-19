package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class MoveToSkylightTask extends Task<LivingEntity> {
   private final float field_220494_a;

   public MoveToSkylightTask(float p_i50357_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220494_a = p_i50357_1_;
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      Optional<Vec3d> optional = Optional.ofNullable(this.func_220493_b(worldIn, entityIn));
      if (optional.isPresent()) {
         entityIn.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220492_1_) -> {
            return new WalkTarget(p_220492_1_, this.field_220494_a, 0);
         }));
      }

   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      return !worldIn.canSeeSky(new BlockPos(owner));
   }

   @Nullable
   private Vec3d func_220493_b(ServerWorld p_220493_1_, LivingEntity p_220493_2_) {
      Random random = p_220493_2_.getRNG();
      BlockPos blockpos = new BlockPos(p_220493_2_);

      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
         if (func_226306_a_(p_220493_1_, p_220493_2_, blockpos1)) {
            return new Vec3d(blockpos1);
         }
      }

      return null;
   }

   public static boolean func_226306_a_(ServerWorld p_226306_0_, LivingEntity p_226306_1_, BlockPos p_226306_2_) {
      return p_226306_0_.canSeeSky(p_226306_2_) && (double)p_226306_0_.getHeight(Heightmap.Type.MOTION_BLOCKING, p_226306_2_).getY() <= p_226306_1_.getPosY();
   }
}