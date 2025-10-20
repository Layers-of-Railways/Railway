/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin_interfaces;

import com.railwayteam.railways.content.switches.TrackSwitch;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;

public interface IGenerallySearchableNavigation {

    void railways$searchGeneral(double maxDistance, boolean forward, PointTest pointTest);

    void railways$searchGeneral(double maxDistance, double maxCost, boolean forward, PointTest pointTest);

    Pair<TrackSwitch, Pair<Boolean, Optional<TrackSwitchBlock.SwitchState>>> railways$findNearestApproachableSwitch(boolean forward);

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
