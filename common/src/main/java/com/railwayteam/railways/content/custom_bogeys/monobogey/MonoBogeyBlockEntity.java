package com.railwayteam.railways.content.custom_bogeys.monobogey;

import com.simibubi.create.content.trains.bogey.StandardBogeyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class MonoBogeyBlockEntity extends StandardBogeyBlockEntity {
    public MonoBogeyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(2);
    }
}
