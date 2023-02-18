package com.railwayteam.railways.util.fabric;

import io.github.fabricators_of_create.porting_lib.block.CustomSoundTypeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateUtilsImpl {
	public static SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
		if (state.getBlock() instanceof CustomSoundTypeBlock custom)
			return custom.getSoundType(state, level, pos, entity);
		return state.getSoundType();
	}
}
