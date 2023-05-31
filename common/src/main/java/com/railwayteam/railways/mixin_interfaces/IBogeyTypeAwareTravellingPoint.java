package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.trains.IBogeyBlock;

public interface IBogeyTypeAwareTravellingPoint { //TODO bogey api
    IBogeyBlock getType();
    void setType(IBogeyBlock block);
}
