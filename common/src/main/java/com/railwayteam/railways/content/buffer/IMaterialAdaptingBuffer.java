package com.railwayteam.railways.content.buffer;

import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface IMaterialAdaptingBuffer {
    @NotNull BlockState getMaterial();
}
