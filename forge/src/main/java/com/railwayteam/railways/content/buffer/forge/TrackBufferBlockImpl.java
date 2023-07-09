package com.railwayteam.railways.content.buffer.forge;

import com.railwayteam.railways.content.buffer.TrackBufferBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TrackBufferBlockImpl extends TrackBufferBlock {
	protected TrackBufferBlockImpl(Properties pProperties) {
		super(pProperties);
	}

	public static TrackBufferBlock create(Properties properties) {
		return new TrackBufferBlockImpl(properties);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, LevelReader level, BlockPos pos, Direction side) {
		return false;
	}
}
