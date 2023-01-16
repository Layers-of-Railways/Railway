package com.railwayteam.railways.content.custom_bogeys.monobogey;

import com.simibubi.create.content.logistics.trains.track.StandardBogeyTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MonoBogeyTileEntity extends StandardBogeyTileEntity {
    public MonoBogeyTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
