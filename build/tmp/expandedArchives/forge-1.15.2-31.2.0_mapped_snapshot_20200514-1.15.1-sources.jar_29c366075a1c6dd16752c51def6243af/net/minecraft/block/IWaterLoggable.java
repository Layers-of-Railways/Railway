package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public interface IWaterLoggable extends IBucketPickupHandler, ILiquidContainer {
   default boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
      return !state.get(BlockStateProperties.WATERLOGGED) && fluidIn == Fluids.WATER;
   }

   default boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
      if (!state.get(BlockStateProperties.WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
         if (!worldIn.isRemote()) {
            worldIn.setBlockState(pos, state.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 3);
            worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
         }

         return true;
      } else {
         return false;
      }
   }

   default Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
      if (state.get(BlockStateProperties.WATERLOGGED)) {
         worldIn.setBlockState(pos, state.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }
}