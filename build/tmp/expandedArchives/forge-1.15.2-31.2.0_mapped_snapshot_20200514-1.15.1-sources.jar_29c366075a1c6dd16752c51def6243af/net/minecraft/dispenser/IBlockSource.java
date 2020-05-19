package net.minecraft.dispenser;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public interface IBlockSource extends ILocatableSource {
   double getX();

   double getY();

   double getZ();

   BlockPos getBlockPos();

   /**
    * Gets the block state of this position and returns it. 
    *  @return Block state in this position
    */
   BlockState getBlockState();

   <T extends TileEntity> T getBlockTileEntity();
}