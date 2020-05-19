package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DolphinJumpGoal extends JumpGoal {
   private static final int[] JUMP_DISTANCES = new int[]{0, 1, 4, 5, 6, 7};
   private final DolphinEntity field_220711_b;
   private final int field_220712_c;
   private boolean field_220713_d;

   public DolphinJumpGoal(DolphinEntity p_i50329_1_, int p_i50329_2_) {
      this.field_220711_b = p_i50329_1_;
      this.field_220712_c = p_i50329_2_;
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      if (this.field_220711_b.getRNG().nextInt(this.field_220712_c) != 0) {
         return false;
      } else {
         Direction direction = this.field_220711_b.getAdjustedHorizontalFacing();
         int i = direction.getXOffset();
         int j = direction.getZOffset();
         BlockPos blockpos = new BlockPos(this.field_220711_b);

         for(int k : JUMP_DISTANCES) {
            if (!this.canJumpTo(blockpos, i, j, k) || !this.isAirAbove(blockpos, i, j, k)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
      BlockPos blockpos = pos.add(dx * scale, 0, dz * scale);
      return this.field_220711_b.world.getFluidState(blockpos).isTagged(FluidTags.WATER) && !this.field_220711_b.world.getBlockState(blockpos).getMaterial().blocksMovement();
   }

   private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
      return this.field_220711_b.world.getBlockState(pos.add(dx * scale, 1, dz * scale)).isAir() && this.field_220711_b.world.getBlockState(pos.add(dx * scale, 2, dz * scale)).isAir();
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      double d0 = this.field_220711_b.getMotion().y;
      return (!(d0 * d0 < (double)0.03F) || this.field_220711_b.rotationPitch == 0.0F || !(Math.abs(this.field_220711_b.rotationPitch) < 10.0F) || !this.field_220711_b.isInWater()) && !this.field_220711_b.onGround;
   }

   public boolean isPreemptible() {
      return false;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      Direction direction = this.field_220711_b.getAdjustedHorizontalFacing();
      this.field_220711_b.setMotion(this.field_220711_b.getMotion().add((double)direction.getXOffset() * 0.6D, 0.7D, (double)direction.getZOffset() * 0.6D));
      this.field_220711_b.getNavigator().clearPath();
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.field_220711_b.rotationPitch = 0.0F;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      boolean flag = this.field_220713_d;
      if (!flag) {
         IFluidState ifluidstate = this.field_220711_b.world.getFluidState(new BlockPos(this.field_220711_b));
         this.field_220713_d = ifluidstate.isTagged(FluidTags.WATER);
      }

      if (this.field_220713_d && !flag) {
         this.field_220711_b.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
      }

      Vec3d vec3d = this.field_220711_b.getMotion();
      if (vec3d.y * vec3d.y < (double)0.03F && this.field_220711_b.rotationPitch != 0.0F) {
         this.field_220711_b.rotationPitch = MathHelper.rotLerp(this.field_220711_b.rotationPitch, 0.0F, 0.2F);
      } else {
         double d0 = Math.sqrt(Entity.horizontalMag(vec3d));
         double d1 = Math.signum(-vec3d.y) * Math.acos(d0 / vec3d.length()) * (double)(180F / (float)Math.PI);
         this.field_220711_b.rotationPitch = (float)d1;
      }

   }
}