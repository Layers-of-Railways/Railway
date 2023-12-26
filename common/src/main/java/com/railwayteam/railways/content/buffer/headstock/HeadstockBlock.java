package com.railwayteam.railways.content.buffer.headstock;

import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRShapes;
import com.railwayteam.railways.util.AdventureUtils;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HeadstockBlock extends HorizontalDirectionalBlock implements IBE<HeadstockBlockEntity>, IWrenchable, ProperWaterloggedBlock, BlockStateBlockItemGroup.GroupedBlock {
    public static final EnumProperty<HeadstockStyle> STYLE = EnumProperty.create("style", HeadstockStyle.class);

    public HeadstockBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false)
            .setValue(STYLE, HeadstockStyle.BUFFER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, WATERLOGGED, STYLE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(@NotNull BlockState state, @NotNull Level worldIn,
                         @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (targetedFace.getAxis().isVertical()) {
            return IWrenchable.super.getRotatedBlockState(originalState, targetedFace);
        } else {
            return originalState.cycle(STYLE);
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

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (switch (state.getValue(STYLE)) {
            case PLAIN -> CRShapes.HEADSTOCK_PLAIN;
            case BUFFER -> CRShapes.HEADSTOCK_BUFFER;
            case LINK, LINKLESS -> CRShapes.HEADSTOCK_LINK_PIN;
            case KNUCKLE, KNUCKLE_SPLIT -> CRShapes.HEADSTOCK_KNUCKLE;
        }).get(state.getValue(FACING));
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
        InteractionResult result = onBlockEntityUse(pLevel, pPos, be -> be.applyMaterialIfValid(pPlayer.getItemInHand(pHand)));
        if (result.consumesAction()) return result;
        return onBlockEntityUse(pLevel, pPos, be -> be.applyDyeIfValid(pPlayer.getItemInHand(pHand)));
    }

    @Override
    public Class<HeadstockBlockEntity> getBlockEntityClass() {
        return HeadstockBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HeadstockBlockEntity> getBlockEntityType() {
        return CRBlockEntities.HEADSTOCK.get();
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return CRBlocks.HEADSTOCK_GROUP.get(state.getValue(STYLE)).asStack();
    }
}
