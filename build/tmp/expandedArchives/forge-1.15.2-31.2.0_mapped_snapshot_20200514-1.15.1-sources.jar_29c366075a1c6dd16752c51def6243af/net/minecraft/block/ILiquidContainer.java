package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public interface ILiquidContainer {
   boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn);

   boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn);
}