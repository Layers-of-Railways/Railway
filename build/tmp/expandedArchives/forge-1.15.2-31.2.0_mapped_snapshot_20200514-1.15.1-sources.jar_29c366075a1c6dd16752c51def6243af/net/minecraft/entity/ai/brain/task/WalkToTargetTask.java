package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class WalkToTargetTask extends Task<MobEntity> {
   @Nullable
   private Path field_220488_a;
   @Nullable
   private BlockPos field_220489_b;
   private float field_220490_c;
   private int field_220491_d;

   public WalkToTargetTask(int p_i50356_1_) {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_PRESENT), p_i50356_1_);
   }

   protected boolean shouldExecute(ServerWorld worldIn, MobEntity owner) {
      Brain<?> brain = owner.getBrain();
      WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
      if (!this.hasReachedTarget(owner, walktarget) && this.func_220487_a(owner, walktarget, worldIn.getGameTime())) {
         this.field_220489_b = walktarget.getTarget().getBlockPos();
         return true;
      } else {
         brain.removeMemory(MemoryModuleType.WALK_TARGET);
         return false;
      }
   }

   protected boolean shouldContinueExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn) {
      if (this.field_220488_a != null && this.field_220489_b != null) {
         Optional<WalkTarget> optional = entityIn.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
         PathNavigator pathnavigator = entityIn.getNavigator();
         return !pathnavigator.noPath() && optional.isPresent() && !this.hasReachedTarget(entityIn, optional.get());
      } else {
         return false;
      }
   }

   protected void resetTask(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn) {
      entityIn.getNavigator().clearPath();
      entityIn.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
      entityIn.getBrain().removeMemory(MemoryModuleType.PATH);
      this.field_220488_a = null;
   }

   protected void startExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn) {
      entityIn.getBrain().setMemory(MemoryModuleType.PATH, this.field_220488_a);
      entityIn.getNavigator().setPath(this.field_220488_a, (double)this.field_220490_c);
      this.field_220491_d = worldIn.getRandom().nextInt(10);
   }

   protected void updateTask(ServerWorld worldIn, MobEntity owner, long gameTime) {
      --this.field_220491_d;
      if (this.field_220491_d <= 0) {
         Path path = owner.getNavigator().getPath();
         Brain<?> brain = owner.getBrain();
         if (this.field_220488_a != path) {
            this.field_220488_a = path;
            brain.setMemory(MemoryModuleType.PATH, path);
         }

         if (path != null && this.field_220489_b != null) {
            WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
            if (walktarget.getTarget().getBlockPos().distanceSq(this.field_220489_b) > 4.0D && this.func_220487_a(owner, walktarget, worldIn.getGameTime())) {
               this.field_220489_b = walktarget.getTarget().getBlockPos();
               this.startExecuting(worldIn, owner, gameTime);
            }

         }
      }
   }

   private boolean func_220487_a(MobEntity p_220487_1_, WalkTarget p_220487_2_, long p_220487_3_) {
      BlockPos blockpos = p_220487_2_.getTarget().getBlockPos();
      this.field_220488_a = p_220487_1_.getNavigator().getPathToPos(blockpos, 0);
      this.field_220490_c = p_220487_2_.getSpeed();
      if (!this.hasReachedTarget(p_220487_1_, p_220487_2_)) {
         Brain<?> brain = p_220487_1_.getBrain();
         boolean flag = this.field_220488_a != null && this.field_220488_a.reachesTarget();
         if (flag) {
            brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, Optional.empty());
         } else if (!brain.hasMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
            brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, p_220487_3_);
         }

         if (this.field_220488_a != null) {
            return true;
         }

         Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards((CreatureEntity)p_220487_1_, 10, 7, new Vec3d(blockpos));
         if (vec3d != null) {
            this.field_220488_a = p_220487_1_.getNavigator().getPathToPos(vec3d.x, vec3d.y, vec3d.z, 0);
            return this.field_220488_a != null;
         }
      }

      return false;
   }

   private boolean hasReachedTarget(MobEntity p_220486_1_, WalkTarget p_220486_2_) {
      return p_220486_2_.getTarget().getBlockPos().manhattanDistance(new BlockPos(p_220486_1_)) <= p_220486_2_.getDistance();
   }
}