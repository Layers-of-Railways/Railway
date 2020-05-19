package net.minecraft.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface ISidedInventoryProvider {
   ISidedInventory createInventory(BlockState p_219966_1_, IWorld p_219966_2_, BlockPos p_219966_3_);
}