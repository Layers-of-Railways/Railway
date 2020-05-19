package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface IPhase {
   boolean getIsStationary();

   /**
    * Generates particle effects appropriate to the phase (or sometimes sounds).
    * Called by dragon's onLivingUpdate. Only used when worldObj.isRemote.
    */
   void clientTick();

   /**
    * Gives the phase a chance to update its status.
    * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
    */
   void serverTick();

   void onCrystalDestroyed(EnderCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable PlayerEntity plyr);

   /**
    * Called when this phase is set to active
    */
   void initPhase();

   void removeAreaEffect();

   /**
    * Returns the maximum amount dragon may rise or fall during this phase
    */
   float getMaxRiseOrFall();

   float getYawFactor();

   PhaseType<? extends IPhase> getType();

   /**
    * Returns the location the dragon is flying toward
    */
   @Nullable
   Vec3d getTargetLocation();

   float func_221113_a(DamageSource p_221113_1_, float p_221113_2_);
}