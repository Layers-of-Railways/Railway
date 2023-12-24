package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.registry.CRBlocks;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

// fixme handle 45 degree turns
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TrackBufferBlock<BE extends TrackBufferBlockEntity> extends HorizontalDirectionalBlock implements IBE<BE>, IWrenchable, ProperWaterloggedBlock {
	protected TrackBufferBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public abstract Class<BE> getBlockEntityClass();

	@Override
	public abstract BlockEntityType<? extends BE> getBlockEntityType();

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

	protected abstract BlockState getCycledStyle(BlockState originalState, Direction targetedFace);

	@Override
	public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
		if (targetedFace.getAxis() == originalState.getValue(FACING).getAxis()) {
			return getCycledStyle(originalState, targetedFace);
		} else {
			return originalState.setValue(FACING, originalState.getValue(FACING).getOpposite());
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
		if (state != null && context instanceof BufferBlockPlaceContext bufferBlockPlaceContext) {
			state = state.setValue(FACING, bufferBlockPlaceContext.facing);
		}
		return withWater(state, context);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		updateWater(level, state, currentPos);
		return state;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return CRBlocks.TRACK_BUFFER.asStack();
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.empty();
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
								 BlockHitResult pHit) {
		if (AdventureUtils.isAdventure(pPlayer))
			return InteractionResult.PASS;
		return onBlockEntityUse(pLevel, pPos, be -> be.applyDyeIfValid(pPlayer.getItemInHand(pHand)));
	}
}
