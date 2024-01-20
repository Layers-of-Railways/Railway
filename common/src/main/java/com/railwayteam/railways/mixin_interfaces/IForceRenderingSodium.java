package com.railwayteam.railways.mixin_interfaces;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface IForceRenderingSodium {
    boolean forceRenderingSodium(@NotNull BlockState state, BlockState adjacentBlockState, @NotNull Direction direction);
}
