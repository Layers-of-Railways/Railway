package com.railwayteam.railways.content.buffer.single_deco;

import com.railwayteam.railways.content.buffer.DyeableBlockEntity;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.util.AdventureUtils;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractDyeableSingleBufferBlock extends HorizontalDirectionalBlock implements IBE<DyeableBlockEntity>, IWrenchable, ProperWaterloggedBlock {

    public AbstractDyeableSingleBufferBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, WATERLOGGED));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(@NotNull BlockState state, @NotNull Level worldIn,
                         @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    protected abstract BlockState cycleStyle(BlockState originalState, Direction targetedFace);

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (targetedFace.getAxis().isVertical()) {
            return IWrenchable.super.getRotatedBlockState(originalState, targetedFace);
        } else {
            return cycleStyle(originalState, targetedFace);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return fluidState(state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            if (context.getClickedFace().getAxis().isVertical()) {
                state = state.setValue(FACING, context.getHorizontalDirection().getOpposite());
            } else {
                state = state.setValue(FACING, context.getClickedFace());
            }
        }
        return withWater(state, context);
    }

    protected abstract VoxelShaper getShaper(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context);

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShaper(state, level, pos, context).get(state.getValue(FACING));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        updateWater(level, state, currentPos);
        return state;
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
                                 BlockHitResult pHit) {
        if (AdventureUtils.isAdventure(pPlayer))
            return InteractionResult.PASS;
        return onBlockEntityUse(pLevel, pPos, be -> be.applyDyeIfValid(pPlayer.getItemInHand(pHand)));
    }

    @Override
    public Class<DyeableBlockEntity> getBlockEntityClass() {
        return DyeableBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DyeableBlockEntity> getBlockEntityType() {
        return CRBlockEntities.DYEABLE.get();
    }
}
