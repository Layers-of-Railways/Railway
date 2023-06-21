package com.railwayteam.railways.content.custom_bogeys.passenger;

import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PassengerBogeyBlockEntity extends AbstractBogeyBlockEntity {
    public PassengerBogeyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public BogeyStyle getDefaultStyle() {
        return CRBogeyStyles.TWOAXLESONE;
    }
}
