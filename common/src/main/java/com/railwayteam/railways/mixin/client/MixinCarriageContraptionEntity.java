package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.mixin_interfaces.IUpdateCount;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CarriageContraptionEntity.class)
public class MixinCarriageContraptionEntity implements IUpdateCount {
    private int updateCount = 0;

    @Override
    public int railways$getUpdateCount() {
        return updateCount;
    }

    @Override
    public void railways$fromParent(IUpdateCount parent) {
        updateCount = parent.railways$getUpdateCount();
    }

    @Override
    public void railways$markUpdate() {
        updateCount++;
    }
}
