package com.railwayteam.railways.content;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FireboxBlock extends HorizontalDirectionalBlock {
  public static final BooleanProperty LIT = BlockStateProperties.LIT;

  public FireboxBlock(Properties props) {
    super(props);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
  }
}
