package com.railwayteam.railways.content.custom_tracks.casing;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CasingCollisionBlockEntity extends SyncedBlockEntity {

    int keepAlive;

    public CasingCollisionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void randomTick() {
        keepAlive--;
        if (keepAlive > 0 || level == null)
            return;
        level.removeBlock(worldPosition, false);
    }

    public void keepAlive() {
        keepAlive = 3;
    }
}
