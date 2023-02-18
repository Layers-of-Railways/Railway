package com.railwayteam.railways.content.coupling.coupler.forge;

import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TrackCouplerBlockImpl extends TrackCouplerBlock {
	protected TrackCouplerBlockImpl(Properties pProperties) {
		super(pProperties);
	}

	public static TrackCouplerBlock create(Properties properties) {
		return new TrackCouplerBlockImpl(properties);
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
