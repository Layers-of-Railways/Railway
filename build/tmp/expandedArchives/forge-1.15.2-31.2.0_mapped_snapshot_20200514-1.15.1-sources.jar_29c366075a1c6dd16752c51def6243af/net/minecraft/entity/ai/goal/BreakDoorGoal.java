package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class BreakDoorGoal extends InteractDoorGoal {
   private final Predicate<Difficulty> field_220699_g;
   protected int breakingTime;
   protected int previousBreakProgress = -1;
   protected int field_220698_c = -1;

   public BreakDoorGoal(MobEntity p_i50332_1_, Predicate<Difficulty> p_i50332_2_) {
      super(p_i50332_1_);
      this.field_220699_g = p_i50332_2_;
   }

   public BreakDoorGoal(MobEntity p_i50333_1_, int p_i50333_2_, Predicate<Difficulty> p_i50333_3_) {
      this(p_i50333_1_, p_i50333_3_);
      this.field_220698_c = p_i50333_2_;
   }

   protected int func_220697_f() {
      return Math.max(240, this.field_220698_c);
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      if (!super.shouldExecute()) {
         return false;
      } else if (!net.minecraftforge.common.ForgeHooks.canEntityDestroy(this.entity.world, this.doorPosition, this.entity)) {
         return false;
      } else {
         return this.func_220696_a(this.entity.world.getDifficulty()) && !this.canDestroy();
      }
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      super.startExecuting();
      this.breakingTime = 0;
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.breakingTime <= this.func_220697_f() && !this.canDestroy() && this.doorPosition.withinDistance(this.entity.getPositionVec(), 2.0D) && this.func_220696_a(this.entity.world.getDifficulty());
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      super.resetTask();
      this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, -1);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      super.tick();
      if (this.entity.getRNG().nextInt(20) == 0) {
         this.entity.world.playEvent(1019, this.doorPosition, 0);
         if (!this.entity.isSwingInProgress) {
            this.entity.swingArm(this.entity.getActiveHand());
         }
      }

      ++this.breakingTime;
      int i = (int)((float)this.breakingTime / (float)this.func_220697_f() * 10.0F);
      if (i != this.previousBreakProgress) {
         this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, i);
         this.previousBreakProgress = i;
      }

      if (this.breakingTime == this.func_220697_f() && this.func_220696_a(this.entity.world.getDifficulty())) {
         this.entity.world.removeBlock(this.doorPosition, false);
         this.entity.world.playEvent(1021, this.doorPosition, 0);
         this.entity.world.playEvent(2001, this.doorPosition, Block.getStateId(this.entity.world.getBlockState(this.doorPosition)));
      }

   }

   private boolean func_220696_a(Difficulty p_220696_1_) {
      return this.field_220699_g.test(p_220696_1_);
   }
}