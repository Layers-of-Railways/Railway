package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.util.ShapeWrapper;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DieselSmokeStackBlock extends SmokeStackBlock implements IBE<DieselSmokeStackBlockEntity> {
    public DieselSmokeStackBlock(Properties properties, SmokeStackType type, ShapeWrapper shape, boolean createsStationarySmoke) {
        super(properties, type, shape, createsStationarySmoke);
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
