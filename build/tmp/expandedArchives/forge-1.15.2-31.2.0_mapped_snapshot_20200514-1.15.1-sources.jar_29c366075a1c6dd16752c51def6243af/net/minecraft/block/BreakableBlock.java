package net.minecraft.block;

import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BreakableBlock extends Block {
   protected BreakableBlock(Block.Properties properties) {
      super(properties);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
      return adjacentBlockState.getBlock() == this ? true : super.isSideInvisible(state, adjacentBlockState, side);
   }
}