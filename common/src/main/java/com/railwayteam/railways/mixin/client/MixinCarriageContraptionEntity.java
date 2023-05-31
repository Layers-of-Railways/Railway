package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.mixin_interfaces.IUpdateCount;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CarriageContraptionEntity.class)
public class MixinCarriageContraptionEntity implements IUpdateCount {
    private int updateCount = 0;

    @Override
    public int snr_getUpdateCount() {
        return updateCount;
    }

    @Override
    public void snr_fromParent(IUpdateCount parent) {
        updateCount = parent.snr_getUpdateCount();
    }

    @Override
    public void snr_markUpdate() {
        updateCount++;
    }
}
