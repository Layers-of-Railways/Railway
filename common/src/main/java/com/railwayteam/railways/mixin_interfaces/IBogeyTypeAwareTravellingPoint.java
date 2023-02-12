package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.logistics.trains.IBogeyBlock;

public interface IBogeyTypeAwareTravellingPoint {
    IBogeyBlock getType();
    void setType(IBogeyBlock block);
}
