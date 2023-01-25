package com.railwayteam.railways.content.custom_bogeys.monobogey;

import net.minecraft.world.level.block.state.BlockState;

public interface IPotentiallyUpsideDownBogey {
    boolean isUpsideDown();
    BlockState getVersion(BlockState base, boolean upsideDown);
}
