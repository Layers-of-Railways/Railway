package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.logistics.trains.entity.BogeyInstance;

public interface IBogeyFrameCanBeMonorail<T extends BogeyInstance> {
    boolean isMonorail();
    T setMonorail(boolean upsideDown);
}
