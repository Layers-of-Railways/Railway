package com.railwayteam.railways.content.switches;

import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.content.logistics.trains.GraphLocation;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SingleTileEdgePoint;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TrackSwitch extends SingleTileEdgePoint {

  public TrackSwitch() {

  }


  @Override
  public boolean canCoexistWith(EdgePointType<?> otherType, boolean front) {
    return otherType == EdgePointType.SIGNAL;
  }

  @Override
  public void onRemoved(TrackGraph graph) {
    removeFromAllGraphs();
  }
}
