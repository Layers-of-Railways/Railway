package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;

public class BreatheAirGoal extends Goal {
   private final CreatureEntity field_205142_a;

   public BreatheAirGoal(CreatureEntity p_i48940_1_) {
      this.field_205142_a = p_i48940_1_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      return this.field_205142_a.getAir() < 140;
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.shouldExecute();
   }

   public boolean isPreemptible() {
      return false;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.navigate();
   }

   private void navigate() {
      Iterable<BlockPos> iterable = BlockPos.getAllInBoxMutable(MathHelper.floor(this.field_205142_a.getPosX() - 1.0D), MathHelper.floor(this.field_205142_a.getPosY()), MathHelper.floor(this.field_205142_a.getPosZ() - 1.0D), MathHelper.floor(this.field_205142_a.getPosX() + 1.0D), MathHelper.floor(this.field_205142_a.getPosY() + 8.0D), MathHelper.floor(this.field_205142_a.getPosZ() + 1.0D));
      BlockPos blockpos = null;

      for(BlockPos blockpos1 : iterable) {
         if (this.canBreatheAt(this.field_205142_a.world, blockpos1)) {
            blockpos = blockpos1;
            break;
         }
      }

      if (blockpos == null) {
         blockpos = new BlockPos(this.field_205142_a.getPosX(), this.field_205142_a.getPosY() + 8.0D, this.field_205142_a.getPosZ());
      }

      this.field_205142_a.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)(blockpos.getY() + 1), (double)blockpos.getZ(), 1.0D);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      this.navigate();
      this.field_205142_a.moveRelative(0.02F, new Vec3d((double)this.field_205142_a.moveStrafing, (double)this.field_205142_a.moveVertical, (double)this.field_205142_a.moveForward));
      this.field_205142_a.move(MoverType.SELF, this.field_205142_a.getMotion());
   }

   private boolean canBreatheAt(IWorldReader worldIn, BlockPos pos) {
      BlockState blockstate = worldIn.getBlockState(pos);
      return (worldIn.getFluidState(pos).isEmpty() || blockstate.getBlock() == Blocks.BUBBLE_COLUMN) && blockstate.allowsMovement(worldIn, pos, PathType.LAND);
   }
}