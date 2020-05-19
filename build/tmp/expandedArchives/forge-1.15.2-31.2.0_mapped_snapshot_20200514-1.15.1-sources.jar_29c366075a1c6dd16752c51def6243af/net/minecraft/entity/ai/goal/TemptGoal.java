package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;

public class TemptGoal extends Goal {
   private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).setDistance(10.0D).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks().setLineOfSiteRequired();
   protected final CreatureEntity creature;
   private final double speed;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double pitch;
   private double yaw;
   protected PlayerEntity closestPlayer;
   private int delayTemptCounter;
   private boolean isRunning;
   private final Ingredient temptItem;
   private final boolean scaredByPlayerMovement;

   public TemptGoal(CreatureEntity creatureIn, double speedIn, Ingredient temptItemsIn, boolean scaredByPlayerMovementIn) {
      this(creatureIn, speedIn, scaredByPlayerMovementIn, temptItemsIn);
   }

   public TemptGoal(CreatureEntity creatureIn, double speedIn, boolean scaredByPlayerMovementIn, Ingredient temptItemsIn) {
      this.creature = creatureIn;
      this.speed = speedIn;
      this.temptItem = temptItemsIn;
      this.scaredByPlayerMovement = scaredByPlayerMovementIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if (!(creatureIn.getNavigator() instanceof GroundPathNavigator) && !(creatureIn.getNavigator() instanceof FlyingPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      if (this.delayTemptCounter > 0) {
         --this.delayTemptCounter;
         return false;
      } else {
         this.closestPlayer = this.creature.world.getClosestPlayer(ENTITY_PREDICATE, this.creature);
         if (this.closestPlayer == null) {
            return false;
         } else {
            return this.isTempting(this.closestPlayer.getHeldItemMainhand()) || this.isTempting(this.closestPlayer.getHeldItemOffhand());
         }
      }
   }

   protected boolean isTempting(ItemStack stack) {
      return this.temptItem.test(stack);
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      if (this.isScaredByPlayerMovement()) {
         if (this.creature.getDistanceSq(this.closestPlayer) < 36.0D) {
            if (this.closestPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D) {
               return false;
            }

            if (Math.abs((double)this.closestPlayer.rotationPitch - this.pitch) > 5.0D || Math.abs((double)this.closestPlayer.rotationYaw - this.yaw) > 5.0D) {
               return false;
            }
         } else {
            this.targetX = this.closestPlayer.getPosX();
            this.targetY = this.closestPlayer.getPosY();
            this.targetZ = this.closestPlayer.getPosZ();
         }

         this.pitch = (double)this.closestPlayer.rotationPitch;
         this.yaw = (double)this.closestPlayer.rotationYaw;
      }

      return this.shouldExecute();
   }

   protected boolean isScaredByPlayerMovement() {
      return this.scaredByPlayerMovement;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.targetX = this.closestPlayer.getPosX();
      this.targetY = this.closestPlayer.getPosY();
      this.targetZ = this.closestPlayer.getPosZ();
      this.isRunning = true;
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.closestPlayer = null;
      this.creature.getNavigator().clearPath();
      this.delayTemptCounter = 100;
      this.isRunning = false;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      this.creature.getLookController().setLookPositionWithEntity(this.closestPlayer, (float)(this.creature.getHorizontalFaceSpeed() + 20), (float)this.creature.getVerticalFaceSpeed());
      if (this.creature.getDistanceSq(this.closestPlayer) < 6.25D) {
         this.creature.getNavigator().clearPath();
      } else {
         this.creature.getNavigator().tryMoveToEntityLiving(this.closestPlayer, this.speed);
      }

   }

   /**
    * @see #isRunning
    */
   public boolean isRunning() {
      return this.isRunning;
   }
}