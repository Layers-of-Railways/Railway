/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.mixin_interfaces.ICarriageBufferDistanceTracker;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;

public class TrackBuffer extends SingleBlockEntityEdgePoint {
    @Override
    public boolean canNavigateVia(TrackNode side) {
        return true;
    }

    /**
     * Prevent trains from stopping directly on buffers
     * @return padding to add to the train's stopping distance
     */
    public static int getBufferRoom(Train train) {
        return getBufferRoom(train, train.navigation.destinationBehindTrain);
    }

    /**
     * Prevent trains from stopping directly on buffers
     * @return padding to add to the train's stopping distance
     */
    public static int getBufferRoom(Train train, boolean backwards) {
        Carriage leadingCarriage = backwards
            ? train.carriages.get(train.carriages.size() - 1)
            : train.carriages.get(0);
        if (leadingCarriage instanceof ICarriageBufferDistanceTracker bufferDistanceTracker) {
            Integer distance = backwards
                ? bufferDistanceTracker.railways$getTrailingDistance()
                : bufferDistanceTracker.railways$getLeadingDistance();
            if (distance != null)
                return distance + 1;
        }
        return 1;
    }
}
