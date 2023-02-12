package com.railwayteam.railways.content.tender;

import com.railwayteam.railways.base.HorizontalConnectedBlock;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TenderBlock extends HorizontalConnectedBlock implements EntityBlock {
  public static final BooleanProperty LIT = BlockStateProperties.LIT;
  public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

  public TenderBlock(Properties props) {
    super(props);
    this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(SHAPE, Shape.DOOR_SHUT));
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return CRBlockEntities.TENDER_BE.create(pos, state); }

  @SuppressWarnings("deprecation")
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    if (!(world.getBlockEntity(pos) instanceof TenderBlockEntity)) return super.use(state, world, pos, player, hand, hitResult);

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
    // TODO remove this testing state-cycle feature
    world.setBlock(pos, state.setValue(SHAPE, switch(state.getValue(SHAPE)) {
      case DOOR_OPEN     -> Shape.DOOR_SHUT;
      case DOOR_SHUT     -> Shape.BOTTOM_CENTER;
      case BOTTOM_CENTER -> Shape.BOTTOM_SIDE;
      case BOTTOM_SIDE   -> Shape.BOTTOM_CORNER;
      case BOTTOM_CORNER -> Shape.TOP_CENTER;
      case TOP_CENTER    -> Shape.TOP_SIDE;
      case TOP_SIDE      -> Shape.TOP_CORNER;
      case TOP_CORNER    -> Shape.BOTTOM_CORNER_L_SHUT;
      case BOTTOM_CORNER_L_SHUT -> Shape.BOTTOM_CORNER_L_OPEN;
      case BOTTOM_CORNER_L_OPEN -> Shape.BOTTOM_CORNER_R_SHUT;
      case BOTTOM_CORNER_R_SHUT -> Shape.BOTTOM_CORNER_R_OPEN;
      case BOTTOM_CORNER_R_OPEN-> Shape.SOLO_OPEN;
      case SOLO_OPEN -> Shape.SOLO_SHUT;
      case SOLO_SHUT -> Shape.DOOR_OPEN;
    }), Block.UPDATE_ALL);
    return InteractionResult.SUCCESS;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(LIT);
    builder.add(SHAPE);
    super.createBlockStateDefinition(builder);
  }

  public enum Shape implements StringRepresentable {
    SOLO_OPEN, SOLO_SHUT,
    DOOR_OPEN, DOOR_SHUT,
    BOTTOM_CORNER_L_OPEN, BOTTOM_CORNER_L_SHUT,
    BOTTOM_CORNER_R_OPEN, BOTTOM_CORNER_R_SHUT,
    BOTTOM_CENTER, BOTTOM_SIDE, BOTTOM_CORNER,
    TOP_CENTER, TOP_SIDE, TOP_CORNER;

    @Override
    public String getSerializedName() {
      return Lang.asId(name());
    }
  }
}
