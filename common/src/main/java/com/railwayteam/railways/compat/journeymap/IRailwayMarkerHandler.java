package com.railwayteam.railways.compat.journeymap;

import java.util.UUID;

public interface IRailwayMarkerHandler {
    void removeTrain(UUID uuid);

    void removeObsolete();

    void runUpdates();

    void registerData(UUID uuid, TrainMarkerData data);

    void reloadMarkers();

    default void onJoinWorld() {};

    default void disable() {};

    default void enable() {};

    default boolean isEnabled() {
        return true;
    }
}
