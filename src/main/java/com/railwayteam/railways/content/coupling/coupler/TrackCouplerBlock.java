package com.railwayteam.railways.content.coupling.coupler;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrackCouplerBlock extends Block implements ITE<TrackCouplerTileEntity>, IWrenchable {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public TrackCouplerBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(POWERED));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(POWERED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
	}

	/**
	 * @deprecated call via {@link
	 * BlockStateBase#hasAnalogOutputSignal} whenever possible.
	 * Implementing/overriding is fine.
	 */
	@Override
	public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
		return true;
	}

	/**
	 * @deprecated call via {@link
	 * BlockStateBase#getAnalogOutputSignal} whenever possible.
	 * Implementing/overriding is fine.
	 */
	@Override
	public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof TrackCouplerTileEntity te)
			return te.getTargetAnalogOutput();
		return 0;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public Class<TrackCouplerTileEntity> getTileEntityClass() {
		return TrackCouplerTileEntity.class;
	}

	@Override
	public BlockEntityType<? extends TrackCouplerTileEntity> getTileEntityType() {
		return CRBlockEntities.TRACK_COUPLER.get();
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		ITE.onRemove(state, worldIn, pos, newState);
	}

	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
								boolean pIsMoving) {
		if (pLevel.isClientSide)
			return;
		boolean powered = pState.getValue(POWERED);
		if (powered == pLevel.hasNeighborSignal(pPos))
			return;
		if (powered) {
			pLevel.scheduleTick(pPos, this, 4);
		} else {
			pLevel.setBlock(pPos, pState.cycle(POWERED), 2);
		}
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
		if (pState.getValue(POWERED) && !pLevel.hasNeighborSignal(pPos))
			pLevel.setBlock(pPos, pState.cycle(POWERED), 2);
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, LevelReader level, BlockPos pos, Direction side) {
		return false;
	}
}
