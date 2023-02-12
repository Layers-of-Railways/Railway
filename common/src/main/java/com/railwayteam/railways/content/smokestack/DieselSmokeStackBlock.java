package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.util.ShapeWrapper;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DieselSmokeStackBlock extends SmokeStackBlock implements ITE<DieselSmokeStackTileEntity> {
    public DieselSmokeStackBlock(Properties properties, SmokeStackType type, ShapeWrapper shape) {
        super(properties, type, shape);
    }

    @Override
    public Class<DieselSmokeStackTileEntity> getTileEntityClass() {
        return DieselSmokeStackTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends DieselSmokeStackTileEntity> getTileEntityType() {
        return CRBlockEntities.DIESEL_SMOKE_STACK.get();
    }
}
