package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class MoveTowardsVillageGoal extends RandomWalkingGoal {
   public MoveTowardsVillageGoal(CreatureEntity p_i50325_1_, double p_i50325_2_) {
      super(p_i50325_1_, p_i50325_2_, 10);
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      ServerWorld serverworld = (ServerWorld)this.creature.world;
      BlockPos blockpos = new BlockPos(this.creature);
      return serverworld.isVillage(blockpos) ? false : super.shouldExecute();
   }

   @Nullable
   protected Vec3d getPosition() {
      ServerWorld serverworld = (ServerWorld)this.creature.world;
      BlockPos blockpos = new BlockPos(this.creature);
      SectionPos sectionpos = SectionPos.from(blockpos);
      SectionPos sectionpos1 = BrainUtil.func_220617_a(serverworld, sectionpos, 2);
      return sectionpos1 != sectionpos ? RandomPositionGenerator.findRandomTargetBlockTowards(this.creature, 10, 7, new Vec3d(sectionpos1.getCenter())) : null;
   }
}