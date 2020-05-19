package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.SoundEvents;

public class AttackingSittingPhase extends SittingPhase {
   private int attackingTicks;

   public AttackingSittingPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   /**
    * Generates particle effects appropriate to the phase (or sometimes sounds).
    * Called by dragon's onLivingUpdate. Only used when worldObj.isRemote.
    */
   public void clientTick() {
      this.dragon.world.playSound(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.dragon.getSoundCategory(), 2.5F, 0.8F + this.dragon.getRNG().nextFloat() * 0.3F, false);
   }

   /**
    * Gives the phase a chance to update its status.
    * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
    */
   public void serverTick() {
      if (this.attackingTicks++ >= 40) {
         this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_FLAMING);
      }

   }

   /**
    * Called when this phase is set to active
    */
   public void initPhase() {
      this.attackingTicks = 0;
   }

   public PhaseType<AttackingSittingPhase> getType() {
      return PhaseType.SITTING_ATTACKING;
   }
}