package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;


public abstract class TrackBufferBlock extends HorizontalDirectionalBlock implements IBE<TrackBufferBlockEntity>, IWrenchable {
	protected TrackBufferBlock(Properties pProperties) {
		super(pProperties);
	}

	@ExpectPlatform
	public static TrackBufferBlock create(Properties properties) {
		throw new AssertionError();
	}

	@Override
	public Class<TrackBufferBlockEntity> getBlockEntityClass() {
		return TrackBufferBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends TrackBufferBlockEntity> getBlockEntityType() {
		return CRBlockEntities.TRACK_BUFFER.get();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(@NotNull BlockState state, @NotNull Level worldIn,
						 @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		IBE.onRemove(state, worldIn, pos, newState);
	}
	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		return InteractionResult.SUCCESS;
	}
}
