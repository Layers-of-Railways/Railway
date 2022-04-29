package com.railwayteam.railways.content.Steamcart;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class SteamCartBlock extends Block {
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public SteamCartBlock(Properties props) {
    super(props);
    this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(POWERED);
    builder.add(FACING);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
    // this prevents the block from showing up in the creative tab
  }
}
