package com.railwayteam.railways.content.coupling.coupler;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class TrackCouplerBlock extends Block implements IBE<TrackCouplerBlockEntity>, IWrenchable {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<TrackCouplerBlockEntity.AllowedOperationMode> MODE = EnumProperty.create("mode", TrackCouplerBlockEntity.AllowedOperationMode.class);

	protected TrackCouplerBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(MODE, TrackCouplerBlockEntity.AllowedOperationMode.BOTH));
	}

	@ExpectPlatform
	public static TrackCouplerBlock create(Properties properties) {
		throw new AssertionError();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(POWERED).add(MODE));
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
		if (level.getBlockEntity(pos) instanceof TrackCouplerBlockEntity te)
			return te.getTargetAnalogOutput();
		return 0;
	}

	@Override
	public Class<TrackCouplerBlockEntity> getBlockEntityClass() {
		return TrackCouplerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends TrackCouplerBlockEntity> getBlockEntityType() {
		return CRBlockEntities.TRACK_COUPLER.get();
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		IBE.onRemove(state, worldIn, pos, newState);
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
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		BlockPos pos = context.getClickedPos();
		BlockState newState = state.cycle(MODE);
		level.setBlock(pos, newState, 3);
		Player player = context.getPlayer();
		if (player != null)
			player.displayClientMessage(newState.getValue(MODE).getTranslatedName(), true);
		return InteractionResult.SUCCESS;
	}
}
