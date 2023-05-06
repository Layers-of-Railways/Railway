package com.railwayteam.railways.content.switches;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.logistics.block.inventories.BottomlessItemHandler;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.Lang;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class TrackSwitchBlock extends HorizontalDirectionalBlock implements ITE<TrackSwitchTileEntity> {
  boolean isAutomatic;

  public static final Property<SwitchExits> EXITS = EnumProperty.create("exits", SwitchExits.class);
  public static final Property<SwitchState> STATE = EnumProperty.create("state", SwitchState.class);
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

  public enum SwitchExits implements StringRepresentable {
    LEFT, RIGHT, BOTH, NONE;

    @Override
    public @NotNull String getSerializedName() {
      return Lang.asId(name());
    }
  }

  public enum SwitchState implements StringRepresentable {
    NORMAL, REVERSE_LEFT,
    REVERSE_RIGHT;

    @Override
    public @NotNull String getSerializedName() {
      return Lang.asId(name());
    }
  }

  @ExpectPlatform
  public static TrackSwitchBlock manual(Properties properties) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static TrackSwitchBlock automatic(Properties properties) {
    throw new AssertionError();
  }

  protected TrackSwitchBlock(Properties properties, boolean isAutomatic) {
    super(properties);
    this.isAutomatic = isAutomatic;
    registerDefaultState(defaultBlockState()
      .setValue(STATE, SwitchState.NORMAL)
      // TODO: Determine which exits are available when block is placed
      .setValue(EXITS, SwitchExits.BOTH)
      .setValue(POWERED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder.add(FACING).add(EXITS).add(STATE).add(POWERED));
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state == null)
      return null;

    Direction facing = context.getHorizontalDirection().getOpposite();
    return state.setValue(FACING, facing);
  }

  @Override
  public Class<TrackSwitchTileEntity> getTileEntityClass() {
    return TrackSwitchTileEntity.class;
  }

  @Override
  public BlockEntityType<? extends TrackSwitchTileEntity> getTileEntityType() {
    return isAutomatic ?
      CRBlockEntities.BRASS_SWITCH.get() :
      CRBlockEntities.ANDESITE_SWITCH.get();
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return isAutomatic ?
      CRShapes.BRASS_SWITCH.get(state.getValue(FACING)) :
      CRShapes.ANDESITE_SWITCH.get(state.getValue(FACING));
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    if (level.getBlockEntity(pos) instanceof TrackSwitchTileEntity te) {
      if (!te.isPowered()) {
        level.setBlockAndUpdate(pos, toggleSwitch(state));
        return InteractionResult.CONSUME;
      }
    }

    return InteractionResult.SUCCESS;
  }

  public static BlockState setPowered(BlockState state) {
    return toggleSwitch(state).setValue(POWERED, true);
  }

  public static BlockState setUnpowered(BlockState state) {
    return toggleSwitch(state).setValue(POWERED, false);
  }

  public static BlockState toggleSwitch(BlockState state) {
    // TODO: Should switch to appropriate state for available exits
    // Also figure out a way to "toggle" 3-way exits
    return state.setValue(STATE,
      state.getValue(STATE) == SwitchState.NORMAL
        ? SwitchState.REVERSE_RIGHT : SwitchState.NORMAL);
  }
}
