package com.railwayteam.railways.content.switches;

import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SingleTileEdgePoint;

public class TrackSwitch extends SingleTileEdgePoint {

  public TrackSwitch() {

  }

  @Override
  public void onRemoved(TrackGraph graph) {
    removeFromAllGraphs();
    // FIXME: removal behavior for when the block itself is destroyed
  }
}
