package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.DamageSource;

public abstract class SittingPhase extends Phase {
   public SittingPhase(EnderDragonEntity p_i46794_1_) {
      super(p_i46794_1_);
   }

   public boolean getIsStationary() {
      return true;
   }

   public float func_221113_a(DamageSource p_221113_1_, float p_221113_2_) {
      if (p_221113_1_.getImmediateSource() instanceof AbstractArrowEntity) {
         p_221113_1_.getImmediateSource().setFire(1);
         return 0.0F;
      } else {
         return super.func_221113_a(p_221113_1_, p_221113_2_);
      }
   }
}