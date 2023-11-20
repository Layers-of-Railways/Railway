package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.util.ShapeWrapper;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DieselSmokeStackBlock extends AbstractSmokeStackBlock<DieselSmokeStackBlockEntity> {
    public DieselSmokeStackBlock(Properties properties, ShapeWrapper shape) {
        super(properties, shape);
    }

    @Override
    public Class<DieselSmokeStackBlockEntity> getBlockEntityClass() {
        return DieselSmokeStackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DieselSmokeStackBlockEntity> getBlockEntityType() {
        return CRBlockEntities.DIESEL_SMOKE_STACK.get();
    }
}
