package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.coupling.coupler.TrackCoupler;
import com.simibubi.create.content.trains.graph.EdgePointType;


public class CREdgePointTypes {
    public static final EdgePointType<TrackCoupler> COUPLER = EdgePointType.register(Railways.asResource("coupler"), TrackCoupler::new);

    @SuppressWarnings("EmptyMethod")
    public static void register() {}
}
