package net.minecraft.block;

import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;

public abstract class DirectionalBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;

   protected DirectionalBlock(Block.Properties builder) {
      super(builder);
   }
}