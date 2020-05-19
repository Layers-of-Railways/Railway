package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class MoveThroughVillageAtNightGoal extends Goal {
   private final CreatureEntity field_220756_a;
   private final int field_220757_b;
   @Nullable
   private BlockPos field_220758_c;

   public MoveThroughVillageAtNightGoal(CreatureEntity p_i50321_1_, int p_i50321_2_) {
      this.field_220756_a = p_i50321_1_;
      this.field_220757_b = p_i50321_2_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      if (this.field_220756_a.isBeingRidden()) {
         return false;
      } else if (this.field_220756_a.world.isDaytime()) {
         return false;
      } else if (this.field_220756_a.getRNG().nextInt(this.field_220757_b) != 0) {
         return false;
      } else {
         ServerWorld serverworld = (ServerWorld)this.field_220756_a.world;
         BlockPos blockpos = new BlockPos(this.field_220756_a);
         if (!serverworld.isCloseToVillage(blockpos, 6)) {
            return false;
         } else {
            Vec3d vec3d = RandomPositionGenerator.func_221024_a(this.field_220756_a, 15, 7, (p_220755_1_) -> {
               return (double)(-serverworld.sectionsToVillage(SectionPos.from(p_220755_1_)));
            });
            this.field_220758_c = vec3d == null ? null : new BlockPos(vec3d);
            return this.field_220758_c != null;
         }
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.field_220758_c != null && !this.field_220756_a.getNavigator().noPath() && this.field_220756_a.getNavigator().getTargetPos().equals(this.field_220758_c);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (this.field_220758_c != null) {
         PathNavigator pathnavigator = this.field_220756_a.getNavigator();
         if (pathnavigator.noPath() && !this.field_220758_c.withinDistance(this.field_220756_a.getPositionVec(), 10.0D)) {
            Vec3d vec3d = new Vec3d(this.field_220758_c);
            Vec3d vec3d1 = this.field_220756_a.getPositionVec();
            Vec3d vec3d2 = vec3d1.subtract(vec3d);
            vec3d = vec3d2.scale(0.4D).add(vec3d);
            Vec3d vec3d3 = vec3d.subtract(vec3d1).normalize().scale(10.0D).add(vec3d1);
            BlockPos blockpos = new BlockPos(vec3d3);
            blockpos = this.field_220756_a.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos);
            if (!pathnavigator.tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D)) {
               this.func_220754_g();
            }
         }

      }
   }

   private void func_220754_g() {
      Random random = this.field_220756_a.getRNG();
      BlockPos blockpos = this.field_220756_a.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (new BlockPos(this.field_220756_a)).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
      this.field_220756_a.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D);
   }
}