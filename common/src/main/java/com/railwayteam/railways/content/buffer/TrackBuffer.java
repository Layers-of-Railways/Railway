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
                ? bufferDistanceTracker.snr$getTrailingDistance()
                : bufferDistanceTracker.snr$getLeadingDistance();
            if (distance != null)
                return distance + 2;
        }
        return 2;
    }
}
