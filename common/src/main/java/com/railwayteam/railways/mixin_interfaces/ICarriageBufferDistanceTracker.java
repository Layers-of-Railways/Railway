package com.railwayteam.railways.mixin_interfaces;

import org.jetbrains.annotations.Nullable;

public interface ICarriageBufferDistanceTracker {
    @Nullable Integer snr$getLeadingDistance();
    @Nullable Integer snr$getTrailingDistance();

    void snr$setLeadingDistance(int distance);
    void snr$setTrailingDistance(int distance);
}
