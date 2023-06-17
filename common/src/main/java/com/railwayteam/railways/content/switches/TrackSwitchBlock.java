package com.railwayteam.railways.content.switches;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Lang;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
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

public abstract class TrackSwitchBlock extends HorizontalDirectionalBlock implements IBE<TrackSwitchTileEntity>, IWrenchable {
  boolean isAutomatic;
  public static final Property<SwitchState> STATE = EnumProperty.create("state", SwitchState.class);
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

  public enum SwitchState implements StringRepresentable {
    NORMAL, REVERSE_LEFT,
    REVERSE_RIGHT;

    @Override
    public @NotNull String getSerializedName() {
      return Lang.asId(name());
    }

    public @NotNull SwitchState nextStateFor(TrackSwitch sw) {
      if (this == NORMAL) {
        if (sw.hasRightExit()) {
          return REVERSE_RIGHT;
        } else if (sw.hasLeftExit()) {
          return REVERSE_LEFT;
        }
      } else if (this == REVERSE_RIGHT) {
        if (sw.hasLeftExit()) {
          return REVERSE_LEFT;
        } else if (sw.hasStraightExit()) {
          return NORMAL;
        }
      } else if (this == REVERSE_LEFT) {
        if (sw.hasStraightExit()) {
          return NORMAL;
        } else if (sw.hasRightExit()) {
          return REVERSE_RIGHT;
        }
      }

      return this;
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
      .setValue(POWERED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder.add(FACING).add(STATE).add(POWERED));
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state == null)
      return null;

    return state.setValue(FACING, context.getHorizontalDirection());
  }

  @Override
  public Class<TrackSwitchTileEntity> getBlockEntityClass() {
    return TrackSwitchTileEntity.class;
  }

  @Override
  public BlockEntityType<? extends TrackSwitchTileEntity> getBlockEntityType() {
    return isAutomatic ?
      CRBlockEntities.BRASS_SWITCH.get() :
      CRBlockEntities.ANDESITE_SWITCH.get();
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    IBE.onRemove(state, level, pos, newState);
  }

  @SuppressWarnings("deprecation")
  @Override
  public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
    return isAutomatic ?
      CRShapes.BRASS_SWITCH.get(state.getValue(FACING)) :
      CRShapes.ANDESITE_SWITCH.get(state.getValue(FACING));
  }

  @SuppressWarnings("deprecation")
  @Override
  public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                        @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
    if (player.isSteppingCarefully())
      return InteractionResult.PASS;
    ItemStack itemInHand = player.getItemInHand(hand);
    if (AllItems.WRENCH.isIn(itemInHand))
      return InteractionResult.PASS;

    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    TrackSwitchTileEntity te = getBlockEntity(level, pos);
    if (te != null) {
      return te.onUse();
    }

    return InteractionResult.SUCCESS;
  }

  @Override
  public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
    super.onProjectileHit(level, state, hit, projectile);

    TrackSwitchTileEntity te = getBlockEntity(level, hit.getBlockPos());
    if (te != null) {
      te.onProjectileHit();
    }
  }

  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, level, pos, block, fromPos, isMoving);

    TrackSwitchTileEntity te = getBlockEntity(level, pos);
    if (te != null) {
      te.checkRedstoneInputs();
    }
  }


}
