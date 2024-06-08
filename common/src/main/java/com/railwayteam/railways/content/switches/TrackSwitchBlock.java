/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.switches;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Lang;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TrackSwitchBlock extends HorizontalDirectionalBlock implements IBE<TrackSwitchBlockEntity>, IWrenchable {
  boolean isAutomatic;
//  public static final Property<SwitchState> STATE = EnumProperty.create("state", SwitchState.class);
  public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;

  public enum SwitchConstraint {
    NONE,
    TO_RIGHT,
    TO_LEFT
    ;

    public boolean canGoRight() {
      return this != TO_LEFT;
    }

    public boolean canGoLeft() {
      return this != TO_RIGHT;
    }
  }

  public enum SwitchState implements StringRepresentable {
    NORMAL(0), REVERSE_LEFT(-1),
    REVERSE_RIGHT(1);

    private final int direction;

    SwitchState(int direction) {
      this.direction = direction;
    }

    public static SwitchState fromSteerDirection(TravellingPoint.SteerDirection direction, boolean forward) {
      return switch (direction) {
        case NONE -> NORMAL;
        case LEFT -> forward ? REVERSE_LEFT : REVERSE_RIGHT;
        case RIGHT -> forward ? REVERSE_RIGHT : REVERSE_LEFT;
      };
    }

    @Override
    public @NotNull String getSerializedName() {
      return Lang.asId(name());
    }

    public boolean canSwitchTo(SwitchState next, SwitchConstraint constraint) {
      return switch (constraint) {
        case NONE -> true;
        case TO_LEFT -> next.direction <= this.direction;
        case TO_RIGHT -> next.direction >= this.direction;
      };
    }

    public @NotNull SwitchState nextStateFor(TrackSwitch sw, SwitchConstraint constraint) {
      if (this == NORMAL) {
        if (sw.hasRightExit() && constraint.canGoRight()) {
          return REVERSE_RIGHT;
        } else if (sw.hasLeftExit() && constraint.canGoLeft()) {
          return REVERSE_LEFT;
        }
      } else if (this == REVERSE_RIGHT) {
        if (constraint == SwitchConstraint.NONE) { // priority for switching differs
          if (sw.hasLeftExit() && constraint.canGoLeft()) {
            return REVERSE_LEFT;
          } else if (sw.hasStraightExit() && constraint.canGoLeft()) {
            return NORMAL;
          }
        } else {
          if (sw.hasStraightExit() && constraint.canGoLeft()) {
            return NORMAL;
          } else if (sw.hasLeftExit() && constraint.canGoLeft()) {
            return REVERSE_LEFT;
          }
        }
      } else if (this == REVERSE_LEFT) {
        if (sw.hasStraightExit() && constraint.canGoRight()) {
          return NORMAL;
        } else if (sw.hasRightExit() && constraint.canGoRight()) {
          return REVERSE_RIGHT;
        }
      }

      return this;
    }

      public SwitchState nextStateForPonder(SwitchConstraint constraint) {
        if (this == NORMAL) {
          if (constraint.canGoRight()) {
            return REVERSE_RIGHT;
          } else if (constraint.canGoLeft()) {
            return REVERSE_LEFT;
          }
        } else if (this == REVERSE_RIGHT) {
          if (constraint == SwitchConstraint.NONE) {
            if (constraint.canGoLeft()) {
              return REVERSE_LEFT;
            }
          } else {
            if (constraint.canGoLeft()) {
              return NORMAL;
            }
          }
        } else if (this == REVERSE_LEFT) {
          if (constraint.canGoRight()) {
            return NORMAL;
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
      //.setValue(STATE, SwitchState.NORMAL)
      .setValue(LOCKED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder.add(FACING)
            //.add(STATE)
            .add(LOCKED));
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state == null)
      return null;

    return state.setValue(FACING, context.getHorizontalDirection())
            .setValue(LOCKED, context.getLevel().hasSignal(context.getClickedPos().below(), Direction.DOWN));
  }

  @Override
  public Class<TrackSwitchBlockEntity> getBlockEntityClass() {
    return TrackSwitchBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends TrackSwitchBlockEntity> getBlockEntityType() {
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
  public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                      @NotNull BlockPos pos, @NotNull CollisionContext context) {
    if (context instanceof EntityCollisionContext entityContext) {
      if (entityContext.getEntity() instanceof Projectile) {
        return isAutomatic ?
                CRShapes.BRASS_SWITCH_PROJECTILE.get(state.getValue(FACING)) :
                CRShapes.ANDESITE_SWITCH_PROJECTILE.get(state.getValue(FACING));
      }
    }
    return isAutomatic ?
      CRShapes.BRASS_SWITCH.get(state.getValue(FACING)) :
      CRShapes.ANDESITE_SWITCH.get(state.getValue(FACING));
  }

  @SuppressWarnings("deprecation")
  @Override
  public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                               @NotNull BlockPos pos, @NotNull CollisionContext context) {
    return hasCollision ? getShape(state, level, pos, context) : Shapes.empty();
  }

  @SuppressWarnings("deprecation")
  @Override
  public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                        @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
    ItemStack itemInHand = player.getItemInHand(hand);
    if (AllItems.WRENCH.isIn(itemInHand))
      return InteractionResult.PASS;

    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    TrackSwitchBlockEntity te = getBlockEntity(level, pos);
    if (te != null) {
      if (player.getGameProfile() == ConductorEntity.FAKE_PLAYER_PROFILE) {
        return te.onProjectileHit() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
      } else {
        return te.onUse(player.isSteppingCarefully());
      }
    }

    return InteractionResult.SUCCESS;
  }

  @Override
  public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
    super.onProjectileHit(level, state, hit, projectile);

    TrackSwitchBlockEntity te = getBlockEntity(level, hit.getBlockPos());
    if (te != null) {
      te.onProjectileHit();
    }
    if (projectile instanceof Arrow) {
      projectile.discard();
    }
  }

  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, level, pos, block, fromPos, isMoving);

    TrackSwitchBlockEntity te = getBlockEntity(level, pos);
    if (te != null) {
      te.checkRedstoneInputs();
    }
  }

  /**
   * @deprecated call via {@link
   * BlockStateBase#hasAnalogOutputSignal} whenever possible.
   * Implementing/overriding is fine.
   */
  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
    return true;
  }

  /**
   * @deprecated call via {@link
   * BlockStateBase#getAnalogOutputSignal} whenever possible.
   * Implementing/overriding is fine.
   */
  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
    if (level.getBlockEntity(pos) instanceof TrackSwitchBlockEntity te)
      return te.getTargetAnalogOutput();
    return 0;
  }
}
