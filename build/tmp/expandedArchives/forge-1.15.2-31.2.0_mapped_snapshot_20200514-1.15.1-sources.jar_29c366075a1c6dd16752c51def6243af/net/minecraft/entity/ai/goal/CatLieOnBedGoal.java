package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CatLieOnBedGoal extends MoveToBlockGoal {
   private final CatEntity cat;

   public CatLieOnBedGoal(CatEntity catIn, double speed, int length) {
      super(catIn, speed, length, 6);
      this.cat = catIn;
      this.field_203112_e = -2;
      this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      return this.cat.isTamed() && !this.cat.isSitting() && !this.cat.func_213416_eg() && super.shouldExecute();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      super.startExecuting();
      this.cat.getAISit().setSitting(false);
   }

   protected int getRunDelay(CreatureEntity creatureIn) {
      return 40;
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      super.resetTask();
      this.cat.func_213419_u(false);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      super.tick();
      this.cat.getAISit().setSitting(false);
      if (!this.getIsAboveDestination()) {
         this.cat.func_213419_u(false);
      } else if (!this.cat.func_213416_eg()) {
         this.cat.func_213419_u(true);
      }

   }

   /**
    * Return true to set given position as destination
    */
   protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
      return worldIn.isAirBlock(pos.up()) && worldIn.getBlockState(pos).getBlock().isIn(BlockTags.BEDS);
   }
}