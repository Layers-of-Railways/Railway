package com.railwayteam.railways.mixin_interfaces;

import com.railwayteam.railways.content.switches.TrackSwitch;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;

public interface IGenerallySearchableNavigation {

    void searchGeneral(double maxDistance, boolean forward, PointTest pointTest);

    void searchGeneral(double maxDistance, double maxCost, boolean forward, PointTest pointTest);

    Pair<TrackSwitch, Pair<Boolean, Optional<TrackSwitchBlock.SwitchState>>> findNearestApproachableSwitch(boolean forward);

    @ApiStatus.Internal
    class FrontierEntry implements Comparable<FrontierEntry> {

        public double distance;
        public int penalty;
        public TrackNode node1;
        public TrackNode node2;
        public TrackEdge edge;

        public FrontierEntry(double distance, int penalty, TrackNode node1, TrackNode node2, TrackEdge edge) {
            this.distance = distance;
            this.penalty = penalty;
            this.node1 = node1;
            this.node2 = node2;
            this.edge = edge;
        }

        @Override
        public int compareTo(FrontierEntry o) {
            return Double.compare(distance + penalty, o.distance + o.penalty);
        }

    }

    @ApiStatus.Internal
    @FunctionalInterface
    interface PointTest {
        boolean test(double distance, double cost, Map<TrackEdge, Pair<Boolean, Couple<TrackNode>>> reachedVia,
                     Pair<Couple<TrackNode>, TrackEdge> current, TrackEdgePoint edgePoint);
    }
}
