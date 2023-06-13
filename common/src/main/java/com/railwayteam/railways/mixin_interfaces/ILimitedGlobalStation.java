package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.trains.entity.Train;

public interface ILimitedGlobalStation extends ILimited {
    boolean isStationEnabled();

    Train getDisablingTrain();

    Train orDisablingTrain(Train before, Train except);
}
