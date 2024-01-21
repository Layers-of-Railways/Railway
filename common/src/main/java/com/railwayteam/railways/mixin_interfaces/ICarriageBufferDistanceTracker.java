package com.railwayteam.railways.mixin_interfaces;

import org.jetbrains.annotations.Nullable;

public interface ICarriageBufferDistanceTracker {
    @Nullable Integer railways$getLeadingDistance();
    @Nullable Integer railways$getTrailingDistance();

    void railways$setLeadingDistance(int distance);
    void railways$setTrailingDistance(int distance);
}
