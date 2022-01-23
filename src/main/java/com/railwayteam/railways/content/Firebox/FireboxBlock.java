package com.railwayteam.railways.content.Firebox;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.HorizontalConnectedBlock;
import com.railwayteam.railways.registry.CRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class FireboxBlock extends HorizontalConnectedBlock implements EntityBlock {
  public static final BooleanProperty LIT = BlockStateProperties.LIT;

  public FireboxBlock(Properties props) {
    super(props);
    this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return CRBlockEntities.FIREBOX_BE.create(pos, state); }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    if (!(world.getBlockEntity(pos) instanceof FireboxBlockEntity)) return super.use(state, world, pos, player, hand, hitResult);

    ItemStack held = player.getItemInHand(hand);
    Item heldItem  = held.getItem();
    if (heldItem.equals(Items.COAL)
    ||  heldItem.equals(Items.CHARCOAL)) {
      // add to fuel storage
    }
//    else if (heldItem.equals(Items.FLINT_AND_STEEL) && !state.getValue(LIT)) {

//    }
//    else if (heldItem.equals(Items.WATER_BUCKET) && state.getValue(LIT)) {
//      state.setValue(LIT, false);
//      ((FireboxBlockEntity)world.getBlockEntity(pos)).notifyUpdateState(pos, state);
//      world.setBlock(pos, state, Block.UPDATE_ALL);
//      player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
//    }

    Railways.LOGGER.debug("TEST SUCCESSFUL");
    return InteractionResult.SUCCESS;
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

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(LIT);
    super.createBlockStateDefinition(builder);
  }
}
