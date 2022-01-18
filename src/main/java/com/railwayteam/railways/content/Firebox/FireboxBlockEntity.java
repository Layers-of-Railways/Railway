package com.railwayteam.railways.content.Firebox;

import com.railwayteam.railways.registry.CRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FireboxBlockEntity extends BlockEntity {
  public FireboxBlockEntity (BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public FireboxBlockEntity (BlockPos pos, BlockState state) {
    this (CRBlockEntities.Type.FIREBOX, pos, state);
  }

  protected void notifyUpdateState (BlockPos pos, BlockState state) {
  }
}
