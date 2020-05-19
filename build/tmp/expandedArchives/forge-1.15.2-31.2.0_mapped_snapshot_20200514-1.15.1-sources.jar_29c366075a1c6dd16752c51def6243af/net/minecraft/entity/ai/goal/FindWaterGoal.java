package net.minecraft.entity.ai.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FindWaterGoal extends Goal {
   private final CreatureEntity field_205152_a;

   public FindWaterGoal(CreatureEntity p_i48936_1_) {
      this.field_205152_a = p_i48936_1_;
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      return this.field_205152_a.onGround && !this.field_205152_a.world.getFluidState(new BlockPos(this.field_205152_a)).isTagged(FluidTags.WATER);
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      BlockPos blockpos = null;

      for(BlockPos blockpos1 : BlockPos.getAllInBoxMutable(MathHelper.floor(this.field_205152_a.getPosX() - 2.0D), MathHelper.floor(this.field_205152_a.getPosY() - 2.0D), MathHelper.floor(this.field_205152_a.getPosZ() - 2.0D), MathHelper.floor(this.field_205152_a.getPosX() + 2.0D), MathHelper.floor(this.field_205152_a.getPosY()), MathHelper.floor(this.field_205152_a.getPosZ() + 2.0D))) {
         if (this.field_205152_a.world.getFluidState(blockpos1).isTagged(FluidTags.WATER)) {
            blockpos = blockpos1;
            break;
         }
      }

      if (blockpos != null) {
         this.field_205152_a.getMoveHelper().setMoveTo((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D);
      }

   }
}