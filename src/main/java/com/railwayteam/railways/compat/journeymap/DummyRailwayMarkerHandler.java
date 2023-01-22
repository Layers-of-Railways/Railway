package com.railwayteam.railways.compat.journeymap;

import java.util.UUID;

public class DummyRailwayMarkerHandler implements IRailwayMarkerHandler {
    static IRailwayMarkerHandler instance = new DummyRailwayMarkerHandler();

    public static IRailwayMarkerHandler getInstance() {
        return instance;
    }

    @Override
    public void removeTrain(UUID uuid) {}

    @Override
    public void removeObsolete() {}

    @Override
    public void runUpdates() {}

    @Override
    public void registerData(UUID uuid, TrainMarkerData data) {}

    @Override
    public void reloadMarkers() {}
}
