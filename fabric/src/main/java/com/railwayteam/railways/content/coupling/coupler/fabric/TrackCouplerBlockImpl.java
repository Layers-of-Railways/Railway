package com.railwayteam.railways.content.coupling.coupler.fabric;

import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlock;
import io.github.fabricators_of_create.porting_lib.block.ConnectableRedstoneBlock;
import io.github.fabricators_of_create.porting_lib.block.WeakPowerCheckingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TrackCouplerBlockImpl extends TrackCouplerBlock implements ConnectableRedstoneBlock, WeakPowerCheckingBlock {
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
	public boolean shouldCheckWeakPower(BlockState state, SignalGetter world, BlockPos pos, Direction side) {
		return false;
	}
}
