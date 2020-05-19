package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public enum EmptyBlockReader implements IBlockReader {
   INSTANCE;

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return null;
   }

   public BlockState getBlockState(BlockPos pos) {
      return Blocks.AIR.getDefaultState();
   }

   public IFluidState getFluidState(BlockPos pos) {
      return Fluids.EMPTY.getDefaultState();
   }
}