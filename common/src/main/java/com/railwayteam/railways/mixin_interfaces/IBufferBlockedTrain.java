package com.railwayteam.railways.mixin_interfaces;

public interface IBufferBlockedTrain {
    boolean snr$isControlBlocked();
    void snr$setControlBlocked(boolean controlBlocked);

    int snr$getBlockedSign();
}
