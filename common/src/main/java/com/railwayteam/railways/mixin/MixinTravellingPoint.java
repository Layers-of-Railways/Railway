package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TravellingPoint.class, remap = false)
public class MixinTravellingPoint {
    @Inject(
            method = "travel(Lcom/simibubi/create/content/trains/graph/TrackGraph;DLcom/simibubi/create/content/trains/entity/TravellingPoint$ITrackSelector;Lcom/simibubi/create/content/trains/entity/TravellingPoint$IEdgePointListener;Lcom/simibubi/create/content/trains/entity/TravellingPoint$ITurnListener;Lcom/simibubi/create/content/trains/entity/TravellingPoint$IPortalListener;)D",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/graph/TrackGraph;getConnectionsFrom(Lcom/simibubi/create/content/trains/graph/TrackNode;)Ljava/util/Map;", ordinal = 2))
    private void snr$flipEdgeCheck(TrackGraph graph, double distance, TravellingPoint.ITrackSelector trackSelector, TravellingPoint.IEdgePointListener signalListener, TravellingPoint.ITurnListener turnListener, TravellingPoint.IPortalListener portalListener, CallbackInfoReturnable<Double> cir) {
        Railways.trackEdgeTemporarilyFlipped = true;
    }
}
