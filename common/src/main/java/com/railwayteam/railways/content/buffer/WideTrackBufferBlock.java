package com.railwayteam.railways.content.buffer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WideTrackBufferBlock extends TrackBufferBlock {
    public WideTrackBufferBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected BlockState getCycledStyle(BlockState originalState, Direction targetedFace) {
        return originalState;
    }
}
