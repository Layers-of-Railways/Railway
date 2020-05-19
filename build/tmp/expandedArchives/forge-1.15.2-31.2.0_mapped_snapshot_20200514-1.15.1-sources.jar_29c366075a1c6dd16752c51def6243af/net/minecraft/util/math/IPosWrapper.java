package net.minecraft.util.math;

import net.minecraft.entity.LivingEntity;

public interface IPosWrapper {
   BlockPos getBlockPos();

   Vec3d getPos();

   boolean isVisibleTo(LivingEntity p_220610_1_);
}