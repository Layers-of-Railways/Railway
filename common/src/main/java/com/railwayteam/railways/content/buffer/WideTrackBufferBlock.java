package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.registry.CRBlockEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WideTrackBufferBlock extends TrackBufferBlock<TrackBufferBlockEntity> {
    public WideTrackBufferBlock(Properties pProperties) {
        super(pProperties);
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
    protected BlockState getCycledStyle(BlockState originalState, Direction targetedFace) {
        return originalState;
    }
}
