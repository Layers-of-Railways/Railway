package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class TallBlockItem extends BlockItem {
   public TallBlockItem(Block blockIn, Item.Properties builder) {
      super(blockIn, builder);
   }

   protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
      context.getWorld().setBlockState(context.getPos().up(), Blocks.AIR.getDefaultState(), 27);
      return super.placeBlock(context, state);
   }
}