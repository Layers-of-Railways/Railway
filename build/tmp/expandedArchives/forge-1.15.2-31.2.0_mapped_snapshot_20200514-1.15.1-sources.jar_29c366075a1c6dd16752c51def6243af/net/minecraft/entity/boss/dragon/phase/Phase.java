package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class Phase implements IPhase {
   protected final EnderDragonEntity dragon;

   public Phase(EnderDragonEntity dragonIn) {
      this.dragon = dragonIn;
   }

   public boolean getIsStationary() {
      return false;
   }

   /**
    * Generates particle effects appropriate to the phase (or sometimes sounds).
    * Called by dragon's onLivingUpdate. Only used when worldObj.isRemote.
    */
   public void clientTick() {
   }

   /**
    * Gives the phase a chance to update its status.
    * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
    */
   public void serverTick() {
   }

   public void onCrystalDestroyed(EnderCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable PlayerEntity plyr) {
   }

   /**
    * Called when this phase is set to active
    */
   public void initPhase() {
   }

   public void removeAreaEffect() {
   }

   /**
    * Returns the maximum amount dragon may rise or fall during this phase
    */
   public float getMaxRiseOrFall() {
      return 0.6F;
   }

   /**
    * Returns the location the dragon is flying toward
    */
   @Nullable
   public Vec3d getTargetLocation() {
      return null;
   }

   public float func_221113_a(DamageSource p_221113_1_, float p_221113_2_) {
      return p_221113_2_;
   }

   public float getYawFactor() {
      float f = MathHelper.sqrt(Entity.horizontalMag(this.dragon.getMotion())) + 1.0F;
      float f1 = Math.min(f, 40.0F);
      return 0.7F / f1 / f;
   }
}