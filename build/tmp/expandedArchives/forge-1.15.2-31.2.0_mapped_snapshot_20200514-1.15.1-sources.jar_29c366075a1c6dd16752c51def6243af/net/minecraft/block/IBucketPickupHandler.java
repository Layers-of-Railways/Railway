package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface IBucketPickupHandler {
   Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state);
}