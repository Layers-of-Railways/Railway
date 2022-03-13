package com.railwayteam.railways.base;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.StandardCTBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class ConnectedBlockCTBehavior extends StandardCTBehaviour {

  public ConnectedBlockCTBehavior(CTSpriteShiftEntry shift) {
    super(shift);
  }

  @Override
  public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
    return (state.getBlock() == other.getBlock() && state.getValue(HorizontalConnectedBlock.FACING) == other.getValue(HorizontalConnectedBlock.FACING));
  }
}
