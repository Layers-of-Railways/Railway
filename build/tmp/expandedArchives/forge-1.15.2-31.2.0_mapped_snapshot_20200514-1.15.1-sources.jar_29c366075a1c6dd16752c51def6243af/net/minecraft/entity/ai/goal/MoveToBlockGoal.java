package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public abstract class MoveToBlockGoal extends Goal {
   protected final CreatureEntity creature;
   public final double movementSpeed;
   /** Controls task execution delay */
   protected int runDelay;
   protected int timeoutCounter;
   private int maxStayTicks;
   /** Block to move to */
   protected BlockPos destinationBlock = BlockPos.ZERO;
   private boolean isAboveDestination;
   private final int searchLength;
   private final int field_203113_j;
   protected int field_203112_e;

   public MoveToBlockGoal(CreatureEntity creature, double speedIn, int length) {
      this(creature, speedIn, length, 1);
   }

   public MoveToBlockGoal(CreatureEntity creatureIn, double speed, int length, int p_i48796_5_) {
      this.creature = creatureIn;
      this.movementSpeed = speed;
      this.searchLength = length;
      this.field_203112_e = 0;
      this.field_203113_j = p_i48796_5_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      if (this.runDelay > 0) {
         --this.runDelay;
         return false;
      } else {
         this.runDelay = this.getRunDelay(this.creature);
         return this.searchForDestination();
      }
   }

   protected int getRunDelay(CreatureEntity creatureIn) {
      return 200 + creatureIn.getRNG().nextInt(200);
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.creature.world, this.destinationBlock);
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.func_220725_g();
      this.timeoutCounter = 0;
      this.maxStayTicks = this.creature.getRNG().nextInt(this.creature.getRNG().nextInt(1200) + 1200) + 1200;
   }

   protected void func_220725_g() {
      this.creature.getNavigator().tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
   }

   public double getTargetDistanceSq() {
      return 1.0D;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (!this.destinationBlock.up().withinDistance(this.creature.getPositionVec(), this.getTargetDistanceSq())) {
         this.isAboveDestination = false;
         ++this.timeoutCounter;
         if (this.shouldMove()) {
            this.creature.getNavigator().tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
         }
      } else {
         this.isAboveDestination = true;
         --this.timeoutCounter;
      }

   }

   public boolean shouldMove() {
      return this.timeoutCounter % 40 == 0;
   }

   protected boolean getIsAboveDestination() {
      return this.isAboveDestination;
   }

   /**
    * Searches and sets new destination block and returns true if a suitable block (specified in {@link
    * net.minecraft.entity.ai.EntityAIMoveToBlock#shouldMoveTo(World, BlockPos) EntityAIMoveToBlock#shouldMoveTo(World,
    * BlockPos)}) can be found.
    */
   protected boolean searchForDestination() {
      int i = this.searchLength;
      int j = this.field_203113_j;
      BlockPos blockpos = new BlockPos(this.creature);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int k = this.field_203112_e; k <= j; k = k > 0 ? -k : 1 - k) {
         for(int l = 0; l < i; ++l) {
            for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
               for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                  blockpos$mutable.setPos(blockpos).move(i1, k - 1, j1);
                  if (this.creature.isWithinHomeDistanceFromPosition(blockpos$mutable) && this.shouldMoveTo(this.creature.world, blockpos$mutable)) {
                     this.destinationBlock = blockpos$mutable;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   /**
    * Return true to set given position as destination
    */
   protected abstract boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos);
}