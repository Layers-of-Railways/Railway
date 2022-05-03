package com.railwayteam.railways.content.Tender;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TenderBlockEntity extends BlockEntity {
  private boolean hasCover = false;

  public TenderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  protected void notifyUpdateState (BlockPos pos, BlockState state) {
  }


}
