package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public interface ICrossbowUser {
   void setCharging(boolean isCharging);

   void shoot(LivingEntity target, ItemStack p_213670_2_, IProjectile projectile, float projectileAngle);

   /**
    * Gets the active target the Task system uses for tracking
    */
   @Nullable
   LivingEntity getAttackTarget();
}