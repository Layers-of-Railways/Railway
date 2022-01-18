package com.railwayteam.railways.content.Boiler;

import com.railwayteam.railways.base.HorizontalConnectedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BoilerBlock extends HorizontalConnectedBlock implements EntityBlock {

  public BoilerBlock(Properties props) {
    super(props);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }
/*
  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
    return EntityBlock.super.getTicker(p_153212_, p_153213_, p_153214_);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> GameEventListener getListener(Level p_153210_, T p_153211_) {
    return EntityBlock.super.getListener(p_153210_, p_153211_);
  }
 */
}
