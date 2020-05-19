package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BedItem extends BlockItem {
   public BedItem(Block blockIn, Item.Properties properties) {
      super(blockIn, properties);
   }

   protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
      return context.getWorld().setBlockState(context.getPos(), state, 26);
   }
}