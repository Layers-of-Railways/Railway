package com.railwayteam.railways.content.custom_bogeys.monobogey;

import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class InvisibleMonoBogeyBlockEntity extends MonoBogeyBlockEntity {
    public InvisibleMonoBogeyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public BogeyStyle getDefaultStyle() {
        return CRBogeyStyles.INVISIBLE_MONOBOGEY;
    }
}
