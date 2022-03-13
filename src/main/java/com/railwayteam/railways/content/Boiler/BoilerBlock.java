package com.railwayteam.railways.content.Boiler;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.HorizontalConnectedBlock;
import com.railwayteam.railways.registry.CRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BoilerBlock extends HorizontalConnectedBlock implements EntityBlock {

  public BoilerBlock(Properties props) {
    super(props);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return CRBlockEntities.BOILER_BE.create(pos, state); }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    BlockEntity be = world.getBlockEntity(pos);
    if (be instanceof BoilerBlockEntity) {

    }
    return InteractionResult.PASS;
  }
}
