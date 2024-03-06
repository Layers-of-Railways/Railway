package com.railwayteam.railways.content.smokestack.block;

import com.railwayteam.railways.content.smokestack.SmokestackStyle;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.util.ShapeWrapper;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractSmokeStackBlock<T extends SmartBlockEntity> extends Block implements ProperWaterloggedBlock, IWrenchable, IBE<T> {
    public static final EnumProperty<SmokestackStyle> STYLE = EnumProperty.create("style", SmokestackStyle.class);
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final ShapeWrapper shape;
    final String variant;

    public AbstractSmokeStackBlock(Properties properties, ShapeWrapper shape, String variant) {
        super(properties);
        this.registerDefaultState(this.makeDefaultState());
        this.shape = shape;
        this.variant = variant;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape.get();
    }

    protected BlockState makeDefaultState() {
        return this.defaultBlockState()
            .setValue(STYLE, SmokestackStyle.STEEL)
            .setValue(ENABLED, true)
            .setValue(POWERED, false)
            .setValue(WATERLOGGED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STYLE).add(ENABLED).add(POWERED).add(WATERLOGGED);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        if (variant.equals("diesel") || variant.equals("caboosestyle"))
            return super.getCloneItemStack(level, pos, state);
        return CRBlocks.SMOKESTACK_GROUP.get(variant).get(state.getValue(STYLE)).asStack();
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull FluidState getFluidState(BlockState state) {
        return fluidState(state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        blockstate = blockstate.setValue(STYLE, SmokestackStyle.STEEL);

        if (context.getLevel().hasNeighborSignal(context.getClickedPos())) {
            blockstate = blockstate.setValue(ENABLED, false).setValue(POWERED, true);
        }

        return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        updateWater(level, state, currentPos);
        return state;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
                                 BlockHitResult pHit) {
        if (AllTags.AllItemTags.WRENCH.matches(pPlayer.getItemInHand(pHand))) {
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
        pState = pState.cycle(ENABLED);
        pLevel.setBlock(pPos, pState, 2);
        if (pState.getValue(WATERLOGGED))
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            boolean powered = level.hasNeighborSignal(pos);
            boolean shouldBeEnabled = !powered;
            if (powered != state.getValue(POWERED)) {
                if (state.getValue(ENABLED) != shouldBeEnabled) {
                    state = state.setValue(ENABLED, shouldBeEnabled);
                }

                level.setBlock(pos, state.setValue(POWERED, powered), 2);
                if (state.getValue(WATERLOGGED)) {
                    level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                }
            }
        }
    }

    @Override
    public Item asItem() {
        return super.asItem();
    }
}
